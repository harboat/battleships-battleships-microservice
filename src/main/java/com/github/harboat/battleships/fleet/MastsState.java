package com.github.harboat.battleships.fleet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@AllArgsConstructor
@Getter @Setter @ToString
public class MastsState {
    private final Map<Integer, MastState> masts;
}
