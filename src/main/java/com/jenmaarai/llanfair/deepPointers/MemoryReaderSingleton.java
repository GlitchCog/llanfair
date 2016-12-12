package com.jenmaarai.llanfair.deepPointers;

import com.jenmaarai.llanfair.deepPointers.exceptions.MemoryReaderException;
import com.jenmaarai.llanfair.deepPointers.windows.WindowsMemoryReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;
import java.util.List;

public class MemoryReaderSingleton {
   private static final Logger LOG = LoggerFactory.getLogger(MemoryReaderSingleton.class);
   
   /**
    * Null if not available
    */
   public static final MemoryReader INSTANCE;
   public static final boolean bigEndian = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
   
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
         
         List<Process> l2 = m.getAllVisibleProcesses();
         
         for (Process p : l2) {
            LOG.info("Process with pid {} has a window named {}", p.getPid(), p.getTitle());
         }
         
         
         List<Process> l = m.getAllProcesses(true);
         
         Process testProcess = null;
         for (Process p : l) {
            LOG.info("Process with pid {} and name {} found", p.getPid(), p.getName());
            if (p.getName().equals("a.exe")) {
               testProcess = p;
            }
         }
         if (testProcess == null) {
            throw new MemoryReaderException("Process not found");
         }
         
         if (!testProcess.isReadable()) {
            throw new MemoryReaderException("Process is unreadable");
         }
         
         while (true) {
            LOG.info("" + m.readInteger(testProcess, 0x600010480L, 4));
            try {
               Thread.sleep(500);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      } catch (MemoryReaderException e) {
         LOG.error("Exception got during play with MemoryReader ;)", e);
      }
   }
}
