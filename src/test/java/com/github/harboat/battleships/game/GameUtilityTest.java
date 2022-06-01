package com.github.harboat.battleships.game;

import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.testng.Assert.assertEquals;

@Listeners({MockitoTestNGListener.class})
public class GameUtilityTest {

    @Mock
    private GameRepository repository;
    private GameUtility gameUtility;

    @BeforeMethod
    public void setUp() {
        gameUtility = new GameUtility(repository);
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void getEnemyIdShouldThrowWhenThereIsNoGameWithThisId() {
        //given
        given(repository.findById("test")).willReturn(Optional.empty());
        //when
        gameUtility.getEnemyId("test", "testPlayer");
        //then
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void getEnemyIdShouldThrowWhenThereIsNoEnemy() {
        //given
        Game game = Game.builder()
                .id("test")
                .playerIds(List.of("testPlayer"))
                .build();
        given(repository.findById("test")).willReturn(Optional.of(game));
        //when
        gameUtility.getEnemyId("test", "testPlayer");
        //then
    }

    @Test
    public void shouldGetEnemyId() {
        //given
        Game game = Game.builder()
                .id("test")
                .playerIds(List.of("testPlayer", "enemy"))
                .build();
        given(repository.findById("test")).willReturn(Optional.of(game));
        //when
        var actual = gameUtility.getEnemyId("test", "testPlayer");
        //then
        assertEquals(actual, "enemy");
    }
}
