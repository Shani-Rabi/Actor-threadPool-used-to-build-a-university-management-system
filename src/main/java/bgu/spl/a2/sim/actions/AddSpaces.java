package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;


/**
 * geet course name and number to add ,add the number for the course available spots.
 */
public class AddSpaces extends Action<Boolean> {

    private String _courseName;
    private String _numToAdd;

    public AddSpaces(String courseName, String numToAdd) {
        _courseName = courseName;
        _numToAdd = numToAdd;
        setActionName("add Spaces");
    }

    /**
     * just add the more spaces to the CoursePrivateState
     */
    protected void start() {
         if(((CoursePrivateState) (this.actorPool.getActors().get(_courseName))).getAvailableSpots().intValue()!=-1) {
             ((CoursePrivateState) (this.actorPool.getActors().get(_courseName))).addAvailableSpots(_numToAdd);

             complete(true);
         }
         else{
             this.actorPool.getActors().get(_courseName).addRecord("Add Spaces");
             complete(false);
         }
    }
}
