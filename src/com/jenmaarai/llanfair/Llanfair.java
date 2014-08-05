package com.jenmaarai.llanfair;

import com.jenmaarai.llanfair.config.Settings;
import com.jenmaarai.sidekick.config.SimpleLoggerConfigurator;
import com.jenmaarai.sidekick.locale.Localizer;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Llanfair extends JFrame {
    
    private static final String PKG = Llanfair.class.getPackage().getName();
    
    private static final Logger LOG = Logger.getLogger(PKG);
    
    
    public Llanfair() {
        super("Llanfair");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        try {
            SimpleLoggerConfigurator.setup(PKG);
        } catch (IOException e) {
            Localizer.error(getClass(), "loggerFailure");
        }        
        Settings.initialize();
        System.out.println("" + Settings.LOCALE.<Locale>get());
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                List<String> unsaved = Settings.getUnsaved();
                if (!unsaved.isEmpty()) {
                    // TODO: externalize to Sidekick Localizer
                    int option = JOptionPane.showConfirmDialog(Llanfair.this, 
                            "<html>" + Localizer.get(Llanfair.this, 
                                "unsavedSettings", "" + unsaved) + "</html>");
                    
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
