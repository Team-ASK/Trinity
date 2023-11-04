package com.trinity.trinity.gameRoom.service;

import com.trinity.trinity.DTO.request.FirstRoomPlayerRequestDto;
import com.trinity.trinity.DTO.request.GameStartPlayerListRequestDto;
import com.trinity.trinity.DTO.request.SecondRoomPlayerRequestDto;
import com.trinity.trinity.DTO.request.ThirdRoomPlayerRequestDto;
import com.trinity.trinity.gameRoom.dto.GameRoom;

import java.util.List;

public interface GameRoomService {
    GameRoom createGameRoom(List<GameStartPlayerListRequestDto> players);

    void updateFirstRoom(FirstRoomPlayerRequestDto firstRoomPlayerRequestDto);

    void updateSecondRoom(SecondRoomPlayerRequestDto secondRoomPlayerRequestDto);

    void updateThridRoom(ThirdRoomPlayerRequestDto thirdRoomPlayerRequestDto);

    boolean gameLogic(String gameRoomId);

    void morningGameLogic(String gameRoomId);

}
