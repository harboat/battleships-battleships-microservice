package com.github.harboat.battleships.fleet;

import lombok.*;

import java.util.Collection;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @ToString
@Builder
public class FleetDto {
    private String playerId;
    private String gameId;
    private Collection<ShipDto> ships;
}
