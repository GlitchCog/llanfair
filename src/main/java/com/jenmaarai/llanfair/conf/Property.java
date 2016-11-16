package com.jenmaarai.llanfair.conf;

import com.jenmaarai.llanfair.control.Input;
import com.jenmaarai.llanfair.view.BlockLayout;
import com.jenmaarai.sidekick.config.Configuration;
import com.jenmaarai.sidekick.error.LineParserException;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Property {
   
   /**
    * Whether this application should always be on top of other applications.
    */
   alwaysOnTop(Boolean.class, false, false),
   
   /**
    * Key stroke used to split the run, defaults to the spacebar.
    */
   keySplit(Input.class, new Input(57), false),
   
   /**
    * Key stroke used to reset the run, defaults to the letter R.
    */
   keyReset(Input.class, new Input(19), false),
   
   /**
    * Complete layout of the application.
    */
   layout(BlockLayout.class, BlockLayout.defaultLayout(), true),
   
   /**
    * Background color for the Timer block.
    */
   timerColorBackground(Color.class, null, true),
   
   /**
    * Main font for the Timer block.
    */
   timerMainFont(Font.class, Font.decode("Arial 26"), true),
   
   /**
    * Screen coordinate of the application top left corner, along the x axis.
    */
   positionX(Integer.class, 0, true),
   
   /**
    * Screen coordinate of the application top left corner, along the y axis.
    */
   positionY(Integer.class, 0, true);
   
   private static final Logger LOG = LoggerFactory.getLogger(Property.class);
   
   private static final Map<Boolean, Configuration> STORES = new HashMap<>();
   
   private final Class<?> type;
   private final Object defaultValue;
   private final boolean isTheme;
   
   private Property(Class<?> type, Object defaultValue, boolean isTheme) {
      this.type = type;
      this.defaultValue = defaultValue;
      this.isTheme = isTheme;
   }
   
   /**
    * Tries to load properties values from a configuration file. 
    * Returns true if the configuration file was found and properly loaded.
    * If the new property file cannot be read, it is discarded and no changes
    * have been made to the values of the properties.
    * 
    * <p>However, if no values have been loaded yet, the default 
    * developer-provided configuration will be kept to ensure the application 
    * has everything it needs. 
    * 
    * <p>If theme is true, the configuration file will be loaded as a theme,
    * otherwise it will be loaded as a set of settings.
    */
   public static boolean load(String file, boolean theme) {
      Configuration configuration = new Configuration(file, new Parser());
      for (Property p : values()) {
         if (p.isTheme == theme) {
            configuration.define(p.name(), p.type, p.defaultValue);
         }
      }
      try {
         if (configuration.load()) {
            STORES.put(theme, configuration);
            return true;
         } else {
            LOG.warn("Cannot find '{}' reverting to previous state", file);
            return false;
         }
      } catch (IOException x) {
         LOG.error("Cannot read '{}' cause: {}", file, x.getMessage());
         return false;
      } catch (LineParserException x) {
         LOG.error("Cannot parse '{}' cause: {}", file, x.toString());
         return false;
      } finally {
         // Ensure default configuration exists
         if (!STORES.containsKey(theme)) {
            STORES.put(theme, configuration);
         }
      }
   }
   
   /**
    * Writes the properties in the appropriate configuration file. 
    * Returns false if an error is encountered during this process. In this 
    * case, the configuration file exact state is unknown and the caller must
    * assume that the configuration is not saved.
    * 
    * <p>Saves the current theme if theme is true, or the current set of 
    * settings otherwise.
    */
   public static boolean save(boolean theme) {
      Configuration configuration = STORES.get(theme);
      if (configuration == null) {
         throw new IllegalStateException("configuration not initialized");
      }
      try {
         configuration.save();
         return true;
      } catch (IOException x) {
         File file = configuration.getPath();
         LOG.error("Cannot write '{}' cause: {}", file, x.getMessage());
         return false;
      }
   }
   
   /**
    * Retrieves the current value of this property. 
    * This method will cast the property value to the specified type without 
    * checking the validity of such a cast. This dynamic cast is only provided 
    * to make it easier for the caller to handle the settings and we assume 
    * that the caller knows the expected return type.
    */
   public <T> T get() {
      Configuration configuration = STORES.get(isTheme);
      if (configuration == null) {
         throw new IllegalStateException("configuration not initialized");
      }
      return configuration.get(name());
   }
   
   /**
    * Sets the current value of this property. 
    * Throws an exception if the new value is not of an appropriate type for 
    * this property.
    */
   public void set(Object value) {
      Configuration configuration = STORES.get(isTheme);
      if (configuration == null) {
         throw new IllegalArgumentException("configuration not initialized");
      }
      if (!type.isInstance(value)) {
         throw new IllegalArgumentException("value is of illegal type");
      }
      configuration.set(name(), value);
   }

}
