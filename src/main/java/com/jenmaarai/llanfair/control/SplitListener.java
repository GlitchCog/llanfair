package com.jenmaarai.llanfair.control;

import java.util.EventListener;

public interface SplitListener extends EventListener {
   
   /**
    * Callback invoked when a new attempt starts.
    */
   void onStart();
   
   /**
    * Callback invoked when a split is triggered during an attempt.
    * Will not be invoked upon triggering the last split, see {@code onDone}.
    */
   void onSplit();
   
   /**
    * Callback invoked when the attempt is reset.
    */
   void onReset();
   
   /**
    * Callback invoked when the attempt is over and the run completed.
    */
   void onDone();
   
}
