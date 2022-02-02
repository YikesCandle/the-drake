package org.thedrake3.core;

import java.io.PrintWriter;

public final class Board implements JSONSerializable{
	private final BoardTile[][] board;
	private final int dimension;

	@Override
	public void toJSON(PrintWriter writer) {
		writer.print("{");
		writer.printf("\"dimension\":%d", dimension);
		writer.print(",\"tiles\":[");
		for (int i = 0; i < this.dimension; ++i) {
			for (int j = 0; j < this.dimension; ++j) {
				board[j][i].toJSON(writer);
				if (i != dimension - 1 || j != dimension - 1)
					writer.print(',');
			}
		}
		writer.print("]}");
	}

	// Konstruktor. Vytvoří čtvercovou hrací desku zadaného rozměru, kde všechny dlaždice jsou prázdné, tedy BoardTile.EMPTY
	public Board(int dimension) {
		this.dimension = dimension;
		board = new BoardTile[dimension][dimension];
		for (int i = 0; i < this.dimension; ++i) {
			for (int j = 0; j < this.dimension; ++j) {
				board[i][j] = board[i][j].EMPTY;
			}
		}
	}

	public int dimension() {
		return this.dimension;
	}

	public BoardTile at(TilePos pos) {
		return this.board[pos.i()][pos.j()];
	}

	// Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
	public Board withTiles(TileAt ...ats) {
		Board newBoard = new Board(this.dimension);
		for (int i = 0; i < this.dimension; ++i) {
			for (int j = 0; j < this.dimension; ++j) {
				newBoard.board[i][j] = this.board[i][j];
			}
		}
		for (TileAt i : ats) {
			newBoard.board[i.pos.i()][i.pos.j()] = i.tile;
		}
		return newBoard;
	}

	// Vytvoří instanci PositionFactory pro výrobu pozic na tomto hracím plánu
	public PositionFactory positionFactory() {
		return new PositionFactory(this.dimension);
	}

	public static class TileAt {
		public final BoardPos pos;
		public final BoardTile tile;
		
		public TileAt(BoardPos pos, BoardTile tile) {
			this.pos = pos;
			this.tile = tile;
		}
	}
}

