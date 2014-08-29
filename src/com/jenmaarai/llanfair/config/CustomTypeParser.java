package com.jenmaarai.llanfair.config;

import com.jenmaarai.sidekick.config.Configuration;
import com.jenmaarai.sidekick.error.ParseException;
import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Parser used to handle serialization of the configuration.
 */
class CustomTypeParser extends Configuration.TypeParser {

    @Override @SuppressWarnings("unchecked")
    public String toString(Class<?> type, String key, Object value) {
        if (type == Color.class) {
            Color color = (Color) value;
            StringBuilder string = new StringBuilder("#");
            string.append(Integer.toHexString(color.getRed()));
            string.append(Integer.toHexString(color.getGreen()));
            string.append(Integer.toHexString(color.getBlue()));
            return string.toString();
        }
        if (key.equals("blockPlacement")) {
            Map<Point, Class<?>> map = (Map<Point, Class<?>>) value;
            StringBuilder string = new StringBuilder();
            for (Point mapKey : map.keySet()) {
                string.append(mapKey.x);
                string.append(",");
                string.append(mapKey.y);
                string.append(">");
                string.append(map.get(mapKey).getSimpleName());
                string.append(";");
            }
            string.deleteCharAt(string.length() - 1);
            return string.toString();
        }
        return super.toString(type, key, value);
    }

    @Override @SuppressWarnings("unchecked")
    public <T> T toValue(Class<T> type, String key, String text) 
            throws ParseException {
        if (type == Locale.class) {
            return (T) Locale.forLanguageTag(text);
        }
        if (type == Color.class) {
            return (T) Color.decode(text);
        }
        if (key.equals("blockPlacement")) {
            Map<Point, Class<?>> map = new HashMap<>();
            String[] blocks = text.split(";");
            for (String block : blocks) {
                String[] split = block.split(">");
                String[] coord = split[0].split(",");
                
                Point point = new Point(
                        Integer.valueOf(coord[0]), 
                        Integer.valueOf(coord[1]));
                try {
                    map.put(point, Class.forName(
                            "com.jenmaarai.llanfair.view." + split[1]));
                } catch (ClassNotFoundException ex) {
                    throw new ParseException("bad block class: " + split[1]);
                }
            }
            return (T) map;
        }
        return super.toValue(type, key, text);
    }

}
