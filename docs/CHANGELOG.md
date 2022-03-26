# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project does not adhere to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
This project uses MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

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
