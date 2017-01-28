package com.jenmaarai.llanfair.deepPointers.linux;

import com.jenmaarai.llanfair.deepPointers.MemoryReader;
import com.jenmaarai.llanfair.deepPointers.MemoryReaderSingleton;
import com.jenmaarai.llanfair.deepPointers.Process;
import com.jenmaarai.llanfair.deepPointers.exceptions.MemoryReaderException;
import com.jenmaarai.llanfair.deepPointers.linux.nativeInterface.LibC;
import com.jenmaarai.llanfair.deepPointers.linux.nativeInterface.iovec;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LinuxMemoryReader implements MemoryReader {
   private static final Logger LOG = LoggerFactory.getLogger(LinuxMemoryReader.class);
   private static final int BUFFER_SIZE = 1024;
   private LibC libC = (LibC) Native.loadLibrary("c", LibC.class);
   
   @Override
   public List<Process> getAllProcesses(boolean onlyReadable) throws MemoryReaderException {
      List<Process> l = new ArrayList<>();
      
      File parent = new File("/proc");
      File[] children = parent.listFiles();
      if (children == null) {
         throw new MemoryReaderException("Can't list /proc directory");
      }
      
      for (File f : children) {
         if (f.getName().matches("[0-9]+")) {
            int pid = Integer.parseInt(f.getName());
            
            String filename = getNameFromPid(pid);
            
            // This is a process
            File memFile = new File("/proc/" + pid + "/mem");
            
            if (memFile.canRead()) {
               l.add(new LinuxProcess(pid, filename, "", true));
            } else {
               if (!onlyReadable) {
                  l.add(new LinuxProcess(pid, filename, "", false));
               }
            }
         }
      }
      
      return l;
   }
   
   @Override
   public List<Process> getAllVisibleProcesses() throws MemoryReaderException {
      return Collections.emptyList();
   }
   
   @Override
   public Process getProcessFromPid(int pid) throws MemoryReaderException {
      String name = getNameFromPid(pid);
      return new LinuxProcess(pid, name, "", false);
   }
   
   private String getNameFromPid(int pid) {
      File cmdFile = new File("/proc/" + pid + "/cmdline");
      String cmdline;
      
      try (InputStream is = new FileInputStream(cmdFile)) {
         byte[] buf = new byte[BUFFER_SIZE];
         int readBytes = is.read(buf);
         if (readBytes <= 0) {
            throw new IOException("Nothing to read from this reader");
         }
         cmdline = new String(buf, StandardCharsets.UTF_8);
      } catch (IOException e) {
         LOG.debug("Exception got during reading of command line args of process {} : {}", pid, e);
         return null;
      }
      
      return cmdline.isEmpty() ? "" : cmdline.split("\u0000")[0];
   }
   
   @Override
   public byte[] readMemory(Process p, long address, int size) throws MemoryReaderException {
      Memory nativeMem = new Memory(size);
      
      iovec local = new iovec(nativeMem, size);
      iovec remote = new iovec(new Pointer(address), size);
      
      if (libC.process_vm_readv(p.getPid(), local, 1, remote, 1, 0) != size) {
         libC.perror("");
         throw new MemoryReaderException("Can't read?");
      }
      
      byte[] buf = new byte[size];
      nativeMem.read(0, buf, 0, size);
      
      return buf;
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
