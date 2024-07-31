package to.be.renamed.dap;

import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.Filterable;
import de.espirit.firstspirit.client.plugin.report.Parameter;
import de.espirit.firstspirit.client.plugin.report.ParameterBoolean;
import de.espirit.firstspirit.client.plugin.report.ParameterMap;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EcomFilterBuilder {

    private final List<Parameter<?>> filters = new LinkedList<>();
    private final Map<String, String> values = new HashMap<>();

    public EcomFilterBuilder addTextField(String name, String placeholder, String defaultValue) {
        filters.add(Parameter.Factory.createText(name, placeholder, defaultValue));
        return this;
    }

    public EcomFilterBuilder addTextField(String name, String placeholder) {
        return addTextField(name, placeholder, "");
    }

    public EcomFilterBuilder addSelect(String name, Map<String, String> items, String defaultValue) {
        if (!items.isEmpty()) {
            filters.add(Parameter.Factory.createSelect(
                name, items.entrySet().stream()
                    .map(item -> Parameter.Factory.createSelectItem(item.getValue(), item.getKey()))
                    .collect(Collectors.toList()),
                defaultValue)
            );
        }
        return this;
    }

    public EcomFilterBuilder addSelect(String name, Map<String, String> items) {
        if (!items.isEmpty()) {
            addSelect(name, items, items.keySet().iterator().next());
        }
        return this;
    }

    private void setFilterMap(ParameterMap filterMap) {
        for (Parameter<?> filter : filters) {
            if (filter instanceof ParameterBoolean) {
                values.put(filter.getName(), Boolean.TRUE.equals(filterMap.get(filter)) ? "true" : null);
            } else {
                String value = (String) filterMap.get(filter);
                values.put(filter.getName(), Strings.isEmpty(value) ? null : value);
            }
        }

    }

    public Map<String, String> getFilter() {
        return values;
    }

    public Filterable asFilterable() {
        return new Filterable() {

            @Override
            public @NotNull List<Parameter<?>> getDefinedParameters() {
                return new ArrayList<>(filters);
            }

            @Override
            public void setFilter(@NotNull ParameterMap filterMap) {
                setFilterMap(filterMap);
            }
        };
    }
}
