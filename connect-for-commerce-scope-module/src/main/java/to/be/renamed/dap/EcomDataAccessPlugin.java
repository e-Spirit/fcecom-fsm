package to.be.renamed.dap;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.client.plugin.Item;
import de.espirit.firstspirit.client.plugin.JavaClientExecutablePluginItem;
import de.espirit.firstspirit.client.plugin.WebeditExecutablePluginItem;
import de.espirit.firstspirit.client.plugin.report.JavaClientExecutableReportItem;
import de.espirit.firstspirit.client.plugin.report.ReportContext;
import de.espirit.firstspirit.webedit.plugin.report.WebeditExecutableReportItem;

import java.util.Collection;
import java.util.Collections;

import javax.swing.*;

public interface EcomDataAccessPlugin {

    interface Defaults<T> {

        default boolean isAvailable() {
            return true;
        }

        default Collection<StaticReportItem> getStaticReportItems() {
            return Collections.emptyList();
        }

        default void getReportFilter(EcomFilterBuilder filters) {
            // no filters
        }

        default String getItemExtract(T item, Language language) {
            return null;
        }

        default String getItemImageUrl(T item, Language language) {
            return null;
        }

        default String getItemFlyoutHtml(T item, Language language) {
            return null;
        }

        default void onItemClick(ReportContext<T> context) {
            // no click behavior
        }

        default Collection<ReportItemAction<T>> getReportItemActions() {
            return Collections.emptyList();
        }
    }

    interface StaticReportItem extends Item<BaseContext>, WebeditExecutablePluginItem<BaseContext>, JavaClientExecutablePluginItem<BaseContext> {

        default String getIconPath(BaseContext context) {
            return null;
        }

        default Icon getIcon(BaseContext context) {
            return null;
        }

        default boolean isVisible(BaseContext context) {
            return true;
        }

        default boolean isEnabled(BaseContext context) {
            return true;
        }
    }

    interface ReportItemAction<T> extends WebeditExecutableReportItem<T>, JavaClientExecutableReportItem<T> {

        default String getIconPath(ReportContext<T> context) {
            return null;
        }

        default Icon getIcon(ReportContext<T> context) {
            return null;
        }

        default boolean isVisible(ReportContext<T> context) {
            return true;
        }

        default boolean isEnabled(ReportContext<T> context) {
            return true;
        }
    }


}
