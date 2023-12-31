package com.trinity.trinity.domain.control.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlayerDto {
    private String userId;

    @Builder
    public PlayerDto(String userId) {
        this.userId = userId;
    }
}
