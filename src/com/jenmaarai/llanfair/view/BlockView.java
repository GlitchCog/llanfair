package com.jenmaarai.llanfair.view;

import com.jenmaarai.llanfair.config.Settings;
import com.jenmaarai.llanfair.model.Run;
import com.jenmaarai.sidekick.swing.GBC;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

public class BlockView extends JPanel {

    public static final Map<Point, Class<?>> DEFAULT = new HashMap<>();
    static {
        DEFAULT.put(new Point(0, 0), Summary.class);
        DEFAULT.put(new Point(0, 1), Watch.class);
    }
    
    private static final Logger LOG = Logger.getLogger(
            BlockView.class.getName());
    
    private Run run;
    
    public BlockView(Run run) {
        super(new GridBagLayout());
        Map<Point, Class<?>> map = Settings.blockPlacement.get();
        for (Point coords : map.keySet()) {
            try {
                Block block = (Block) map.get(coords).newInstance();
                add(block, GBC.g(coords.x, coords.y));
                block.setRun(run);
            } catch (InstantiationException | IllegalAccessException ex) {
                // TODO: error()
                LOG.log(Level.SEVERE, "cannot instantiate block {0}", 
                        ex.getMessage());
            }
        }
        setRun(run);
    }
    
    public final void setRun(Run run) {
        if (run == null) {
            throw new IllegalArgumentException("run is null");
        }
        this.run = run;
    }
    
}
