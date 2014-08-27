package com.jenmaarai.llanfair.model;

import com.jenmaarai.llanfair.config.Clock;
import com.jenmaarai.sidekick.locale.Localizer;
import com.jenmaarai.sidekick.time.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Run {
    
    private String game;
    private String category;
    private String version;
    private String goal;
    
    private String globalNotes;
    private Map<Integer, String> segmentNotes;
    
    private List<Segment> segments;
    private List<Counter> counters;
    
    private int revision;
    private List<List<Segment>> segmentsHistory;
    private List<List<Counter>> countersHistory;
    
    private Attempt attempt;
    private List<Attempt> history;
    
    private Time bestRun;
    private List<Time> bestSegments;

    private Time startDelay;
    private long startStamp;
    
    /**
     * Creation of an empty run. This run provides a single unnamed segment,
     * thus allowing the user to start the timer from the get-go and use 
     * Llanfair as a simple timer and not a speedrun timer per se. This run
     * also provides a single unnamed counter for the same reasons.
     */
    public Run() {
        game = "";
        category = "";
        version = "";
        goal = "";
        globalNotes = "";
        segmentNotes = new HashMap<>();
        revision = 1;
        segmentsHistory = new ArrayList<>();
        countersHistory = new ArrayList<>();
        attempt = new Attempt();
        history = new ArrayList<>();
        bestRun = Time.ZERO;
        bestSegments = new ArrayList<>();
        startDelay = Time.ZERO;
        startStamp = Clock.ms();
        
        segments = new ArrayList<>();
        segments.add(new Segment(Localizer.get(Run.class, "segment") + " 1"));
        counters = new ArrayList<>();
        counters.add(new Counter(Localizer.get(Run.class, "counter") + " 1"));
    }
    
    public Time getElapsed() {
        return new Time(Clock.ms() - startStamp);
    }
}
