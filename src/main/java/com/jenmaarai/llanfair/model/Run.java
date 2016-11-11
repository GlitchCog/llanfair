package com.jenmaarai.llanfair.model;

import com.jenmaarai.sidekick.time.Time;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Run {
   
   private static final Logger LOG = LoggerFactory.getLogger(Run.class);
   
   private String game;
   private String abbreviation;
   private String category;
   private String platform;
   private String region;
   private boolean emulated;
   
   private List<Segment> segments = new ArrayList<>();
   
   /**
    * Creates an empty run containing a single segment.
    */
   public Run() {
      segments.add(new Segment());
   }

   /**
    * Returns the complete name of the game being runned.
    */
   public String getGame() {
      return game;
   }

   /**
    * Returns the name of this run category.
    */
   public String getCategory() {
      return category;
   }

   /**
    * Returns the platform that this game is being runned on.
    */
   public String getPlatform() {
      return platform;
   }

   /**
    * Return the region of the game being runned.
    */
   public String getRegion() {
      return region;
   }

   /**
    * Indicates whether this run takes place on an emulator or not.
    */
   public boolean isEmulated() {
      return emulated;
   }
   
   /**
    * Returns the number of segments in this run, cannot be lower than 1.
    */
   public int getSegmentCount() {
      return segments.size();
   }
   
   /**
    * Returns the time of this run.
    * The returned time will be based on real time (as opposed to game time).
    */
   public Time getTime() {
      return getSplitTime(getSegmentCount() - 1);
   }
   
   /**
    * Returns the split time of a specific segment.
    * May return null if the split time for the specific segment is undefined,
    * because the user forgot to split and skipped it during the run. 
    * The returned time will be based on real time (as opposed to game time).
    */
   public Time getSplitTime(int segmentId) {
      if (segmentId < 0 || segmentId >= getSegmentCount()) {
         LOG.error("Invalid segment id '{}'", segmentId);
         throw new IllegalArgumentException("invalid segment id");
      }
      return segments.get(segmentId).getTime();
   }
   
   /**
    * Returns the segment time of a specific segment.
    * May return null if the specific segment or the preceding one has an
    * undefined split time, usually because it has been skipped during the run.
    * The returned time will be based on real time (as opposed to game time).
    */
   public Time getSegmentTime(int segmentId) {
      if (segmentId < 0 || segmentId >= getSegmentCount()) {
         LOG.error("Invalid segment id '{}'", segmentId);
         throw new IllegalArgumentException("invalid segment id");
      }
      Time previous = segmentId == 0 ? Time.ZERO : getSplitTime(segmentId - 1);
      return Time.delta(segments.get(segmentId).getTime(), previous);
   }
   
   /**
    * Returns the best recorded segment time of a specific segment.
    * May return null if the run has not yet been completed once.
    * The returned time will be based on real time (as opposed to game time).
    */
   public Time getSegmentBest(int segmentId) {
      if (segmentId < 0 || segmentId >= getSegmentCount()) {
         LOG.error("Invalid segment id '{}'", segmentId);
         throw new IllegalArgumentException("invalid segment id");
      }
      return segments.get(segmentId).getBest();
   }
   
   /**
    * Returns the sum of best segment times for this run.
    * This time can be null if the run has not yet been completed once.
    * The returned time will be based on real time (as opposed to game time).
    */
   public Time getSumOfBest() {
      Time time = new Time(0L);
      segments.stream().forEach((segment) -> time.add(segment.getBest()));
      return time;
   }
   
   /**
    * Sets the new split time for a specific segment.
    * Caller must ensure that the split time is coherent for said segment in
    * this particular run. In other words, the split time of segment n should
    * be strictly superior to the split time of segment n-1 if defined, or 0.
    * However, the split time can also be null (undefined) unless this is the
    * split time of the last segment of the run.
    */
   public void setSplitTime(int segmentId, Time split) {
      if (segmentId < 0 || segmentId >= getSegmentCount()) {
         LOG.error("Invalid segment id '{}'", segmentId);
         throw new IllegalArgumentException("invalid segment id");
      }
      if (split == null && segmentId == getSegmentCount() - 1) {
         LOG.error("Last segment of the run cannot be set to null");
         throw new IllegalArgumentException("null last segment split time");
      }
      Time previous = segmentId == 0 ? Time.ZERO : getSplitTime(segmentId - 1);
      if (split != null && split.compareTo(previous) <= 0) {
         LOG.error("Split time too low for segment {}: {}", segmentId, split);
         throw new IllegalArgumentException("split time too low for segment");
      }
      segments.get(segmentId).setTime(split);
      lookForBestSegment(segmentId, split);
   }
   
   /**
    * Sets the split times for this run.
    * Caller must ensure that the size of the provided list is equal to the
    * number of segments in this run. Each time must also be coherent for
    * the structure of this run. In other words, the split time of segment n 
    * should be strictly superior to the split time of segment n-1 if defined, 
    * or 0. However, any split time can also be null (undefined) except for the
    * split time of the last segment of the run.
    */
   public void setSplitTimes(List<Time> times) {
      if (times == null) {
         LOG.error("List of times is null");
         throw new IllegalArgumentException("null list of times");
      }
      if (times.size() != getSegmentCount()) {
         LOG.error(
                 "Size mismatch {} times for {} segments", 
                 times.size(), getSegmentCount());
         throw new IllegalArgumentException("list size mismatch");
      }
      List<Segment> backup = new ArrayList<>(segments);
      try {
         for (int i = 0; i < getSegmentCount(); i++) {
            setSplitTime(i, times.get(i));
         }
      } catch (IllegalArgumentException x) { 
         segments = backup;
         throw x;
      }
   }
   
   /**
    * Sets the best segment time of a specific segment.
    * Automatically sets the best time to the current segment time if defining
    * a new best time that is null or superior to the current segment time.
    */
   public void setSegmentBest(int segmentId, Time time) {
      if (segmentId < 0 || segmentId >= getSegmentCount()) {
         LOG.error("Invalid segment id '{}'", segmentId);
         throw new IllegalArgumentException("invalid segment id");
      }
      Time segment = getSegmentTime(segmentId);
      if (time == null || time.compareTo(segment) > 0) {
         time = segment;
      }
      segments.get(segmentId).setBest(time);
   }
   
   /**
    * Sets a new best segment time for a specific segment, based on split time.
    * Finds out the segment time from the given split and saves it as a new 
    * best segment time if it is better than the current best segment time.
    * The given split time itself is discarded.
    */
   public void lookForBestSegment(int segmentId, Time split) {
      if (segmentId < 0 || segmentId >= getSegmentCount()) {
         LOG.error("Invalid segment id '{}'", segmentId);
         throw new IllegalArgumentException("invalid segment id");
      }
      Time previous = segmentId == 0 ? Time.ZERO : getSplitTime(segmentId - 1);
      Time segment  = Time.delta(split, previous);
      Time best     = getSegmentBest(segmentId);
      
      if (segment != null && segment.compareTo(best) < 0) {
         setSegmentBest(segmentId, segment);
      }
   }
   
}
