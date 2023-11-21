package com.trinity.match.demo.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CheatRedisServiceImpl implements CheatRedisService {

    @Qualifier("cheatRedisTemplate")
    private final RedisTemplate<String, String> cheatRedisTemplate;
    @Qualifier("gameRedisTemplate")
    private final RedisTemplate<String, String> gameRedisTemplate;

    private ZSetOperations<String, String> cheatOperations;
    private HashOperations<String, String, String> gameOperations;

    private static final String CHEAT_QUEUE = "cheatQueue";

    @PostConstruct
    private void init() {
        cheatOperations = cheatRedisTemplate.opsForZSet();
        gameOperations = gameRedisTemplate.opsForHash();
    }

    @Override
    public boolean addUser(String userId) {
        double time = System.currentTimeMillis();
        return cheatOperations.add(CHEAT_QUEUE, userId, time);
    }

    @Override
    public void recoverList(List<Pair<String, Double>> waitingList) {
        for (Pair<String, Double> userAndScore : waitingList) {
            cheatOperations.add(CHEAT_QUEUE, userAndScore.getFirst(), userAndScore.getSecond());
        }
    }

    @Override
    public void deleteData(String key) {
        cheatOperations.remove(CHEAT_QUEUE, key);
    }

    @Override
    public Object validate(String findUserId) {
        return gameOperations.get("connectingMember", findUserId);
    }


    @Override
    public long getSize() {
        return cheatOperations.size(CHEAT_QUEUE);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> getSet() {
        return cheatOperations.rangeWithScores(CHEAT_QUEUE, 0, 0);
    }
}
