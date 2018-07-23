package bgu.spl.a2;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * test for VersionMonitor class
 */
public class VersionMonitorTest extends TestCase { //

    public void testGet() {//test the get method
        VersionMonitor toTest = new VersionMonitor();
        int currVersion = toTest.getVersion();
        assertEquals(currVersion, 0);
    }

    /**
     * test the inc method
     */
    public void testinc() {
        VersionMonitor toTest = new VersionMonitor();
        int currVersion = toTest.getVersion();
        assertEquals(currVersion, 0);
        toTest.inc();
        assertEquals(currVersion + 1, toTest.getVersion());
    }

    /**
     * test the await method
     */
    public void testawait() {
        VersionMonitor toTest = new VersionMonitor();
        int currVersion = toTest.getVersion();
        assertEquals(currVersion, 0);
        toTest.inc();
        assertEquals(currVersion + 1, toTest.getVersion());
        AtomicBoolean waiter=new AtomicBoolean(false);
        Thread T1 = new Thread(()->{// thread for the await
             try {
                 toTest.await(currVersion+1);
                 waiter.set(true);
             }
             catch (InterruptedException ex)
             {
             }
            });
             T1.start();
        try {
            Thread.sleep(1000);
        }
        catch(Exception ex) {
        }
         assertFalse(waiter.get()); // this means that the wait function wasn't over, it still waiting
        toTest.inc();// increment
        try {
            Thread.sleep(1000);
        }
        catch(Exception ex) {
        }
        assertTrue(waiter.get()); // this means the wait function was over
        waiter.set(false);
        Thread T2 = new Thread(()-> {
            try {
                toTest.await(currVersion); // the version is different from the field so the thread will continue
                waiter.set(true);
            } catch (InterruptedException e) {
            }

        });
                T2.start();
        try {
            Thread.sleep(1000);
        }
        catch(Exception ex) {
        }
        assertTrue(waiter.get()); // it means it didn't wait for inc
    }




}