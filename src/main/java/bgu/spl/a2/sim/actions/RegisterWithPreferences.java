package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * get a student name , list of preferences, list of grade and register the student for one of the courses according to his preferences
 */
public class RegisterWithPreferences extends Action<Boolean> {
    private String _studentName;
    private List<String> _preferences;
    private List<String> _grades;
    private int i = 0;

    public RegisterWithPreferences(String studentName, List<String> preferences, List<String> grades) {
        _studentName = studentName;
        _preferences = preferences;
        _grades = grades;
        setActionName("Register With Preferences");
    }

    /**
     * try register the student to one of his favorite courses.
     */
    protected void start() {

        List<Action<Boolean>> actions = new ArrayList<>();
        Action<Boolean> register;
        if (_grades.get(i).equals("-"))
            register = new ParticipateInCourse(_preferences.get(i), _studentName, -1);
        else
            register = new ParticipateInCourse(_preferences.get(i), _studentName, Integer.parseInt(_grades.get(i)));
        actions.add(register);
        sendMessage(register, _preferences.get(i), new CoursePrivateState()); // we're trying to register to the first course
        then(actions, () -> {
            if (actions.get(i).getResult().get()) { // checking if we managed to register in the last try
                actorPool.getPrivaetState(_studentName).addRecord("Register With Preferences");
                complete(true);
            } else {
                i++; // moving on to check the next preference
                if (i == _preferences.size()) {
                    actorPool.getPrivaetState(_studentName).addRecord("Register With Preferences");
                    complete(false);
                } else {
                    Action<Boolean> registertemp;
                    if (_grades.get(i).equals("-"))
                        registertemp = new ParticipateInCourse(_preferences.get(i), _studentName, -1);
                    else
                        registertemp = new ParticipateInCourse(_preferences.get(i), _studentName, Integer.parseInt(_grades.get(i)));
                    actions.add(registertemp);
                    sendMessage(registertemp, _preferences.get(i),new CoursePrivateState()); // trying to register to the next preferences
                    then(actions, _callBack);
                }
            }
        });

    }
}