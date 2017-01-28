package com.jenmaarai.llanfair.deepPointers.linux.nativeInterface;

import com.sun.jna.Library;

public interface LibC extends Library {
   long process_vm_readv(int pid, iovec bufTab, long bufTabSize, iovec remoteTab, long remoteTabSize, long flags);// throws LastErrorException;
   
   void perror(String s);
}
