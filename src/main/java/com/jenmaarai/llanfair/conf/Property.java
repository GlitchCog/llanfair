package com.jenmaarai.llanfair.conf;

import com.jenmaarai.llanfair.control.Input;
import com.jenmaarai.llanfair.view.BlockLayout;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Property {
   
   /**
    * Whether this application should always be on top of other applications.
    */
   alwaysOnTop(false, false),
   
   /**
    * Key stroke used to split the run, defaults to the spacebar.
    */
   keySplit(new Input(57), false),
   
   /**
    * Key stroke used to reset the run, defaults to the letter R.
    */
   keyReset(new Input(19), false),
   
   /**
    * Complete layout of the application.
    */
   layout(BlockLayout.defaultLayout(), true),
   
   /**
    * Font for the Title block.
    */
   titleFont(Font.decode("Arial 26"), true),
   
   /**
    * Background color for the Timer block.
    */
   timerColorBackground(null, true),
   
   /**
    * Main font for the Timer block.
    */
   timerMainFont(Font.decode("Arial 36"), true),
   
   /**
    * Screen coordinate of the application top left corner, along the x axis.
    */
   positionX(0, true),
   
   /**
    * Screen coordinate of the application top left corner, along the y axis.
    */
   positionY(0, true);
   
   private static final Logger LOG = LoggerFactory.getLogger(Property.class);
   
   private static final Map<Boolean, Configuration> STORES = new HashMap<>();
   
   private final Object defaultValue;
   private final boolean isTheme;
   
   private Property(Object defaultValue, boolean isTheme) {
      this.defaultValue = defaultValue;
      this.isTheme = isTheme;
   }
   
   /**
    * Initializes both settings and theme properties. This method must be 
    * invoked as soon as possible during the application startup to make sure
    * that properties are accessible.
    */
   public static void initialize() {
      STORES.put(true, new Configuration());
      STORES.put(false, new Configuration());
      
      for (Property property : values()) {
         STORES.get(property.isTheme)
               .define(property.name(), property.defaultValue);
      }
   }
   
   /**
    * Tries to load properties values from a configuration file. 
    * Returns true if the configuration file was found and properly loaded.
    * If the new property file cannot be read, it is discarded and no changes
    * have been made to the values of the properties.
    * 
    * <p>If {@code theme} is true, the configuration file will be loaded as a
    * theme, otherwise it will be loaded as a set of settings.
    */
   public static boolean load(String file, boolean theme) {
      Configuration configuration = STORES.get(theme);
      if (configuration == null) {
         LOG.error("Configuration has not been initialized");
         throw new IllegalStateException("configuration not initialized");
      }
      try {
         configuration.load(Paths.get(file));
         return true;
      } catch (IOException | IllegalArgumentException x) {
         return false;
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
         LOG.error("Configuration has not been initialized");
         throw new IllegalStateException("configuration not initialized");
      }
      try {
         configuration.save();
         return true;
      } catch (IOException | IllegalArgumentException x) {
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
      configuration.set(name(), value);
   }

}
