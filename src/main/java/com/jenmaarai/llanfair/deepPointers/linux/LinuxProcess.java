package com.jenmaarai.llanfair.deepPointers.linux;

import com.jenmaarai.llanfair.deepPointers.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LinuxProcess implements Process {
   private static final Logger LOG = LoggerFactory.getLogger(LinuxProcess.class);
   
   private final int pid;
   private final String name;
   private final String title;
   private final boolean readable;
   
   public LinuxProcess(int pid, String name, String title, boolean readable) {
      this.pid = pid;
      this.name = name;
      this.title = title;
      this.readable = readable;
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
