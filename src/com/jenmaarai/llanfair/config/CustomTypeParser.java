package com.jenmaarai.llanfair.config;

import com.jenmaarai.sidekick.config.Configuration;
import com.jenmaarai.sidekick.error.ParseException;
import java.awt.Color;
import java.util.Locale;

/**
 * Parser used to handle serialization of the configuration.
 */
class CustomTypeParser extends Configuration.TypeParser {

    @Override
    public String toString(Class<?> type, String key, Object value) {
        if (type == Color.class) {
            Color color = (Color) value;
            StringBuilder string = new StringBuilder("#");
            string.append(Integer.toHexString(color.getRed()));
            string.append(Integer.toHexString(color.getGreen()));
            string.append(Integer.toHexString(color.getBlue()));
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
        return super.toValue(type, key, text);
    }

}
