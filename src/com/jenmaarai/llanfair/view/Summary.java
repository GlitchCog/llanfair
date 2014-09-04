package com.jenmaarai.llanfair.view;

import com.jenmaarai.llanfair.model.Run;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Summary extends Block {
    
    private Run run;
    
    public Summary() {
        add(new JLabel("Summary"));
    }
    
    @Override
    public final void setRun(Run run) {
        if (run == null) {
            throw new IllegalArgumentException("run is null");
        }
        this.run = run;
    }

}
