package pl.loadbalancer.control;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Balancer {

    private final List<String> groups = new ArrayList<>();

    public Balancer() {
        GroupsConfiguration.INSTANCE.getGroups().forEach(g ->
                IntStream.range(0, g.weight).forEach((i) -> groups.add(g.name)));
        System.out.println("Balancer: created");
    }

    public String routeUser(String clientId) {
        return groups.get(Math.abs(clientId.hashCode() % 10));
    }

}
