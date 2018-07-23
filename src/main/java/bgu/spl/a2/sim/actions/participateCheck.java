package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.HashMap;
import java.util.List;

/**
 * list of prerequisites,string name and check the pre conditions for the student.
 */
public class participateCheck extends Action<Boolean> {
    private List<String> _prerequisites;
    private String _name;

    public participateCheck(List<String> prerequisites, String name) {
        _name = name;
        _prerequisites = prerequisites;
        setActionName("Participate Check");
    }

    /**
     * return true if the student in relevant for this course ,else return false.
     */
    protected void start() {

        StudentPrivateState theStudent = (StudentPrivateState) (this.actorPool.getActors().get(_name));
        HashMap<String, Integer> grades = theStudent.getGrades();
        Boolean result = true;
        for (String currcourse : _prerequisites) {//check the grades of the student.
            if (!grades.containsKey(currcourse)) {//if the student dont have currcourse
                result = false;
                break;

            }
        }// end of the for.
        if (result) {
            complete(true);
        } else
            complete(false);

    }
}


