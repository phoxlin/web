package com.core.smart.threadlocal;

/**
 * Created by Administrator on 2017/11/11.
 */
public class SequenceC implements Sequence {

    private static MyThreadLocal<Integer> myContainer = new MyThreadLocal<Integer>(){
        protected Integer initialValue(){
            return 0;
        }
    };



    @Override
    public int getNumber() {
        myContainer .set(myContainer.get()+1);

        return myContainer.get();
    }

    public static void main(String[] args){
        Sequence sequence = new SequenceC();

        ClientThread thread1= new ClientThread(sequence);
        ClientThread thread2= new ClientThread(sequence);
        ClientThread thread3= new ClientThread(sequence);

        thread1.start();
        thread2.start();
        thread3.start();
    }
}
