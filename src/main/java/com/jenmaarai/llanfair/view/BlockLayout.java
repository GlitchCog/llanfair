package com.jenmaarai.llanfair.view;

import com.jenmaarai.sidekick.error.ParserException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockLayout {
   
   private static final Logger LOG 
           = LoggerFactory.getLogger(BlockLayout.class);
   
   private static final Pattern CHUNK_REGEX 
           = Pattern.compile("^([\\.\\w]+)@(\\d+),(\\d+)$");
   
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
    * Returns a textual representation of this layout.
    * The returned string will consist of a semicolon-separated list of chunks
    * for the main layout, followed by semicolon-separated lists of chunks for
    * each existing dock in this layout. Layouts are separated by slashes.
    */
   @Override public String toString() {
      StringBuilder builder = new StringBuilder(listToString(mainLayout));
      dockLayouts.stream().forEach((layout) -> {
         builder.append("/");
         builder.append(listToString(layout));
      });
      return builder.toString();
   }
   
   /**
    * Returns a textual representation for a list of chunks.
    * The returned string will be a semicolon-separated list of the chunks.
    */
   private String listToString(List<Chunk> layout) {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < layout.size(); i++) {
         builder.append(layout.get(i).toString());
         if (i < layout.size() - 1) {
            builder.append(";");
         }
      }
      return builder.toString();
   }
   
   /**
    * Parses and returns a block layout read from a specific string.
    * The supplied string must correspond to the result of a call to
    * {@code toString()} on a block layout instance.
    * 
    * @throws ParserException  if serialized is malformed
    */
   public static BlockLayout parse(String serialized) throws ParserException {
      BlockLayout object       = new BlockLayout();
      String[]    layoutTokens = serialized.split("/");
      
      for (int i = 0; i < layoutTokens.length; i++) {
         List<Chunk> layout = (i == 0) ? object.mainLayout : new ArrayList<>();
         String[] chunkTokens = layoutTokens[i].split(";");
         
         for (String chunkToken : chunkTokens) {
            Chunk chunk = parseChunk(chunkToken);
            if (chunk == null) {
               throw new ParserException("malformed chunk");
            }
            layout.add(chunk);
         }
         if (i > 0 && !layout.isEmpty()) {
            object.dockLayouts.add(layout);
         }
      }
      return object;
   }
   
   /**
    * Parses and returns a chunk read from a specific string.
    * Returns null if the supplied string is malformed.
    */
   private static Chunk parseChunk(String serialized) {
      Matcher matcher = CHUNK_REGEX.matcher(serialized);
      if (!matcher.matches()) {
         LOG.error("Chunk format not found in string '{}'", serialized);
      } else {
         try {
            Class<?> readClass = Class.forName(matcher.group(1));
            return new Chunk(
               Integer.parseInt(matcher.group(2)),
               Integer.parseInt(matcher.group(3)),
               readClass.asSubclass(Block.class));
            
         } catch (ClassNotFoundException | ClassCastException x) {
            LOG.error("Illegal class in chunk '{}'", matcher.group(1));
         } catch (ExceptionInInitializerError x) {
            LOG.error("Error during instantiation of '{}'", matcher.group(1));
         } catch (NumberFormatException x) {
            LOG.error(
                    "Illegal coordinates in chunk '{},{}'", 
                    matcher.group(2), matcher.group(3));
         }
      }
      return null;
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
      
      /**
       * Returns a textual representation for this chunk.
       * The returned string consists of the fully qualified block class name
       * followed by the '@' sign and its coordinates within the layout.
       */
      @Override public String toString() {
         return blockClass.getCanonicalName() + "@" + x + "," + y;
      }
      
   }

}
