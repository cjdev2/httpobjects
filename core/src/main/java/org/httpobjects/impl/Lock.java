package org.httpobjects.impl;

public class Lock {
        private boolean locked = true;
//        {
//            System.out.println("LOCK ESTABLISHED " + this);
//            for(StackTraceElement e : Thread.currentThread().getStackTrace()){
//                System.out.println("   " + e.getClassName() + "." + e.getMethodName());
//            }
//            
//        }
        void unlock(){
            synchronized(this){
                locked = false;
                this.notifyAll();
             }
        }

        void waitForUnlock(){
//            System.out.println("WAITING FOR LOCK " + this);
            synchronized(this){
                while(locked){
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }    
            }

//            System.out.println("DONE WAITING FOR LOCK " + this);
        }
    }