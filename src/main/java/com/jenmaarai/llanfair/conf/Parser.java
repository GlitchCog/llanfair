package com.jenmaarai.llanfair.conf;

import com.jenmaarai.llanfair.control.Input;
import com.jenmaarai.llanfair.view.BlockLayout;
import com.jenmaarai.sidekick.error.ParserException;

public class Parser extends com.jenmaarai.sidekick.config.Parser {

   /**
    * Converts properties textual representation into actual values.
    */
   @Override public Object toValue(Class<?> type, String key, String text) 
                                                   throws ParserException {
      if (type == BlockLayout.class) {
         return BlockLayout.parse(text);
      } else if (type == Input.class) {
         return Input.parse(text);
      } else {
         return super.toValue(type, key, text);
      }
   }
   
}
