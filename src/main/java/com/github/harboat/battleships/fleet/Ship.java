package com.github.harboat.battleships.fleet;

import com.github.harboat.clients.game.OccupiedCells;
import com.github.harboat.clients.game.ShipType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.*;

import java.util.Optional;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @ToString
@Builder
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Ship {
    private ShipType shipType;
    private MastsState masts;
    private OccupiedCells cells;

    Optional<Ship> hit(Integer cellId) {
        if (!masts.getMasts().containsKey(cellId)) {
            return  Optional.empty();
        }
        masts.getMasts().put(cellId, MastState.HIT);
        return Optional.of(this);
    }

    Boolean isAlive() {
        return masts.getMasts().values()
                .stream()
                .anyMatch(MastState.ALIVE::equals);
    }

    ShipDto toDto() {
        return ShipDto.builder()
                .masts(masts.getMasts().keySet())
                .cells(cells.getPositions())
                .shipType(shipType)
                .build();
    }
}
