package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * get warehouse, name of department,list of students,list of courses,and name of computer .
 * acquire a computer and add signatures for the students.
 */
public class AdministrativeCheck extends Action<Boolean> {
    private String _department;
    private List<String> _studentsList;
    private List<String> _coursesList;
    private String _computer;
    Warehouse _warehouse;

    public AdministrativeCheck(Warehouse warehouse, String department, List<String> studentsList, List<String> coursesList, String computer) {
        _department = department;
        _studentsList = studentsList;
        _coursesList = coursesList;
        _computer = computer;
        _warehouse = warehouse;
        setActionName("Administrative Check");
    }

    /**
     * lock a computer and call check and sign for all of the students in the department
     */
    protected void start() {

        Promise tookCompuuter = _warehouse.acquire(_computer);
        tookCompuuter.subscribe(() -> { // code to do when we'll get the computer
            List<Action<Boolean>> actions = new ArrayList<>();
            for (String student : _studentsList) { // we check for every student if he meets the requirements
                Action<Boolean> signStudent = new checkAndSign(student, _coursesList, _warehouse.getComputer(_computer));
                sendMessage(signStudent, student, new StudentPrivateState());
                actions.add(signStudent);
            }
            then(actions, () -> { //once all the students finish cheking, we'll release the computer
                _warehouse.release(_computer);
                actorPool.getPrivaetState(_department).addRecord("Administrative Check");
                complete(true);
            });
        });


    }
}
