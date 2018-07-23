package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;

/**
 * this action is for ensure that register and unregister would happen in the same order they have been inserted
 */
public class waitForUnregister extends Action<Boolean>{
    public waitForUnregister() {setActionName("Wait For Unregister");}
    protected void start(){
        complete(true);}
}
