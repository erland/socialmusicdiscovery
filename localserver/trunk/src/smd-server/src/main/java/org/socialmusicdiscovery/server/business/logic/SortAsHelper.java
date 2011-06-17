package org.socialmusicdiscovery.server.business.logic;


import org.socialmusicdiscovery.server.api.ConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.config.MappedConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.config.MergedConfigurationManager;
import org.socialmusicdiscovery.server.business.logic.config.PersistentConfigurationManager;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Helper class which generates "sortAs" values based on regular expressions
 */
public class SortAsHelper {
    private static ConfigurationContext config = new MappedConfigurationContext(SortAsHelper.class.getName() + ".", new MergedConfigurationManager(new PersistentConfigurationManager()));

    /**
     * Generate a sortAs value based on the object type and value passed as input
     * @param type The type of object to get a sortAs value for
     * @param basedOnValue The value to generate the sortAs value on, this is typically the value of the "name" attribute of the entity
     * @return The generated sortAs value
     */
    public static String getSortAsForValue(String type, String basedOnValue) {

        // Do advanced regular expression replacement
        String regExp = config.getStringParameter(type + ".sortAsExpression");
        String valueExp = config.getStringParameter(type + ".sortAsValue");
        String result;
        if (basedOnValue != null && valueExp != null && regExp != null && regExp.length()>0) {
            result = basedOnValue.replaceAll(regExp, valueExp).trim();
            if(result.length()==0) {
                result = basedOnValue;
            }
        } else {
            result = basedOnValue;
        }

        // Remove accented characters
        if (result != null && config.getBooleanParameter(type + ".removeAccents", false)) {
            String nfdNormalizedString = Normalizer.normalize(result, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            result = pattern.matcher(nfdNormalizedString).replaceAll("");

        }

        // Remove articles that should be ignored
        String ignoredArticles = config.getStringParameter(type + ".ignoredArticles");
        if (result != null && ignoredArticles != null && ignoredArticles.length()>0) {
            String tmpResult = result.replaceAll(ignoredArticles,"").trim();
            if(tmpResult.length()>0) {
                result = tmpResult;
            }
        }

        // Convert to upper case
        if (result != null && config.getBooleanParameter(type + ".uppercase", true)) {
            result = result.toUpperCase();
        }

        // Remove punctuation characters
        if (result != null && config.getBooleanParameter(type + ".removePunctuation", true)) {
            String tmpResult = result.replaceAll("\\p{Punct}","");
            if(tmpResult.length()>0) {
                result = tmpResult;
            }
        }

        // Remove duplicate spaces and extra spaces in beginning end of value
        if (result != null) {
            return result.replaceAll("  "," ").trim();
        }else {
            return result;
        }
    }
}
