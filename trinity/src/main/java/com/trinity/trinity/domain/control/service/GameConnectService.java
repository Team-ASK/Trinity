package com.trinity.trinity.domain.control.service;

import com.trinity.trinity.domain.control.dto.PlayerDto;

import java.util.List;

public interface GameConnectService {
    PlayerDto connectToGameServer();

    boolean matchMaking(String userId);

    boolean cheatMatchMaking(String userId);

    boolean checkUserStatus(List<PlayerDto> players);

    void createGameRoom(List<PlayerDto> players);
}
