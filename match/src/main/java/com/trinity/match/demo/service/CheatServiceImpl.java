package com.trinity.match.demo.service;

import com.trinity.match.demo.redis.CheatRedisService;
import com.trinity.match.demo.webClient.CheatWebClientService;
import com.trinity.match.domain.matchQ.dto.request.GameServerPlayerListRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheatServiceImpl implements CheatService {

    private final CheatRedisService cheatRedisService;
    private final RedissonClient matchRedissonClient;
    private final CheatWebClientService cheatWebClientService;

    private static final String CHEAT_LOCK_NAME = "cheatQueueLock";

    @Override
    public boolean joinQueue(String userId) {
        RLock lock = matchRedissonClient.getLock(CHEAT_LOCK_NAME);
        lock.lock();
        try {
            cheatRedisService.addUser(userId);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Scheduled(fixedRate = 2000)
    private void checkCheatQueueSize() {
        RLock lock = matchRedissonClient.getLock(CHEAT_LOCK_NAME);
        lock.lock();
        List<Pair<String, Double>> waitingList = new ArrayList<>();
        try {
            // 대기 큐의 크기가 3 보다 작으면 그만
            if (cheatRedisService.getSize() < 3) return;

            while (waitingList.size() != 3) {
                // 1순위 사람 뽑기
                Set<ZSetOperations.TypedTuple<String>> rangeWithScores = cheatRedisService.getSet();
                if (rangeWithScores == null || rangeWithScores.isEmpty()) break;

                ZSetOperations.TypedTuple<String> next = rangeWithScores.iterator().next();
                String findUserId = next.getValue();
                Double score = next.getScore();

                // 게임 서버 redis에 접근해 유효성 검사
                Object state = cheatRedisService.validate(findUserId);

                if (state != null && state.toString().equals("WAITING")) {
                    waitingList.add(Pair.of(findUserId, score));
                }
                cheatRedisService.deleteData(findUserId);
            }

            // 게임 서버에 보낼 리스트의 크기가 3보다 작으면 다시 대기큐에 넣고 돌아가기
            if (waitingList.size() < 3) {
                cheatRedisService.recoverList(waitingList);
                return;
            }

            for (Pair<String, Double> userAndScore : waitingList) {
                cheatRedisService.deleteData(userAndScore.getFirst());
            }

            List<GameServerPlayerListRequestDto> playerList = new ArrayList<>();
            for (Pair<String, Double> userAndScore : waitingList)
                playerList.add(GameServerPlayerListRequestDto.builder()
                        .userId(userAndScore.getFirst())
                        .build());

            cheatWebClientService.postCheat(playerList, waitingList);

        } catch (Exception e) {
            // 에러 발생하면 에러 메시지 찍고 대기 큐에 다시 넣기
            log.error(e.getMessage());
            cheatRedisService.recoverList(waitingList);
        } finally {
            lock.unlock();
        }
    }
}
