package com.jenmaarai.llanfair.deepPointers;

public interface Process {
   int getPid();
   
   String getName();
   
   String getTitle();
   
   boolean isReadable();
}
