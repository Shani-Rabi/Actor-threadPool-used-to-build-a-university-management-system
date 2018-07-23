package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * get a course name and unregister all the students.
 * change the course available spots to -1
 */
public class closeCourseHelp extends Action<Boolean> {
    private String _coursename;

    public closeCourseHelp(String coursename) {
        _coursename = coursename;
        setActionName("Close Course Help");
    }

    /**
     * doing Unregister to all the students in the course ,and setAvailableSpots to -1
     */
    protected void start() {

        ((CoursePrivateState) (actorPool.getActors().get(_coursename))).setAvailableSpots(new Integer(-1)); // first of all, set to -1 tp no new students would register
        List<Action<Boolean>> actions = new ArrayList<>();
        for (String student : ((CoursePrivateState) (this.actorPool.getActors().get(_coursename))).getRegStudents()) {
            Unregister action = new Unregister(_coursename, student);
            actions.add(action);
            sendMessage(action, _coursename, new CoursePrivateState());
        }
        then(actions, () -> {
            complete(true);
        });

    }
}
