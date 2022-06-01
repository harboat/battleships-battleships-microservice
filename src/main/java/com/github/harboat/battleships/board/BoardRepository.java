package com.github.harboat.battleships.board;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BoardRepository extends MongoRepository<Board, String> {
    Optional<Board> findByGameIdAndPlayerId(String gameId, String playerId);
}
