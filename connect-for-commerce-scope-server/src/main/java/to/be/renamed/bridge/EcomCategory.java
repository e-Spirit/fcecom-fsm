package to.be.renamed.bridge;

import to.be.renamed.bridge.client.Json;

import de.espirit.firstspirit.json.JsonArray;
import de.espirit.firstspirit.json.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EcomCategory extends EcomId implements Serializable {

    private static final long serialVersionUID = 151051737286568433L;
    private final EcomCategory parent;
    private final List<EcomCategory> children;

    public EcomCategory(Json json) {
       this(json, null);
    }

    private EcomCategory(Json json, EcomCategory parent) {
        super(json);
        this.parent = parent;
        this.children = new LinkedList<>();

        if (hasChildren(json)) {
            ((JsonArray) json.getValue().get("children").getValue()).stream()
                .map(child -> new EcomCategory(new Json((JsonObject) child), this))
                .filter(EcomCategory::isValid)
                .forEach(children::add);
        }
    }

    private boolean hasChildren(final Json json) {
        return json.getValue().hasAttribute("children") &&
               json.getValue().get("children").getValue() != null &&
               !"".equals(json.getValue().get("children").getValue()) &&
               !"[]".equals(json.getValue().get("children").getValue());
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
}
