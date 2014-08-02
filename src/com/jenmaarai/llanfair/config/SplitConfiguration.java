package com.jenmaarai.llanfair.config;

import com.jenmaarai.sidekick.config.Configuration;
import java.io.File;

/**
 * A regular configuration split in multiple files. In its current form, this 
 * class splits the configuration object in a settings.cfg and a theme.thm to
 * allow users to exchange themes independently from their settings.
 */
public class SplitConfiguration {
    
    private Configuration settings;
    private Configuration theme;
    
    public SplitConfiguration(File root) {
        if (root == null) {
            throw new IllegalArgumentException("root is null");
        }
        if (!root.isDirectory()) {
            throw new IllegalArgumentException("root must be a directory");
        }
        if (!root.canRead() || !root.canWrite()) {
            throw new IllegalArgumentException("read/write denied to " + root);
        }
        if (!root.exists()) {
            if (!root.mkdir()) {
                throw new IllegalArgumentException("failed to mkdir " + root);
            }
        }
        settings = new Configuration(new File(root, "settings.cfg"));
        theme = new Configuration(new File(root, "theme.thm"));
    }

}
