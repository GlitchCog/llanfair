package com.jenmaarai.llanfair.view;

import com.jenmaarai.llanfair.conf.PropertyListener;
import com.jenmaarai.llanfair.control.Splitter;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jenmaarai.llanfair.control.SplitListener;

public abstract class Block extends    JPanel 
                            implements SplitListener, PropertyListener {
   
   private static final Logger LOG = LoggerFactory.getLogger(Block.class);
   
   protected Splitter splitter = null;

   public Block(Splitter splitter) {
      super();
      if (splitter == null) {
         LOG.error("Null splitter instance");
         throw new IllegalArgumentException("null splitter");
      }
      this.splitter = splitter;
      propertyUpdated(null);
   }
   
}
