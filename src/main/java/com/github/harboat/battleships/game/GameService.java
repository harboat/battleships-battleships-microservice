package com.github.harboat.battleships.game;

import com.github.harboat.battleships.CoreQueueProducer;
import com.github.harboat.battleships.board.BoardService;
import com.github.harboat.battleships.fleet.FleetService;
import com.github.harboat.clients.game.GameCreate;
import com.github.harboat.clients.game.GameCreated;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Random;
import java.util.Set;

@Service
@AllArgsConstructor
public class GameService {

    private final GameRepository repository;
    private final BoardService boardService;
    private final FleetService fleetService;
    private final CoreQueueProducer producer;
    private final Random random = new SecureRandom();
    @Transactional
    public void createGame(GameCreate gameCreate) {
        Set<String> playerIds = gameCreate.fleet().keySet();
        Game game = repository.save(
                Game.builder()
                    .playerIds(playerIds)
                    .turnOfPlayer(playerIds.stream().toList().get(random.nextInt(0, 2)))
                    .build()
        );
        playerIds.forEach(p -> boardService.createBoard(game.getId(), p, gameCreate.size()));
        playerIds.forEach(p -> fleetService.create(game.getId(), p, gameCreate.fleet().get(p)));
        producer.sendResponse(
                new GameCreated(game.getId(), playerIds, game.getTurnOfPlayer())
        );
    }
}
