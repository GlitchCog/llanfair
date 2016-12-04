package com.jenmaarai.llanfair.view;

import com.jenmaarai.llanfair.conf.Property;
import com.jenmaarai.llanfair.control.Splitter;
import com.jenmaarai.llanfair.model.Run;
import com.jenmaarai.sidekick.swing.RichLabel;
import java.awt.Font;
import net.miginfocom.swing.MigLayout;

public class Title extends Block {
   
   private RichLabel game;
   private RichLabel category;
   private RichLabel details;

   public Title(Splitter splitter) {
      super(splitter);
      build();
      propertyUpdated(null);
   }
   
   private void build() {
      game = new RichLabel("");
      category = new RichLabel("");
      details = new RichLabel("");
      
      setLayout(new MigLayout(""));
      add(game,     "center, pushx, wrap");
      add(category, "center, pushx, wrap");
      add(details,  "center, pushx");
   }

   @Override public void onStart() {}

   @Override public void onSplit() {}

   @Override public void onDone() {}

   @Override public void onReset() {}

   @Override public void onRunUpdate() {
      Run run = splitter.getRun();
      game.setText(run.getGame());
      category.setText(run.getCategory());
      
      if (run.getPlatform() != null) {
         if (run.getRegion() != null) {
            details.setText(String.format(
                    "%s, %s", run.getPlatform(), run.getRegion()));
         } else {
            details.setText(run.getPlatform());
         }
      } else {
         details.setText(run.getRegion());
      }
   }
   
   @Override public final void propertyUpdated(Property property) {
      if (property == null || property.name().startsWith("title")) {
         Font font = Property.titleFont.get();
         game.setFont(font);
         category.setFont(font);
         details.setFont(font);
      }
   }
   
}
