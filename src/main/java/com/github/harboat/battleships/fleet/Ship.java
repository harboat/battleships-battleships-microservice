package com.github.harboat.battleships.fleet;

import com.github.harboat.clients.game.OccupiedCells;
import com.github.harboat.clients.game.ShipType;
import lombok.*;

import java.util.Optional;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @ToString
@Builder
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
