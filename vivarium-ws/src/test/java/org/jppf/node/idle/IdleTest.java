/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package org.jppf.node.idle;

public class IdleTest
{
    public static void main(String[] args)
    {
        IdleTimeDetectorFactoryImpl idleTimeFactory = new org.jppf.node.idle.IdleTimeDetectorFactoryImpl();
        IdleTimeDetector idleTimeDetector = idleTimeFactory.newIdleTimeDetector();
        long idleMS = idleTimeDetector.getIdleTimeMillis();
        System.out.println("Idle time 1: " + idleMS);
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        idleMS = idleTimeDetector.getIdleTimeMillis();
        System.out.println("Idle time 2: " + idleMS);
    }

}
