package com.trinity.trinity.domain.control.api;

import com.trinity.trinity.domain.control.dto.PlayerDto;
import com.trinity.trinity.domain.control.service.GameConnectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameConnectController {

    private final GameConnectService gameConnectService;

    @GetMapping({"", "/"})
    public ResponseEntity<?> connectToGameServer() {
        return ResponseEntity.ok()
                .body(gameConnectService.connectToGameServer());
    }

    @GetMapping("/match/{userId}")
    public ResponseEntity<?> matchMaking(@Validated @PathVariable String userId) {
        boolean result = gameConnectService.matchMaking(userId);
        if(result) return ResponseEntity.ok().build();
        else {
            return ResponseEntity.badRequest().body("MatchingFail");
        }
    }

    @GetMapping("/cheat/match/{userId}")
    public ResponseEntity<?> cheatMatchMaking(@Validated @PathVariable String userId) {
        boolean result = gameConnectService.cheatMatchMaking(userId);
        if(result) return ResponseEntity.ok().build();
        else {
            return ResponseEntity.badRequest().body("MatchingFail");
        }
    }



    @PostMapping("/players")
    public ResponseEntity<?> takePlayer(@Validated @RequestBody List<PlayerDto> players) {
        if(gameConnectService.checkUserStatus(players)) {
            log.info("all member ready");
            gameConnectService.createGameRoom(players);
            return ResponseEntity.ok().build();
        } else {
            log.info("do not ready for game");
            return ResponseEntity.badRequest().body("Do not ready for game");
        }
    }
}
