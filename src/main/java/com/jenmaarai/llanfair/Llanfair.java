package com.jenmaarai.llanfair;

import com.jenmaarai.llanfair.conf.Properties;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Llanfair extends JFrame {
   
   private static final Logger LOG = LoggerFactory.getLogger(Llanfair.class);
   
   /**
    * Application building begins when a new Llanfair instance is invoked.
    */
   private Llanfair() {
      super("Llanfair");
      LOG.info("...Application started");
      Properties.load("default.cfg", false);
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
    * Defines the behavior of the application when the user tries to exit.
    */
   private void setShutdownHook() {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      addWindowListener(new WindowAdapter() {
         @Override public void windowClosed(WindowEvent event) {
            LOG.info("...Application closed");
            Properties.save(false);
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
