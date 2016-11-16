package com.jenmaarai.llanfair.view;

import com.jenmaarai.llanfair.conf.Property;
import com.jenmaarai.llanfair.control.Splitter;
import com.jenmaarai.sidekick.swing.GBC;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;

public class Timer extends Block {

   private ScheduledExecutorService callback;
   private JLabel mainTimer = new JLabel("--");
   
   public Timer(Splitter splitter) {
      super(splitter);
      build();
      propertyUpdated(null);
   }
   
   private void build() {
      mainTimer.setHorizontalAlignment(JLabel.RIGHT);
      
      setLayout(new GridBagLayout());
      add(mainTimer, GBC.grid(0, 0).anchor(GBC.CENTER)
                     .fill(GBC.HORIZONTAL).weight(1.0f, 1.0f));
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

   @Override public void propertyUpdated(Property property) {
      if (property == null || property.name().startsWith("timer")) {
         Color background = Property.timerColorBackground.get();
         if (background == null) {
            setOpaque(false);
         } else {
            setOpaque(true);
            setBackground(background);
         }
         Font font = Property.timerMainFont.get();
         mainTimer.setFont(font);
      }
   }
   
   
   
}
