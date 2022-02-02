package org.thedrake3.core;

import java.io.PrintWriter;
import java.util.Optional;

public class GameState implements JSONSerializable{
	private final Board board;
	private final PlayingSide sideOnTurn;
	private final Army blueArmy;
	private final Army orangeArmy;
	private final GameResult result;

	@Override
	public void toJSON(PrintWriter writer) {
		writer.print('{');
		writer.print("\"result\":");
		result.toJSON(writer);
		writer.print(',');
		writer.print("\"board\":");
		board.toJSON(writer);
		writer.print(',');
		writer.print("\"blueArmy\":");
		blueArmy.toJSON(writer);
		writer.print(',');
		writer.print("\"orangeArmy\":");
		orangeArmy.toJSON(writer);
		writer.print('}');
	}

	public GameState(
			Board board, 
			Army blueArmy, 
			Army orangeArmy) {
		this(board, blueArmy, orangeArmy, PlayingSide.BLUE, GameResult.IN_PLAY);
	}
	
	public GameState(
			Board board, 
			Army blueArmy, 
			Army orangeArmy, 
			PlayingSide sideOnTurn, 
			GameResult result) {
		this.board = board;
		this.sideOnTurn = sideOnTurn;
		this.blueArmy = blueArmy;
		this.orangeArmy = orangeArmy;
		this.result = result;
	}
	
	public Board board() {
		return board;
	}
	
	public PlayingSide sideOnTurn() {
		return sideOnTurn;
	}
	
	public GameResult result() {
		return result;
	}
	
	public Army army(PlayingSide side) {
		if(side == PlayingSide.BLUE) {
			return blueArmy;
		}
		
		return orangeArmy;
	}
	
	public Army armyOnTurn() {
		return army(sideOnTurn);
	}
	
	public Army armyNotOnTurn() {
		if(sideOnTurn == PlayingSide.BLUE)
			return orangeArmy;
		
		return blueArmy;
	}
	
	public Tile tileAt(TilePos pos) {
		if (pos == TilePos.OFF_BOARD)
			return BoardTile.MOUNTAIN;
		if (blueArmy.boardTroops().at(pos).isPresent())
			return blueArmy.boardTroops().at(pos).get();
		if (orangeArmy.boardTroops().at(pos).isPresent())
			return orangeArmy.boardTroops().at(pos).get();
		return board.at(pos);
	}
	
	private boolean canStepFrom(TilePos origin) {
		if (origin == BoardPos.OFF_BOARD)
			return false;
		if (result != GameResult.IN_PLAY)
			return false;
		if (!blueArmy.boardTroops().isLeaderPlaced() || blueArmy.boardTroops().isPlacingGuards())
			return false;
		if (!orangeArmy.boardTroops().isLeaderPlaced() || orangeArmy.boardTroops().isPlacingGuards())
			return false;
		if (!tileAt(origin).hasTroop())
			return false;
		if (blueArmy.boardTroops().at(origin).isPresent() && sideOnTurn == PlayingSide.ORANGE)
			return false;
		if (orangeArmy.boardTroops().at(origin).isPresent() && sideOnTurn == PlayingSide.BLUE)
			return false;
		return true;
	}

	private boolean canStepTo(TilePos target) {
		if (result != GameResult.IN_PLAY || !tileAt(target).canStepOn())
			return false;
		return true;
	}
	
	private boolean canCaptureOn(TilePos target) {
		if (result != GameResult.IN_PLAY)
			return false;
		if (!tileAt(target).hasTroop())
			return false;
		if (blueArmy.boardTroops().at(target).isPresent() && sideOnTurn == PlayingSide.BLUE)
			return false;
		if (orangeArmy.boardTroops().at(target).isPresent() && sideOnTurn == PlayingSide.ORANGE)
			return false;
		return true;
	}
	
	public boolean canStep(TilePos origin, TilePos target)  {
		return canStepFrom(origin) && canStepTo(target);
	}
	
	public boolean canCapture(TilePos origin, TilePos target)  {
		return canStepFrom(origin) && canCaptureOn(target);
	}
	
	public boolean canPlaceFromStack(TilePos target) {
		if (!canStepTo(target))
			return false;
		if (tileAt(target).hasTroop())
			return false;
		if (armyOnTurn().stack().isEmpty())
			return false;

		if (!armyOnTurn().boardTroops().isLeaderPlaced()){
			if (sideOnTurn == PlayingSide.BLUE && target.row() != 1)
				return false;
			if (sideOnTurn == PlayingSide.ORANGE && target.row() != board.dimension())
				return false;
		}
		if (armyOnTurn().boardTroops().isPlacingGuards()) {
			if (!target.isNextTo(armyOnTurn().boardTroops().leaderPosition()))
				return false;
		}
		else if (armyOnTurn().boardTroops().isLeaderPlaced() &&
				!armyOnTurn().boardTroops().at(target.step(0, 1)).isPresent() &&
				!armyOnTurn().boardTroops().at(target.step(1, 0)).isPresent() &&
				!armyOnTurn().boardTroops().at(target.step(0, -1)).isPresent() &&
				!armyOnTurn().boardTroops().at(target.step(-1, 0)).isPresent())
			return false;
		if (armyOnTurn().boardTroops().isPlacingGuards() && !target.isNextTo(armyOnTurn().boardTroops().leaderPosition()))
			return false;
		return true;
	}
	
	public GameState stepOnly(BoardPos origin, BoardPos target) {		
		if(canStep(origin, target))		 
			return createNewGameState(
					armyNotOnTurn(),
					armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);
		
		throw new IllegalArgumentException();
	}
	
	public GameState stepAndCapture(BoardPos origin, BoardPos target) {
		if(canCapture(origin, target)) {
			Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
			GameResult newResult = GameResult.IN_PLAY;
			
			if(armyNotOnTurn().boardTroops().leaderPosition().equals(target))
				newResult = GameResult.VICTORY;
			
			return createNewGameState(
					armyNotOnTurn().removeTroop(target), 
					armyOnTurn().troopStep(origin, target).capture(captured), newResult);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState captureOnly(BoardPos origin, BoardPos target) {
		if(canCapture(origin, target)) {
			Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
			GameResult newResult = GameResult.IN_PLAY;
			
			if(armyNotOnTurn().boardTroops().leaderPosition().equals(target))
				newResult = GameResult.VICTORY;
			
			return createNewGameState(
					armyNotOnTurn().removeTroop(target),
					armyOnTurn().troopFlip(origin).capture(captured), newResult);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState placeFromStack(BoardPos target) {
		if(canPlaceFromStack(target)) {
			return createNewGameState(
					armyNotOnTurn(), 
					armyOnTurn().placeFromStack(target), 
					GameResult.IN_PLAY);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState resign() {
		return createNewGameState(
				armyNotOnTurn(), 
				armyOnTurn(), 
				GameResult.VICTORY);
	}
	
	public GameState draw() {
		return createNewGameState(
				armyOnTurn(), 
				armyNotOnTurn(), 
				GameResult.DRAW);
	}
	
	private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
		if(armyOnTurn.side() == PlayingSide.BLUE) {
			return new GameState(board, armyOnTurn, armyNotOnTurn, PlayingSide.BLUE, result);
		}
		
		return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result); 
	}
}
