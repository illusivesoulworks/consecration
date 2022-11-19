# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project does not adhere to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
This project uses MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

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
