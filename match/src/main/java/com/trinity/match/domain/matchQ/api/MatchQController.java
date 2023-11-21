package com.trinity.match.domain.matchQ.api;

import com.trinity.match.domain.matchQ.service.MatchQService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchQController implements MatchQSwaggerController {

    private final MatchQService matchQService;

    @Override
    public ResponseEntity<?> joinQueue(String userId) {
        if (matchQService.joinQueue(userId)) {
            return ResponseEntity.ok().body("success");
        } else {
            return ResponseEntity.badRequest().body("fail");
        }
    }

    @Override
    public ResponseEntity<?> cheatJoinQueue(String userId) {
        if (matchQService.cheatJoinQueue(userId)) {
            return ResponseEntity.ok().body("success");
        } else {
            return ResponseEntity.badRequest().body("fail");
        }
    }
}
