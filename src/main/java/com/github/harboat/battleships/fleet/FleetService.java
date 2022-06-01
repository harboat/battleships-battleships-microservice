package com.github.harboat.battleships.fleet;

import com.github.harboat.battleships.CoreQueueProducer;
import com.github.harboat.battleships.board.BoardService;
import com.github.harboat.battleships.game.GameUtility;
import com.github.harboat.clients.game.ShipDto;
import com.github.harboat.clients.game.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FleetService {

    private FleetRepository repository;
    private GameUtility gameUtility;
    private CoreQueueProducer coreQueueProducer;
    private BoardService boardService;

    @Transactional
    public void create(String gameId, String playerId, Collection<com.github.harboat.clients.game.ShipDto> shipDtos) {
        List<Ship> ships = getShips(shipDtos);
        Fleet fleet = buildFleet(gameId, playerId, ships);
        repository.save(fleet);
        boardService.markOccupied(gameId, playerId, fleet.getAllCells());
    }

    private Fleet buildFleet(String gameId, String playerId, List<Ship> ships) {
        return Fleet.builder()
                .gameId(gameId)
                .playerId(playerId)
                .ships(ships)
                .build();
    }

    private List<Ship> getShips(Collection<ShipDto> shipDtos) {
        return shipDtos.stream()
                .map(s -> Ship.builder()
                        .shipType(s.getType())
                        .masts(convertMasts(s.getMasts().getPositions()))
                        .cells(new OccupiedCells(s.getCells().getPositions()))
                        .build()
                ).toList();
    }

    public MastsState convertMasts(Collection<Integer> masts) {
        return new MastsState(
                masts.stream()
                        .collect(Collectors.toMap(m -> m, m -> MastState.ALIVE))
        );
    }

    public void shoot(ShotRequest shotRequest) {
        var gameId = shotRequest.gameId();
        var playerId = shotRequest.playerId();
        var cellId = shotRequest.cellId();
        var enemyId = gameUtility.getEnemyId(gameId, playerId);
        var currentFleet = repository.findByGameIdAndPlayerId(gameId, enemyId).orElseThrow();
        Set<Cell> cells = takeAShoot(gameId, playerId, cellId, currentFleet);
        repository.save(currentFleet);
        coreQueueProducer.sendResponse(
                new ShotResponse(gameId, playerId, cells)
        );
    }

    public void shoot(NukeShotRequest nukeShotRequest) {
        var gameId = nukeShotRequest.gameId();
        var playerId = nukeShotRequest.playerId();
        var cellId = nukeShotRequest.cellId();
        var enemyId = gameUtility.getEnemyId(gameId, playerId);

        var myFleet = repository.findByGameIdAndPlayerId(gameId, playerId).orElseThrow();
        if(!myFleet.hasFourMastShipWithThreeMastsAlive()) {
            shoot(new ShotRequest(nukeShotRequest.gameId(), nukeShotRequest.playerId(), nukeShotRequest.cellId()));
            return;
        }

        var currentFleet = repository.findByGameIdAndPlayerId(gameId, enemyId).orElseThrow();

        Size boardSize = boardService.getBoardSize(gameId, playerId);

        List<Integer> nukedCells = determineNukedCells(cellId, boardSize);

        Set<Cell> cells = new HashSet<>();
        for (Integer cell : nukedCells) {
            cells.addAll(takeAShoot(gameId, playerId, cell, currentFleet));
        }

        repository.save(currentFleet);
        coreQueueProducer.sendResponse(
                new ShotResponse(gameId, playerId, cells)
        );
    }

    private Set<Cell> takeAShoot(String gameId, String playerId, Integer cellId, Fleet currentFleet) {
        Optional<Ship> ship = currentFleet.takeAShot(cellId);
        boardService.markHit(gameId, playerId, cellId);
        if (ship.isEmpty()) {
            return Set.of(new Cell(cellId, false));
        }
        if (!currentFleet.isAlive()) {
            coreQueueProducer.sendResponse(
                    new PlayerWon(gameId, playerId)
            );
        }
        Ship s = ship.get();
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(cellId, true));
        if (!s.isAlive()) {
            s.getCells().getPositions()
                    .forEach(c -> cells.add(new Cell(c, false)));
            s.getMasts().getMasts().keySet()
                    .forEach(m -> cells.add(new Cell(m, true)));
        }
        return cells;
    }

    private List<Integer> determineNukedCells(Integer cellId, Size boardWidth) {
        List<Integer> adjacentFields = new ArrayList<>();
        adjacentFields.add(cellId);
        adjacentFields.addAll(addLeftCells(cellId, boardWidth.width()));
        adjacentFields.addAll(addRightCells(cellId, boardWidth.width()));
        adjacentFields.addAll(addTopandBottomCells(cellId, boardWidth.width()));
        return adjacentFields.stream().filter(i -> i > 0 && i < boardWidth.width() * boardWidth.height()).
                collect(Collectors.toList());
    }

    private List<Integer> addLeftCells(int cellId, int boardWidth) {
        if (cellId % boardWidth == 1) return Collections.emptyList();
        return List.of(cellId + boardWidth - 1, cellId - 1, cellId - boardWidth - 1);
    }

    private List<Integer> addRightCells(int cellId, int boardWidth) {
        if (cellId % boardWidth == 0) return Collections.emptyList();
        return List.of(cellId - boardWidth + 1, cellId + 1, cellId + boardWidth + 1);
    }

    private List<Integer> addTopandBottomCells(int cellId, int boardWidth) {
        return List.of(cellId - boardWidth, cellId + boardWidth);
    }
}
