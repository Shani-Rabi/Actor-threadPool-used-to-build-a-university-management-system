package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * get a student name and course name and remove the grade from the students grades
 *
 */
public class removeGrade extends Action<Boolean> {

    private String _name;
    private String _courseName;

    public removeGrade(String name, String courseName) {
        _name = name;
        _courseName = courseName;
        setActionName("Remove Grade");
    }

    /**
     * remove the grade from the student.
     */
    protected void start() {

        ((StudentPrivateState) (this.actorPool.getActors().get(_name))).getGrades().remove(_courseName);
        complete(true);
    }
}
