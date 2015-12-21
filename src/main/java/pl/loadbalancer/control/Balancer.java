package pl.loadbalancer.control;

import javax.inject.Inject;

public class Balancer {

    public Balancer() {
        System.out.println("Balancer: created");
    }

    @Inject
    private UserRepository repository;

    @Inject
    private ListBalancingStrategy strategy;

    public String routeUser(String clientId) {
        return repository.userGroup(clientId).orElseGet(() -> {
            String calculated = strategy.calculateNextFreeGroup();
            repository.assignUserToGroup(clientId, calculated);
            return calculated;
        });
    }

}
