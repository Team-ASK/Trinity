package com.trinity.trinity.domain.control.service;

import com.google.gson.Gson;
import com.trinity.trinity.domain.control.dto.PlayerDto;
import com.trinity.trinity.domain.control.dto.response.*;
import com.trinity.trinity.domain.logic.dto.GameRoom;
import com.trinity.trinity.domain.logic.service.CreateService;
import com.trinity.trinity.domain.control.enums.UserStatus;
import com.trinity.trinity.global.redis.service.RedisService;
import com.trinity.trinity.global.webClient.service.WebClientService;
import com.trinity.trinity.global.webSocket.WebSocketFrameHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameConnectServiceImpl implements GameConnectService {

    private final RedisService redisService;
    private final WebClientService webClientService;
    private final CreateService createService;
    private final WebSocketFrameHandler webSocketFrameHandler;

    @Override
    public PlayerDto connectToGameServer() {
        String userId = UUID.randomUUID().toString();
        redisService.saveData(userId, String.valueOf(UserStatus.LOBBY));

        return PlayerDto.builder()
                .userId(userId)
                .build();
    }

    @Override
    public boolean matchMaking(String userId) {
        if(redisService.getData(userId) == null) return false;
        redisService.saveData(userId, String.valueOf(UserStatus.WAITING));
        webClientService.get(userId);
        return true;
    }

    @Override
    public boolean cheatMatchMaking(String userId) {
        if(redisService.getData(userId) == null) return false;
        redisService.saveData(userId, String.valueOf(UserStatus.WAITING));
        webClientService.getCheat(userId);
        return true;
    }

    @Override
    public boolean checkUserStatus(List<PlayerDto> players) {
        for(PlayerDto p : players) {
            if(!redisService.getData(p.getUserId()).equals("WAITING")) return false;
        }
        return true;
    }

    @Override
    public void createGameRoom(List<PlayerDto> players) {

        GameRoom gameRoom = createService.createGameRoom(players);

        log.info("round No : " + gameRoom.getRoundNo());

        redisService.saveGameRoomUserStatus(gameRoom.getGameRoomId());

        Gson gson = new Gson();

        CommonDataDto commonDataDto = CommonDataDto.builder()
                .conflictAsteroid(false)
                .barrierUpgrade(false)
                .fertilizerUpgrade(false)
                .carbonCaptureNotice(gameRoom.isCarbonCaptureNotice())
                .build();

        FirstRoomResponseDto firstRoomResponseDto = FirstRoomResponseDto.builder()
                .type("firstDay")
                .build();
        firstRoomResponseDto.modifyFirstRoomResponseDto(commonDataDto, gameRoom);

        SecondRoomResponseDto secondRoomResponseDto = SecondRoomResponseDto.builder()
                .type("firstDay")
                .build();
        secondRoomResponseDto.modifySecondRoomResponseDto(commonDataDto, gameRoom);

        ThirdRoomResponseDto thirdRoomResponseDto = ThirdRoomResponseDto.builder()
                .type("firstDay")
                .build();
        thirdRoomResponseDto.modifyThirdRoomResponseDto(commonDataDto, gameRoom);

        String firstRoom = gson.toJson(firstRoomResponseDto);
        String secondRoom = gson.toJson(secondRoomResponseDto);
        String thirdRoom = gson.toJson(thirdRoomResponseDto);

        String firstUserClientId = redisService.getClientId(gameRoom.getFirstRoom().getUserId());
        String secondUserClientId = redisService.getClientId(gameRoom.getSecondRoom().getUserId());
        String thirdUserClientId = redisService.getClientId(gameRoom.getThirdRoom().getUserId());

        webSocketFrameHandler.sendDataToClient(firstUserClientId, firstRoom);
        webSocketFrameHandler.sendDataToClient(secondUserClientId, secondRoom);
        webSocketFrameHandler.sendDataToClient(thirdUserClientId, thirdRoom);

        redisService.saveData(gameRoom.getFirstRoom().getUserId(), String.valueOf(UserStatus.PLAYING));
        redisService.saveData(gameRoom.getSecondRoom().getUserId(), String.valueOf(UserStatus.PLAYING));
        redisService.saveData(gameRoom.getThirdRoom().getUserId(), String.valueOf(UserStatus.PLAYING));

    }
}
