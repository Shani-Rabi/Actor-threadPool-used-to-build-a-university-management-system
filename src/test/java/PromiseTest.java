package bgu.spl.a2;

import junit.framework.Assert;
import junit.framework.TestCase;

import javax.security.auth.callback.Callback;

/**
 * test PromiseClass
 */
public class PromiseTest extends TestCase {

    /**
     * Tests for get method.
     */
public void testGet() {
    Promise<String> toTest=new Promise<>();
        try {
            String x = toTest.get();
            Assert.fail();
        }
        catch (IllegalStateException ex) {

        }
        catch (Exception ex) {
            Assert.fail();
        }
        toTest.resolve("ass2");

    try {
        String x = toTest.get();
        assertEquals(x,"ass2");
    }
    catch (Exception ex) {
        Assert.fail();
    }
}

    /**
     * Tests for isResolved method.
     */
    public void testisResolved() {
        Promise<String> toTest=new Promise<>();
            boolean x = toTest.isResolved();
            assertEquals(x,false);

            toTest.resolve("ass2");
	    x= toTest.isResolved();
            assertEquals(x,true);

    }

    /**
     * Tests for Resolve method.
     */
    public void testResolve() {
        int [] array= new int[6];
        Promise<String> toTest = new Promise<>();
        callback a=()->array[0]=0;
        callback b=()->array[1]=1;
        callback c=()->array[2]=2;
        callback d=()->array[3]=3;
        callback e=()->array[4]=4;
        callback f=()->array[5]=5;
        toTest.subscribe(a);
        toTest.subscribe(b);
        toTest.subscribe(c);
        toTest.subscribe(d);
        toTest.subscribe(e);
        toTest.subscribe(f);
        toTest.resolve("ass2");
        assertEquals(toTest.get(),"ass2");
        for (int i=0;i<array.length;i++){
            assertEquals(array[i],i);
        }
        try{
            toTest.resolve("again");
            Assert.fail();
        }
        catch (IllegalStateException ex){
            assertEquals(toTest.get(),"ass2");
        }
        catch (Exception ex) {
            Assert.fail();
        }


    }


    /**
     * tests for Subscribe method
     */
    public void testSubscribe() {
        int [] array= new int[1];
        Promise<String> toTest = new Promise<>();
        toTest.resolve("ass2");
        callback a=()->array[0]=5;
        toTest.subscribe(a);
        assertEquals(array[0],5);
    }

}
