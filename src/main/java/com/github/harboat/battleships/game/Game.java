package com.github.harboat.battleships.game;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;

@Document
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @ToString
@Builder
public class Game {
    @Id private String id;

    private Collection<String> playerIds;

    private String turnOfPlayer;
}
