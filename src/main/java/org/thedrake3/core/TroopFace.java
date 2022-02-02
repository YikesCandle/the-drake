package org.thedrake3.core;

import java.io.PrintWriter;

public enum TroopFace implements JSONSerializable{
    AVERS, REVERS;

    @Override
    public void toJSON(PrintWriter writer) {
        if (this == AVERS)
            writer.print("\"AVERS\"");
        if (this == REVERS)
            writer.print("\"REVERS\"");
    }

    public TroopFace flip() {
        if (this == AVERS)
            return REVERS;
        return AVERS;
    }
}
