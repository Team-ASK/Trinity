package com.trinity.match.domain.matchQ.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface MatchQSwaggerController {

    @GetMapping("/join/{userId}")
    ResponseEntity<?> joinQueue(@Validated @PathVariable String userId);

    @GetMapping("/cheat/join/{userId}")
    ResponseEntity<?> cheatJoinQueue(@Validated @PathVariable String userId);
}
