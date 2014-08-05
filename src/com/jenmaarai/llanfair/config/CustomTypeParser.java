package com.jenmaarai.llanfair.config;

import com.jenmaarai.sidekick.config.Configuration;
import com.jenmaarai.sidekick.error.ParseException;
import java.util.Locale;

/**
 * Parser used to handle serialization of the configuration.
 */
class CustomTypeParser extends Configuration.TypeParser {

    @Override @SuppressWarnings("unchecked")
    public <T> T toValue(Class<T> type, String key, String text) 
            throws ParseException {
        if (type == Locale.class) {
            return (T) Locale.forLanguageTag(text);
        }
        return super.toValue(type, key, text);
    }

}
