package com.jenmaarai.llanfair.model;

import com.jenmaarai.sidekick.time.Time;
import java.io.Serializable;
import javax.swing.Icon;

public class Segment implements Serializable {
   
   private Icon icon = null;
   private String name = null;
   private Time time = null;
   private Time best = null;

   /**
    * Returns the icon associated to this segment, can be null.
    */
   public Icon getIcon() {
      return icon;
   }

   /**
    * Returns the name of this segment, cannot be null but can be empty.
    */
   public String getName() {
      return name;
   }

   /**
    * Returns the split time of this segment.
    */
   public Time getTime() {
      return time;
   }
   
   /**
    * Returns the best saved segment time of this segment.
    */
   public Time getBest() {
      return best;
   }

   /**
    * Sets the icon associated to this segment, can be null.
    */
   public void setIcon(Icon icon) {
      this.icon = icon;
   }

   /**
    * Sets the name of this segment, cannot be null but can be empty.
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Sets the split time of this segment.
    */
   public void setTime(Time time) {
      this.time = time;
   }
   
   /**
    * Sets the best saved segment time of this segment.
    */
   public void setBest(Time best) {
      this.best = best;
   }
   
}
