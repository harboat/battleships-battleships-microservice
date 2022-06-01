package com.github.harboat.battleships.shot;

import com.github.harboat.battleships.fleet.FleetService;
import com.github.harboat.clients.game.NukeShotRequest;
import com.github.harboat.clients.game.ShotRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ShotService {
    private FleetService fleetService;

    public void takeAShoot(ShotRequest shotRequest) {
        fleetService.shoot(shotRequest);
    }

    public void takeAShoot(NukeShotRequest nukeShotRequest) {
        fleetService.shoot(nukeShotRequest);
    }
}
