package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * get a string name and department name and close the course
 * change the course available spots to -1.
 */
public class CloseCourse extends Action<Boolean> {
    private String _department;
    private String _name;

    public CloseCourse(String name, String department) {
        _name = name;
        _department = department;
        setActionName("Close Course");
    }

    /**
     * call courseHelp(that would unregister all students) and then remove the course from the department list
     */
    protected void start() {

        List<Action<Boolean>> actions = new ArrayList<>();
        closeCourseHelp courseHelp = new closeCourseHelp(_name);
        actions.add(courseHelp);
        sendMessage(courseHelp, _name, new CoursePrivateState());
        then(actions, () -> {
            ((DepartmentPrivateState) (this.actorPool.getActors().get(_department))).getCourseList().remove(_name);
            this.actorPool.getActors().get(_department).addRecord("Close Course");
            complete(true);
        });
    }

}
