package com.jenmaarai.llanfair.config;

/**
 * Delegate used to retrieve the current system time.
 * Using this class allows us to specify a unique and consistent method across
 * the application, and to eventually update it if need be.
 */
public class Clock {

    /**
     * Returns the current number of milliseconds on the system clock.
     * 
     * @return the current number of milliseconds on the clock
     */
    public static long ms() {
        return System.nanoTime() / 1000000L;
    }
}
