package com.jenmaarai.llanfair.conf;

import com.jenmaarai.llanfair.control.Input;
import com.jenmaarai.llanfair.view.BlockLayout;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A configuration set able to store properties of different types with
 * persistancy on the file system. Serialization is handled through the native
 * Java serialization interface but the end result is a valid XML file that can
 * be easily parsed or read by the user. While this class supports using 
 * properties whose value class is not explicitly serializable, no control can
 * be provided over their process if that is the case.
 *
 * <p>When first creating a configuration set, the caller must use
 * {@link #define(String, Object)} to register the properties along with
 * their default values and can then use {@link #get(String)} and
 * {@link #set(String, Object)} to manipulate them.
 */
public class Configuration implements Serializable {
   
   private static final Logger LOG 
           = LoggerFactory.getLogger(Configuration.class);

   /**
    * XStream XML parser, customized to improve legibility of output.
    */
   private static final XStream XSTREAM = new XStream();
   static {
      XSTREAM.alias("blockLayout", BlockLayout.class);
      XSTREAM.alias("layoutChunk", BlockLayout.Chunk.class);
      XSTREAM.alias("input", Input.class);
   }

   private Path path;
   private boolean unsaved;
   private SortedMap<String, Object> values;
   private SortedMap<String, Object> defaults;
   
   /**
    * Creates an empty configuration.
    */
   public Configuration() {
      path = null;
      values = new TreeMap<>();
      defaults = new TreeMap<>();
      unsaved = false;
   }

   /**
    * Creates a new configuration with a specific file. At this point the
    * configuration set is empty and no values have been loaded from the
    * specified file.
    */
   public Configuration(String path) {
      this(Paths.get(path));
   }

   /**
    * Creates a new configuration with a specific file and parser. At this
    * point the configuration set is empty and no values have been loaded from
    * the specified file.
    */
   public Configuration(Path path) {
      this();
      setPath(path);
   }

   /**
    * Sets the path of the file used for persistancy of this configuration. The
    * specified path can point to a non-existent file, but it cannot be null.
    * Otherwise, no tests are performed to ensure the validity of the file
    * until read or write operations are started.
    */
   public final void setPath(Path path) {
      if (path == null) {
         LOG.error("Null path specified");
         throw new IllegalArgumentException("null path");
      }
      this.path = path;
   }

   /**
    * Returns the current path of the file backing this configuration. May 
    * return null if the default configuration is used and no file has been
    * defined for serialization.
    */
   public Path getPath() {
      return path;
   }

   /**
    * Returns true if no properties have been defined for this configuration.
    */
   public boolean isEmpty() {
      return values.isEmpty();
   }

   /**
    * Tests if a specific property has been defined in this configuration.
    */
   public boolean has(String key) {
      return values.containsKey(key);
   }

   /**
    * Defines a new property in this configuration. If a property of given key
    * already exists, it is overriden. The specified value is the default value
    * for the property, it will be immediately assigned as the current value.
    *
    * <p>The key of a property cannot be null nor can it be an empty string. It
    * should be a meaningful name, since properties will be written in a
    * human-friendly way within the configuration file.
    */
   public void define(String key, Object value) {
      if (key == null || key.equals("")) {
         LOG.error("Null or empty property key name");
         throw new IllegalArgumentException("null or empty key");
      }
      if (values.containsKey(key)) {
         LOG.warn("Overriding property '{}' with new definition", key);
      }
      values.put(key, value);
      defaults.put(key, value);
   }

   /**
    * Undefines a property in this configuration. If a property of given name
    * exists, it will be removed and successive calls to {@code set()} or
    * {@code get()} on this property will result in an exception.
    */
   public void undefine(String key) {
      if (key == null || key.equals("")) {
         LOG.error("Null or empty property key name");
         throw new IllegalArgumentException("null or empty key");
      }
      values.remove(key);
      defaults.remove(key);
      unsaved = true;
   }

   /**
    * Returns the value of the property of given key. The caller must specify
    * the type of the expected value, but it is his responsability to ensure
    * that said type is appropriate for the given property. This dynamic cast
    * is provided as a mean to facilitate the use of the properties; we assume
    * that the caller knows the type of data that each property stores.
    */
   @SuppressWarnings("unchecked")
   public <T> T get(String key) {
      if (key == null || key.equals("")) {
         LOG.error("Null or empty property key name");
         throw new IllegalArgumentException("null key");
      }
      if (!defaults.containsKey(key)) {
         LOG.error("Property '{}' has not been defined", key);
         throw new IllegalArgumentException("property has not been defined");
      }
      return (T) values.get(key);
   }

   /**
    * Returns a submap of the configuration containing the values of all
    * properties whose key matches the specified regular expression. The caller
    * may specify the expected unique type of the returned value if he knows
    * exactly what will be returned or simply use {@code Object}.
    */
   @SuppressWarnings("unchecked")
   public <T> SortedMap<String, T> getAll(Pattern regex) {
      if (regex == null) {
         LOG.error("Regular expression is null");
         throw new IllegalArgumentException("null regex");
      }
      SortedMap<String, T> subMap = new TreeMap<>();
      values.keySet().stream()
            .filter(k -> regex.matcher(k).matches())
            .forEach(k -> subMap.put(k, (T) values.get(k)));
      return subMap;
   }

   /**
    * Assigns a new value to a property. Using this method will never create a
    * property. The caller must define the property before hand by calling
    * {@link #define(String, Class, Object)}.
    */
   public void set(String key, Object value) {
      if (key == null || key.equals("")) {
         LOG.error("Null or empty property key name");
         throw new IllegalArgumentException("null key");
      }
      if (!defaults.containsKey(key)) {
         LOG.error("Property '{}' has not been defined", key);
         throw new IllegalArgumentException("property has not been defined");
      }
      values.put(key, value);
      unsaved = true;
   }

   /**
    * Reverts all properties to the default values supplied during their
    * definition. This operation cannot be undone.
    */
   public void reset() {
      defaults.keySet().stream().forEach(k -> values.put(k, defaults.get(k)));
      unsaved = true;
   }

   /**
    * Indicates if this configuration has any unsaved changes. This will return
    * true if the current state of the configuration has not yet been saved
    * after calls to methods that modify or may modify the actual values of the 
    * properties.
    */
   public boolean hasUnsavedChanges() {
      return unsaved;
   }

   /**
    * Opens a configuration file and loads its data in memory. This method
    * must be called after the properties have been defined. Properties
    * noted in the configuration file that do not correspond to defined
    * properties will be ignored. If a defined property is not included in the
    * configuration file, its default value will be used and the configuration
    * will be considered unsaved.
    * 
    * <p>This method will replace the current path of the configuration file
    * with the one provided unless an error occured. In this case, the path
    * of the configuration file will remain as it was.
    *
    * @throws IOException if the file cannot be opened or read
    * @throws IllegalArgumentException if the file is not a valid configuration
    */
   public void load(Path path) throws IOException {
      if (path == null) {
         LOG.error("Configuration file path is null");
         throw new IllegalArgumentException("null path");
      }
      try (BufferedReader reader = Files.newBufferedReader(path)) {
         Object input = XSTREAM.fromXML(reader);
         if (!(input instanceof SortedMap)) {
            LOG.error("No configuration map in '{}'", path);
            throw new IllegalArgumentException("no configuration map");
         }
         unsaved = false;
         SortedMap<String, Object> inputMap = (SortedMap) input;
         defaults.keySet().stream()
               .filter(k -> !inputMap.containsKey(k))
               .forEach(k -> { 
                  inputMap.put(k, defaults.get(k));
                  unsaved = true;
               });
         values = inputMap;
         setPath(path);
      } catch (IOException x) {
         LOG.error("'{}' cannot be read ({})", path, x.getMessage());
         throw x;
      } catch (XStreamException x) {
         LOG.error("'{}' is not a valid xml file ({})", path, x.getMessage());
         throw new IllegalArgumentException(x.getMessage());
      }
   }

   /**
    * Writes the configuration in a file at its original path. 
    * This method will avoid writing anything if no changes have been made to
    * the configuration. If this method properly terminates, no unsaved changes
    * remain and the configuration file is in the same state than the current
    * configuration instance.
    * 
    * <p>In the event of an {@code IOException} being raised while overwriting
    * a configuration file, the exact state of the file cannot be ascertained 
    * and it is best assumed that the file has been corrupted.
    *
    * @throws IOException if the stream cannot be opened or written
    * @throws IllegalArgumentException if no path has been defined
    */
   public void save() throws IOException {
      if (unsaved) {
         if (path == null) {
            LOG.error("No path has been defined for the configuration");
            throw new IllegalArgumentException("no defined path");
         }
         try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(XSTREAM.toXML(values));
            unsaved = false;
         } catch (IOException x) {
            LOG.error("'{}' cannot be written ({})", path, x.getMessage());
            throw x;
         } catch (XStreamException x) {
            LOG.error("Cannot marshall to XML ({})", x.getMessage());
            throw new IllegalArgumentException("marshalling failed");
         }
      }
   }

}
