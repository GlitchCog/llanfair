package com.jenmaarai.llanfair.control;

import com.jenmaarai.llanfair.model.Run;
import com.jenmaarai.sidekick.time.Time;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Splitter {
   
   private static final Logger LOG = LoggerFactory.getLogger(Splitter.class);

   private Run run = new Run();
   private List<Time> times = new ArrayList<>();
   
   private long start = 0L;
   private State state = State.READY;
   
   /**
    * Returns the current state of the splitter.
    */
   public State getState() {
      return state;
   }
   
   /**
    * Returns the index of the current segment.
    * The returned value only makes sense if the splitter is {@code RUNNING}.
    */
   public int getCurrentSegment() {
      return times.size();
   }
   
   /**
    * Immediately starts a new run.
    * Splitter must be in {@code READY} state to be started.
    */
   public void start() {
      if (state != State.READY) {
         LOG.error("Splitter not ready, current state {}", state);
         throw new IllegalStateException("splitter not ready");
      }
      start = Clock.now();
      state = State.RUNNING;
   }
   
   /**
    * Moves the run to the next segment.
    * If the run was at its last segment, the run will stop afterwards and the
    * splitter will be in {@code OVER} state. Splitter must be in 
    * {@code RUNNING} state for the user to split.
    */
   public void split() {
      if (state != State.RUNNING) {
         LOG.error("Splitter not running, current state {}", state);
         throw new IllegalStateException("splitter not running");
      }
      times.add(new Time(Clock.now() - start));
      
      if (times.size() == run.getSegmentCount()) {
         state = State.OVER;
      }
   }
   
   /**
    * Validates the split times made during the attempt.
    * New split times are saved as the personal best attempt for the current 
    * run and best segment times are updated if necessary. Afterwards, the 
    * splitter will be in {@code READY} state. The splitter must be in
    * {@code OVER} state to be able to save the run.
    */
   public void saveRun() {
      if (state != State.OVER) {
         LOG.error("Splitter not over, current state {}", state);
         throw new IllegalStateException("splitter not over");
      }
      run.setSplitTimes(times);
      times.clear();
      state = State.READY;
   }
   
   public enum State {
      
      /**
       * In this state the splitter is ready to start running.
       */
      READY,
      
      /**
       * In this state the splitter is currently timing a run.
       */
      RUNNING,
      
      /**
       * In this state the current run is over.
       */
      OVER;              
      
   }
   
}
