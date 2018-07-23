package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * get a course name ,student name and remove the student from the course students
 */
public class Unregister extends Action<Boolean> {
    private String _coursename;
    private String _name;

    public Unregister(String coursename, String name) {
        _name = name;
        _coursename = coursename;
        setActionName("Unregister");
    }

    /**
     * this method return false if the student is not in the course, true if yes and unregister him to this course.
     */
    protected void start() {
        CoursePrivateState theCourse = (CoursePrivateState) (this.actorPool.getActors().get(_coursename));
        Action<Boolean> waitForUnregister = new waitForUnregister();
        List<Action<Boolean>> actions = new ArrayList<>();
        actions.add(waitForUnregister);
        sendMessage(waitForUnregister, _name, new StudentPrivateState());
        then(actions, () -> {
            if (theCourse.getRegStudents().contains(_name)) {
                theCourse.getRegStudents().remove(_name);
                theCourse.setRegistered(new Integer(theCourse.getRegistered() - 1));
                if (theCourse.getAvailableSpots().intValue() != -1)
                    theCourse.setAvailableSpots(new Integer(theCourse.getAvailableSpots() + 1));
                Action<Boolean> removeGrade = new removeGrade(_name, _coursename);
                actions.add(removeGrade);
                sendMessage(removeGrade, _name, new StudentPrivateState());
                then(actions, () -> {
                    theCourse.addRecord("Unregister");
                    complete(true);
                });
            } else {
                theCourse.addRecord("Unregister");
                complete(false);
            }

        });

    }

}


