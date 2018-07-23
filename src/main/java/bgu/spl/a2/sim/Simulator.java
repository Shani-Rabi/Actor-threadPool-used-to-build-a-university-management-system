/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.actions.*;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
    static JsonObject jsonObject;
    private static Warehouse theWareHouse;
    public static ActorThreadPool actorThreadPool;

    /**
     * Begin the simulation Should not be called before attachActorThreadPool()
     */
    public static void start() {
        JsonArray computers = jsonObject.get("Computers").getAsJsonArray();
        theWareHouse = new Warehouse();
        actorThreadPool.start();
        for (JsonElement cuurObject : computers) {//add the computers to warehouse
            String type = cuurObject.getAsJsonObject().get("Type").getAsString();
            String sigsuccess = cuurObject.getAsJsonObject().get("Sig Success").getAsString();
            String sigfail = cuurObject.getAsJsonObject().get("Sig Fail").getAsString();
            theWareHouse.addComputer(type, sigsuccess, sigfail);

        }

        JsonArray phase1 = jsonObject.get("Phase 1").getAsJsonArray();
        int counter = phase1.size();
        CountDownLatch theLatch = new CountDownLatch(counter);
        for (JsonElement cuurObject : phase1) {
            getActionAndSubmit(cuurObject.getAsJsonObject(), theLatch);
        }
        try {
            theLatch.await();
        } catch (InterruptedException ex) {
        }


        JsonArray phase2 = jsonObject.get("Phase 2").getAsJsonArray();
        counter = phase2.size();
        theLatch = new CountDownLatch(counter);
        for (JsonElement cuurObject : phase2) {
            getActionAndSubmit(cuurObject.getAsJsonObject(), theLatch);
        }
        try {
            theLatch.await();
        } catch (InterruptedException ex) {
        }

        JsonArray phase3 = jsonObject.get("Phase 3").getAsJsonArray();
        counter = phase3.size();
        theLatch = new CountDownLatch(counter);
        for (JsonElement cuurObject : phase3) {
            getActionAndSubmit(cuurObject.getAsJsonObject(), theLatch);
        }
        try {
            theLatch.await();
        } catch (InterruptedException ex) {
        }
    }

    /**
     * attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
     *
     * @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
     */
    public static void attachActorThreadPool(ActorThreadPool myActorThreadPool) {
        actorThreadPool = myActorThreadPool;
    }

    /**
     * shut down the simulation
     * returns list of private states
     */
    public static HashMap<String, PrivateState> end() {
        try {
            actorThreadPool.shutdown();
        } catch (InterruptedException ignored) {
        }
        return new HashMap<>(actorThreadPool.getActors());
    }

    public static void main(String[] args) {
        File jsonfile = new File(args[0]);
        JsonParser parser = new JsonParser();
        try {
            jsonObject = parser.parse(new FileReader(jsonfile)).getAsJsonObject();
            attachActorThreadPool(new ActorThreadPool(jsonObject.get("threads").getAsInt()));
            start();
            HashMap<String, PrivateState> SimulationResult = end();
            FileOutputStream fout = new FileOutputStream("result.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(SimulationResult);
            oos.close();
        } catch (Exception e) {// return 1;
        }
       //return 0;
    }

    /**
     * @param thejson - json file that we want to work on
     * @param theLatch -the CountDownLatch, we use him for the subscribe comment.
     */
    protected static void getActionAndSubmit(JsonObject thejson, CountDownLatch theLatch) {
        String actionName = thejson.get("Action").getAsString();
        Action theAction = null;
        String theActor = null;
        PrivateState theState = null;
        // checking which action we got:
        if (actionName.equals("Open Course")) {
            theState = new DepartmentPrivateState();
            theActor = thejson.get("Department").getAsString();
            String courseName = thejson.get("Course").getAsString();
            int size = thejson.get("Space").getAsInt();
            JsonArray jsonarray = thejson.get("Prerequisites").getAsJsonArray();
            ArrayList<String> array = new ArrayList<>();
            for (JsonElement element : jsonarray) {
                array.add(element.getAsString());
            }
            theAction = new OpenCourse(theActor, courseName, size, array);

        } else if (actionName.equals("Add Student")) {
            theState = new DepartmentPrivateState();
            theActor = thejson.get("Department").getAsString();
            String studentName = thejson.get("Student").getAsString();
            theAction = new AddStudent(theActor, studentName);
        } else if (actionName.equals("Participate In Course")) {
            int grade;
            theState = new CoursePrivateState();
            theActor = thejson.get("Course").getAsString();
            String studentName = thejson.get("Student").getAsString();
            JsonArray jsonarray = thejson.get("Grade").getAsJsonArray();
            if (jsonarray.get(0).getAsString().equals("-"))
                grade = -1;
            else
                grade = jsonarray.get(0).getAsInt();
            theAction = new ParticipateInCourse(theActor, studentName, grade);
        } else if (actionName.equals("Unregister")) {
            theState = new CoursePrivateState();
            theActor = thejson.get("Course").getAsString();
            String studentName = thejson.get("Student").getAsString();
            theAction = new Unregister(theActor, studentName);
        } else if (actionName.equals("Close Course")) {
            theState = new DepartmentPrivateState();
            theActor = thejson.get("Department").getAsString();
            String courseName = thejson.get("Course").getAsString();
            theAction = new CloseCourse(courseName, theActor);
        } else if (actionName.equals("Add Spaces")) {
            theState = new CoursePrivateState();
            theActor = thejson.get("Course").getAsString();
            String numToAdd = thejson.get("Number").getAsString();
            theAction = new AddSpaces(theActor, numToAdd);
        } else if (actionName.equals("Administrative Check")) {
            theState = new DepartmentPrivateState();
            theActor = thejson.get("Department").getAsString();
            String computer = thejson.get("Computer").getAsString();
            JsonArray students = thejson.get("Students").getAsJsonArray();
            ArrayList<String> StudentsToCheck = new ArrayList<>();
            for (JsonElement element : students) {
                StudentsToCheck.add(element.getAsString());
            }
            JsonArray courses = thejson.get("Conditions").getAsJsonArray();
            ArrayList<String> ConditionsToCheck = new ArrayList<>();
            for (JsonElement element : courses) {
                ConditionsToCheck.add(element.getAsString());
            }
            theAction = new AdministrativeCheck(theWareHouse, theActor, StudentsToCheck, ConditionsToCheck, computer);

        } else if (actionName.equals("Register With Preferences")) {
            theState = new StudentPrivateState();
            String studentName = thejson.get("Student").getAsString();
            JsonArray preferences = thejson.get("Preferences").getAsJsonArray();
            ArrayList<String> preferencesToRegister = new ArrayList<>();
            for (JsonElement element : preferences) {
                preferencesToRegister.add(element.getAsString());
            }
            JsonArray grades = thejson.get("Grade").getAsJsonArray();
            ArrayList<String> gradesToRegister = new ArrayList<>();
            for (JsonElement element : grades) {
                gradesToRegister.add(element.getAsString());
            }
            theActor = studentName;
            theAction = new RegisterWithPreferences(studentName, preferencesToRegister, gradesToRegister);
        }


        if (theAction != null) {
            theAction.getResult().subscribe(() -> {
                theLatch.countDown();
            });
            actorThreadPool.submit(theAction, theActor, theState);
        }
    }
}