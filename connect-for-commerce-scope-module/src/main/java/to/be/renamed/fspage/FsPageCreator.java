package to.be.renamed.fspage;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.error.CreatePageException;
import to.be.renamed.error.ErrorCode;
import de.espirit.common.TypedFilter;
import de.espirit.common.base.Logging;
import de.espirit.common.util.Listable;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.ReferenceEntry;
import de.espirit.firstspirit.access.store.IDProvider;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.pagestore.Page;
import de.espirit.firstspirit.access.store.pagestore.PageFolder;
import de.espirit.firstspirit.access.store.pagestore.PageStoreRoot;
import de.espirit.firstspirit.access.store.sitestore.*;
import de.espirit.firstspirit.access.store.templatestore.PageTemplate;
import de.espirit.firstspirit.access.store.templatestore.TemplateStoreRoot;
import de.espirit.firstspirit.agency.LanguageAgent;
import de.espirit.firstspirit.agency.StoreAgent;
import de.espirit.firstspirit.forms.FormData;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

public class FsPageCreator {
    private static final String CATEGORY_PAGE_TYPE = "category";
    private static final String PRODUCT_PAGE_TYPE = "product";
    private static final String CONTENT_PAGE_TYPE = "content";
    private static final String PAGE_ID_FORM_FIELD = "id";
    private static final String PAGE_TYPE_FORM_FIELD = "type";

    private static final String TEMPLATE_NOT_FOUND = "Template with uid '%s' not found!";
    private static final String INCORRECT_PAGE_TYPE = "Type '%s' does  not match product, category or content!";
    private static final String PAGE_ALREADY_EXISTS = "PageRef for page with id '%s' already exists!";

    private final EcomConnectScope scope;
    private final Language language;
    private PageRef pageRef = null;

    public FsPageCreator(EcomConnectScope scope) {
        this.scope = scope;
        language = scope.getLanguage(null);
    }

    public Language getLanguage() {
        return language;
    }

    @Nullable
    protected static String getPageId(Page page, Language language) {
        Object pageId = page.getFormData().get(language, PAGE_ID_FORM_FIELD).get();
        if (pageId instanceof String) {
            String id = (String) pageId;
            if (!id.isEmpty()) {
                return id;
            }
        }
        return null;
    }

    public PageRef getPageRef(String id) {
        if (pageRef != null) {
            return pageRef;
        }

        Listable<Page> pages = scope.getBroker()
                .requireSpecialist(StoreAgent.TYPE)
                .getStore(Store.Type.PAGESTORE)
                .getChildren(
                        new TypedFilter<>(Page.class) {
                            @Override
                            public boolean accept(Page page) {
                                return Objects.equals(id, getPageId(page, language));
                            }
                        }, true);

        Page page = pages.getFirst();
        if (page != null) {
            List<PageRef> pageRefs = Arrays.stream(page.getIncomingReferences())
                    .filter(ref -> !ref.getRelease() && ref.isType(ReferenceEntry.SITE_STORE_REFERENCE) && !ref.isRemote())
                    .map(ReferenceEntry::getReferencedElement)
                    .filter(PageRef.class::isInstance)
                    .map(PageRef.class::cast)
                    .collect(Collectors.toList());
            if (!pageRefs.isEmpty()) {
                pageRef = pageRefs.listIterator().next();
                if (pageRefs.size() > 1) {
                    Logging.logWarning("Page with uid=" + page.getUid() + " has more than one PageRef", getClass());
                }
            }
        }

        return pageRef;
    }

    public PageRef create(String id, String fsPageTemplate, String pageType, Map<String, Object> displayNames) {
        if (getPageRef(id) != null) {
            Logging.logWarning(format(PAGE_ALREADY_EXISTS, id), this.getClass());
            throw new CreatePageException(format(PAGE_ALREADY_EXISTS, id), null, ErrorCode.PAGE_ALREADY_EXISTS.get());
        }

        StoreAgent storeAgent = scope.getBroker().requireSpecialist(StoreAgent.TYPE);
        TemplateStoreRoot templates = (TemplateStoreRoot) storeAgent.getStore(Store.Type.TEMPLATESTORE);

        PageTemplate template = templates.getPageTemplates().getTemplate(fsPageTemplate);

        if (template == null) {
            Logging.logError(format(TEMPLATE_NOT_FOUND, fsPageTemplate), getClass());
            throw new CreatePageException(format(TEMPLATE_NOT_FOUND, fsPageTemplate), null, ErrorCode.TEMPLATE_NOT_FOUND.get());
        }

        LanguageAgent languageAgent = scope.getBroker().requireSpecialist(LanguageAgent.TYPE);
        Collection<Language> projectLanguages = languageAgent.getProjectLanguages(false).values();
        Language masterLanguage = languageAgent.getMasterLanguage();

        if (pageType.equals(CATEGORY_PAGE_TYPE) || pageType.equals(PRODUCT_PAGE_TYPE)) {
            return createCategoryOrProductPage(id, pageType, displayNames, storeAgent, template, projectLanguages, masterLanguage);
        } else if (pageType.equals(CONTENT_PAGE_TYPE)) {
            return createContentPage(id, pageType, displayNames, storeAgent, template, projectLanguages, masterLanguage);
        } else {
            Logging.logError(format(INCORRECT_PAGE_TYPE, pageType), getClass());
            throw new CreatePageException(format(INCORRECT_PAGE_TYPE, pageType), null, ErrorCode.INCORRECT_PAGE_TYPE.get());
        }
    }

    private PageRef createContentPage(String id, String pageType, Map<String, Object> displayNames, StoreAgent storeAgent, PageTemplate template, Collection<Language> projectLanguages, Language masterLanguage) {
        Map<Language, String> lang2DisplayName = Collections.singletonMap(masterLanguage, id);

        Page page = null;
        PageRefFolder pageRefsContainer = null;

        try {
            PageStoreRoot pages = (PageStoreRoot) storeAgent.getStore(Store.Type.PAGESTORE);

            page = pages.createPage(id, template, true, lang2DisplayName);
            if (!page.isLocked()) {
                page.setLock(true);
            }
            setDisplayNames(displayNames, projectLanguages, page, id, pageType);
            for (Language projectLanguage : projectLanguages) {
                if (getLanguage().equals(projectLanguage)) {
                    if (!page.isTranslated(projectLanguage)) {
                        page.addTranslated(projectLanguage);
                    }
                } else {
                    if (page.isTranslated(projectLanguage)) {
                        page.removeTranslated(projectLanguage);
                    }
                }
            }

            FormData formData = page.getFormData();
            formData.get(getLanguage(), PAGE_ID_FORM_FIELD).set(id);
            formData.get(getLanguage(), PAGE_TYPE_FORM_FIELD).set(pageType);
            page.setFormData(formData);
            page.save();
            page.setLock(false);

            SiteStoreRoot sites = (SiteStoreRoot) storeAgent.getStore(Store.Type.SITESTORE);

            pageRefsContainer = sites.createPageRefFolder(id, lang2DisplayName, true);
            pageRef = pageRefsContainer.createPageRef(page.getUid(), page, true);
            if (!pageRef.isLocked()) {
                pageRef.setLock(true);
            }
            setDisplayNames(displayNames, projectLanguages, pageRef, id, pageType);
            pageRef.save();
        } catch (Exception e) {
            Logging.logWarning(e.getMessage(), e, getClass());
        } finally {
            Stream.of(page, pageRefsContainer, pageRef).forEach(this::unlockElement);
        }
        return pageRef;
    }

    private PageRef createCategoryOrProductPage(String id, String pageType,
                                                Map<String, Object> displayNames,
                                                StoreAgent storeAgent,
                                                PageTemplate template,
                                                Collection<Language> projectLanguages,
                                                Language masterLanguage) {
        String containerUid = '_' + (pageType.equals(CATEGORY_PAGE_TYPE) ? (pageType.substring(0, pageType.length() - 1) + "ies") : (pageType + 's'));
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

            page = pagesContainer.createPage(pageType, template, true);
            if (!page.isLocked()) {
                page.setLock(true);
            }
            setDisplayNames(displayNames, projectLanguages, page, id, pageType);
            FormData formData = page.getFormData();
            formData.get(getLanguage(), PAGE_ID_FORM_FIELD).set(id);
            formData.get(getLanguage(), PAGE_TYPE_FORM_FIELD).set(pageType);
            page.setFormData(formData);
            page.save();
            page.setLock(false);

            SiteStoreRoot sites = (SiteStoreRoot) storeAgent.getStore(Store.Type.SITESTORE);
            pageRefsContainer = (PageRefFolder) sites.getStoreElement(containerUid, SiteStoreFolder.UID_TYPE);
            if (pageRefsContainer == null) {
                pageRefsContainer = sites.createPageRefFolder(containerUid, lang2DisplayName, false);
                pageRefsContainer.setLock(true, true);
                for (Language projectLanguage : projectLanguages) {
                    FolderLangSpec folderLangSpec = pageRefsContainer.getFolderLangSpec(projectLanguage);
                    folderLangSpec.setShowInSiteMap(false);
                    folderLangSpec.setVisible(false);
                }
                pageRefsContainer.save();
            }

            pageRef = pageRefsContainer.createPageRef(page.getUid(), page, true);
            if (!pageRef.isLocked()) {
                pageRef.setLock(true);
            }
            setDisplayNames(displayNames, projectLanguages, pageRef, id, pageType);
            pageRef.save();

        } catch (Exception e) {
            Logging.logWarning(e.getMessage(), e, getClass());
        } finally {
            Stream.of(pagesContainer, page, pageRefsContainer, pageRef).forEach(this::unlockElement);
        }
        return pageRef;
    }

    private void unlockElement(IDProvider element) {
        if (element != null && element.isLocked()) {
            try {
                element.setLock(false, !element.isFolder());
            } catch (Exception e) {
                Logging.logInfo(e.getMessage(), e, getClass());
            }
        }
    }

    private static void setDisplayNames(Map<String, Object> displayNames, Collection<Language> projectLanguages, IDProvider element, String id, String pageType) {
        for (Language language : projectLanguages) {
            boolean displayNameProvided = false;
            for (Map.Entry<String, Object> entry : displayNames.entrySet()) {
                String key = entry.getKey();
                if (key != null && key.equalsIgnoreCase(language.getAbbreviation())) {
                    element.setDisplayName(language, (String) displayNames.get(key));
                    displayNameProvided = true;
                }
            }
            if (!displayNameProvided) {
                element.setDisplayName(language, pageType + "_" + id);
                Logging.logWarning("No page display name found for project language '" + language.getAbbreviation() + "'", FsPageCreator.class);
            }
        }
    }
}
