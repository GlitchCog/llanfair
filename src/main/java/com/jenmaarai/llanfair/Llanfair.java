package com.jenmaarai.llanfair;

import com.jenmaarai.llanfair.conf.Properties;
import com.jenmaarai.sidekick.locale.Localizer;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.SwingDispatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Llanfair extends JFrame {
   
   private static final Logger LOG = LoggerFactory.getLogger(Llanfair.class);
   
   /**
    * Returned if the application cannot create the default directories.
    */
   public static final int ERROR_DIRECTORY_CREATE = 1;
   
   /**
    * Returned if the native hook cannot be registered.
    */
   public static final int ERROR_NATIVE_HOOK = 2;
   
   /**
    * Application building begins when a new Llanfair instance is invoked.
    */
   private Llanfair() {
      super("Llanfair");
      LOG.info("...Application started");
      Localizer.initialize("lang", false);
      
      configure();
      setNativeHook();
      setShutdownHook();
      resolve();
   }
   
   /**
    * Main entry point of the application.
    * Takes care of command line arguments and launches the application itself.
    */
   public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> new Llanfair());
   }
   
   /**
    * Configures the application.
    * Creates default directory structure, initializes properties and loads
    * default values.
    */
   private void configure() {
      try {
         Files.createDirectory(Paths.get("settings"));
         Files.createDirectory(Paths.get("themes"));
      } catch (FileAlreadyExistsException x) {
         LOG.info("Default directories already created");
      } catch (IOException | SecurityException x) {
         LOG.error(
                 "Cannot create default directories in '{}', cause: {}:{}", 
                  Paths.get("."), x.getClass(), x.getMessage());
         Localizer.error(this, "errorDirectoryCreate", x.getMessage());
         System.exit(ERROR_DIRECTORY_CREATE);
      }
      Properties.load("settings/default.lls", false);
      Properties.load("themes/default.llt", true);
   }
   
   /**
    * Registers native hook for global hotkeys.
    */
   private void setNativeHook() {
      Level logLevel = Level.WARNING;
      java.util.logging.Logger.getLogger("org.jnativehook").setLevel(logLevel);
      try {
         GlobalScreen.registerNativeHook();
         GlobalScreen.setEventDispatcher(new SwingDispatchService());
      } catch (NativeHookException x) {
         LOG.error("Cannot register native hook, cause: {}", x.getMessage());
         Localizer.error(this, "errorNativeHook", x.getMessage());
         System.exit(ERROR_NATIVE_HOOK);
      }
   }
   
   /**
    * Defines the behavior of the application when the user tries to exit.
    */
   private void setShutdownHook() {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      addWindowListener(new WindowAdapter() {
         @Override public void windowClosed(WindowEvent event) {
            try {
               GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException x) {
               LOG.warn("Failed to clean native hook ({})", x.getMessage());
               try {
                  GlobalScreen.unregisterNativeHook();
               } catch (NativeHookException y) {
                  LOG.warn("Failed to clean native hook ({})", x.getMessage());
               }
            }
            Properties.save(false);
            Properties.save(true);
            LOG.info("...Application closed");
         }
      });
   }
   
   /**
    * Resolves the application on screen.
    * Invoked when the application is initialized and ready to be used.
    */
   private void resolve() {
      pack();
      setLocationRelativeTo(null);
      setVisible(true);
   }
}
