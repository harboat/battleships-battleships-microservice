package com.github.harboat.battleships.fleet;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FleetRepository extends MongoRepository<Fleet, String> {
    Optional<Fleet> findByGameIdAndPlayerId(String gameId, String playerId);
}
