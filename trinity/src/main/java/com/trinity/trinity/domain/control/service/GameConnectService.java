package com.trinity.trinity.domain.control.service;

import com.trinity.trinity.domain.control.dto.PlayerDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GameConnectService {
    PlayerDto connectToGameServer();

    boolean matchMaking(String userId);

    boolean checkUserStatus(List<PlayerDto> players);

//    void checkEnteringQ(String userId, String response)
;
    void createGameRoom(List<PlayerDto> players);
}
