package com.github.harboat.battleships;

import com.github.harboat.battleships.game.GameService;
import com.github.harboat.battleships.shot.ShotService;
import com.github.harboat.clients.game.GameCreate;
import com.github.harboat.clients.game.NukeShotRequest;
import com.github.harboat.clients.game.ShotRequest;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@RabbitListener(
        queues = {"${rabbitmq.queues.game}"}
)
public class GameQueueConsumer {

    private GameService gameService;
    private ShotService shotService;

    @RabbitHandler
    public void consume(GameCreate gameCreate) {
        gameService.createGame(gameCreate);
    }

    @RabbitHandler
    public void consumer(ShotRequest shotRequest) {
        shotService.takeAShoot(shotRequest);
    }

    @RabbitHandler
    public void consumer(NukeShotRequest nukeShotRequest) {
        shotService.takeAShoot(nukeShotRequest);
    }

}
