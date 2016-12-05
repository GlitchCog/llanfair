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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WindowsMemoryReader implements MemoryReader {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsMemoryReader.class);
   
   Kernel32 kernel32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
   User32 user32 = (User32) Native.loadLibrary("user32", User32.class);
   Psapi psapi = (Psapi) Native.loadLibrary("Psapi", Psapi.class);
   
   /**
    * @return null if error
    */
   private Pointer getProcessHandleFromPid(int pid) {
      int error;
      
      Pointer ph = kernel32.OpenProcess(Constants.PROCESS_VM_READ | Constants.PROCESS_QUERY_INFORMATION,
            false, pid);
      if ((error = kernel32.GetLastError()) != 0) {
         LOG.debug("Can't open process with PID {}, error {} : {}", pid, error, ErrorCodesToString.get(error));
         return null;
      }
      
      return ph;
   }
   
   /**
    * @return null if error
    */
   private String getProcessNameFromProcessHandle(Pointer ph, int pid) {
      int error;
      
      byte[] filename = new byte[512];
      
      int nbRead = psapi.GetModuleBaseNameW(ph, new Pointer(0), filename, 512);
      
      if ((error = kernel32.GetLastError()) != 0) {
         LOG.debug("Can't get name of process with PID {}, error {} : {}", pid, error, ErrorCodesToString.get(error));
         return null;
      }
      
      return new String(filename, StandardCharsets.UTF_16LE).trim();
   }
   
   @Override
   public List<Process> getAllProcesses() throws MemoryReaderException {
      return getAllProcesses(false);
   }
   
   @Override
   public List<Process> getAllProcesses(boolean onlyReadable) throws MemoryReaderException {
      List<Process> l = new ArrayList<>();
      
      int[] processList = new int[1024];
      IntByReference nbRead = new IntByReference(0);
      
      if (!psapi.EnumProcesses(processList, 1024, nbRead)) {
         LOG.error("Can't enumerate processes");
         throw new MemoryReaderException("Process enumeration unavailable");
      }
      
      for (int i = 0; i < nbRead.getValue(); i++) {
         int pid = processList[i];
         
         if (pid != 0) {
            Pointer ph = getProcessHandleFromPid(pid);
            if (ph == null) {
               // Unreadable process
               if (!onlyReadable) {
                  l.add(new WindowsProcess(pid, null, false));
               }
            } else {
               try {
                  String filename = getProcessNameFromProcessHandle(ph, pid);
                  l.add(new WindowsProcess(pid, filename, filename != null));
               } finally {
                  kernel32.CloseHandle(ph);
               }
            }
         }
      }
      
      return l;
   }
   
   @Override
   public Process getProcessWithName(String name) throws MemoryReaderException {
      IntByReference pid = new IntByReference(0);
      user32.GetWindowThreadProcessId(user32.FindWindowW(null, name), pid);
      
      return new WindowsProcess(pid.getValue(), "not implemented", true);
   }
   
   @Override
   public Process getProcessFromPid(int pid) throws MemoryReaderException {
      throw new MemoryReaderException("unimplemented");
   }
   
   @Override
   public byte[] readMemory(Process p, long address, int size) throws MemoryReaderException {
      byte[] result = new byte[size];
      
      Pointer ph = getProcessHandleFromPid(p.getPid());
      
      if (ph == null) {
         int error = kernel32.GetLastError();
         throw new MemoryReaderException("Process with pid " + p.getPid() + " not found : " + error
               + " " + ErrorCodesToString.get(error));
      }
      
      IntByReference sizeRead = new IntByReference(0);
      if (!kernel32.ReadProcessMemory(ph, address, result, size, sizeRead)) {
         int error = kernel32.GetLastError();
         throw new MemoryReaderException("Process with pid " + p.getPid() + " can't be read at address "
               + address + " with size " + size + ": " + error + " " + ErrorCodesToString.get(error));
      }
      
      return result;
   }
}
