package com.jenmaarai.llanfair;

import com.jenmaarai.llanfair.conf.Property;
import com.jenmaarai.llanfair.control.Input;
import com.jenmaarai.llanfair.control.Splitter;
import com.jenmaarai.llanfair.model.Run;
import com.jenmaarai.llanfair.view.BlockView;
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
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
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
   
   private Splitter splitter = new Splitter();
   
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
      createView();
      resolve();
      
      splitter.setRun(Run.readFile(Paths.get("alttp.xml")));
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
      Property.load("settings/default.lls", false);
      Property.load("themes/default.llt", true);
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
      
      GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
         @Override
         public void nativeKeyPressed(NativeKeyEvent event) {
            Splitter.State state = splitter.getState();
            if (Property.keySplit.<Input>get().equals(event)) {
               if (state == Splitter.State.READY) {
                  splitter.start();
               } else if (state == Splitter.State.RUNNING) {
                  splitter.split();
               } 
            } else if (Property.keyReset.<Input>get().equals(event)) {
               if (state != Splitter.State.READY) {
                  splitter.reset(false);
               }
            }
         }
         @Override public void nativeKeyReleased(NativeKeyEvent e) {}
         @Override public void nativeKeyTyped(NativeKeyEvent e) {}
      });
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
            splitter.getRun().writeFile(Paths.get("alttp.xml"));
            Property.save(false);
            Property.save(true);
            LOG.info("...Application closed");
         }
      });
   }
   
   /**
    * Creates and places the UI element of this application.
    */
   private void createView() {
      System.setProperty("sidekick.aatext", "true");
      BlockView blockView = new BlockView(splitter);
      splitter.addSplitListener(blockView);
      getContentPane().add(blockView);
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
