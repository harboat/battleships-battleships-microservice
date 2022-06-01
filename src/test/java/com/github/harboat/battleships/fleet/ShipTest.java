package com.github.harboat.battleships.fleet;

import com.github.harboat.clients.game.OccupiedCells;
import com.github.harboat.clients.game.ShipType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Test
public class ShipTest {

    private Ship ship;

    @BeforeMethod
    public void setUp() {
        ship = new Ship(ShipType.DESTROYER,
                new MastsState(new HashMap<Integer, MastState>() {{
                    put(1, MastState.ALIVE);
                }}),
                new OccupiedCells(Arrays.asList(2, 11, 12)));
    }

    @Test
    public void shouldReturnDtoWithProperMasts() {
        //given
        //when
        ShipDto actual = ship.toDto();
        //then
        assertEquals(actual.getMasts(), List.of(1));
    }

    @Test
    public void shouldReturnDtoWithProperOccupiedCells() {
        //given
        //when
        ShipDto actual = ship.toDto();
        //then
        assertEquals(actual.getCells(), List.of(2, 11, 12));
    }
}
