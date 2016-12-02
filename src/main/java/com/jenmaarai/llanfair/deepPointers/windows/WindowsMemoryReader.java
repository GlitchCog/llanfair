package com.jenmaarai.llanfair.deepPointers.windows;

import com.jenmaarai.llanfair.deepPointers.MemoryReader;
import com.jenmaarai.llanfair.deepPointers.Process;
import com.jenmaarai.llanfair.deepPointers.exceptions.MemoryReaderException;
import com.jenmaarai.llanfair.deepPointers.windows.nativeinterface.Kernel32;
import com.jenmaarai.llanfair.deepPointers.windows.nativeinterface.Psapi;
import com.jenmaarai.llanfair.deepPointers.windows.nativeinterface.User32;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WindowsMemoryReader implements MemoryReader {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsMemoryReader.class);
   
   Kernel32 kernel32 = (Kernel32) Native.loadLibrary("kernel32",Kernel32.class);
   User32 user32 = (User32) Native.loadLibrary("user32", User32.class);
   Psapi psapi = (Psapi) Native.loadLibrary("Psapi", Psapi.class);
   
   @Override
   public List<Process> getAllProcesses() throws MemoryReaderException {
      List<Process> l = new ArrayList<>();
      
      int error;
      int[] processlist = new int[1024];
      
      psapi.EnumProcesses(processlist, 1024, new IntByReference());
      
      for(int pid : processlist){
         if(pid != 0) {
            byte[] filename = new byte[512];
            Pointer ph = kernel32.OpenProcess(Constants.PROCESS_VM_READ | Constants.PROCESS_QUERY_INFORMATION,
                  false, pid);
            if((error = kernel32.GetLastError()) != 0){
               LOG.info("Can't open process with PID {}, error {} : {}", pid, error, ErrorCodesToString.get(error));
               continue;
            }
            try {
               psapi.GetModuleBaseNameW(ph, new Pointer(0), filename, 512);
               if ((error = kernel32.GetLastError()) != 0) {
                  LOG.info("Can't get name of process with PID {}, error {} : {}", pid, error, ErrorCodesToString.get(error));
                  continue;
               }
               l.add(new WindowsProcess(pid, new String(filename), true));
            } finally {
               
            }
         }
      }
      
      return l;
   }
   
   @Override
   public List<Process> getProcessesWithName(String name) throws MemoryReaderException {
      IntByReference pid = new IntByReference(0);
      user32.GetWindowThreadProcessId(user32.FindWindowW(null, name), pid);
      
      return Collections.singletonList(new WindowsProcess(pid.getValue(), "not implemented", true));
   }
   
   @Override
   public Process getProcessFromPid(int pid) throws MemoryReaderException {
      throw new MemoryReaderException("unimplemented");
   }
   
   @Override
   public byte[] readMemory(Process p, long address, int size) throws MemoryReaderException {
      throw new MemoryReaderException("unimplemented");
   }
}
