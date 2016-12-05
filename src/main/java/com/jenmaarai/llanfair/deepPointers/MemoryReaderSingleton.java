package com.jenmaarai.llanfair.deepPointers;

import com.jenmaarai.llanfair.deepPointers.exceptions.MemoryReaderException;
import com.jenmaarai.llanfair.deepPointers.windows.WindowsMemoryReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MemoryReaderSingleton {
   private static final Logger LOG = LoggerFactory.getLogger(MemoryReaderSingleton.class);
   
   /**
    * Null if not available
    */
   public static final MemoryReader INSTANCE;
   
   static {
      switch (getOS()) {
         case WINDOWS:
            LOG.info("OS is Windows");
            INSTANCE = new WindowsMemoryReader();
            break;
         
         default:
            LOG.warn("Direct process memory reading is unavailable");
            INSTANCE = null;
            break;
      }
   }
   
   private static SupportedOS getOS() {
      String os = System.getProperty("os.name").toLowerCase();
      
      if (os.startsWith("windows")) {
         return SupportedOS.WINDOWS;
      }
      
      if (os.startsWith("linux")) {
         return SupportedOS.LINUX;
      }
      
      if (os.startsWith("mac os x")) {
         return SupportedOS.MACOSX;
      }
      
      return SupportedOS.UNSUPPORTED;
   }
   
   public static void main(String[] args) {
      try {
         MemoryReader m = MemoryReaderSingleton.INSTANCE;
      
         //List<Process> l = m.getProcessWithName("steam");
         
         List<Process> l = m.getAllProcesses();
      
         for (Process p : l) {
            if(p.isReadable()) {
               LOG.info("Process with pid {} and name {} found", p.getPid(), p.getName());
            } else {
               LOG.info("Unreadable process with pid {}", p.getPid());
            }
         }
      } catch (MemoryReaderException e) {
         LOG.error("Exception got during play with MemoryReader ;)", e);
      }
   }
}
