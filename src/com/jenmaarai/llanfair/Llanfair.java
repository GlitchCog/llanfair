package com.jenmaarai.llanfair;

import com.jenmaarai.llanfair.config.Settings;
import com.jenmaarai.sidekick.config.SimpleLoggerConfigurator;
import com.jenmaarai.sidekick.locale.Localizer;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Llanfair extends JFrame {
    
    private static final String PKG = Llanfair.class.getPackage().getName();
    
    private static final Logger LOG = Logger.getLogger(PKG);
    
    
    public Llanfair() {
        super("Llanfair");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        try {
            SimpleLoggerConfigurator.setup(PKG);
        } catch (IOException e) {
            Localizer.error(getClass(), "loggerFailure");
        }        
        Settings.initialize();
        System.out.println("" + Settings.LOCALE.<Locale>get());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Llanfair instance = new Llanfair();
                instance.display();
            }
        });
    }
    
    public void display() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
