package com.github.harboat.battleships.game;

import com.github.harboat.battleships.CoreQueueProducer;
import com.github.harboat.battleships.board.BoardService;
import com.github.harboat.battleships.fleet.FleetService;
import com.github.harboat.clients.game.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Listeners({MockitoTestNGListener.class})
public class GameServiceTest {

    @Mock
    private GameRepository repository;
    @Mock
    private BoardService boardService;
    @Mock
    private FleetService fleetService;
    @Mock
    private CoreQueueProducer producer;
    private GameService service;
    private Game game;
    private ShipDto ship1;
    private ShipDto ship2;

    @BeforeMethod
    public void setUp() {
        service = new GameService(repository, boardService, fleetService, producer);
        game = new Game("test", List.of("testPlayer"), "testPlayer");
        given(repository.save(any())).willReturn(game);
        ship1 = new com.github.harboat.clients.game.ShipDto(ShipType.DESTROYER,
                new Masts(List.of(1)),
                new OccupiedCells(List.of(2, 11, 12)));
        ship2 = new com.github.harboat.clients.game.ShipDto(ShipType.DESTROYER,
                new Masts(List.of(3, 4)),
                new OccupiedCells(List.of(2, 12, 13, 14, 15, 5)));
    }

    @Test
    public void shouldCreateGameWithProperPlayerIds() {
        //given
        ArgumentCaptor<GameCreated> captor = ArgumentCaptor.forClass(GameCreated.class);
        GameCreate gameCreate = new GameCreate(new Size(10, 10),
                new HashMap<String, Collection<ShipDto>>() {{
                    put("testPlayer1", List.of(ship1, ship2));
                    put("testPlayer2", List.of(ship1, ship2));
                }});
        Game game1 = Game.builder()
                .playerIds(List.of("testPlayer1", "testPlayer2"))
                .turnOfPlayer("testPlayer1")
                .build();
        given(repository.save(any())).willReturn(game1);
        //when
        service.createGame(gameCreate);
        verify(producer).sendResponse(captor.capture());
        var actual = captor.getValue();
        //then
        assertEquals(actual.players(), List.of("testPlayer1", "testPlayer2"));
    }

    @Test
    public void shouldCreateGameWithNotEmptyId() {
        //given
        ArgumentCaptor<GameCreated> captor = ArgumentCaptor.forClass(GameCreated.class);
        GameCreate gameCreate = new GameCreate(new Size(10, 10),
                new HashMap<String, Collection<ShipDto>>() {{
                    put("testPlayer1", List.of(ship1, ship2));
                    put("testPlayer2", List.of(ship1, ship2));
                }});
        //when
        service.createGame(gameCreate);
        verify(producer).sendResponse(captor.capture());
        var actual = captor.getValue();
        //then
        assertNotNull(actual.gameId());
    }
}
