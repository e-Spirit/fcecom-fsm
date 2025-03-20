package to.be.renamed;

import to.be.renamed.bridge.EcomIdUtilities;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.StoreElement;
import de.espirit.firstspirit.access.store.StoreListener;
import de.espirit.firstspirit.access.store.pagestore.Page;
import de.espirit.firstspirit.agency.LanguageAgent;
import de.espirit.firstspirit.agency.OperationAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.agency.StoreAgent;
import de.espirit.firstspirit.webedit.WebeditUiAgent;
import de.espirit.firstspirit.webedit.server.ClientScriptOperation;

import net.logicsquad.minifier.MinificationException;
import net.logicsquad.minifier.Minifier;
import net.logicsquad.minifier.js.JSMinifier;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Objects;

/**
 * This StoreListener updates the Report Item Icon, so that
 * a newly created page is instantly reflected with
 * a clear visual change in the Report
 *
 * @param scope Used to get:
 *               1. Project's MasterLanguage
 *               2. ProjectAppConfiguration
 *               3. OperationAgent (for executing JS in ContentCreator)
 */
public record EcomReportListener(EcomConnectScope scope) implements StoreListener {

    private static StoreListener STORE_LISTENER;

    @Override
    public void structureChanged(final StoreElement storeElement) {
    }

    @Override
    public void elementMoved(final StoreElement storeElement, final StoreElement storeElement1) {
    }

    /**
     * Besides other use-cases, this method will be called right after a
     * Page is created in FirstSpirit and the Frontend API has written the
     * EcomId into the according field in form data.
     *
     * @param storeElement The newly created, maybe with an ID set already.
     */
    @Override
    public void elementChanged(final StoreElement storeElement) {
        markManaged(storeElement);
    }

    @Override
    public void addedToScope(final StoreElement storeElement) {
    }

    @Override
    public void removedFromScope(final StoreElement storeElement, final StoreElement parent) {
    }

    /**
     * Helper method to get the ecom pageId in a simple way.
     * It handles the "language creation" problem by just fetching the master language.
     *
     * @param page StoreElement got from elementChanged
     * @return pageId as String if valid and present and null if it's missing or invalid
     */
    private String extractPageId(Page page) {
        final Language masterLanguage = scope.getBroker().requireSpecialist(LanguageAgent.TYPE).getMasterLanguage();
        return EcomIdUtilities.getPageId(page, masterLanguage, scope);
    }

    /**
     * This checks the incoming events and their StoreElement parameter
     * to be a Page and then decides on the icon type.
     *
     * @param storeElement StoreElement got from elementChanged
     */
    private void markManaged(StoreElement storeElement) {
        if (storeElement instanceof Page) {
            // Extract pageId from form data.
            if (!EcomIdUtilities.hasPageIdField((Page) storeElement, scope)) {
                return;
            }

            final String pageId = extractPageId((Page) storeElement);

            if (pageId != null) {
                if (storeElement.getName().startsWith("product")) {
                    markReportIcon(pageId, "fcecom-product-managed.svg", scope.getBroker());
                } else if (storeElement.getName().startsWith("category")) {
                    markReportIcon(pageId, "fcecom-category-managed.svg", scope.getBroker());
                } else {
                    markReportIcon(pageId, "fcecom-content-managed.svg", scope.getBroker());
                }
            }
        }
    }

    /**
     * Registers a StoreListener to the PageStore.
     * Existing StoreListeners are first removed to prevent double events.
     *
     * @param scope Used to get:
     *               1. Project's MasterLanguage
     *               2. ProjectAppConfiguration
     *               3. OperationAgent (for executing JS in ContentCreator)
     */
    public static void register(EcomConnectScope scope) {
        final Store store = scope.getBroker().requireSpecialist(StoreAgent.TYPE).getStore(Store.Type.PAGESTORE);

        if (STORE_LISTENER != null) {
            store.removeStoreListener(STORE_LISTENER);
        }

        STORE_LISTENER = new EcomReportListener(scope);
        store.addStoreListener(STORE_LISTENER);
    }

    /**
     * Sends a command to the CC to execute JavaScript that changes the ReportItem icon.
     * This makes it possible to change the icon to whatever we want, but without
     * any changes in the scroll state of the report or other visual problems.
     *
     * @param ecomId Used to identify the single ReportItem.
     * @param icon   Used to determine the correct icon, e.g. for category / product / content.
     * @param broker Broker to initiate the CC execution.
     */
    public static void markReportIcon(String ecomId, String icon, SpecialistsBroker broker) {
        if (broker.requestSpecialist(WebeditUiAgent.TYPE) != null) {
            String javascript = minifyJS("""
                                             /**
                                              * Finds all elements in the entire page matching `selector`, even if they are in shadowRoots.
                                              * Just like `querySelectorAll`, but automatically expand on all child `shadowRoot` elements.
                                              * Original code by [Domi](https://stackoverflow.com/users/2228771), retrieved from
                                              * @see https://stackoverflow.com/a/71692555/2228771.
                                              * @license Licensed under CC BY-SA 4.0 (https://creativecommons.org/licenses/by-sa/4.0/)
                                              */
                                             const querySelectorAllShadows = (selector, el = document.body) => {
                                                 // recurse on childShadows
                                                 const childShadows = Array.from(el.querySelectorAll('*')).map(el => el.shadowRoot).filter(Boolean);
                                                 const childResults = childShadows.map(child => querySelectorAllShadows(selector, child));
                                             
                                                 // fuse all results into singular, flat array
                                                 const result = Array.from(el.querySelectorAll(selector));
                                                 return result.concat(childResults).flat();
                                             }
                                             
                                             const pageId = '%s'
                                             const icon = '%s'
                                             
                                             const markReportIcon = () => {
                                                 querySelectorAllShadows('.report-entry-wrapper[data-identifier="' + pageId + '"] img.report-entry-icon')
                                                     .forEach((reportItemIcon) => reportItemIcon.setAttribute('src', `${icon}`))
                                             }
                                             
                                             setTimeout(markReportIcon, 500)""".formatted(ecomId, icon));
            Objects.requireNonNull(broker.requireSpecialist(OperationAgent.TYPE).getOperation(ClientScriptOperation.TYPE))
                .perform(javascript, false);
        }
    }

    /**
     * Minifies JavaScript to reduce transported data.
     * If minification does not work, the raw input is returned as fail-safe.
     *
     * @param javascript Raw JavaScript String.
     * @return Minified JavaScript as String.
     */
    private static String minifyJS(final String javascript) {
        try {
            final Reader input = new StringReader(javascript);
            final Writer output = new StringWriter();
            Minifier minifier = new JSMinifier(input);

            minifier.minify(output);
            return output.toString();
        } catch (MinificationException exception) {
            Logging.logWarning("markReportIcon · Could not minifyJS javascript, using non-minified version.", exception, EcomReportListener.class);
            return javascript;
        }
    }
}
