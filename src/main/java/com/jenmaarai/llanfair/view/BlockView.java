package com.jenmaarai.llanfair.view;

import com.jenmaarai.llanfair.conf.Property;
import com.jenmaarai.llanfair.conf.PropertyListener;
import com.jenmaarai.llanfair.control.SplitListener;
import com.jenmaarai.llanfair.control.Splitter;
import com.jenmaarai.sidekick.swing.GBC;
import java.awt.Container;
import java.awt.GridBagLayout;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockView extends    JPanel 
                       implements SplitListener, PropertyListener {
   
   private static final Logger LOG = LoggerFactory.getLogger(BlockView.class);
   
   private final Splitter splitter;
   
   private final List<Block> instances = new ArrayList<>();
   private final BlockLayout blockLayout = Property.layout.get();
   
   public BlockView(Splitter splitter) {
      this.splitter = splitter;
      initialize();
   }
   
   /**
    * Creates each layout as defined by the block layout.
    * The blocks pertaining to the main layout will be placed within this panel
    * and each dock layout will be generated within a new accessory frame.
    */
   private void initialize() {
      createLayout(blockLayout.getMainLayout(), this);
      // TODO: This part is temporary, maybe a better way to impl. docks ?
      // Maybe something with non-modal dialog boxes
      for (List<BlockLayout.Chunk> layout : blockLayout.getDockLayouts()) {
         JFrame frame = new JFrame();
         createLayout(layout, frame.getContentPane());
         frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         frame.setLocationRelativeTo(this);
         frame.pack();
         frame.setVisible(true);
      }
   }
   
   /**
    * Fills a container with a specific layout.
    * Each created Block will be instantiated and added to the container at
    * the coordinates specified by the layout chunk. Furthermore, each Block
    * will be saved in the list of created instances for future reference.
    */
   private void createLayout(List<BlockLayout.Chunk> layout, Container panel) {
      panel.setLayout(new GridBagLayout());
      for (BlockLayout.Chunk chunk : layout) {
         try {
            Constructor<? extends Block> ctor 
                    = chunk.getBlockClass().getConstructor(Splitter.class);
            
            Block block = ctor.newInstance(splitter);
            panel.add(block, GBC.grid(chunk.getX(), chunk.getY())
                              .fill(GBC.BOTH).weight(1.0f, 1.0f));
            instances.add(block);
            
         } catch ( NoSuchMethodException    | SecurityException 
                 | InstantiationException   | IllegalAccessException 
                 | IllegalArgumentException | InvocationTargetException x) {
            LOG.error(
                    "Error instantiating block {}, {}:{}", 
                    chunk.getBlockClass(), x.getClass(), x.getMessage());
            throw new IllegalArgumentException("bad layout");
         }
      }
   }

   @Override public void onStart() {
      instances.stream().forEach((block) -> block.onStart());
   }
   
   @Override public void onSplit() {
      instances.stream().forEach((block) -> block.onSplit());
   }
   
   @Override public void onDone() {
      instances.stream().forEach((block) -> block.onDone());
   }
   
   @Override public void onReset() {
      instances.stream().forEach((block) -> block.onReset());
   }

   @Override public void propertyUpdated(Property property) {
      instances.stream().forEach((block) -> block.propertyUpdated(property));
   }
   

}
