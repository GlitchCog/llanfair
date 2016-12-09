package com.jenmaarai.llanfair.deepPointers.windows;

import com.jenmaarai.llanfair.deepPointers.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WindowsProcess implements Process {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsProcess.class);
   
   private final int pid;
   private final String name;
   private final String title;
   private final boolean readable;
   
   WindowsProcess(int pid, String name, String title, boolean readable) {
      this.pid = pid;
      this.name = name;
      this.readable = readable;
      this.title = title;
   }
   
   @Override
   public int getPid() {
      return pid;
   }
   
   @Override
   public String getName() {
      return name;
   }
   
   @Override
   public String getTitle() {
      return title;
   }
   
   @Override
   public boolean isReadable() {
      return readable;
   }
}
