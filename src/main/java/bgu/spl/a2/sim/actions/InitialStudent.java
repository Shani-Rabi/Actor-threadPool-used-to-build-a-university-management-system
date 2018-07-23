package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;

/**
 *   initial the private state
 */
public class InitialStudent extends Action<Boolean> {


    public InitialStudent() {
        setActionName("Initial Student");
    }

    /**
     * just initial this Student in the maps.
     */
    protected void start() {

        complete(true);
    }

}
