package org.socialmusicdiscovery.server.support.format;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.socialmusicdiscovery.server.support.format.antlr.TitleFormatLexer;
import org.socialmusicdiscovery.server.support.format.antlr.TitleFormatParser;
import org.socialmusicdiscovery.server.support.format.provider.AttributeDataProvider;

import java.util.HashMap;
import java.util.Map;

public class TitleFormat {
    /**
     * Parser instance used by this formatter
     */
    TitleFormatParser parser;

    /**
     * Indication if the parser has been called once, this is needed to make the reset() method work correctly
     */
    boolean initialized = false;

    /**
     * Create a new title formatter class using the provided data provider and format
     *
     * @param dataProvider The data provider to use when converting keywords to values
     * @param format       The formatting string to use when doing the formatting
     */
    public TitleFormat(DataProvider dataProvider, String format) {
        parser = new TitleFormatParser(dataProvider, new CommonTokenStream(new TitleFormatLexer(new ANTLRStringStream(format))));
    }

    /**
     * Create a new title formatter class using the specified format and the default data provider
     *
     * @param format The formatting string to use when doing the formatting
     */
    public TitleFormat(String format) {
        parser = new TitleFormatParser(new AttributeDataProvider(), new CommonTokenStream(new TitleFormatLexer(new ANTLRStringStream(format))));
    }

    /**
     * Returns the formatted string based on the provided object, the object will have the name "object".
     * Calling this method is the same as calling {@link #format(Map)} with a single object in the map with the key "object".
     *
     * @param object The object to take values from during the formatting
     * @return The formatted string
     */
    public String format(Object object) {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("object", object);
        try {
            if (initialized) {
                parser.reset();
            }
            initialized = true;
            return parser.format(items);
        } catch (RecognitionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the formatted string based on the provided objects
     *
     * @param objects The objects which keyword values should be taken from
     * @return The formatted string
     */
    public String format(Map<String, Object> objects) {
        try {
            if (initialized) {
                parser.reset();
            }
            initialized = true;
            return parser.format(objects);
        } catch (RecognitionException e) {
            throw new RuntimeException(e);
        }
    }
}
