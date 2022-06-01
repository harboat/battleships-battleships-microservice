package com.github.harboat.battleships.fleet;

import com.github.harboat.clients.game.ShipType;
import lombok.*;

import java.util.Collection;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @ToString
@Builder
public class ShipDto {
    private ShipType shipType;
    private Collection<Integer> masts;
    private Collection<Integer> cells;
}
