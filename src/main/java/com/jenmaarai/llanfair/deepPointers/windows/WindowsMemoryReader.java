package com.jenmaarai.llanfair.deepPointers.windows;

import com.jenmaarai.llanfair.deepPointers.MemoryReader;
import com.jenmaarai.llanfair.deepPointers.Process;
import com.jenmaarai.llanfair.deepPointers.exceptions.MemoryReaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WindowsMemoryReader implements MemoryReader {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsMemoryReader.class);
   
   @Override
   public List<Process> getAllProcesses() throws MemoryReaderException {
      throw new MemoryReaderException("unimplemented");
   }
   
   @Override
   public List<Process> getProcessesWithName(String name) throws MemoryReaderException {
      throw new MemoryReaderException("unimplemented");
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
