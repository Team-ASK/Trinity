package com.trinity.match.global.redis.service;

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
public class RedisServiceImpl implements RedisService {
    @Qualifier("matchRedisTemplate")
    private final RedisTemplate<String, String> matchRedisTemplate;
    @Qualifier("gameRedisTemplate")
    private final RedisTemplate<String, String> gameRedisTemplate;
    @Qualifier("cheatRedisTemplate")
    private final RedisTemplate<String, String> cheatRedisTemplate;

    private ZSetOperations<String, String> matchOperations;
    private ZSetOperations<String, String> cheatOperations;
    private HashOperations<String, String, String> gameOperations;

    @PostConstruct
    private void init() {
        matchOperations = matchRedisTemplate.opsForZSet();
        gameOperations = gameRedisTemplate.opsForHash();
        cheatOperations = cheatRedisTemplate.opsForZSet();
    }

    private static final String MATCH_QUEUE = "matchQueue";
    private static final String CHEAT_QUEUE = "cheatQueue";

    @Override
    public boolean addUser(String userId) {
        double time = System.currentTimeMillis();
        return matchOperations.add(MATCH_QUEUE, userId, time);
    }

    @Override
    public boolean addCheatUser(String userId) {
        double time = System.currentTimeMillis();
        return cheatOperations.add(CHEAT_QUEUE, userId, time);
    }

    @Override
    public void recoverList(List<Pair<String, Double>> waitingList) {
        for (Pair<String, Double> userAndScore : waitingList) {
            matchOperations.add(MATCH_QUEUE, userAndScore.getFirst(), userAndScore.getSecond());
        }
    }

    @Override
    public void deleteData(String key) {
        matchOperations.remove(MATCH_QUEUE, key);
    }

    @Override
    public long getSize() {
        return matchOperations.size(MATCH_QUEUE);
    }

    @Override
    public long getCheatSize() {
        return cheatOperations.size(CHEAT_QUEUE);
    }

    @Override
    public Object validate(String findUserId) {
        return gameOperations.get("connectingMember", findUserId);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> getSet() {
        return matchOperations.rangeWithScores(MATCH_QUEUE, 0, 0);
    }
}
