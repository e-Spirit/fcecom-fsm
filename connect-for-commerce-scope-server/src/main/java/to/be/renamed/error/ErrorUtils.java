package to.be.renamed.error;

import de.espirit.common.base.Logging;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static java.lang.String.format;

/**
 * Utilities for error handling
 */
public final class ErrorUtils {
    private static final String ERROR_CODES_BUNDLE_NAME = "ErrorCodes";
    private static final String NO_RESOURCE_BUNDLE_FOR_LOCALE = "Could not find resource bundle '%s' for locale '%s', using default bundle";
    private ErrorUtils() {}

    /**
     * Converts a list of BodyValidationErrors to a pretty string
     * @param bridgeErrors List of BridgeError
     * @param locale Locale for localization
     * @return A localized pretty string containing error code and description
     */
    public static String prettyErrorString(List<BridgeError> bridgeErrors, Locale locale) {
        ResourceBundle errorCodes;
        try {
            errorCodes = ResourceBundle.getBundle(ERROR_CODES_BUNDLE_NAME, locale);
        } catch (MissingResourceException e) {
            Logging.logWarning(format(NO_RESOURCE_BUNDLE_FOR_LOCALE, ERROR_CODES_BUNDLE_NAME, locale.toString()), e,  ErrorUtils.class);
            errorCodes = ResourceBundle.getBundle(ERROR_CODES_BUNDLE_NAME);
        }

        StringBuilder prettyErrors = new StringBuilder();
        if (!bridgeErrors.isEmpty()) {
            for(BridgeError bridgeError : bridgeErrors) {
                prettyErrors.append(errorCodes.getString("errorCode")).append(" ");
                prettyErrors.append(bridgeError.getCode());
                prettyErrors.append(" | ");
                prettyErrors.append(format(errorCodes.getString(bridgeError.getCode()), bridgeError.getField()));
                prettyErrors.append("\n");
            }
        }
        return prettyErrors.toString();
    }


}
