package org.thedrake3.core;

import java.io.PrintWriter;

public enum PlayingSide implements JSONSerializable{
    ORANGE, BLUE;

    @Override
    public void toJSON(PrintWriter writer) {
        if (this == ORANGE)
            writer.print("\"ORANGE\"");
        if (this == BLUE)
            writer.print("\"BLUE\"");
    }
}
