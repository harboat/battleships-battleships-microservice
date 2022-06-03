package com.github.harboat.battleships.shot;

import com.github.harboat.battleships.fleet.FleetService;
import com.github.harboat.clients.game.NukeShotRequest;
import com.github.harboat.clients.game.ShotRequest;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2")
public class ShotService {
    private FleetService fleetService;

    public void takeAShoot(ShotRequest shotRequest) {
        fleetService.shoot(shotRequest);
    }

    public void takeAShoot(NukeShotRequest nukeShotRequest) {
        fleetService.shoot(nukeShotRequest);
    }
}
