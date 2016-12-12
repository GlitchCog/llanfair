package com.jenmaarai.llanfair.deepPointers.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryReaderException extends Exception {
   private static final Logger LOG = LoggerFactory.getLogger(MemoryReaderException.class);
   
   public MemoryReaderException() {
      super();
   }
   
   public MemoryReaderException(String message) {
      super(message);
   }
   
   public MemoryReaderException(String message, Throwable cause) {
      super(message, cause);
   }
   
   public MemoryReaderException(Throwable cause) {
      super(cause);
   }
}
