package me.veso.attendanceservice.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.entity.UserId;
import me.veso.attendanceservice.repository.UserIdRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UsersCache {
    private final UserIdRepository userIdRepository;
    private final CacheManager cacheManager;

    @PostConstruct
    public void preloadCache(){
        List<UserId> all = userIdRepository.findAll();
        if(all.isEmpty()){
            return;
        }

        Cache usersByUserIdCache = cacheManager.getCache("usersByUserId");

        if (usersByUserIdCache != null){
            usersByUserIdCache.clear();
            all.forEach(user -> usersByUserIdCache.put(user.getUserId(), user));
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void refreshCache(){
        preloadCache();
    }
}
