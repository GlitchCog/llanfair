package com.jenmaarai.llanfair.view;

import com.jenmaarai.llanfair.control.Splitter;
import java.awt.Color;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;

public class Timer extends Block {

   private ScheduledExecutorService callback;
   private JLabel mainTimer = new JLabel("--");
   
   public Timer(Splitter splitter) {
      super(splitter);
      add(mainTimer);
   }

   @Override public void onStart() {
      mainTimer.setForeground(Color.BLUE);
      callback = Executors.newSingleThreadScheduledExecutor();
      Runnable task = () -> mainTimer.setText("" + splitter.getElapsed());
      callback.scheduleAtFixedRate(task, 0L, 10L, TimeUnit.MILLISECONDS);
   }

   @Override public void onSplit() {
   }

   @Override public void onDone() {
      mainTimer.setForeground(Color.BLACK);
      callback.shutdownNow();
   }

   @Override public void onReset() {
      mainTimer.setForeground(Color.BLACK);
      mainTimer.setText("Ready");
      callback.shutdownNow();
   }
   
}
