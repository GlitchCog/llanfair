package com.jenmaarai.llanfair.config;

import java.io.File;

/**
 * The true configuration of Llanfair. Provides two levels of configuration, one
 * global and one run-dependent (local). Both configurations are themselves
 * {@code SplitConfiguration}s.
 */
public class DualConfiguration {
    
    private SplitConfiguration global;
    private SplitConfiguration local;
    
    public DualConfiguration() {
        global = new SplitConfiguration(new File("."));
        local = new SplitConfiguration(new File("."));
    }

}
