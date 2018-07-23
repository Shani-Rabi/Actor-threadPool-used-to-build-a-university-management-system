package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * string department, string name,int size ,array of prerequisites.
 * open the course in the department.
 */
public class OpenCourse extends Action<Boolean> {
    private String _name;
    private String _department;
    private int _size;
    private ArrayList<String> _prerequisites;


    public OpenCourse(String department, String name, int size, ArrayList<String> prerequisites) {
        _name = name;
        _department = department;
        _size = size;
        _prerequisites = prerequisites;
        setActionName("Open Course");
    }


    /**
     * call InitialCourse and then add the course to the department.
     */
    protected void start() {

        List<Action<Boolean>> actions = new ArrayList<>();
        CoursePrivateState theState = new CoursePrivateState();
        InitialCourse initialCourse = new InitialCourse(_size, _prerequisites, theState);
        actions.add(initialCourse);
        sendMessage(initialCourse, _name, theState);

        then(actions, () -> {
            ((DepartmentPrivateState) (this.actorPool.getPrivaetState(_department))).getCourseList().add(_name);
            this.actorPool.getActors().get(_department).addRecord("Open Course");
            complete(true);

        });

    }
}
