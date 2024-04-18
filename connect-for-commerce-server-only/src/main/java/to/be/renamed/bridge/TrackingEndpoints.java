package to.be.renamed.bridge;

import java.util.UUID;

public enum TrackingEndpoints {

    GET_CATEGORIES(UUID.fromString("94bd4f90-6d69-40e3-94d2-4ee467426123"), "[Connect for Commerce] GET categories", "Categories"),
    GET_CATEGORIES_IDS(UUID.fromString("2660bc08-26f4-433d-a135-daca809be9ea"), "[Connect for Commerce] GET categories/ids", "Categories"),
    HEAD_CATEGORIES_TREE(UUID.fromString("f2a8aef4-1afb-4bf6-a804-a9ab161d3887"), "[Connect for Commerce] HEAD categories/tree", "Categories"),
    GET_CATEGORIES_TREE(UUID.fromString("b12e0836-3f85-41a2-925e-b5723fdd0432"), "[Connect for Commerce] GET categories/tree", "Categories"),
    GET_PRODUCTS(UUID.fromString("345a05ac-dc61-49d1-bd8c-35b3131a9e16"), "[Connect for Commerce] GET products", "Products"),
    GET_PRODUCTS_IDS(UUID.fromString("baead5dd-2e55-4390-9d59-7cfa4fb37161"), "[Connect for Commerce] GET products/ids", "Products"),
    HEAD_CONTENT(UUID.fromString("74453665-0a77-415c-ab41-9ceb600fd682"), "[Connect for Commerce] HEAD content", "Content"),
    GET_CONTENT(UUID.fromString("bebb24b3-1d59-4b3b-ab33-a3b8110aa964"), "[Connect for Commerce] GET content", "Content"),
    POST_CONTENT(UUID.fromString("40d96fbd-546b-403b-8344-60e7810bac9b"), "[Connect for Commerce] POST content", "Content"),
    PUT_CONTENT(UUID.fromString("8fe9a31c-c381-468c-bcab-fde6eb5aa8a8"), "[Connect for Commerce] PUT content", "Content"),
    DELETE_CONTENT(UUID.fromString("9369b5cb-ffb0-471b-8827-fba36301e017"), "[Connect for Commerce] DELETE content", "Content"),
    GET_CONTENT_IDS(UUID.fromString("d0523084-873e-43a8-affa-446a28f202f9"), "[Connect for Commerce] GET content/ids", "Content"),
    GET_STOREFRONT_URL(UUID.fromString("f003c8d1-c87b-4619-926a-50785c6b9648"), "[Connect for Commerce] GET storefront-url", "Storefront-URL"),
    GET_LOOKUP_URL(UUID.fromString("bb960cd1-7914-4bae-971b-2ea88b6ce542"), "[Connect for Commerce] GET lookup-url", "Storefront-URL");

    public final UUID uuid;
    public final String label;
    public final String categoryName;

    TrackingEndpoints(final UUID uuid, final String label, final String categoryName) {
        this.uuid = uuid;
        this.label = label;
        this.categoryName = categoryName;
    }


    @Override
    public String toString() {
        return categoryName + " - " + label + " (" + uuid + ")";
    }
}
