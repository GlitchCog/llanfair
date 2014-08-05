package com.jenmaarai.llanfair.config;

import static com.jenmaarai.llanfair.config.SplitConfiguration.Category;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
            = new SplitConfiguration(new File("runs"));  
    
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
        global.load();
    }
    
    /**
     * Writes the current value of each property to their respective file.
     */
    public static void save() {
        global.save();
        local.save();
    }
    
    public static List<String> getUnsaved() {
        List<String> list = new ArrayList<>();
        for (Category category : global.getUnsavedCategories()) {
            list.add("global/" + category);
        }
        for (Category category : local.getUnsavedCategories()) {
            list.add("local/" + category);
        }
        return list;
    }
    
    /**
     * Registers a new listener with the settings. Whenever the value of any
     * setting is modified, every listeners will be notified. The change event
     * will contain the setting enum constant that has changed.
     * 
     * @param cl  the listener to register
     */
    public static void addChangeListener(ChangeListener cl) {
        if (cl == null) {
            throw new IllegalArgumentException("listener is null");
        }
        listeners.add(ChangeListener.class, cl);
    }
    
    /**
     * Removes the given listener from the list of listeners.
     * 
     * @param cl  the listener to remove
     */
    public static void removeChangeListener(ChangeListener cl) {
        if (cl == null) {
            throw new IllegalArgumentException("listener is null");
        }
        listeners.remove(ChangeListener.class, cl);
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
    
    /**
     * Sets the value of this property. The caller must specify in which 
     * configuration the value will be set. 
     * 
     * @param value    the new value of this property
     * @param locally  true to set the value in the local configuration
     */
    public void set(Object value, boolean locally) {
        if (locally) {
            local.define(category, type, name(), defaultValue);
            local.set(name(), value);
        } else {
            global.set(name(), value);
        }
        fireChangeEvent();
    }
    
    // TODO: remove() to undefine a property from the local configuration
    
    /**
     * Fires an event to all listeners that this property has changed.
     */
    private void fireChangeEvent() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener l : listeners.getListeners(ChangeListener.class)) {
            l.stateChanged(event);
        }
    }

}