package com.jenmaarai.llanfair.view;

import com.jenmaarai.llanfair.config.Settings;
import com.jenmaarai.llanfair.model.Run;
import com.jenmaarai.sidekick.time.Time;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Main component displaying the timer. The watch is also able to display a
 * secondary segment timer, and information relative to the current segment.
 */
public class Watch extends JPanel implements ActionListener {
    
    private Timer timer;
    private Run run;
    
    private JLabel mainTimer;
    
    public Watch() {
        timer = new Timer(10, this);
        run = new Run();
        mainTimer = new JLabel(Time.ZERO.toString());
        timer.start();
        add(mainTimer);
        setBackground(Settings.colorBackground.<Color>get());
    }
    
    /**
     * Stops the timer thread. This method must be called before exiting the
     * application to properly release all resources.
     */
    public void stop() {
        timer.stop();
    }

    /**
     * Callback invoked by the updater timer thread. Every hundredth of a 
     * second we update the values of the timers and change their color
     * accordingly.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Time elapsed = run.getElapsed();
        mainTimer.setText(elapsed.toString());
    }


}
