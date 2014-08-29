package com.jenmaarai.llanfair.view;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;

public class BlockView extends JPanel {

    public static final Map<Point, Class<?>> DEFAULT = new HashMap<>();
    static {
        DEFAULT.put(new Point(0, 0), Watch.class);
    }
    
}
