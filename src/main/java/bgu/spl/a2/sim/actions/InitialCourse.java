package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.List;

/**
 * get a int size, list of prerequisites,and course private state and initial the private state
 */
public class InitialCourse extends Action<Boolean> { //this class initial the Course state
    private int _size;
    private List<String> _prerequisites;
    private CoursePrivateState _state;

    public InitialCourse(int size, List<String> prerequisites, CoursePrivateState state) {
        _size = size;
        _prerequisites = prerequisites;
        _state = state;
        setActionName("Initial Course");
    }

    /**
     * initial the coursePrivateState
     */
    protected void start() {

        _state.setAvailableSpots(new Integer(_size));
        _state.setPrequisites(_prerequisites);
        complete(true);
    }
}
