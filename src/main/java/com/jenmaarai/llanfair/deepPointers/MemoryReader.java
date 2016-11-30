package com.jenmaarai.llanfair.deepPointers;

import com.jenmaarai.llanfair.deepPointers.exceptions.MemoryReaderException;

import java.util.List;

public interface MemoryReader {
   List<Process> getAllProcesses() throws MemoryReaderException;
   
   List<Process> getProcessesWithName(String name) throws MemoryReaderException;
   
   Process getProcessFromPid(int pid) throws MemoryReaderException;
   
   byte[] readMemory(Process p, long address, int size) throws MemoryReaderException;
}
