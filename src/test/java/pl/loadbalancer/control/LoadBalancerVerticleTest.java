package pl.loadbalancer.control;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;

@Ignore
public class LoadBalancerVerticleTest {

    static WebTarget target = ClientBuilder.newClient().target("http://localhost:8080/loadbalancer/resources/route");

    @Test
    public void shouldReturnSameGroupsForKnownClients() throws InterruptedException {
        //given
        List<String> clientIds = testClientIds();

        //when
        Map<String, String> clientGroups = getGroupsForClients(clientIds);

        //then
        for (String clientId : clientIds) {
            Response response = target.path(clientId).request().get();

            Assert.assertThat(response.readEntity(String.class), is(clientGroups.get(clientId)));
            Assert.assertThat(response.getStatusInfo().getFamily(), is(SUCCESSFUL));
        }
    }

    @Test
    public void shouldBalanceUsersToGroupsFromConfiguration() {
        //given
        List<String> clientIds = testClientIds();

        //when
        Map<String, String> clientGroups = getGroupsForClients(clientIds);

        //then
        GroupsConfiguration.INSTANCE.getGroups().forEach(g -> {
            double frequency = calculatePercentageFrequencyFor(g.getName(), clientGroups.values());
            Assert.assertThat(frequency, is(closeTo(g.getWeight() * 10, 5)));
        });
    }

    private List<String> testClientIds() {
        int size = 10000;
        List<String> clientIds = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            clientIds.add(UUID.randomUUID().toString());
        }
        return clientIds;
    }

    private Map<String, String> getGroupsForClients(List<String> clientIds) {
        return clientIds.stream().collect(Collectors.toMap((c) -> c, c -> {
            Response response = target.path(c).request().get();
            Assert.assertThat(response.getStatusInfo().getFamily(), is(SUCCESSFUL));
            return response.readEntity(String.class);
        }));
    }

    private double calculatePercentageFrequencyFor(String string, Collection<String> all) {
        double occurrences = all.stream().filter(s -> s.equals(string)).count();
        return occurrences / all.size() * 100;
    }

}