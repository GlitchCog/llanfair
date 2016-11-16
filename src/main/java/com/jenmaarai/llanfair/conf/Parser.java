package com.jenmaarai.llanfair.conf;

import com.jenmaarai.llanfair.control.Input;
import com.jenmaarai.llanfair.view.BlockLayout;
import com.jenmaarai.sidekick.error.ParserException;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parser extends com.jenmaarai.sidekick.config.Parser {
   
   private static final Logger LOG = LoggerFactory.getLogger(Parser.class);
   
   private static final Map<Integer, String> FONT_STYLES = new HashMap<>();
   static {
      FONT_STYLES.put(Font.PLAIN, " ");
      FONT_STYLES.put(Font.BOLD, " BOLD ");
      FONT_STYLES.put(Font.ITALIC, " ITALIC ");
      FONT_STYLES.put(Font.BOLD + Font.ITALIC, " BOLD ITALIC ");
   }

   /**
    * Converts properties values into textual representations.
    */
   @Override public String toString(Class<?> type, String key, Object value) {
      if (type == Color.class) {
         Color color = (Color) value;
         if (color == null) {
            return "";
         } else {
            return String.format(
                    "%d,%d,%d,%d", color.getRed(), color.getGreen(), 
                    color.getBlue(), color.getAlpha());
         }
      } else if (type == Font.class) {
         Font font = (Font) value;
         return String.format(
                 "%s%s%d", font.getName(), 
                 FONT_STYLES.get(font.getStyle()), font.getSize());
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
      } else if (type == Font.class) {
         Font font = Font.decode(text); 
         if (font == null) {
            LOG.error("Invalid font format '{}'", text);
            throw new ParserException("invalid font");
         }
         return font;
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
