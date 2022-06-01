package com.github.harboat.battleships.fleet;

import com.github.harboat.battleships.CoreQueueProducer;
import com.github.harboat.battleships.board.BoardService;
import com.github.harboat.battleships.game.GameUtility;
import com.github.harboat.clients.game.ShipDto;
import com.github.harboat.clients.game.*;
import com.github.harboat.clients.notification.NotificationRequest;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Listeners({MockitoTestNGListener.class})
public class FleetServiceTest {

    @Mock
    private FleetRepository repository;
    @Mock
    private GameUtility gameUtility;
    @Mock
    private CoreQueueProducer coreQueueProducer;
    @Mock
    private BoardService boardService;
    private FleetService service;
    @Captor
    private ArgumentCaptor<NotificationRequest<FleetDto>> notificationRequestCaptor;
    @Captor
    private ArgumentCaptor<Fleet> fleetCaptor;
    private com.github.harboat.clients.game.ShipDto ship1;
    private com.github.harboat.clients.game.ShipDto ship2;


    @BeforeMethod
    public void setUp() {
        service = new FleetService(repository, gameUtility, coreQueueProducer, boardService);
        ship1 = new com.github.harboat.clients.game.ShipDto(ShipType.DESTROYER,
                new Masts(List.of(1)),
                new OccupiedCells(List.of(2, 11, 12)));
        ship2 = new com.github.harboat.clients.game.ShipDto(ShipType.DESTROYER,
                new Masts(List.of(3, 4)),
                new OccupiedCells(List.of(2, 12, 13, 14, 15, 5)));
    }

    @Test
    public void shouldConvertMasts() {
        //given
        //when
        MastsState actual = service.convertMasts(List.of(1, 2, 3));
        //then
        assertEquals(actual.getMasts(), new MastsState(new HashMap<Integer, MastState>() {{
            put(1, MastState.ALIVE);
            put(2, MastState.ALIVE);
            put(3, MastState.ALIVE);
        }}).getMasts());
    }

    @Test
    public void shouldCreateFleetWithProperPlayerId() {
        //given
        Collection<ShipDto> collection = List.of(ship1, ship2);
        //when
        service.create("test", "testPlayer", collection);
        verify(repository).save(fleetCaptor.capture());
        var actual = fleetCaptor.getValue();
        //then
        assertEquals(actual.getPlayerId(), "testPlayer");
    }

    @Test
    public void shouldCreateFleetWithProperFleet() {
        //given
        Collection<ShipDto> collection = List.of(ship1, ship2);
        //when
        service.create("test", "testPlayer", collection);
        verify(repository).save(fleetCaptor.capture());
        var actual = fleetCaptor.getValue();
        //then
        assertEquals(actual.getShips().size(), collection.size());
    }

    @Test
    public void shouldShootAndMiss() {
        //given
        ArgumentCaptor<ShotResponse> shotResponseCaptor = ArgumentCaptor.forClass(ShotResponse.class);
        Ship ship = new Ship(ShipType.SUBMARINE,
                new MastsState(new HashMap<Integer, MastState>() {{
                    put(3, MastState.ALIVE);
                    put(4, MastState.ALIVE);
                }}),
                new OccupiedCells(Arrays.asList(2, 12, 13, 14, 15, 5)));
        Fleet fleet = new Fleet("testFleet", "test", "testPlayer", List.of(ship));
        ShotRequest shotRequest = new ShotRequest("test", "testPlayer", 10);
        given(repository.findByGameIdAndPlayerId(any(), any())).willReturn(Optional.of(fleet));
        //when
        service.shoot(shotRequest);
        verify(coreQueueProducer).sendResponse(shotResponseCaptor.capture());
        var actual = shotResponseCaptor.getValue();
        //then
        assertTrue(actual.cells().contains(new Cell(10, false)));
    }

    @Test
    public void shouldShootAndSunk() {
        //given
        ArgumentCaptor<ShotResponse> shotResponseCaptor = ArgumentCaptor.forClass(ShotResponse.class);
        Ship ship = new Ship(ShipType.DESTROYER,
                new MastsState(new HashMap<Integer, MastState>() {{
                    put(1, MastState.ALIVE);
                }}),
                new OccupiedCells(Arrays.asList(2, 11, 12)));
        Fleet fleet = new Fleet("testFleet", "test", "testPlayer", List.of(ship));
        ShotRequest shotRequest = new ShotRequest("test", "testPlayer", 1);
        given(repository.findByGameIdAndPlayerId(any(), any())).willReturn(Optional.of(fleet));
        doNothing().when(coreQueueProducer).sendResponse(shotResponseCaptor.capture());
        //when
        service.shoot(shotRequest);
        verify(coreQueueProducer, times(2)).sendResponse(any());
    }

    @Test
    public void shouldShootNukeWithProperGameId() {
        //given
        Ship ship = new Ship(ShipType.DESTROYER,
                new MastsState(new HashMap<Integer, MastState>() {{
                    put(1, MastState.ALIVE);
                }}),
                new OccupiedCells(Arrays.asList(2, 11, 12)));
        NukeShotRequest request = new NukeShotRequest("test", "testPlayer", 1);
        given(repository.save(any())).willReturn(null);
        given(gameUtility.getEnemyId("test", "testPlayer")).willReturn("testEnemy");
        Fleet fleet = new Fleet("testFleet", "test","testPlayer", List.of(ship));
        given(repository.findByGameIdAndPlayerId("test", "testPlayer")).willReturn(Optional.of(fleet));
        given(repository.findByGameIdAndPlayerId("test", "testEnemy")).willReturn(Optional.of(fleet));
        ArgumentCaptor<ShotResponse> captor = ArgumentCaptor.forClass(ShotResponse.class);
        //when
        service.shoot(request);
        verify(coreQueueProducer,times(2)).sendResponse(captor.capture());
        var actual = captor.getValue();
        //then
        assertEquals(actual.gameId(), "test");
    }

    @Test
    public void shouldShootNukeWithProperPlayerId() {
        //given
        Ship ship = new Ship(ShipType.DESTROYER,
                new MastsState(new HashMap<Integer, MastState>() {{
                    put(1, MastState.ALIVE);
                }}),
                new OccupiedCells(Arrays.asList(2, 11, 12)));
        NukeShotRequest request = new NukeShotRequest("test", "testPlayer", 1);
        given(repository.save(any())).willReturn(null);
        given(gameUtility.getEnemyId("test", "testPlayer")).willReturn("testEnemy");
        Fleet fleet = new Fleet("testFleet", "test","testPlayer", List.of(ship));
        given(repository.findByGameIdAndPlayerId("test", "testPlayer")).willReturn(Optional.of(fleet));
        given(repository.findByGameIdAndPlayerId("test", "testEnemy")).willReturn(Optional.of(fleet));
        ArgumentCaptor<ShotResponse> captor = ArgumentCaptor.forClass(ShotResponse.class);
        //when
        service.shoot(request);
        verify(coreQueueProducer,times(2)).sendResponse(captor.capture());
        var actual = captor.getValue();
        //then
        assertEquals(actual.playerId(), "testPlayer");
    }
}
