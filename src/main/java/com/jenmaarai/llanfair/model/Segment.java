package com.jenmaarai.llanfair.model;

import com.jenmaarai.sidekick.time.Time;
import javax.swing.Icon;

public class Segment {
   
   /**
    * Current version of this class.
    * This value should be updated whenever changes made to this class break 
    * backward compatibility.
    */
   public static final long serialVersionUid = 20161104L;
   
   private Icon   icon = null;
   private String name = "";
   private Time   time = null;
   private Time   best = null;

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
    * Returns the current saved time of this segment.
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
      if (name == null) {
         throw new IllegalArgumentException("null name");
      }
      this.name = name;
   }

   /**
    * Sets the current saved time of this segment.
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
