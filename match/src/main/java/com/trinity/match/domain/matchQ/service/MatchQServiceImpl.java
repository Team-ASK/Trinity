package com.trinity.match.domain.matchQ.service;

import com.trinity.match.domain.matchQ.dto.request.GameServerPlayerListRequestDto;
import com.trinity.match.global.webClient.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
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
public class MatchQServiceImpl implements MatchQService {

    @Qualifier("matchRedisTemplate")
    private final RedisTemplate<String, String> matchRedisTemplate;
    @Qualifier("gameRedisTemplate")
    private final RedisTemplate<String, String> gameRedisTemplate;

    private final WebClientService webClientService;

    @Override
    public void joinQueue(String userId) {
        double time = System.currentTimeMillis();
        matchRedisTemplate.opsForZSet().add("matchQueue", userId, time);
    }

    @Scheduled(fixedRate = 10000)
    private void checkQueueSize() {
        // SessionCallback 내에 트랜잭션 구현
        matchRedisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) {
                List<Pair<String, Double>> waitingList = new ArrayList<>();
                try {
                    // watch함수로 대기 큐의 변경 감지
                    operations.watch("matchQueue");

                    // 대기 큐의 크기가 3 보다 작으면 그만
                    Long size = operations.opsForZSet().size("matchQueue");
                    if (size < 3) return null;

                    while (waitingList.size() != 3) {
                        // 1순위 사람 뽑기
                        Set<ZSetOperations.TypedTuple<String>> rangeWithScores = operations.opsForZSet().rangeWithScores("matchQueue", 0, 0);
                        if (rangeWithScores == null || rangeWithScores.isEmpty()) break;

                        ZSetOperations.TypedTuple<String> next = rangeWithScores.iterator().next();
                        String findUserId = next.getValue();
                        Double score = next.getScore();

                        // 게임 서버 redis에 접근해 유효성 검사
                        Object state = gameRedisTemplate.opsForHash().get("connectingMember", findUserId);
                        if (state != null && state.toString().equals("WAITING")) {
                            waitingList.add(Pair.of(findUserId, score));
                        }
                        operations.opsForZSet().remove("matchQueue", findUserId);
                    }

                    // 게임 서버에 보낼 리스트의 크기가 3보다 작으면 다시 대기큐에 넣고 돌아가기
                    if (waitingList.size() < 3) {
                        recoverList(waitingList);
                        return null;
                    }

                    // 트랜잭션 시작
                    operations.multi();

                    // 트랜잭션 실행
                    operations.exec();

                    List<GameServerPlayerListRequestDto> playerList = new ArrayList<>();
                    for (Pair<String, Double> userAndScore : waitingList)
                        playerList.add(GameServerPlayerListRequestDto.builder()
                                .userId(userAndScore.getFirst())
                                .build());

                    webClientService.post(playerList);

                } catch (Exception e) {
                    // 에러 발생하면 에러 메시지 찍고 대기 큐에 다시 넣기
                    log.error(e.getMessage());
                    recoverList(waitingList);
                }
                return null;
            }

        });
    }

    private void recoverList(List<Pair<String, Double>> waitingList) {
        for (Pair<String, Double> userAndScore : waitingList) {
            matchRedisTemplate.opsForZSet().add("matchQueue", userAndScore.getFirst(), userAndScore.getSecond());
        }
    }
}