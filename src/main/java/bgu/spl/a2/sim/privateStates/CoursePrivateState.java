package bgu.spl.a2.sim.privateStates;

import bgu.spl.a2.PrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState {

    private Integer availableSpots;
    private Integer registered;
    private List<String> regStudents;
    private List<String> prequisites;

    /**
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     */
    public CoursePrivateState() {
        availableSpots = new Integer(0);
        registered = new Integer(0);
        regStudents = new ArrayList<>();
    }

    /**
     * @param toadd
     */
    public void addAvailableSpots(String toadd) {
        availableSpots = new Integer(getAvailableSpots() + java.lang.Integer.parseInt(toadd));
    }

    /**
     * @param num
     */
    public void setAvailableSpots(Integer num) {
        availableSpots = num;
    }

    /**
     * @param numOfRegistered
     */
    public void setRegistered(Integer numOfRegistered) {
        registered = numOfRegistered;
    }

    /**
     * @param prequisites
     */
    public void setPrequisites(List<String> prequisites) {
        this.prequisites = prequisites;
    }

    public Integer getAvailableSpots() {
        return availableSpots;
    }

    public Integer getRegistered() {
        return registered;
    }

    public List<String> getRegStudents() {
        return regStudents;
    }

    public List<String> getPrequisites() {
        return prequisites;
    }
}
