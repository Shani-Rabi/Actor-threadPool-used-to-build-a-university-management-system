package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

import java.util.HashMap;
import java.util.Map;

/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 */
public class Warehouse {
    HashMap<Computer, SuspendingMutex> map = new HashMap<>();

    public void addComputer(String computer, String success, String fail) {
        Computer tempComputer = new Computer(computer);
        tempComputer.successSig = Long.valueOf(success).longValue();
        tempComputer.failSig = Long.valueOf(fail).longValue();

        map.put(tempComputer, new SuspendingMutex(tempComputer));

    }

    /**
     * @param computer
     * @return Promise which hold the result we
     */
    public Promise acquire(String computer) {
        Computer currComputer = getComputer(computer);
        if (currComputer != null)
            return map.get(currComputer).down();
        else
            return null;
    }

    /**
     * @param computer -realease this computer
     */
    public void release(String computer) {
        Computer currComputer = getComputer(computer);
        if (currComputer != null)
            map.get(currComputer).up();
    }

    /**
     * @param computer
     * @return  Computer which his computertype is computer
     */
    public Computer getComputer(String computer) {
        for (Map.Entry<Computer, SuspendingMutex> pair : map.entrySet()) {
            if (pair.getKey().computerType.equals(computer)) {
                return pair.getKey();
            }
        }
        return null;

    }
}




	

