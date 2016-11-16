package com.jenmaarai.llanfair.conf;

import java.util.EventListener;

public interface PropertyListener extends EventListener {
   
   /**
    * Callback invoked whenever a new value has been defined for a property.
    * Property may be null to indicate that every property may have changed.
    */
   void propertyUpdated(Property property);

}
