package com.trinity.trinity.global.webClient.service;

import com.trinity.trinity.global.redis.service.RedisService;
import com.trinity.trinity.global.webClient.config.WebClientConfig;
import com.trinity.trinity.global.webSocket.WebSocketFrameHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientService {

    private final RedisService redisService;
    private final WebSocketFrameHandler webSocketFrameHandler;
    private final WebClientConfig webClientConfig;

    public void get(String userId) {
        webClientConfig.webClient()
                .get()
                .uri("/api/match/join/{userId}", userId)
                .retrieve()
                .bodyToMono(String.class) // 반환되는 응답의 타입. 필요에 따라 변경
                .subscribe(
                        response -> log.info(response),
                        error -> {
                            String clientId = redisService.getClientId(userId);
                            redisService.removeMatching(userId);
                            redisService.removeUserId(clientId);
                            redisService.removeClientSession(userId);
                            webSocketFrameHandler.sendDataToClient(clientId, "fail and please retry");
                            log.error(error.getMessage());
                        }
                ); // 비동기 처리를 위해 subscribe() 호출;
    }

    public void getCheat(String userId) {
        webClientConfig.webClient()
                .get()
                .uri("/api/match/cheat/join/{userId}", userId)
                .retrieve()
                .bodyToMono(String.class) // 반환되는 응답의 타입. 필요에 따라 변경
                .subscribe(
                        response -> log.info(response),
                        error -> {
                            String clientId = redisService.getClientId(userId);
                            redisService.removeMatching(userId);
                            redisService.removeUserId(clientId);
                            redisService.removeClientSession(userId);
                            webSocketFrameHandler.sendDataToClient(clientId, "fail and please retry");
                            log.error(error.getMessage());
                        }
                ); // 비동기 처리를 위해 subscribe() 호출;
    }
}
