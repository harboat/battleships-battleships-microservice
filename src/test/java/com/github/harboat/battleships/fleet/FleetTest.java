package com.github.harboat.battleships.fleet;

import com.github.harboat.clients.game.OccupiedCells;
import com.github.harboat.clients.game.ShipType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.*;

@Test
public class FleetTest {
    private final String gameId = "test";
    private final String playerId = "testPlayer";
    private final String fleetId = "testFleet";

    private Fleet fleet;

    @BeforeMethod
    public void setUp() {
        Ship ship1 = new Ship(ShipType.DESTROYER,
                new MastsState(new HashMap<Integer, MastState>() {{
                    put(1, MastState.ALIVE);
                }}),
                new OccupiedCells(Arrays.asList(2, 11, 12)));

        Ship ship2 = new Ship(ShipType.SUBMARINE,
                new MastsState(new HashMap<Integer, MastState>() {{
                    put(3, MastState.ALIVE);
                    put(4, MastState.ALIVE);
                }}),
                new OccupiedCells(Arrays.asList(2, 12, 13, 14, 15, 5)));
        fleet = new Fleet(fleetId, gameId, playerId, Arrays.asList(ship1, ship2));
    }

    @Test
    public void shouldReturnProperCells() {
        //given
        //when
        Collection<Integer> actual = fleet.getAllCells();
        //then
        assertTrue(actual.containsAll(List.of(2, 11, 12, 13, 14, 15, 5)));
    }

    @Test
    public void shouldReturnDTOWithProperGameId() {
        //given
        //when
        FleetDto actual = fleet.toDto();
        //then
        assertEquals(actual.getGameId(), gameId);
    }

    @Test
    public void shouldReturnDTOWithProperPlayerId() {
        //given
        //when
        FleetDto actual = fleet.toDto();
        //then
        assertEquals(actual.getPlayerId(), playerId);
    }

    @Test
    public void shouldReturnDTOWithProperShips() {
        //given
        //when
        FleetDto actual = fleet.toDto();
        //then
        assertEquals(actual.getShips().toString(),
                fleet.getShips().stream().map(Ship::toDto).toList().toString());
    }

    @Test
    public void shouldReturnTrueIfAlive() {
        //given
        //when
        var actual = fleet.isAlive();
        //then
        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseIfSunk() {
        //given
        Ship deadShip =  new Ship(ShipType.DESTROYER,
                new MastsState(new HashMap<Integer, MastState>() {{
                    put(1, MastState.HIT);
                }}),
                new OccupiedCells(Arrays.asList(2, 11, 12)));
        Fleet deadFleet = new Fleet(fleetId, gameId, playerId, List.of(deadShip));
        //when
        var actual = deadFleet.isAlive();
        //then
        assertFalse(actual);
    }

    @Test
    public void shouldTakeAShotAndMiss() {
        //given
        //when
        var actual = fleet.takeAShot(100);
        //then
        assertFalse(actual.isPresent());
    }

    @Test
    public void shouldTakeAShotAndHit() {
        //given
        //when
        var actual = fleet.takeAShot(3);
        //then
        assertTrue(actual.isPresent());
    }


}
