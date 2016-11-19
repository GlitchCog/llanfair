package com.jenmaarai.llanfair.view;

import com.jenmaarai.llanfair.conf.Property;
import com.jenmaarai.llanfair.control.Splitter;
import com.jenmaarai.sidekick.swing.GBC;
import com.jenmaarai.sidekick.swing.RichLabel;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;

public class Title extends Block {
   
   private RichLabel game;

   public Title(Splitter splitter) {
      super(splitter);
      build();
      propertyUpdated(null);
   }
   
   private void build() {
      game = new RichLabel("", JLabel.CENTER);
      
      setLayout(new GridBagLayout());
      add(game, GBC.grid(0, 0).anchor(GBC.CENTER)
                     .fill(GBC.HORIZONTAL).weight(1.0f, 1.0f));
   }

   @Override public void onStart() {
   }

   @Override public void onSplit() {
   }

   @Override public void onDone() {
   }

   @Override public void onReset() {
   }

   @Override public void onRunUpdate() {
      game.setText(splitter.getRun().getGame());
   }
   
   @Override public final void propertyUpdated(Property property) {
      if (property == null || property.name().startsWith("title")) {
         game.setFont(Property.titleFont.get());
      }
   }
   
}
