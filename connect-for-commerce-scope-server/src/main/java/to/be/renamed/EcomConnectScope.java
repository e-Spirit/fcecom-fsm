package to.be.renamed;

import to.be.renamed.bridge.EcomContent;
import to.be.renamed.bridge.EcomElement;
import to.be.renamed.bridge.EcomId;
import to.be.renamed.bridge.client.Json;
import to.be.renamed.error.BridgeException;
import to.be.renamed.module.ServiceFactory;
import to.be.renamed.module.projectconfig.access.ProjectAppConfigurationService;
import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;

import de.espirit.common.TypedFilter;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.sitestore.PageRef;
import de.espirit.firstspirit.access.store.templatestore.PageTemplate;
import de.espirit.firstspirit.access.store.templatestore.TemplateStoreRoot;
import de.espirit.firstspirit.agency.LanguageAgent;
import de.espirit.firstspirit.agency.OperationAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.agency.StoreAgent;
import de.espirit.firstspirit.agency.UIAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessPlugin;
import de.espirit.firstspirit.webedit.WebeditUiAgent;
import de.espirit.firstspirit.webedit.server.ClientScriptOperation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EcomConnectScope {

    private static final String COULD_NOT_GET_URL = "Could not get URL from bridge, check bridge logs";

    private static final Map<Locale, ResourceBundle> BUNDLES = new HashMap<>();
    private static final String BUNDLE_NAME = "reports";
    private final SpecialistsBroker broker;
    private Language language;

    public EcomConnectScope(SpecialistsBroker broker) {
        this.broker = broker;

        WebeditUiAgent webeditUiAgent = broker.requestSpecialist(WebeditUiAgent.TYPE);
        if (webeditUiAgent != null) {
            language = webeditUiAgent.getDisplayLanguage();
        } else {
            UIAgent uiAgent = broker.requestSpecialist(UIAgent.TYPE);
            language = uiAgent != null ? uiAgent.getDisplayLanguage() : null;
        }

        if (language == null) {
            language = broker.requireSpecialist(LanguageAgent.TYPE).getMasterLanguage();
        }
    }

    public static EcomConnectScope create(SpecialistsBroker broker) {
        return new EcomConnectScope(broker);
    }

    public SpecialistsBroker getBroker() {
        return broker;
    }

    public Language getLanguage(String lang) {
        return lang == null ? getLanguage() : broker.requireSpecialist(LanguageAgent.TYPE).getProjectLanguages(false).get(lang);
    }

    public Language getLanguage() {
        return language;
    }

    public static String getLang(Language language) {
        return language.getAbbreviation();
    }

    public String getLang() {
        return getLang(getLanguage());
    }

    public String getLabel(Locale locale, String key) {
        BUNDLES.computeIfAbsent(locale, loc -> {
            Locale defaultLocale = Locale.ROOT;
            Locale.setDefault(Locale.ENGLISH);
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, loc);
            Locale.setDefault(defaultLocale);
            return bundle;
        });
        try {
            return BUNDLES.get(locale).getString(key);
        } catch (Exception e) {
            Logging.logWarning(e.getMessage(), e, EcomConnectScope.class);
            return key;
        }
    }

    public String getLabel(Language language, String key) {
        return getLabel(language.getLocale(), key);
    }

    public String getLabel(String key) {
        return getLabel(language, key);
    }


    public void openReport(SpecialistsBroker broker, Class<? extends DataAccessPlugin<?>> dap, Map<String, Object> params) {
        final ProjectAppConfigurationService projectAppConfigurationService = ServiceFactory.getProjectAppConfigurationService(broker);
        final ProjectAppConfiguration projectAppConfiguration = projectAppConfigurationService.loadConfiguration();
        // without cc extensions
        if (!projectAppConfiguration.getGeneralConfig().useCCExtensions()) {
            String javascript = String.format("() => (WE_API.Report.show(\"%s\", %s, true))", dap.getName(), Json.asJsonElement(params).toString());
            Objects.requireNonNull(broker.requireSpecialist(OperationAgent.TYPE)
                                       .getOperation(ClientScriptOperation.TYPE))
                .perform(javascript, false);
        } else {
            // with cc extensions
            EcomContentCreatorMessage.create("openReport").add("reportParam", dap.getName()).add(params).apply(broker);
        }
    }

    public void openReport(SpecialistsBroker broker, Class<? extends DataAccessPlugin<?>> dap) {
        openReport(broker, dap, Collections.emptyMap());
    }

    public void openStoreFrontUrl(SpecialistsBroker broker, EcomId ecomId) {
        EcomContentCreatorMessage message = EcomContentCreatorMessage.create("openStoreFrontUrl");

        try {
            message.add("url", ServiceFactory.getBridgeService(broker).getStoreFrontUrl(ecomId));
        } catch (BridgeException e) {
            Logging.logWarning(COULD_NOT_GET_URL, e, this.getClass());
        }

        message
            .add("id", ecomId.getId())
            .add("type", ecomId.getType())
            .apply(broker);
    }


    public void setContentCreatorPreviewElement(SpecialistsBroker broker, EcomId ecomId) {
        EcomContentCreatorMessage message = EcomContentCreatorMessage.create("setContentCreatorPreviewElement");

        try {
            message.add("url", ServiceFactory.getBridgeService(broker).getStoreFrontUrl(ecomId));
        } catch (BridgeException e) {
            Logging.logWarning(COULD_NOT_GET_URL, e, this.getClass());
        }
        message
            .add("id", ecomId.getId())
            .add("type", ecomId.getType())
            .add("lang", ecomId.getLang())
            .add("url", ServiceFactory.getBridgeService(broker).getStoreFrontUrl(ecomId));

        EcomElement element = ecomId.getElement(this);
        if (element.getPageRef() == null) {
            if (ecomId instanceof EcomContent) {
                message.add("templates", ((TemplateStoreRoot) broker.requireSpecialist(StoreAgent.TYPE)
                    .getStore(Store.Type.TEMPLATESTORE))
                    .getPageTemplates()
                    .getChildren(new TypedFilter<>(PageTemplate.class) {
                        @Override
                        public boolean accept(PageTemplate template) {
                            return !template.isHidden();
                        }
                    }, true).toList().stream()
                    .map(template -> Map.of("id", template.getUid(),
                                            "label", template.getDisplayName(getLanguage())))
                    .collect(Collectors.toList())
                );
            }
        } else {
            message.add("fsid", ecomId.getElement(this).getFSID());
        }
        message.apply(broker);
    }


    public void setContentCreatorPreviewElement(SpecialistsBroker broker, String id, String pageType, PageRef pageRef) {
        final ProjectAppConfigurationService projectAppConfigurationService = ServiceFactory.getProjectAppConfigurationService(broker);
        final ProjectAppConfiguration projectAppConfiguration = projectAppConfigurationService.loadConfiguration();
        // without cc extensions
        if (!projectAppConfiguration.getGeneralConfig().useCCExtensions()) {
            String javascript = String.format("() => (WE_API.Common.setPreviewElement(%s))", Json.asJsonElement(getFSID(pageRef)).toString());
            broker.requireSpecialist(OperationAgent.TYPE)
                .getOperation(ClientScriptOperation.TYPE)
                .perform(javascript, false);
        } else {
            // with cc extensions
            EcomContentCreatorMessage message = EcomContentCreatorMessage.create("setContentCreatorPreviewElement")
                .add("id", id)
                .add("type", pageType)
                .add("lang", this.getLanguage())
                .add("url", null)
                .add("fsid", getFSID(pageRef));

            message.apply(broker);
        }
    }

    private Map<String, String> getFSID(PageRef pageRef) {
        if (pageRef != null) {
            Map<String, String> fsid = new HashMap<>();
            fsid.put("id", String.valueOf(pageRef.getId()));
            fsid.put("store", pageRef.getStore().getType().getName());
            fsid.put("language", getLanguage().getAbbreviation());
            return fsid;
        }
        return Collections.emptyMap();
    }
}
