package com.jenmaarai.llanfair.model;

import com.jenmaarai.sidekick.time.Time;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Run implements Serializable {
   
   /**
    * Current version of this class.
    * This value should be updated whenever changes made to this class break 
    * backward compatibility.
    */
   public static final long serialVersionUid = 20161104L;
   
   /**
    * XStream XML parser, customized to improve legibility of output.
    */
   private static final XStream XSTREAM = new XStream();
   static {
      XSTREAM.alias("run", Run.class);
      XSTREAM.alias("segment", Segment.class);
   }
   
   private static final Logger LOG = LoggerFactory.getLogger(Run.class);
   
   private String game = null;
   private String abbreviation = null;
   private String category = null;
   private String platform = null;
   private String region = null;
   private boolean emulated = false;
   private List<Segment> segments = new ArrayList<>();
   
   private transient EventListenerList listeners;
   
   /**
    * Creates an empty run containing a single segment.
    */
   public Run() {
      buildTransientObjects();
      segments.add(new Segment());
   }
   
   /**
    * Registers a change listener with this run.
    * The change listener is notified whenever any changes happen to this run.
    */
   public void addChangeListener(ChangeListener listener) {
      if (listener == null) {
         LOG.error("Null change listener");
         throw new IllegalArgumentException("null listener");
      }
      listeners.add(ChangeListener.class, listener);
   }

   /**
    * Returns the complete name of the game being runned.
    */
   public String getGame() {
      return game;
   }
   
   /**
    * Defines the name of the game this run is on.
    */
   public void setGame(String game) {
      this.game = game;
      fireChangeEvent();
   }

   /**
    * Returns the name of this run category.
    */
   public String getCategory() {
      return category;
   }

   /**
    * Defines the category of this run.
    */
   public void setCategory(String category) {
      this.category = category;
      fireChangeEvent();
   }

   /**
    * Returns the platform that this game is being runned on.
    */
   public String getPlatform() {
      return platform;
   }

   /**
    * Defines the platform this run performs on.
    */
   public void setPlatform(String platform) {
      this.platform = platform;
      fireChangeEvent();
   }
   
   /**
    * Return the region of the game being runned.
    */
   public String getRegion() {
      return region;
   }

   /**
    * Defines the region of the game being runned.
    */
   public void setRegion(String region) {
      this.region = region;
      fireChangeEvent();
   }
   
   /**
    * Indicates whether this run takes place on an emulator or not.
    */
   public boolean isEmulated() {
      return emulated;
   }

   /**
    * Defines whether this run takes place on an emulator or not.
    */
   public void setEmulated(boolean emulated) {
      this.emulated = emulated;
      fireChangeEvent();
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
      return Time.difference(segments.get(segmentId).getTime(), previous);
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
      Time segment  = Time.difference(split, previous);
      Time best     = getSegmentBest(segmentId);
      
      if (segment != null && segment.compareTo(best) < 0) {
         setSegmentBest(segmentId, segment);
      }
   }
   
   /**
    * Writes this run to the given file.
    * If the file does not exist, it is created. If it exists, it is 
    * overwritten without warning. Returns true if serialization was completed
    * without any problem.
    */
   public boolean writeFile(Path path) {
      if (path == null) {
         LOG.error("No path defined for current run");
         throw new IllegalArgumentException("null path");
      }
      try (BufferedWriter writer = Files.newBufferedWriter(path)) {
         String xmlOutput = XSTREAM.toXML(this);
         writer.write(xmlOutput);
      } catch (IOException x) {
         LOG.error("Error writing file '{}', {}:{}", 
                  path, x.getClass().getSimpleName(), x.getMessage());
         return false;
      } catch (XStreamException x) {
         LOG.error("Error serializing run to xml, {}:{}", 
                  x.getClass().getSimpleName(), x.getMessage());
         return false;
      }
      return true;
   }
   
   /**
    * Deserializes and returns a run read from the given file.
    * Returns null if the file cannot be read or does not exist.
    */
   public static Run readFile(Path path) {
      if (path == null) {
         LOG.error("No path defined for current run");
         throw new IllegalArgumentException("null path");
      }
      try (BufferedReader reader = Files.newBufferedReader(path)) {
         return (Run) XSTREAM.fromXML(reader);
      } catch (IOException x) {
         LOG.error("Error reading file '{}', {}:{}", 
                  path, x.getClass().getSimpleName(), x.getMessage());
      } catch (XStreamException x) {
         LOG.error("Error deserializing xml, {}:{}", 
                  x.getClass().getSimpleName(), x.getMessage());
      } catch (ClassCastException x) {
         LOG.error("Xml does not contain a valid run");
      }
      return null;
   }
   
   /**
    * Method invoked during deserialization of this class.
    */
   private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      buildTransientObjects();
   }
   
   /**
    * Creates the different transient objects used by this class.
    */
   private void buildTransientObjects() {
      listeners = new EventListenerList();
   }
   
   /**
    * Fires an event to every listening change listeners.
    */
   private void fireChangeEvent() {
      ChangeListener[] array = listeners.getListeners(ChangeListener.class);
      for (ChangeListener listener : array) {
         listener.stateChanged(new ChangeEvent(this));
      }
   }
   
}
