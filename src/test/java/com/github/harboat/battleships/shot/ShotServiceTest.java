package com.github.harboat.battleships.shot;

import com.github.harboat.battleships.fleet.FleetService;
import com.github.harboat.clients.game.ShotRequest;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.mockito.BDDMockito.verify;
import static org.testng.Assert.assertEquals;

@Listeners({MockitoTestNGListener.class})
public class ShotServiceTest {
    @Mock
    private FleetService fleetService;
    private ShotService service;
    @Captor
    private ArgumentCaptor<ShotRequest> captor;

    @BeforeMethod
    public void setUp() {
        service = new ShotService(fleetService);
    }

    @Test
    public void shouldTakeAShotWithProperUsername() {
        //given
        ShotRequest shotRequest = new ShotRequest("test", "testUsername", 1);
        //when
        service.takeAShoot(shotRequest);
        verify(fleetService).shoot(captor.capture());
        var actual = captor.getValue();
        //then
        assertEquals(actual.playerId(), "testUsername");
    }

    @Test
    public void shouldTakeAShotWithProperGameId() {
        //given
        ShotRequest shotRequest = new ShotRequest("test", "testUsername", 1);
        //when
        service.takeAShoot(shotRequest);

        verify(fleetService).shoot(captor.capture());
        var actual = captor.getValue();
        //then
        assertEquals(actual.gameId(), "test");
    }

    @Test
    public void shouldTakeAShotWithProperCellId() {
        //given
        ShotRequest shotRequest = new ShotRequest("test", "testUsername", 1);
        //when
        service.takeAShoot(shotRequest);
        verify(fleetService).shoot(captor.capture());
        var actual = captor.getValue();
        //then
        assertEquals(actual.cellId(), 1);
    }

}
