package com.trinity.trinity.gameRoom.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ThirdRoom {
    private int fertilizerAmount;
    private String player;
    private String message;
    private boolean asteroidStatus;
    private boolean blackholeStatus;
    private int barrierStatus;
    private boolean barrierDevTry;
    private String developer;

    private boolean inputFertilizerTry;
    private boolean makeFertilizerTry;

    @Builder
    public ThirdRoom(int fertilizerAmount, String player, String message, boolean asteroidStatus, boolean blackholeStatus, int barrierStatus, boolean barrierDevTry, String developer, boolean inputFertilizerTry, boolean makeFertilizerTry) {
        this.fertilizerAmount = fertilizerAmount;
        this.player = player;
        this.message = message;
        this.asteroidStatus = asteroidStatus;
        this.blackholeStatus = blackholeStatus;
        this.barrierStatus = barrierStatus;
        this.barrierDevTry = barrierDevTry;
        this.developer = developer;
        this.inputFertilizerTry = inputFertilizerTry;
        this.makeFertilizerTry = makeFertilizerTry;
    }
}