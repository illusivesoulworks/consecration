# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
Prior to version 6.0.0, this project used MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

## [6.0.0+1.19.2] - 2023.07.10
### Added
- Added Fabric and Quilt support
### Changed
- Changed Consecration I -> Consecration II potion brewing to use Glowstone Dust instead of Redstone Dust
- Configuration system is now provided by SpectreLib
- Configuration file is now located in the root folder's `defaultconfigs` folder
- Changed to [Semantic Versioning](http://semver.org/spec/v2.0.0.html)
- Updated to Minecraft 1.19.2

## [1.18.2-5.0.1.3] - 2023.07.03
### Added
- Added `uk_ua` localization (thanks cmbcoldspot!) [#70](https://github.com/illusivesoulworks/consecration/pull/70)

## [1.18.2-5.0.1.2] - 2023.01.29
### Fixed
- Fixed certain mods crashing with Consecration when using their armor

## [1.18.2-5.0.1.1] - 2022.11.19
### Fixed
- Fixed holy material parsing failing in some cases when the material does not start with the material name

## [1.18.2-5.0.1.0] - 2022.10.09
### Added
- Added Tinkers' Construct integration
- Added Silent Gear integration
- Added Werewolves - Become a Beast! integration (if `"silver"` is added as a holy material)
- Added Tetra integration
- [API] Added "smite" as a ToolAction that will enable smiting behavior for any tools that qualify
### Fixed
- Fixed holy material parsing to ignore namespaces, existing configurations will still work but mod integrations
  and future features will rely on namespace-less entries (i.e. use `"gold"` instead of `"minecraft:gold"`)

## [1.18.2-5.0.0.2] - 2022.03.29
### Added
- Added JEED support [#62](https://github.com/TheIllusiveC4/Consecration/issues/62)
### Fixed
- Fixed missing Consecration effect texture
- Fixed NPE crashes

## [1.18.2-5.0.0.1] - 2022.03.26
### Fixed
- Fixed CurseForge upload

## [1.18.2-5.0.0.0] - 2022.03.26
### Changed
- Updated to Minecraft 1.18.2
- Optimized performance on servers
- Changed enchantment name from "Shadow Protection" to "Undead Protection"
- All holy sources except for `holyDamage` and `holyMaterials` have been moved from configs to tags. Tagging anything as
  `consecration:holy` will identify the object as holy for the purposes of Consecration
- Undead types have been moved from configs to tags. Tagging an entity as `consecration:undead` will mark it as an
  undead. The tags `consecration:fire_resistant`, `consecration:holy_resistant`, and `consecration:resistant` mark undead
  and additionally make them resistant from fire, holy, and both respectively
- `bystanderNerf` config option changed to `damageReductionVsMobs` and made a percentage value

## [1.17.1-4.1.0.0] - 2022.03.25
### Changed
- Updated to Minecraft 1.17.1

## [1.16.5-4.0.1.1] - 2022.03.19
### Added
- Added `zh_cn` localization (thanks Kzeroko!) [#61](https://github.com/TheIllusiveC4/Consecration/pull/61)

## [1.16.5-4.0.1.0] - 2022.03.18
### Added
- Added the ability to craft fire arrows by using a regular arrow on a campfire or soul campfire [#52](https://github.com/TheIllusiveC4/Consecration/issues/52)

## [1.16.5-4.0.0.6] - 2021.07.22
### Fixed
- Fixed Tinkers' Construct compatibility [#59](https://github.com/TheIllusiveC4/Consecration/issues/59)

## [1.16.5-4.0.0.5] - 2021.06.20
### Added
- Added Tinkers' Construct compatibility [#57](https://github.com/TheIllusiveC4/Consecration/issues/57)

## [1.16.5-4.0.0.4] - 2021.05.12
### Fixed
- Fixed Tetra compatibility [#56](https://github.com/TheIllusiveC4/Consecration/issues/56)
- Fixed `holyItems` config option [#55](https://github.com/TheIllusiveC4/Consecration/issues/55)

## [1.16.5-4.0.0.3] - 2021.01.23
### Fixed
- Fixed dimension configs

## [1.16.4-4.0.0.2] - 2021.01.05
### Changed
- Fire Arrow recipe is now compatible with modded coals
- Fire Arrow recipe now only works with the regular Minecraft arrow
- Fire Stick recipe is now compatible with modded coals
### Fixed
- Fixed recipe error with fire arrow [#50](https://github.com/TheIllusiveC4/Consecration/issues/50)

## [1.16.4-4.0.0.1] - 2020.12.31
### Added
- Added back Tetra, Silent Gear, and Spartan Weaponry compatibility
### Changed
- Updated to Minecraft 1.16.4
### Fixed
- Fixed undead "unholy" and "absolute" modifiers not working in the configuration list
- Fixed holy negatively affecting non-undead entities

## [1.16.3-4.0.0.0] - 2020.09.12
### Changed
- Updated to Minecraft 1.16.3

## [3.0.1] - 2020.07.29
### Changed
- Fire Stick can now be used in the offhand in addition to the mainhand

## [3.0.0.1] - 2020.07.21
### Fixed
- Fixed undead entities being deleted upon entering world
- Fixed game crashing when re-entering worlds in the same session [#48](https://github.com/TheIllusiveC4/Consecration/issues/48)

## [3.0] - 2020.07.05
### Changed
- Ported to 1.16.1 Forge

## [2.0.0.1] - 2020.12.31
### Fixed
- Fixed undead "unholy" and "absolute" modifiers not working in the configuration list

## [2.0](https://github.com/TheIllusiveC4/Consecration/compare/f52a2f0176d8710041c8e05c4f2a0ce61c9069fc...master) - 2020.06.01
### Changed
- Ported to 1.15.2 Forge

## [2.0-beta2](https://github.com/TheIllusiveC4/Consecration/compare/c7601b45c10bd53bd04bbceab6754076c01b3346...f52a2f0176d8710041c8e05c4f2a0ce61c9069fc) - 2020.03.07
### Added
- Consecration API with methods to add and get configuration details
### Changed
- Ported to 1.14.4 Forge
- Reagant for Consecration potions changed back to Golden Apple
- Undead, unholy, and smite-proof mob configuration options condensed into a single undead list
### Removed
- Removed Fire Bomb
- Removed Holy Water
- Removed Blessed Dust
- Removed priest trades for Consecration potions
- Removed cross-mod support, separated into [Consecration - Compatibility Add-on](https://www.curseforge.com/minecraft/mc-mods/consecration-compat)
