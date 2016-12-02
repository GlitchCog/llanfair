package com.jenmaarai.llanfair.deepPointers.windows.nativeinterface;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {
   /**
    * Get a process handle from a pid. <br />
    * To avoid memory leaks, you have to call the CloseHandle function.
    *
    * @param rights  the rights wanted for this process
    * @param inherit false
    * @return a pointer to the process handle
    * @see com.jenmaarai.llanfair.deepPointers.windows.Constants for the rights
    */
   Pointer OpenProcess(int rights, boolean inherit, int pid);
   
   /**
    * Closes the handle to free the resources.
    *
    * @return if the function succeeded
    */
   boolean CloseHandle(Pointer handle);
   
   /**
    * Read the memory of a process opened with PROCESS_VM_READ.
    *
    * @param processHandle        the process handle
    * @param address              the address to read
    * @param sizeToRead           the size in bytes to read
    * @param outNumberOfBytesRead the number of bytes effectively read
    * @return if the function succeeded
    */
   boolean ReadProcessMemory(Pointer processHandle, long address, byte[] outputBuffer, int sizeToRead, IntByReference outNumberOfBytesRead);
   
   /**
    * Get the last error code.
    *
    * @see com.jenmaarai.llanfair.deepPointers.windows.ErrorCodesToString to get details
    */
   int GetLastError();
}
