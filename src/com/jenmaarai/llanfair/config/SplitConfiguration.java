package com.jenmaarai.llanfair.config;

import com.jenmaarai.sidekick.config.Configuration;
import com.jenmaarai.sidekick.error.ParseException;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * A twist on the regular configuration to split it in multiple files. 
 * This configuration splits its properties on as many files as there are
 * enumerate values in {@code Category}.
 */
class SplitConfiguration {
    
    private EnumMap<Category, Configuration> configurations;
    
    /**
     * Creates a new configuration object in the given directory.
     * If the directory does not exist, it will be created. The directory will
     * then be populated by as many files as there are configuration categories.
     * 
     * @param root  root folder of this configuration
     */
    public SplitConfiguration(File root) {
        if (root == null) {
            throw new IllegalArgumentException("root is null");
        }
        if (!root.isDirectory()) {
            throw new IllegalArgumentException("root must be a directory");
        }
        if (!root.canRead() || !root.canWrite()) {
            throw new IllegalArgumentException("read/write denied to " + root);
        }
        if (!root.exists()) {
            if (!root.mkdir()) {
                throw new IllegalArgumentException("failed to mkdir " + root);
            }
        }
        configurations = new EnumMap<>(Category.class);
        for (Category category : Category.values()) {
            configurations.put(category, 
                    new Configuration(new File(root, category.path)));
        }
    }
    
    /**
     * Defines a new property in this configuration.
     * The property must specify its category, key, the type of values to store
     * and a default value.
     * 
     * @param category      category of the property
     * @param type          type of the values stored in this property
     * @param key           unique name of this property
     * @param defaultValue  default value of this property
     */
    public void define(Category category, Class<?> type, String key, 
            Object defaultValue) {
        if (category == null) {
            throw new IllegalArgumentException("category is null");
        }
        if (existsElsewhere(category, key)) {
            throw new IllegalArgumentException("duplicate property " + key);
        }
        configurations.get(category).define(key, type, defaultValue);
    }
    
    /**
     * Indicates if a property of given key exists in this configuration.
     * Such a property exists if at least of the sub configurations has a 
     * property with that name.
     * 
     * @param  key  the unique name of the property to test
     * @return true if any sub configuration has a property with that name
     */
    public boolean has(String key) {
        boolean has = false;
        for (Configuration configuration : configurations.values()) {
            has &= configuration.has(key);
        }
        return has;
    }
    
    /**
     * Retrieves the value of the property of given key. This method is 
     * parametrized to facilitate the use of the properties, but it is the 
     * caller responsability to ensure that the parametrized type is
     * appropriate for this property.
     * 
     * @param  <T>  the type of the expected value
     * @return the value of this property in this configuration
     */
    public <T> T get(String key) {
        for (Configuration configuration : configurations.values()) {
            if (configuration.has(key)) {
                return configuration.get(key);
            }
        }
        throw new IllegalArgumentException("property " + key + " not found");
    }
    
    /**
     * Loads the content of every sub configurations from their respective file.
     * This method must be call after every property have been defined. Any 
     * property read from a configuration file that has not been defined will be
     * discarded.
     * 
     * @throws IOException if a read operation failed
     * @throws ParseException if a sub configuration is malformed
     */
    public void load() throws IOException, ParseException {
        for (Configuration configuration : configurations.values()) {
            configuration.load();
        }
    }
    
    /**
     * Indicates if a given key exists in any sub configuration except the
     * specified one. This method allows us to ensure that a given key is unique
     * across all sub configurations while still preserving the flexibility of
     * the {@code define()} method.
     * 
     * @param  category  the category to exclude from the search
     * @param  key       the unique name of the property to test
     * @return true if any sub configurations except for the specified one has
     *   a property with the given key
     */
    private boolean existsElsewhere(Category category, String key) {
        boolean has = false;
        for (Category cat : Category.values()) {
            if (cat != category) {
                has &= configurations.get(cat).has(key);
            }
        }
        return has;
    }
    
    /**
     * Enumerates all sub configuration categories.
     */
    public static enum Category {
        
        SETTING("settings.cfg"),
        THEME("theme.cfg");
        
        private String path;
        
        private Category(String path) {
            this.path = path;
        }
        
    }

}
