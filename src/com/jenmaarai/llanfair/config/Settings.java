package com.jenmaarai.llanfair.config;

import static com.jenmaarai.llanfair.config.SplitConfiguration.Category;
import java.io.File;
import java.util.Locale;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * The configuration of Llanfair. Provides two levels of configuration, one
 * global and one run-dependent (local). Each property can either be stored in
 * the settings part of the configuration or the theme.<p>
 * 
 * Before making use of any setting, {@code initialize()} must have been called
 * once, preferably as early as possible by the main class.
 */
public enum Settings {
        
    LOCALE(Category.SETTING, Locale.class, Locale.ENGLISH);
    
    private static SplitConfiguration global 
            = new SplitConfiguration(new File("."));
    
    private static SplitConfiguration local
            = new SplitConfiguration(new File("."));  
    
    private static EventListenerList listeners = new EventListenerList();
    
    private Category category;
    private Class<?> type;
    private Object defaultValue;

    private Settings(Category category, Class<?> type, Object defaultValue) {
        this.category = category;
        this.type = type;
        this.defaultValue = defaultValue;
    }
    
    /**
     * Initialize the settings by defining every properties, and retrieving
     * their values from the files on disk. At this stage, only the global
     * configuration will be loaded.
     */
    public static void initialize() {
        for (Settings set : values()) {
            global.define(set.category, set.type, set.name(), set.defaultValue);
        }
    }
    
    public static void addPropertyChangeListener(ChangeListener pcl) {
        if (pcl == null) {
            throw new IllegalArgumentException("listener is null");
        }
        listeners.add(ChangeListener.class, pcl);
    }
    
    /**
     * Retrieves the value of this property. This method will first look into
     * the local configuration. If this property has been defined in the local
     * configuration, it will return its value from there. If not, it will 
     * return its value from the global configuration.<p>
     * 
     * This method is parametrized to facilitate the use of the properties, but
     * it is the caller responsability to ensure that the parametrized type is
     * appropriate for this property.
     * 
     * @param  <T>  the type of the expected value
     * @return the value of this property as configured in the local 
     *   configuration or failing that, in the global configuration
     */
    public <T> T get() {
        if (local.has(name())) {
            return local.get(name());
        } else {
            return global.get(name());
        }
    }
    
    public void set(Object value, boolean locally) {
        if (locally) {
            local.set(name(), value);
        } else {
            global.set(name(), value);
        }
    }
    
    private void fireChangeEvent() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener l : listeners.getListeners(ChangeListener.class)) {
            l.stateChanged(event);
        }
    }

}