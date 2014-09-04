package com.jenmaarai.llanfair;

import com.jenmaarai.llanfair.config.Settings;
import com.jenmaarai.llanfair.model.Run;
import com.jenmaarai.llanfair.view.BlockView;
import com.jenmaarai.llanfair.view.Watch;
import com.jenmaarai.sidekick.config.SimpleLoggerConfigurator;
import com.jenmaarai.sidekick.locale.Localizer;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class Llanfair extends JFrame implements NativeKeyListener {
    
    private static final String PKG = "com.jenmaarai";
    private static final Logger LOG = Logger.getLogger(PKG);
    
    public Llanfair() {
        super("Llanfair");
        try {
            SimpleLoggerConfigurator.setup(PKG);
        } catch (IOException e) {
            Localizer.error(getClass(), "loggerFailure");
        }        
        if (!Settings.initialize()) {
            System.exit(-1);
        }
        setNativeHook();
        setWindowBehavior();
        
        add(new BlockView(new Run()));
    }
    
    /**
     * Main entry point of the application.
     * 
     * @param args  array of command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Llanfair instance = new Llanfair();
                instance.display();
            }
        });
    }
    
    /**
     * Resolves the main frame on screen.
     */
    public void display() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        final NativeKeyEvent event = e;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println(event.paramString());
            }
        });
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {}

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}
    
    /**
     * Initialize the JNativeHook library to register out-of-focus input.
     */
    private void setNativeHook() {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.getInstance().addNativeKeyListener(this);
        } catch (NativeHookException ex) {
            Localizer.error(getClass(), "nativeHookFailure", ex.getMessage());
            LOG.log(Level.SEVERE, "NativeHookException: {0}", ex.getMessage());
            System.exit(-1);
        }
    }
    
    /**
     * Initializes the behavior of this frame.
     * When the user tries to close the main frame, we must check if any
     * changes have been made to the application and ask the user if he wants
     * to save them or not.
     */
    private void setWindowBehavior() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                List<String> unsaved = Settings.getUnsaved();
                if (!unsaved.isEmpty()) {
                    int option = Localizer.confirm(
                            Llanfair.this, "unsavedSettings", unsaved);
                    
                    if (option == JOptionPane.YES_OPTION) {
                        Settings.save();   
                        dispose();
                    } else if (option == JOptionPane.NO_OPTION) {
                        dispose();
                    }
                } else {
                    dispose();
                }
            }
        });
    }

    /**
     * Release all resources and terminate.
     */
    @Override
    public void dispose() {
        super.dispose();
        GlobalScreen.unregisterNativeHook();
    }

}
