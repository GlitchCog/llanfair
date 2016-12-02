package com.jenmaarai.llanfair.deepPointers.windows.nativeinterface;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Psapi extends StdCallLibrary {
   /**
    * Lists all the pids running on the system.
    *
    * @param processIDsOut buffer
    * @param size          size of the buffer
    * @param nbRead        number of pids found
    * @return if the function succeeded
    */
   boolean EnumProcesses(int[] processIDsOut, int size, IntByReference nbRead);
   
   /**
    * Get the executable name from a process handle with PROCESS_VM_READ and PROCESS_QUERY_INFORMATION. <br />
    * It's a null terminated unicode string.
    *
    * @param hModule the module to get the name of (0 for the root process)
    * @param name buffer
    * @param nSize buffer size
    * @return 0 if failed, string size if success
    */
   int GetModuleBaseNameW(Pointer hProcess, Pointer hModule, byte[] name, int nSize);
}
