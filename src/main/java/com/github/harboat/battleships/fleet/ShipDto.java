package com.github.harboat.battleships.fleet;

import com.github.harboat.clients.game.ShipType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.*;

import java.util.Collection;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @ToString
@Builder
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ShipDto {
    private ShipType shipType;
    private Collection<Integer> masts;
    private Collection<Integer> cells;
}
