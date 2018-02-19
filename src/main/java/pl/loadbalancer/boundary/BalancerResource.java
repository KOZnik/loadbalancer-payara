package pl.loadbalancer.boundary;

import pl.loadbalancer.control.Balancer;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Stateless
@Path("/route")
public class BalancerResource {

    @PostConstruct
    public void initialized() {
        System.out.println("BalancerResource: created");
    }

    @Inject
    private Balancer balancer;

    @GET
    @Path("/{clientId}")
    @Produces("application/json")
    public String route(@PathParam("clientId") String clientId) {
        return balancer.routeUser(clientId);
    }

}
