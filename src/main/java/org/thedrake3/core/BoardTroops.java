package org.thedrake3.core;

import java.io.PrintWriter;
import java.util.*;
import java.util.prefs.BackingStoreException;

public class BoardTroops implements JSONSerializable{
	private final PlayingSide playingSide;
	private final Map<BoardPos, TroopTile> troopMap;
	private final TilePos leaderPosition;
	private final int guards;

	@Override
	public void toJSON(PrintWriter writer) {
		writer.print("{\"side\":");
		playingSide.toJSON(writer);
		writer.print(',');
		writer.print("\"leaderPosition\":");
		leaderPosition.toJSON(writer);
		writer.print(',');
		writer.printf("\"guards\":%d", guards);
		writer.print(',');
		writer.print("\"troopMap\":{");
		if (!troopMap.isEmpty()) {
			Map.Entry<BoardPos,TroopTile> entryT = troopMap.entrySet().iterator().next();
			BoardPos key = entryT.getKey();
			int dim = key.getDimension();
			int count = 0;
			Set<String> positios = new TreeSet<>();
			for (Map.Entry<BoardPos, TroopTile> entry:
					troopMap.entrySet()) {
				positios.add(entry.getKey().toString());
			}
			for (String position:
					positios) {
				char first = position.charAt(0);
				char second = position.charAt(1);
				int j = second - '0' - 1;
				int i = first - 'a';
				BoardPos pos = new BoardPos(dim, i, j);
				TroopTile trooptile = troopMap.get(pos);
				pos.toJSON(writer);
				writer.print(':');
				trooptile.toJSON(writer);
				count++;
				if (count != troopMap.size())
					writer.print(',');
			}
		}

		writer.print('}');
		writer.print('}');
	}

	public BoardTroops(PlayingSide playingSide) {
		this.playingSide = playingSide;
		this.troopMap = Collections.emptyMap();
		this.leaderPosition = TilePos.OFF_BOARD;
		this.guards = 0;
	}
	
	public BoardTroops(
			PlayingSide playingSide,
			Map<BoardPos, TroopTile> troopMap,
			TilePos leaderPosition, 
			int guards) {
		this.playingSide = playingSide;
		this.troopMap = troopMap;
		this.leaderPosition = leaderPosition;
		this.guards = guards;
	}

	public Optional<TroopTile> at(TilePos pos) {
		return Optional.ofNullable(this.troopMap.get(pos));
	}
	
	public PlayingSide playingSide() {
		return this.playingSide;
	}
	
	public TilePos leaderPosition() {
		return this.leaderPosition;
	}

	public int guards() {
		return this.guards;
	}
	
	public boolean isLeaderPlaced() {
		return this.leaderPosition != TilePos.OFF_BOARD;
	}
	
	public boolean isPlacingGuards() {
		return this.isLeaderPlaced() && this.guards < 2;
	}	
	
	public Set<BoardPos> troopPositions() {
		return this.troopMap.keySet();
	}

	public BoardTroops placeTroop(Troop troop, BoardPos target) {
		if (this.troopMap.get(target) != null)
			throw new IllegalArgumentException();

		TroopTile newTroopTile = new TroopTile(troop, this.playingSide, TroopFace.AVERS);
		Map<BoardPos, TroopTile> newMap = new HashMap<>(troopMap);
		newMap.put(target, newTroopTile);
		TilePos newLeaderPos = this.isLeaderPlaced() ? leaderPosition : target;
		return new BoardTroops(playingSide, newMap, newLeaderPos, isPlacingGuards() ? guards + 1 : guards);
	}
	
	public BoardTroops troopStep(BoardPos origin, BoardPos target) {
		if (!isLeaderPlaced() || isPlacingGuards())
			throw new IllegalStateException();
		if (this.troopMap.get(origin) == null || this.troopMap.get(target) != null)
			throw new IllegalArgumentException();
		this.troopMap.put(target, troopMap.get(origin).flipped());
		troopMap.remove(origin);
		if (this.leaderPosition.equalsTo(origin.i(), origin.j()))
			return new BoardTroops(playingSide, troopMap, target, guards);
		return new BoardTroops(playingSide, troopMap, leaderPosition, guards);
	}
	
	public BoardTroops troopFlip(BoardPos origin) {
		if(!isLeaderPlaced()) {
			throw new IllegalStateException(
					"Cannot move troops before the leader is placed.");			
		}
		
		if(isPlacingGuards()) {
			throw new IllegalStateException(
					"Cannot move troops before guards are placed.");			
		}
		
		if(!at(origin).isPresent())
			throw new IllegalArgumentException();
		
		Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
		TroopTile tile = newTroops.remove(origin);
		newTroops.put(origin, tile.flipped());

		return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
	}
	
	public BoardTroops removeTroop(BoardPos target) {
		if(!isLeaderPlaced()) {
			throw new IllegalStateException(
					"Cannot move troops before the leader is placed.");
		}

		if(isPlacingGuards()) {
			throw new IllegalStateException(
					"Cannot move troops before guards are placed.");
		}

		if(!at(target).isPresent())
			throw new IllegalArgumentException();
		this.troopMap.remove(target);
		if (this.leaderPosition.equalsTo(target.i(), target.j()))
			return new BoardTroops(this.playingSide, this.troopMap, TilePos.OFF_BOARD, this.guards);
		return new BoardTroops(this.playingSide, this.troopMap, this.leaderPosition, this.guards);
	}	
}
