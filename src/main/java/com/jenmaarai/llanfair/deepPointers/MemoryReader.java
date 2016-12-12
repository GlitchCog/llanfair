package com.jenmaarai.llanfair.deepPointers;

import com.jenmaarai.llanfair.deepPointers.exceptions.MemoryReaderException;

import java.util.List;

public interface MemoryReader {
   /**
    * @return If onlyReadable, returns a list of all readable processes.
    */
   List<Process> getAllProcesses(boolean onlyReadable) throws MemoryReaderException;
   
   /**
    * @return only processes with visible windows (not daemon)
    */
   List<Process> getAllVisibleProcesses() throws MemoryReaderException;
   
   /**
    * @return the Process corresponding to this pid
    */
   Process getProcessFromPid(int pid) throws MemoryReaderException;
   
   /**
    * @return the byte table at this address with this size
    */
   byte[] readMemory(Process p, long address, int size) throws MemoryReaderException;
   
   /**
    * @return the integer at this memory address, using correct endianness and convert it to long.
    */
   long readInteger(Process p, long address, int size) throws MemoryReaderException;
}
