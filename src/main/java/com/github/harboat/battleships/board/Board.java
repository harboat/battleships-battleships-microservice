package com.github.harboat.battleships.board;

import com.github.harboat.clients.game.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @ToString
@Builder
public class Board {
    @Id private String id;
    private String playerId;
    private String gameId;
    private Size size;
    private Map<Integer, Cell> cells;
}
