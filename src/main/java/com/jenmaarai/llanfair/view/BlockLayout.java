package com.jenmaarai.llanfair.view;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockLayout {
   
   private static final Logger LOG 
           = LoggerFactory.getLogger(BlockLayout.class);
   
   private final List<Chunk> mainLayout = new ArrayList<>();
   private final List<List<Chunk>> dockLayouts = new ArrayList<>();
   
   /**
    * Provides a default layout containing a {@code Timer} as only block.
    */
   public static BlockLayout defaultLayout() {
      BlockLayout blockLayout = new BlockLayout();
      blockLayout.mainLayout.add(new Chunk(0, 0, Title.class));
      blockLayout.mainLayout.add(new Chunk(0, 1, Timer.class));
      return blockLayout;
   }
   
   /**
    * Returns a copy of the layout for main frame.
    */
   public List<Chunk> getMainLayout() {
      return new ArrayList<>(mainLayout);
   }

   /**
    * Returns a copy of the list of dock layouts.
    */
   public List<List<Chunk>> getDockLayouts() {
      return new ArrayList<>(dockLayouts);
   }

   /**
    * A chunk of data representing the placement of a block within the layout.
    * Each chunk defines its coordinates within the {@code GridBagLayout} of
    * its container and the class of block that should be placed there.
    */
   public static class Chunk {
      
      private int x;
      private int y;
      private final Class<? extends Block> blockClass;
      
      public Chunk(int x, int y, Class<? extends Block> blockClass) {
         this.x = x;
         this.y = y;
         this.blockClass = blockClass;
      }

      /**
       * Returns the x coordinate of the block within the layout.
       */
      public int getX() {
         return x;
      }

      /**
       * Returns the y coordinate of the block within the layout.
       */
      public int getY() {
         return y;
      }

      /**
       * Returns the class of the block described by this chunk.
       */
      public Class<? extends Block> getBlockClass() {
         return blockClass;
      }
      
   }

}
