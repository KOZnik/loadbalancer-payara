package pl.loadbalancer.control;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Singleton
public class LocalUserRepository implements UserRepository {

    @PostConstruct
    public void initialized() {
        System.out.println("LocalUserRepository: created");
    }

    private Map<String, String> cache = new HashMap<>();

    @Lock(LockType.WRITE)
    @Override
    public void assignUserToGroup(String userId, String group) {
        cache.put(userId, group);
    }

    @Lock(LockType.READ)
    @Override
    public Optional<String> userGroup(String userId) {
        return Optional.ofNullable(cache.get(userId));
    }
}
