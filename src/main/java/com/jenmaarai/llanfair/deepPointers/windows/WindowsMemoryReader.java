package com.jenmaarai.llanfair.deepPointers.windows;

import com.jenmaarai.llanfair.deepPointers.MemoryReader;
import com.jenmaarai.llanfair.deepPointers.MemoryReaderSingleton;
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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WindowsMemoryReader implements MemoryReader {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsMemoryReader.class);
   
   Kernel32 kernel32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
   User32 user32 = (User32) Native.loadLibrary("user32", User32.class);
   Psapi psapi = (Psapi) Native.loadLibrary("Psapi", Psapi.class);
   
   /**
    * @return null if error
    */
   private Pointer getProcessHandleFromPid(int pid) {
      return getProcessHandleFromPid(pid, WindowsConstants.PROCESS_VM_READ | WindowsConstants.PROCESS_QUERY_INFORMATION);
   }
   
   /**
    * @return null if error
    */
   private Pointer getProcessHandleFromPid(int pid, int rights) {
      int error;
      
      Pointer ph = kernel32.OpenProcess(rights, false, pid);
      if ((error = kernel32.GetLastError()) != 0) {
         LOG.debug("Can't open process with PID {}, error {} : {}", pid, error, ErrorCodesToString.get(error));
         return null;
      }
      
      return ph;
   }
   
   /**
    * @return null if error
    */
   private String getProcessNameFromProcessHandle(Pointer ph) {
      
      byte[] filename = new byte[512];
      
      psapi.GetModuleBaseNameW(ph, new Pointer(0), filename, 512);
      
      if (kernel32.GetLastError() != 0) {
         return null;
      }
      
      return new String(filename, StandardCharsets.UTF_16LE).trim();
   }
   
   /**
    * @return null if error
    */
   private String getProcessNameFromPid(int pid){
      Pointer ph = getProcessHandleFromPid(pid);
      if(ph != null){
         try{
            return getProcessNameFromProcessHandle(ph);
         } finally {
            kernel32.CloseHandle(ph);
         }
      }
      return null;
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
            Process p = getProcessFromPid(pid);
            if (p != null && (!onlyReadable || p.isReadable())) {
               l.add(p);
            }
         }
      }
      
      return l;
   }
   
   @Override
   public List<Process> getAllVisibleProcesses() throws MemoryReaderException {
      List<Process> l = new ArrayList<>();
      
      Map<Integer, String> m = getAllNames();
      for(Map.Entry<Integer, String> e : m.entrySet()){
         String name = getProcessNameFromPid(e.getKey());
         l.add(new WindowsProcess(e.getKey(), name, e.getValue(), true));
      }
      
      return l;
   }
   
   private Map<Integer, String> getAllNames() throws MemoryReaderException {
      final Map<Integer, String> m = new HashMap<>();
      
      User32.WNDENUMPROC f = (hWnd, arg) -> {
         if (user32.IsWindowVisible(hWnd)) {
            IntByReference pid = new IntByReference(0);
            
            user32.GetWindowThreadProcessId(hWnd, pid);
            if (kernel32.GetLastError() != 0) {
               return true;
            }
            
            byte[] buffer = new byte[1024];
            user32.GetWindowTextW(hWnd, buffer, 1024);
            if (kernel32.GetLastError() != 0) {
               return true;
            }
            
            String name = new String(buffer, StandardCharsets.UTF_16LE).trim();
            if("".equals(name)){
               // Probably a subwindow
               return true;
            }
            
            m.put(pid.getValue(), name);
            
            LOG.info("{} : {}", pid.getValue(), name);
            
         }
         return true;
      };
      
      if (!user32.EnumWindows(f, null)) {
         throw new MemoryReaderException("Can't enum windows");
      }
      
      
      return m;
   }
   
   @Override
   public Process getProcessFromPid(int pid) {
      Pointer ph = getProcessHandleFromPid(pid);
      if (ph == null) {
         return new WindowsProcess(pid, null, null, false);
      }
      try {
         String filename = getProcessNameFromProcessHandle(ph);
         
         return new WindowsProcess(pid, filename, null, filename != null);
      } finally {
         kernel32.CloseHandle(ph);
      }
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
   
   @Override
   public long readInteger(Process p, long address, int size) throws MemoryReaderException {
      byte[] mem = this.readMemory(p, address, size);
      
      if (!MemoryReaderSingleton.bigEndian) {
         // Reverse the byte array
         byte temp;
         for (int i = 0; i < size / 2; i++) {
            temp = mem[i];
            mem[i] = mem[size - 1 - i];
            mem[size - 1 - i] = temp;
         }
      }
      
      return new BigInteger(mem).longValue();
   }
}
