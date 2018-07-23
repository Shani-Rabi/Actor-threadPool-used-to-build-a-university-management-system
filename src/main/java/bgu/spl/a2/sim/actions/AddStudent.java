package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * get name of department and name  and add the student to the department
 */
public class AddStudent extends Action<Boolean> {
    private String _department;
    private String _name;

    public AddStudent(String department, String name) {
        _name = name;
        _department = department;
        setActionName("Add Student");
    }

    /**
     * call InitialStudent, and then add the student to the depatrment
     */
    protected void start() {

        List<Action<Boolean>> actions = new ArrayList<>();
        InitialStudent initialStudent = new InitialStudent();
        actions.add(initialStudent);
        sendMessage(initialStudent, _name, new StudentPrivateState()); //creating another action that would create actor for the stdunt
        then(actions, () -> {

            ((DepartmentPrivateState) (this.actorPool.getPrivaetState(_department))).getStudentList().add(_name); //addind the student to the course list
            this.actorPool.getActors().get(_department).addRecord("Add Student");
            complete(true);

        });
    }
}
