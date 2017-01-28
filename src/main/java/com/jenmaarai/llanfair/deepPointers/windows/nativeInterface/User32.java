package com.jenmaarai.llanfair.deepPointers.windows.nativeInterface;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface User32 extends StdCallLibrary {
   /**
    * Get the pid from a window handle.
    *
    * @param winHandle the window handle
    * @param outPID    the pid of this window handler
    * @return the tid of the thread that created this window
    */
   int GetWindowThreadProcessId(Pointer winHandle, IntByReference outPID);
   
   /**
    * Get a window handle for a window which has a certain title.
    *
    * @param winClass null
    * @param title    the window title
    * @return the window handler
    */
   Pointer FindWindowW(String winClass, String title);
   
   /**
    * Get the window name of a window handle.
    *
    * @param winHandle the window handle
    * @param name      buffer
    * @param nSize     buffer size
    * @return 0 if failed, string size if success
    */
   int GetWindowTextW(Pointer winHandle, byte[] name, int nSize);
   
   /**
    * A callback function for EnumWindows. Must return true if you want to continue iterating windows.
    */
   interface WNDENUMPROC extends StdCallCallback {
      boolean callback(Pointer hWnd, Pointer arg);
   }
   
   /**
    * For each top-level window, call the callback function from f. <br />
    * arg is an argument to be passed to this callback function with the pointer to the window handle.
    *
    * @param f   a WNDENUMPROC callback
    * @param arg the arg to ba passed
    * @return true if success
    */
   boolean EnumWindows(WNDENUMPROC f, Pointer arg);
   
   /**
    * Returns whether the window of this handle is visible.
    */
   boolean IsWindowVisible(Pointer winHandle);
}
