package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.List;

/**
 * get a studentName, list of courses, and computer .make the check and sign for the student.
 */
public class checkAndSign extends Action<Boolean> {
    private List<String> _coursesList;
    private Computer _computer;
    private String _studentName;

    public checkAndSign(String studentName, List<String> coursesList, Computer computer) {
        _coursesList = coursesList;
        _computer = computer;
        _studentName = studentName;
        setActionName("Check And Sign");
    }

    /**
     * update the student sig according to if he meets the requirements
     */
    protected void start(){
        if (_computer.checkAndSign(_coursesList, ((StudentPrivateState) this.actorPool.getPrivaetState(_studentName)).getGrades()) == _computer.getSuccessSig()) {
            ((StudentPrivateState) this.actorPool.getPrivaetState(_studentName)).setSignature(_computer.getSuccessSig());
            complete(true);
        } else {
            ((StudentPrivateState) this.actorPool.getPrivaetState(_studentName)).setSignature(_computer.getFailSig());
            complete(false);
        }
    }
}

