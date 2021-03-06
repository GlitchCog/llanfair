package com.jenmaarai.llanfair.control;

import java.time.Instant;

public class Clock {

   /**
    * Returns the current internal clock time in milliseconds since the epoch.
    */
   public static long now() {
      return Instant.now().toEpochMilli();
   }
   
}
