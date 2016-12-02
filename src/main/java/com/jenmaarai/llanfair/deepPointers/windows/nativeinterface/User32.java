package com.jenmaarai.llanfair.deepPointers.windows.nativeinterface;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface User32 extends StdCallLibrary {
   /**
    * Get the pid from a window handler.
    * @param hWnd the window handler
    * @param lpdwProcessId the pid of this window handler
    * @return the tid of the thread that created this window
    */
   int GetWindowThreadProcessId(Pointer hWnd, IntByReference lpdwProcessId);
   
   /**
    * Get a window handler for a window which has a certain title.
    * @param winClass null
    * @param title the window title
    * @return the window handler
    */
   Pointer FindWindowW(String winClass, String title);
}
