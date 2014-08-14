package com.jenmaarai.llanfair.model;

import com.jenmaarai.sidekick.time.Time;
import java.util.List;
import java.util.Map;

public class Run {
    
    private String game;
    private String category;
    private String version;
    private String goal;
    
    private String globalNotes;
    private Map<Integer, String> segmentNotes;
    
    private int revision;
    private List<Segment> segments;
    private List<Counter> counters;
    
    private Attempt attempt;
    private List<Attempt> history;
    
    private Time bestRun;
    private List<Time> bestSegments;

    private Time startDelay;
    private long startStamp;
}
