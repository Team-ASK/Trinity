package com.trinity.match.global.webClient;

import com.trinity.match.domain.matchQ.dto.request.GameServerPlayerListRequestDto;
import com.trinity.match.global.redis.service.RedisServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientService {

    private final WebClientConfig webClientConfig;
    private final RedisServiceImpl redisService;

    public void post(List<GameServerPlayerListRequestDto> playerList, List<Pair<String, Double>> waitingList) {
        webClientConfig.webClient()
                .post()
                .uri("/players")
                .body(BodyInserters.fromValue(playerList))
                .retrieve()
                .bodyToMono(String.class) // 반환되는 응답의 타입. 필요에 따라 변경
                .subscribe(
                        response -> log.info(response),
                        error -> {
                        redisService.recoverList(waitingList);
                            log.error(error.getMessage());
                        }
                ); // 비동기 처리를 위해 subscribe() 호출;
    }

    public void postCheat(List<GameServerPlayerListRequestDto> playerList) {
        webClientConfig.webClient()
                .post()
                .uri("/players")
                .body(BodyInserters.fromValue(playerList))
                .retrieve()
                .bodyToMono(String.class) // 반환되는 응답의 타입. 필요에 따라 변경
                .subscribe(
                        response -> log.info(response),
                        error -> log.error(error.getMessage())
                ); // 비동기 처리를 위해 subscribe() 호출;
    }
}