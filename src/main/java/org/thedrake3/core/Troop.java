package org.thedrake3.core;

import java.io.PrintWriter;
import java.util.List;

public class Troop implements JSONSerializable{
    private String name;
    private Offset2D aversePivot;
    private Offset2D reversePivot;
    private List<TroopAction> aversActions;
    private List<TroopAction> reverseActions;


    @Override
    public void toJSON(PrintWriter writer) {
        writer.print('\"'); writer.print(name); writer.print('\"');
    }

    public Troop(String name, Offset2D aversPivot, Offset2D reversPivot,
                 List<TroopAction> aversActions, List<TroopAction> reverseActions) {
        this.name = name;
        this.aversePivot = aversPivot;
        this.reversePivot = reversPivot;
        this.aversActions = aversActions;
        this.reverseActions = reverseActions;
    }

    public Troop(String name, Offset2D pivot,
                 List<TroopAction> aversActions, List<TroopAction> reverseActions)
    {
        this.name = name;
        this.aversePivot = pivot;
        this.reversePivot = pivot;
        this.aversActions = aversActions;
        this.reverseActions = reverseActions;
    }

    public Troop(String name,
                 List<TroopAction> aversActions, List<TroopAction> reverseActions) {
        this.name = name;
        this.aversePivot = new Offset2D(1, 1);
        this.reversePivot = new Offset2D(1, 1);
        this.aversActions = aversActions;
        this.reverseActions = reverseActions;
    }
    public List<TroopAction> actions(TroopFace face) {
        if (face == TroopFace.AVERS)
            return aversActions;
        return reverseActions;
    }

    public String name() {
        return this.name;
    }

    public Offset2D pivot(TroopFace face) {
        if (face == TroopFace.AVERS)
            return this.aversePivot;
        return this.reversePivot;
    }
}
