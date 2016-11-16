package com.jenmaarai.llanfair.conf;

import com.jenmaarai.llanfair.control.Input;
import com.jenmaarai.llanfair.view.BlockLayout;
import com.jenmaarai.sidekick.error.ParserException;
import java.awt.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parser extends com.jenmaarai.sidekick.config.Parser {
   
   private static final Logger LOG = LoggerFactory.getLogger(Parser.class);

   /**
    * Converts properties values into textual representations.
    */
   @Override public String toString(Class<?> type, String key, Object value) {
      if (type == Color.class) {
         Color color = (Color) value;
         return String.format(
                 "%d,%d,%d,%d", color.getRed(), color.getGreen(), 
                 color.getBlue(), color.getAlpha());
      } else {
         return super.toString(type, key, value);
      }
   }

   /**
    * Converts properties textual representation into actual values.
    */
   @Override public Object toValue(Class<?> type, String key, String text) 
                                                   throws ParserException {
      if (type == BlockLayout.class) {
         return BlockLayout.parse(text);
      } else if (type == Input.class) {
         return Input.parse(text);
      } else if (type == Color.class) {
         try {
            String[] tokens = text.split(",");
            return new Color(
                    Integer.parseInt(tokens[0]), 
                    Integer.parseInt(tokens[1]), 
                    Integer.parseInt(tokens[2]), 
                    Integer.parseInt(tokens[3]));
         } catch (NumberFormatException | ArrayIndexOutOfBoundsException x) {
            LOG.error("Unrecognized color code '{}'", text);
            throw new ParserException("invalid color");
         } catch (NullPointerException x) {
            return null;
         }
      } else {
         return super.toValue(type, key, text);
      }
   }
   
}
