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
