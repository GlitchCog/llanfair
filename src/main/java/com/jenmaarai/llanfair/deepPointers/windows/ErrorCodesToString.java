package com.jenmaarai.llanfair.deepPointers.windows;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class ErrorCodesToString {
   private static final Map<Integer, String> m = new HashMap<>();
   
   static {
      m.put(0, "SUCCESS");
      m.put(5, "ACCESS_DENIED");
      m.put(6, "INVALID_HANDLE");
   }
   
   public static String get(int code) {
      return m.get(code);
   }
}
