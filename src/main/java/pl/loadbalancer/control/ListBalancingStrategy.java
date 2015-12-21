package pl.loadbalancer.control;

import pl.loadbalancer.control.GroupsConfiguration.Group;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ListBalancingStrategy {

    @PostConstruct
    public void initialized() {
        System.out.println("ListBalancingStrategy: created");
    }

    public static final int QUEUE_SIZE = 10000;

    final List<String> seed = new ArrayList<>();
    int index = 0;

    public ListBalancingStrategy() {
        prepareSeed();
    }

    private void prepareSeed() {
        for (Group group : GroupsConfiguration.INSTANCE.getGroups()) {
            IntStream.range(0, group.weight).forEach(t -> seed.add(group.name));
        }
    }

    public String calculateNextFreeGroup() {
        if (index == seed.size()) {
            index = 0;
        }
        return seed.get(index++);
    }
}
