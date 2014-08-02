package com.jenmaarai.llanfair;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Llanfair extends JFrame {
    
    public Llanfair() {
        super("Llanfair");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
