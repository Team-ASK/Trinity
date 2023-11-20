package com.trinity.match.domain.matchQ.service;

import com.trinity.match.domain.matchQ.dto.request.GameServerPlayerListRequestDto;
import com.trinity.match.global.redis.service.RedisService;
import com.trinity.match.global.webClient.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchQServiceImpl implements MatchQService {

    private final RedisService redisService;
    private final RedissonClient matchRedissonClient;
    private final WebClientService webClientService;

    private static final String LOCK_NAME = "matchQueueLock";

    static Queue<String> cheatQ = new ArrayDeque<>();

    @Override
    public boolean joinQueue(String userId) {
        RLock lock = matchRedissonClient.getLock(LOCK_NAME);
        lock.lock();
        try {
            return redisService.addUser(userId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean cheatJoinQueue(String userId) {
        RLock lock = matchRedissonClient.getLock(LOCK_NAME);
        lock.lock();
        try {
            redisService.addCheatUser(userId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        } finally {
            lock.unlock();
        }

        if (cheatQ.size() == 3) {
            List<GameServerPlayerListRequestDto> playerList = new ArrayList<>();
            for (String q : cheatQ) {
                playerList.add(GameServerPlayerListRequestDto.builder()
                        .userId(q)
                        .build());
            }

            webClientService.postCheat(playerList);
        }

        return true;
    }

    @Scheduled(fixedRate = 2000)
    private void checkQueueSize() {
        RLock lock = matchRedissonClient.getLock(LOCK_NAME);
        lock.lock();
        List<Pair<String, Double>> waitingList = new ArrayList<>();
        try {
            // 대기 큐의 크기가 3 보다 작으면 그만
            if (redisService.getSize() < 3) return;

            while (waitingList.size() != 3) {
                // 1순위 사람 뽑기
                Set<ZSetOperations.TypedTuple<String>> rangeWithScores = redisService.getSet();
                if (rangeWithScores == null || rangeWithScores.isEmpty()) break;

                ZSetOperations.TypedTuple<String> next = rangeWithScores.iterator().next();
                String findUserId = next.getValue();
                Double score = next.getScore();

                // 게임 서버 redis에 접근해 유효성 검사
                Object state = redisService.validate(findUserId);

                if (state != null && state.toString().equals("WAITING")) {
                    waitingList.add(Pair.of(findUserId, score));
                }
                redisService.deleteData(findUserId);
            }

            // 게임 서버에 보낼 리스트의 크기가 3보다 작으면 다시 대기큐에 넣고 돌아가기
            if (waitingList.size() < 3) {
                redisService.recoverList(waitingList);
                return;
            }

            for (Pair<String, Double> userAndScore : waitingList) {
                redisService.deleteData(userAndScore.getFirst());
            }

            List<GameServerPlayerListRequestDto> playerList = new ArrayList<>();
            for (Pair<String, Double> userAndScore : waitingList)
                playerList.add(GameServerPlayerListRequestDto.builder()
                        .userId(userAndScore.getFirst())
                        .build());

            webClientService.post(playerList, waitingList);

        } catch (Exception e) {
            // 에러 발생하면 에러 메시지 찍고 대기 큐에 다시 넣기
            log.error(e.getMessage());
            redisService.recoverList(waitingList);
        } finally {
            lock.unlock();
        }
    }
}