## [3.17.1](https://github.com/e-Spirit/fcecom-fsm/compare/v3.17.0...v3.17.1) (2025-08-22)

### Changes

* Hiding the category tree dropdown in the category report, if the category tree is just a flat list of categories.

## [3.17.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.16.1...v3.17.0) (2025-03-20)

### Changes

* Added the ability to change the standard form field names for ShopID and PageType.
* Unifed the internal usage of page types.

## [3.16.1](https://github.com/e-Spirit/fcecom-fsm/compare/v3.16.0...v3.16.1) (2025-03-10)

### Changes

* Moved `nimbus-jose-jwt` dependency to the version catalog.

## [3.16.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.15.5...v3.16.0) (2025-02-05)

### Changes

* Added a new ShareView feature that enables secure sharing of preview content outside of the ContentCreator.

### UPDATE NOTICE

ShareView is a mode of the Frontend API ecosystem that allows users to preview content outside the ContentCreator without requiring it to be released in FirstSpirit.

This feature involves a token generation process that grants users access to a generated token, enabling them to view preview content from the Frontend API Backend or a similar implementation of the Frontend API Server package.

While the functionality works out of the box, some configuration steps are required to enable this view. Refer to the [Frontend API documentation](https://docs.e-spirit.com/ecom/fsconnect-com-api/fsconnect-com-frontend-api/latest/share-view/).

## [3.15.5](https://github.com/e-Spirit/fcecom-fsm/compare/v3.15.4...v3.15.5) (2025-01-10)

### Changes

* Fixed an issue where the icon in reports did not update immediately after adding a category, product, or content page to FirstSpirit.

## [3.15.4](https://github.com/e-Spirit/fcecom-fsm/compare/v3.15.3...v3.15.4) (2025-01-07)

### Changes

* Replaced throwing an exception if the CaaS executable can't be found by logging an info message instead.
* Updated gradle wrapper version to 8.5.
* Introduced a gradle version catalog for managing dependencies.
* Updated firstspirit-module plugin to version 6.4.1.
* Updated firstspirit-module-annotations plugin to version 6.4.1.
* Updated guava to version 33.0.0-jre.
* Updated gson version 2.11.0.
* Updated Java version to 17.
* Added missing dependency org.apache.commons:commons-lang3.
* Added missing dependency net.logicsquad:minifier.
* Removed unused dependency com.espirit.caas:caas-connect-global.

## [3.15.3](https://github.com/e-Spirit/fcecom-fsm/compare/v3.15.2...v3.15.3) (2024-11-06)

### Changes

* Make the `EcomConnectClientResourcePlugin` only read the configuration if the project app is installed in the current project.

## [3.15.2](https://github.com/e-Spirit/fcecom-fsm/compare/v3.15.1...v3.15.2) (2024-10-30)

### Changes

* A problem in the handling of categories and products with the same ID has been fixed.

## [3.15.1](https://github.com/e-Spirit/fcecom-fsm/compare/v3.15.0...v3.15.1) (2024-10-14)

### Changes

* Applied consistent formatting across all files.

## [3.15.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.14.1...v3.15.0) (2024-10-01)

### Changes

* Added a new automated test connection feature, accessible via an executable.

## [3.14.1](https://github.com/e-Spirit/fcecom-fsm/compare/v3.14.0...v3.14.1) (2024-09-27)

### Changes

* Improved error handling to handle exception that occurred when the ID form field was missing for a page.
* Fixed an incorrect return value when retrieving multiple products or categories.

## [3.14.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.13.0...v3.14.0) (2024-09-09)

### Changes

* Streamlined EcomConnectScope object creation by utilizing static `create` method.

## [3.13.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.12.0...v3.13.0) (2024-09-04)

### Changes

* Added executable for adding AI generated seo tags to a product page.
* Fixed a problem causing the category tree filter to not update when the UI language was changed.
* Refactored code to improve readability.
* The FSM now sends a new header containing the project uuid.
* Added javadoc to EcomElement.
* Improved automatic creation of CaaS indices.
* Added a visual indication inside the reports for shop-driven pages that are managed in FirstSpirit.
* Avoid potential concurrency issues when accessing labels.
* Fixed a bug that prevented the reports to be shown inside the SiteArchitect.

## [3.12.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.11.0...v3.12.0) (2024-07-31)

### Changes

* Made cache configurable in the project app configuration.
* Removed workaround that was previously required to fix the x-total header.

## [3.11.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.10.0...v3.11.0) (2024-07-10)

### Changes

* Added executables for bulk page creation of product and category pages.
* Fixed a bug in the config migration which caused unnecessary migration.
* Small internal changes.

## [3.10.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.9.0...v3.10.0) (2024-06-20)

### Changes

* Added a search by category name to the category report.

### UPDATE NOTICE

* Keep in mind that your bridge must support a search query for the `/categories` endpoint.
The bridges since version 2.5.0 support this.

## [3.9.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.8.1...v3.9.0) (2024-06-10)

### Changes

* Added paginated loading of report items.
* Removed PagedBridgeRequest class.
* Fixed the displaying of the number of search results in the reports.

## [3.8.1](https://github.com/e-Spirit/fcecom-fsm/compare/v3.8.0...v3.8.1) (2024-06-05)

### Changes

* Refactoring of DAP classes.

## [3.8.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.7.0...v3.8.0) (2024-05-17)

### Changes

* Extended Test Connection with request to `/categories/ids`

## [3.7.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.6.0...v3.7.0) (2024-04-18)

### Changes

* Updated dependencies.
* Added anonymous tracking of Bridge API usage.

### UPDATE NOTICE

* The FirstSpirit module now anonymously logs the internal use of various Bridge API features.
  This serves the sole purpose of concentrating the further development of the module on utilized functions.

## [3.6.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.5.0...v3.6.0) (2024-03-22)

### Changes

* Updated dependencies.

## [3.5.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.4.1...v3.5.0) (2024-01-30)

### Changes

* The module now creates a CaaS index for `findPage` queries of the Frontend API

## [3.4.1](https://github.com/e-Spirit/fcecom-fsm/compare/v3.4.0...v3.4.1) (2024-01-19)

### Changes

* Removed obsolete check for new content endpoint.

## [3.4.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.3.0...v3.4.0) (2023-11-27)

### Changes

* Removed unused error codes.
* Added missing icons to the reports in the SiteArchitect.
* The unused `name` parameter for some OpenStorefrontUrl types was removed to unify the behavior between different triggers.

## [3.3.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.2.0...v3.3.0) (2023-11-16)

### Changes

* Fixed incorrect loading of the ContentCreator extension.
* More flexible specification of the "Bridge API URL" in the project component.
* Improved mapping of store languages to FirstSpirit languages.

### UPDATE NOTICE

* The path component `/api`, which was previously fixed for bridges and cannot be configured, is now expected as an optional part of the "Bridge API
  URL" in the configuration of the project component. This enables more flexible hosting of a bridge. To ensure compatibility for existing projects,
  this adjustment is made automatically when the project component is updated.

## [3.2.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.1.0...v3.2.0) (2023-10-25)

### Changes

* Added a warning dialog that is displayed in the ContentCreator with information about the error in case of connection issues with the bridge.

## [3.1.0](https://github.com/e-Spirit/fcecom-fsm/compare/v3.0.0...v3.1.0) (2023-10-06)

### Changes

* More detailed presentation of the results of the bridge connection tests in the project component.

Information on previous releases can be found in
the [Release Notes](https://docs.e-spirit.com/ecom/fsconnect-com/FirstSpirit_Connect_for_Commerce_Releasenotes_EN.html).
