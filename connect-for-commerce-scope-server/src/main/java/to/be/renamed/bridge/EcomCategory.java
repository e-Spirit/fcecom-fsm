package to.be.renamed.bridge;

import to.be.renamed.bridge.client.Json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;

public class EcomCategory extends EcomId implements Serializable {

    private static final long serialVersionUID = 151051737286568433L;
    private static final String CHILDREN_KEY = "children";
    private final EcomCategory parent;
    private final List<EcomCategory> children;

    public EcomCategory(Json json) {
        this(json, null);
    }

    public EcomCategory(String id, String type, String lang, String pageRefUid, String label) {
        super(id, type, lang, pageRefUid, label);
        this.parent = null;
        this.children = null;
    }

    private EcomCategory(Json json, EcomCategory parent) {
        super(json);
        this.parent = parent;
        this.children = new LinkedList<>();

        if (hasChildren(json)) {
            (json.getValue().get(CHILDREN_KEY).getAsJsonArray())
                .forEach(child -> {
                    EcomCategory category = new EcomCategory(new Json(child), this);
                    if (category.isValid()) {
                        children.add(category);
                    }
                });
        }
    }

    private boolean hasChildren(final Json json) {
        return json.getValue().has(CHILDREN_KEY) &&
               json.getValue().get(CHILDREN_KEY) != null &&
               json.getValue().get(CHILDREN_KEY).isJsonArray() &&
               !json.getValue().get(CHILDREN_KEY).getAsJsonArray().isEmpty();
    }

    public boolean hasParent() {
        return parent != null;
    }

    public EcomCategory getParent() {
        return parent;
    }

    public List<EcomCategory> getParentChain() {
        List<EcomCategory> parentChain = new LinkedList<>();
        EcomCategory current = this;
        while (current.hasParent()) {
            current = current.getParent();
            parentChain.add(0, current);
        }
        return parentChain;
    }

    public List<EcomCategory> getHierarchy() {
        List<EcomCategory> hierarchy = getParentChain();
        hierarchy.add(this);
        return hierarchy;
    }

    public List<EcomCategory> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public String getType() {
        return EcomId.CATEGORY_TEMPLATE_UID;
    }

    @Override
    public String toString() {
        return format(
            "EcomCategory {id: %s, type: %s, lang: %s, pageRefUid: %s, label: %s, parentId: %s, childrenSize: %d}",
            id,
            type,
            lang,
            pageRefUid,
            label,
            parent != null ? parent.getId() : "null",
            children != null ? children.size() : 0
        );
    }
}
