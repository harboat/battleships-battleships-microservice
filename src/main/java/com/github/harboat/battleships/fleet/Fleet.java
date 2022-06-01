package com.github.harboat.battleships.fleet;

import com.github.harboat.clients.game.ShipType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Document
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @ToString
@Builder
public class Fleet {
    @Id private String id;
    private String gameId;
    private String playerId;
    private Collection<Ship> ships;

    Collection<Integer> getAllCells() {
        return ships.stream()
                .flatMap(s ->
                        Stream.concat(
                                s.getMasts().getMasts().keySet().stream(),
                                s.getCells().getPositions().stream()
                        )
                )
                .collect(Collectors.toSet());
    }

    Boolean isAlive() {
        return ships.stream()
                .anyMatch(Ship::isAlive);
    }

    Optional<Ship> takeAShot(Integer cellId) {
        return ships.stream()
                .map(s -> s.hit(cellId))
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(ship -> ship);
    }

    FleetDto toDto() {
        return FleetDto.builder()
                .gameId(gameId)
                .playerId(playerId)
                .ships(ships.stream().map(Ship::toDto).toList())
                .build();
    }

    boolean hasFourMastShipWithThreeMastsAlive() {
        return ships.stream()
                .filter(s -> s.getShipType().equals(ShipType.BATTLESHIP))
                .anyMatch(s ->
                        s.getMasts().getMasts().values()
                                .stream()
                                .filter(m -> m.equals(MastState.ALIVE))
                                .toList().size() >= 3
                );
    }
}
