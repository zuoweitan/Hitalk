package com.vivifram.second.hitalk;

import com.avos.avoscloud.AVObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vivifram.second.hitalk.manager.RetrofitManager;
import com.zuowei.utils.helper.ILoginService;
import com.zuowei.utils.helper.LoginHelper;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void hello_world() {
        System.out.print("Hello World");
    }

    @Test
    public void textLong() {
        System.out.println(getAndAddRequest(new AtomicLong(),Long.MAX_VALUE));
    }

    public static long addCap(long a, long b) {
        long u = a + b;
        if (u < 0L) {
            u = Long.MAX_VALUE;
        }
        return u;
    }

    public static long getAndAddRequest(AtomicLong requested, long n) {
        // add n to field but check for overflow
        while (true) {
            long current = requested.get();
            long next = addCap(current, n);
            if (requested.compareAndSet(current, next)) {
                return current;
            }
        }
    }
    @Test
    public void testRetrofit(){
        //LoginHelper.getInstance().requestVerifyMobilePhone("15000435912");
    }

    @Test
    public void testPath(){
        String path = "dadad/dadad/adasdasdasd.jpg";

        Pattern pattern = Pattern.compile(".*\\.jpg|\\.png|\\.gif|\\.bmp");
        Matcher matcher = pattern.matcher(path.toLowerCase());
        while (matcher.find()){
            System.out.println(matcher.groupCount());
            //System.out.println(matcher.group(1));
        }
    }

    @Test
    public void testGson() throws ClassNotFoundException {
       /* B b = new B();
        b.b = "b";
        b.a = "a";
        String json = new Gson().toJson(b);
        A a = new Gson().fromJson(json,A.class);
        *//*A a1 = (A) new Gson().fromJson(json,Class.forName(a.className));*//*
        b = (B) a;*/
        HashMap<String,Integer> entries = new HashMap<>();
        entries.put("abc",1);

        System.out.println(new Gson().toJson(entries));

        HashMap hashMap = new Gson().fromJson(new Gson().toJson(entries), entries.getClass());

        Type type = new TypeToken<HashMap<String, Integer>>() {
        }.getType();
        System.out.println(new Gson().fromJson(new Gson().toJson(entries), type));


    }

    static class A{
        String a;
        String className;
        {
            className = getClass().getName();
        }

        protected AVObject encoder(){return null;}
        protected A decoder(AVObject avObject){return null;}

        public static void print(){}
    }


    static class B extends A{
        String b;

        @Override
        public String toString() {
            return a + ":" +b;
        }

        @Override
        protected AVObject encoder() {
            AVObject avObject = new AVObject();
            avObject.put("b",b);
            return avObject;
        }

        @Override
        protected A decoder(AVObject avObject) {
            return null;
        }
    }

    @Test
    public void testSync() throws InterruptedException {
        System.out.println("stat at time = "+new Date());
        final CountDownLatch lock = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.countDown();
            }
        }).start();
        lock.await();
        System.out.println("do next at time = "+new Date());
    }

    class Parent{
        int i;
        public Parent(){
            this(0);
        }
        public Parent(int i){
            this.i = i;
            printi();
        }

        protected void printi(){
            i+=1;
            System.out.println(i);
            System.out.println(this);
        }
    }

    class Child extends Parent{

        public Child() {
        }

        public Child(int i) {
            super(i);
            System.out.println("HHHHHHHH");
        }

        @Override
        protected void printi() {
            super.printi();
        }
    }

    @Test
    public void testClass(){
        new Child();
    }

}