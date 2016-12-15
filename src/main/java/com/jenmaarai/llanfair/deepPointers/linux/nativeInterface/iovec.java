package com.jenmaarai.llanfair.deepPointers.linux.nativeInterface;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class iovec extends Structure implements Structure.ByReference {
   public Pointer address;
   public SizeT size;
   
   public iovec(Pointer address, int size) {
      this.address = address;
      this.size = new SizeT(size);
   }
   
   @Override
   protected List<String> getFieldOrder() {
      return Arrays.asList("address", "size");
   }
   
   public static class SizeT extends IntegerType {
      public SizeT() { this(0); }
      public SizeT(long value) { super(Native.SIZE_T_SIZE, value, true); }
   }
}
