package com.github.harboat.battleships.game;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GameUtility {

    private GameRepository repository;

    public String getEnemyId(String gameId, String playerId) {
        return repository.findById(gameId).orElseThrow()
                .getPlayerIds().stream()
                .filter(pid -> !pid.equals(playerId))
                .findFirst().orElseThrow();
    }
}
