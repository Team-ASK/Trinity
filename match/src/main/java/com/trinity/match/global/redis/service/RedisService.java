package com.trinity.match.global.redis.service;

import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Set;

public interface RedisService {

    boolean addUser(String userId);
    void recoverList(List<Pair<String, Double>> waitingList);
    void deleteData(String key);
    long getSize();
    Object validate(String findUserId);
    Set<ZSetOperations.TypedTuple<String>> getSet();
}
