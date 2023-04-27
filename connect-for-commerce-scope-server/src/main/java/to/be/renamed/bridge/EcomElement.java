package to.be.renamed.bridge;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.OrphanedPageRefException;
import de.espirit.common.TypedFilter;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import de.espirit.common.util.Listable;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.ReferenceEntry;
import de.espirit.firstspirit.access.store.IDProvider;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.pagestore.Page;
import de.espirit.firstspirit.access.store.pagestore.PageFolder;
import de.espirit.firstspirit.access.store.pagestore.PageStoreRoot;
import de.espirit.firstspirit.access.store.sitestore.FolderLangSpec;
import de.espirit.firstspirit.access.store.sitestore.PageRef;
import de.espirit.firstspirit.access.store.sitestore.PageRefFolder;
import de.espirit.firstspirit.access.store.sitestore.SiteStoreFolder;
import de.espirit.firstspirit.access.store.sitestore.SiteStoreRoot;
import de.espirit.firstspirit.access.store.templatestore.PageTemplate;
import de.espirit.firstspirit.access.store.templatestore.TemplateStoreRoot;
import de.espirit.firstspirit.agency.LanguageAgent;
import de.espirit.firstspirit.agency.StoreAgent;
import de.espirit.firstspirit.forms.FormData;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EcomElement {

    private final EcomConnectScope scope;
    private final EcomId ecomId;
    private final Language language;
    private PageRef pageRef = null;

    EcomElement(EcomConnectScope scope, EcomId ecomId) {
        this.scope = scope;
        this.ecomId = ecomId;
        language = scope.getLanguage(ecomId.getLang());
        if (ecomId.hasPageRefUid()) {
            pageRef = (PageRef) scope.getBroker().requireSpecialist(StoreAgent.TYPE).getStore(Store.Type.SITESTORE).getStoreElement(ecomId.getPageRefUid(), IDProvider.UidType.SITESTORE_LEAF);
        }
    }

    public Language getLanguage() {
        return language;
    }

    public PageRef getPageRef() {
        if (pageRef == null) {
            Listable<Page> pages = scope.getBroker()
                .requireSpecialist(StoreAgent.TYPE)
                .getStore(Store.Type.PAGESTORE)
                .getChildren(
                    new TypedFilter<>(Page.class) {
                        @Override
                        public boolean accept(Page page) {
                            return Objects.equals(ecomId.getId(), EcomId.getPageId(page, language));
                        }
                    }, true);

            Page page = pages.getFirst();
            if (page != null) {
                List<PageRef> pageRefs = Arrays.stream(page.getIncomingReferences())
                    .filter(ref -> !ref.getRelease() && ref.isType(ReferenceEntry.SITE_STORE_REFERENCE) && !ref.isRemote())
                    .map(ReferenceEntry::getReferencedElement)
                    .filter(el -> el instanceof PageRef)
                    .map(PageRef.class::cast)
                    .collect(Collectors.toList());
                if (!pageRefs.isEmpty()) {
                    pageRef = pageRefs.listIterator().next();
                    if (pageRefs.size() > 1) {
                        Logging.logWarning("Page with uid=" + page.getUid() + " has more then one PageRef", getClass());
                    }
                }
            }
        }
        return pageRef;
    }

    /**
     * de.espirit.firstspirit.webedit.client.api.FSID
     */
    public Map<String, String> getFSID() {
        PageRef pageRef = getPageRef();
        if (pageRef != null) {
            Map<String, String> fsid = new HashMap<>();
            fsid.put("id", String.valueOf(pageRef.getId()));
            fsid.put("store", pageRef.getStore().getType().getName());
            fsid.put("language", getLanguage().getAbbreviation());
            return fsid;
        }
        return null;
    }

    /**
     * Returns the data needed for a bridge call as an FS JsonObject
     *
     * @return data as JsonObject
     * @throws OrphanedPageRefException if page ref has no page
     */
    public EcomElementDTO getEcomElementDTO() throws OrphanedPageRefException {
        PageRef pageRef = getPageRef();
        Page page = pageRef.getPage();
        if (page != null) {
            LanguageAgent languageAgent = scope.getBroker().requireSpecialist(LanguageAgent.TYPE);
            Collection<Language> projectLanguages = languageAgent.getProjectLanguages(false).values();

            return new EcomElementDTO(page.getTemplate().getUid(),
                    pageRef.isStartNode(),
                    pageRef.getUid(),
                    getLanguageDependentLabels(pageRef, projectLanguages),
                    getLanguageDependentPaths(pageRef, projectLanguages));
        }
        return null;
    }

    /**
     * Returns the language dependent labels for a given page ref
     *
     * @param pageRef          PageRef of which the labels are returned
     * @param projectLanguages Languages of the labels
     * @return labels as HashMap with language abbreviations as keys
     */
    private static HashMap<String, String> getLanguageDependentLabels(PageRef pageRef, Collection<Language> projectLanguages) {
        // create label obj
        HashMap<String, String> label = new HashMap<>();
        // fill label obj

        for (Language language : projectLanguages) {
            String localizedLabel = pageRef.getDisplayName(language);
            if (Strings.isEmpty(localizedLabel)) {
                localizedLabel = pageRef.getDisplayName(pageRef.getProject().getMasterLanguage());
            }
            label.put(language.getAbbreviation().toLowerCase(Locale.getDefault()), localizedLabel);
        }

        return label;
    }

    /**
     * Returns the language dependent paths for a given page ref
     *
     * @param pageRef          PageRef of which the labels are returned
     * @param projectLanguages Languages of the labels
     * @return paths as HashMap with language abbreviations as keys
     */
    private static HashMap<String, String> getLanguageDependentPaths(PageRef pageRef, Collection<Language> projectLanguages) {
        HashMap<String, String> path = new HashMap<>();
        for (Language language : projectLanguages) {
            String localizedPath = encodePath(pageRef.getDisplayName(language));
            path.put(language.getAbbreviation().toLowerCase(Locale.ROOT), localizedPath);
        }
        return path;
    }

    /**
     * URL Encodes a path segment
     *
     * @param path The string to encode
     * @return A URL encoded string
     */
    private static String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    public void create(String templateUid) {
        if (getPageRef() == null) {
            StoreAgent storeAgent = scope.getBroker().requireSpecialist(StoreAgent.TYPE);
            TemplateStoreRoot templates = (TemplateStoreRoot) storeAgent.getStore(Store.Type.TEMPLATESTORE);

            String type = ecomId.getType();
            PageTemplate template = templates.getPageTemplates().getTemplate(templateUid != null ? templateUid : type);

            if (template == null) {
                throw new IllegalArgumentException("Template with uid=" + (templateUid != null ? templateUid : type) + " not found!");
            }

            LanguageAgent languageAgent = scope.getBroker().requireSpecialist(LanguageAgent.TYPE);
            Collection<Language> projectLanguages = languageAgent.getProjectLanguages(false).values();
            Language masterLanguage = languageAgent.getMasterLanguage();

            if (ecomId instanceof EcomContent) {
                Map<Language, String> lang2DisplayName = Collections.singletonMap(masterLanguage, ecomId.getId());

                Page page = null;
                PageRefFolder pageRefsContainer = null;

                try {
                    PageStoreRoot pages = (PageStoreRoot) storeAgent.getStore(Store.Type.PAGESTORE);
                    page = pages.createPage(ecomId.getId(), template, true, lang2DisplayName);
                    if (!page.isLocked()) {
                        page.setLock(true);
                    }

                    for (Language language : projectLanguages) {
                        if (getLanguage().equals(language)) {
                            if (!page.isTranslated(language)) {
                                page.addTranslated(language);
                            }
                        } else {
                            if (page.isTranslated(language)) {
                                page.removeTranslated(language);
                            }
                        }
                    }

                    FormData formData = page.getFormData();
                    formData.get(getLanguage(), EcomId.PAGE_ID_FORM_FIELD).set(ecomId.getId());
                    page.setFormData(formData);
                    page.save();
                    page.setLock(false);

                    SiteStoreRoot sites = (SiteStoreRoot) storeAgent.getStore(Store.Type.SITESTORE);
                    pageRefsContainer = sites.createPageRefFolder(ecomId.getId(), lang2DisplayName, true);
                    pageRef = pageRefsContainer.createPageRef(page.getUid(), page, true);
                } catch (Exception e) {
                    Logging.logWarning(e.getMessage(), e, EcomId.class);
                } finally {
                    Stream.of(page, pageRefsContainer, pageRef).forEach(element -> {
                        if (element != null && element.isLocked()) {
                            try {
                                element.setLock(false, !element.isFolder());
                            } catch (Exception e) {
                                Logging.logInfo(e.getMessage(), e, getClass());
                            }
                        }
                    });
                }

            } else {
                // EcomProduct / EcomCategory
                String containerUid = '_' + (type.endsWith("y") ? (type.substring(0, type.length() - 1) + "ies") : (type + 's'));
                Map<Language, String> lang2DisplayName = Collections.singletonMap(masterLanguage, '[' + containerUid.substring(1) + ']');

                PageFolder pagesContainer = null;
                Page page = null;
                PageRefFolder pageRefsContainer = null;

                try {
                    PageStoreRoot pages = (PageStoreRoot) storeAgent.getStore(Store.Type.PAGESTORE);
                    pagesContainer = (PageFolder) pages.getStoreElement(containerUid, PageFolder.UID_TYPE);
                    if (pagesContainer == null) {
                        pagesContainer = pages.createPageFolder(containerUid, lang2DisplayName, false);
                    }

                    page = pagesContainer.createPage(type, template, true);
                    if (!page.isLocked()) {
                        page.setLock(true);
                    }
                    FormData formData = page.getFormData();
                    formData.get(getLanguage(), EcomId.PAGE_ID_FORM_FIELD).set(ecomId.getId());
                    page.setFormData(formData);
                    page.save();
                    page.setLock(false);

                    SiteStoreRoot sites = (SiteStoreRoot) storeAgent.getStore(Store.Type.SITESTORE);
                    pageRefsContainer = (PageRefFolder) sites.getStoreElement(containerUid, SiteStoreFolder.UID_TYPE);
                    if (pageRefsContainer == null) {
                        pageRefsContainer = sites.createPageRefFolder(containerUid, lang2DisplayName, false);
                        pageRefsContainer.setLock(true, true);
                        for (Language language : projectLanguages) {
                            FolderLangSpec folderLangSpec = pageRefsContainer.getFolderLangSpec(language);
                            folderLangSpec.setShowInSiteMap(false);
                            folderLangSpec.setVisible(false);
                        }
                        pageRefsContainer.save();
                    }

                    pageRef = pageRefsContainer.createPageRef(page.getUid(), page, true);
                } catch (Exception e) {
                    Logging.logWarning(e.getMessage(), e, EcomId.class);
                } finally {
                    Stream.of(pagesContainer, page, pageRefsContainer, pageRef).forEach(element -> {
                        if (element != null && element.isLocked()) {
                            try {
                                element.setLock(false, !element.isFolder());
                            } catch (Exception e) {
                                Logging.logInfo(e.getMessage(), e, getClass());
                            }
                        }
                    });
                }
            }
        }
    }

    public void updatePageId(String pageId) {
        PageRef pageRef = getPageRef();
        if (pageRef != null) {
            Page page = pageRef.getPage();
            try {
                page.setLock(true, true);
                FormData formData = page.getFormData();
                formData.get(getLanguage(), EcomId.PAGE_ID_FORM_FIELD).set(pageId);
                page.setFormData(formData);
                page.save();
            } catch (Exception e) {
                Logging.logError(e.getMessage(), e, getClass());
            } finally {
                if (page.isLocked()) {
                    try {
                        page.setLock(false, true);
                    } catch (Exception e) {
                        Logging.logWarning(e.getMessage(), e, getClass());
                    }
                }
            }
        }
    }
}
