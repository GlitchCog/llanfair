package com.jenmaarai.llanfair.model;

import com.jenmaarai.sidekick.time.Time;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Run {
   
   private static final Logger LOG = LoggerFactory.getLogger(Run.class);
   
   private String game;
   private String category;
   private String platform;
   private String region;
   private boolean emulator;
   
   // Split server API
   private String abbreviation;
   private String gameId;
   private String categoryId;
   private String platformId;
   private String regionId;
   
   private List<Segment> segments = new ArrayList<>();
   
   
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
   public boolean isEmulator() {
      return emulator;
   }
   
   /**
    * Returns the current saved time of this run.
    * The returned time will be based on real time (as opposed to game time).
    */
   public Time getRealTime() {
      return getRealTime(segments.size() - 1);
   }
   
   /**
    * Returns the current saved time of this run until a specific segment.
    * In other words, returns the sum of all saved segment times in the run
    * recorded personal best, up until the specified segment, included. Returns 
    * a time of zero (not null) if the segment id is negative. The returned 
    * time will be based on real time (as opposed to game time).
    */
   public Time getRealTime(int segmentId) {
      if (segmentId >= segments.size()) {
         throw new IllegalArgumentException("invalid segment id " + segmentId);
      }
      Time realTime = new Time(0L);
      for (int i = 0; i <= segmentId; i++) {
         realTime.add(segments.get(i).getTime());
      }
      return realTime;
   }
   
   /**
    * Returns the sum of best segment times for this run.
    * The returned time will be based on real time (as opposed to game time).
    */
   public Time getRealTimeSumOfBest() {
      Time realTime = new Time(0L);
      for (Segment segment : segments) {
         realTime.add(segment.getBest());
      }
      return realTime;
   }

}
