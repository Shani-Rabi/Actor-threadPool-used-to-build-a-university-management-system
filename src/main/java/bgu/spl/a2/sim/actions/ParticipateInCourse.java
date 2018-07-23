package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * get a coursename,name of students ,grade and register the student to the course if possible with the grade.
 */
public class ParticipateInCourse extends Action<Boolean> {
    private String _name;
    private String _coursename;
    private int _grade;

    public ParticipateInCourse(String coursename, String name, int grade) {
        _name = name;
        _coursename = coursename;
        _grade = grade;
        setActionName("Participate In Course");
    }

    /**
     * this method check first if the student has the pre courses for this course, then check if the course has a place for him, if yes call add grade,and then addthe student and complete.
     */
    protected void start() {
        CoursePrivateState theCourse = (CoursePrivateState) (this.actorPool.getActors().get(_coursename));
        if (!((CoursePrivateState) actorPool.getPrivaetState(_coursename)).getRegStudents().contains(_name)) {
            List<Action<Boolean>> actions = new ArrayList<>();
            List<String> pre = theCourse.getPrequisites();

            Action<Boolean> checkParticipate = new participateCheck(pre, _name);
            actions.add(checkParticipate);
            sendMessage(checkParticipate, _name,new StudentPrivateState());
            then(actions, () -> {
                Boolean result = checkParticipate.getResult().get();
                if (result) { //add the student to the course
                    if (theCourse.getAvailableSpots().intValue() > 0) {
                        theCourse.getRegStudents().add(_name);
                        theCourse.setRegistered(new Integer(theCourse.getRegistered() + 1));
                        theCourse.setAvailableSpots(new Integer(theCourse.getAvailableSpots() - 1));
                        Action<Boolean> addGrade = new addGrade(_grade, _name, _coursename);
                        actions.add(addGrade);
                        sendMessage(addGrade, _name, new StudentPrivateState());
                        then(actions, () -> {

                            theCourse.addRecord("Participate In Course");
                            complete(true);
                        });
                    } else {
                        theCourse.addRecord("Participate In Course");
                        complete(false);
                    }
                } else {
                    theCourse.addRecord("Participate In Course");
                    complete(false);
                }

            });
        } else {

            theCourse.addRecord("Participate In Course");
            complete(false);
        }
    }

}
