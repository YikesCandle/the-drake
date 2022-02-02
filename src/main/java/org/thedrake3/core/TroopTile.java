package org.thedrake3.core;

import java.io.PrintWriter;
import java.util.*;

final public class TroopTile implements Tile, JSONSerializable {
    private final Troop troop;
    private final PlayingSide side;
    private final TroopFace face;

    public TroopTile(Troop troop, PlayingSide side, TroopFace face) {
        this.troop = troop;
        this.side = side;
        this.face = face;
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.print('{');
        writer.print("\"troop\":");
        troop.toJSON(writer);
        writer.print(',');
        writer.print("\"side\":");
        side.toJSON(writer);
        writer.print(',');
        writer.print("\"face\":");
        face.toJSON(writer);
        writer.print('}');
    }

    @Override
    public List<Move> movesFrom(BoardPos pos, GameState state) {
        Set<Move> moves = new HashSet<>();
        for (TroopAction action : troop.actions(face)) {
            moves.addAll(action.movesFrom(pos, side, state));
        }
        return new ArrayList<>(moves);
    }

    public PlayingSide side() {
        return this.side;
    }

    public TroopFace face() {
        return this.face;
    }

    public Troop troop() {
        return this.troop;
    }

    public boolean canStepOn() {
        return false;
    }

    public boolean hasTroop() {
        return true;
    }

    public TroopTile flipped() {
        return new TroopTile(this.troop, this.side, this.face.flip());
    }
}
