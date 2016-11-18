package com.jenmaarai.llanfair.control;

import com.jenmaarai.llanfair.model.Run;
import com.jenmaarai.sidekick.time.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.event.EventListenerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Splitter {
   
   private static final Logger LOG = LoggerFactory.getLogger(Splitter.class);

   private Run        run   = new Run();
   private List<Time> times = new ArrayList<>();
   
   private long  start = 0L;
   private State state = State.READY;
   
   private EventListenerList listeners = new EventListenerList();
   
   public Splitter() {
      run.addChangeListener((e) -> fireSplitEvent(SplitListener::onRunUpdate));
   }

   /**
    * Returns the run being used by this splitter.
    */
   public Run getRun() {
      return run;
   }
   
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
    * Returns the elapsed time since the splitter was started.
    * Splitter must be in {@code RUNNING} state.
    */
   public Time getElapsed() {
      if (state != State.RUNNING) {
         LOG.error("Splitter not running, current state {}", state);
         throw new IllegalStateException("splitter not running");
      }
      return new Time(Clock.now() - start);
   }
   
   /**
    * Registers a new split listener.
    * It will be notified of the various actions made by the splitter.
    */
   public void addSplitListener(SplitListener listener) {
      if (listener == null) {
         LOG.error("Null split listener");
         throw new IllegalArgumentException("null listener");
      }
      listeners.add(SplitListener.class, listener);
   }
   
   /**
    * Unregisters the specific split listener.
    */
   public void removeSplitListener(SplitListener listener) {
      if (listener == null) {
         LOG.error("Null split listener");
         throw new IllegalArgumentException("null listener");
      }
      listeners.remove(SplitListener.class, listener);
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
      fireSplitEvent(SplitListener::onStart);
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
         fireSplitEvent(SplitListener::onDone);
      } else {
         fireSplitEvent(SplitListener::onSplit);
      }
   }
   
   /**
    * Resets the splitter after or during a run.
    * If the splitter is in {@code OVER} state, the complete run will be saved
    * if it is better, which includes saving the new best segments if the
    * current attempt contains any. 
    * 
    * <p>If {@code save} is true, the new best segment times will be saved if 
    * the current attempt contains any, even if the attempt is incomplete and 
    * will itself be discarded.
    * 
    * <p>After this call, the splitter will be in {@code READY} state. The 
    * splitter must be in {@code OVER} or {@code RUNNING state} to be reset.
    */
   public void reset(boolean save) {
      if (state == State.READY) {
         LOG.error("Splitter is ready no sense in resetting");
         throw new IllegalStateException("splitter is ready");
      }
      if (state == State.OVER) {
         Time thisAttempt  = times.get(times.size() - 1);
         Time personalBest = run.getTime();
         if (thisAttempt.compareTo(personalBest) < 0) {
            run.setSplitTimes(times);
         }
      } else if (save) {
         for (int i = 0; i < times.size(); i++) {
            run.lookForBestSegment(i, times.get(i));
         }
      }
      state = State.READY;
      times.clear();
      fireSplitEvent(SplitListener::onReset);
   }
   
   /**
    * Fires a split event to every split listeners.
    */
   private void fireSplitEvent(Consumer<SplitListener> operator) {
      SplitListener[] array = listeners.getListeners(SplitListener.class);
      for (SplitListener listener : array) {
         operator.accept(listener);
      }
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
