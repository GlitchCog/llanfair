package com.jenmaarai.llanfair.model;

import com.jenmaarai.sidekick.time.Time;
import java.util.Date;
import java.util.List;

/**
 * Contains every data pertaining to an attempt at a given run.
 */
public class Attempt {

    private int revision;
    private Date date;
    private List<Time> segments;
    private List<Integer> counters;
    private boolean globalRecord;
    private List<Integer> segmentRecord;
    
}
