package com.jenmaarai.llanfair.control;

import com.jenmaarai.sidekick.error.ParserException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Input {
   
   private static final Logger LOG = LoggerFactory.getLogger(Input.class);
   
   private int keyCode;
   private int keyMask = 0;
   
   public Input(int keyCode) {
      this.keyCode = keyCode;
   }
   
   public Input(int keyCode, int... keyModifiers) {
      this.keyCode = keyCode;
      for (int modifier : keyModifiers) {
         keyMask |= modifier;
      }
   }

   /**
    * Returns the key code associated with this input.
    */
   public int getKeyCode() {
      return keyCode;
   }

   /**
    * Returns the key mask associated with this input.
    * The key mask is a bit mask of the different modifiers, like CTRL or ALT.
    */
   public int getKeyMask() {
      return keyMask;
   }
   
   /**
    * Returns true if the supplied event corresponds to this input.
    * An event is equals to this input if the key code is the same and the
    * same key modifiers are also pressed.
    */
   public boolean equals(NativeKeyEvent event) {
      if (event == null) {
         return false;
      }
      return (event.getKeyCode()   == keyCode) 
          && (event.getModifiers() == keyMask);
   }
   
   /**
    * Parses and returns an input from its textual representation.
    * Returns an undefined key input even given a null string.
    * 
    * @throws ParserException  if the string is malformed
    */
   public static Input parse(String serialized) throws ParserException {
      if (serialized == null) {
         return new Input(NativeKeyEvent.VC_UNDEFINED);
      }
      String[] tokens = serialized.split("\\+");
      if (tokens.length < 1 || tokens.length > 2) {
         LOG.error("Token count mismatch in string '{}'", serialized);
         throw new ParserException("invalid input");
      }
      try {
         Input object = new Input(Integer.parseInt(tokens[tokens.length - 1]));
         if (tokens.length == 2) {
            object.keyMask = Integer.parseInt(tokens[0]);
         }
         return object;
      } catch (NumberFormatException x) {
         LOG.error("Key codes are not number in string '{}'", serialized);
         throw new ParserException("invalid input");
      }
   }

   /**
    * Returns the textual representation of this input.
    * The returned string consists of the eventual key mask and the key code
    * of this input separated by a plus sign.
    */
   @Override public String toString() {
      return ((keyMask == 0) ? "" : keyMask + "+") + keyCode;
   }
   
}
