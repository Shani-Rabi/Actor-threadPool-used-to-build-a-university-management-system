package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * get grade,name and course name and add the the grade for the students list
 */
public class addGrade extends Action<Boolean> {


    private String _name;
    private String _courseName;
    private int _grade;

    /**
     * @param grade
     * @param name
     * @param courseName
     * construcrtor.
     */
    public addGrade(int grade, String name, String courseName) {
        _name = name;
        _grade = grade;
        _courseName = courseName;
        setActionName("add Grade");
    }

    /**
     * just add the grade to the StudentnPrivateState
     */
    protected void start() {

        ((StudentPrivateState) (this.actorPool.getActors().get(_name))).getGrades().put(_courseName, _grade);
        complete(true);
    }
}
