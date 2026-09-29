# Minecraft 1.21.11 parity continuation

Baseline: `f32716bada13bba40250aa56792ca9a4d6eaac7e` on
`fix-1.21.11-mob-accuracy`. Target runtime: Minecraft 1.21.1, Fabric and NeoForge.

## Confirmed corrections

- Mob spear comparisons now calculate only main-hand attack damage. The old
  1.21.1 `ItemAttributeModifiers.compute(base, slot)` also included attack speed
  and unrelated attributes. The replacement preserves the 1.21.11 operation
  order and uses zero when the mob has no attack-damage attribute.
- Spear comparisons now respect the vanilla preferred-weapon tags: bows for
  skeletons (except Wither Skeletons), tridents for drowned, crossbows for
  pillagers, and crossbows plus golden spears for adult piglins.
- Piglin comparisons involving spears preserve the equipment-change restriction,
  consider loved items, then preferred weapons, then damage and item quality.
  This covers both replacing a crossbow with a golden spear and retaining a
  golden spear over a golden sword. Babies have no preferred-weapon tag.
- Added the four exact vanilla tag resources and matching data-generation entries.

## Verified without changes

- Zombie Horse baby dimensions and passenger attachment already match in the
  underlying 1.21.1 class. The baby attachment is based on entity height minus
  0.03125, then scaled by 0.5. The inherited rearing offset also matches.
- Lunge has the same level-based impulse (0.458 per level), exhaustion (4 per
  level), one durability cost, mounted/water/elytra restrictions, and sound
  selection. The official code requires food level **greater than 6**, or the
  ability to fly; this is stricter than the release notes' wording.
- The internal aggregate Lunge sound remains an adapter for 1.21.1's single-sound
  enchantment codec; the mixin chooses the same level-indexed sound as 1.21.11.

## Evidence

Compared the official 1.21.11 client JAR and Mojang mappings from its launcher
manifest with the official 1.21.1 client and mappings. Relevant classes:
`Mob`, `ItemAttributeModifiers`, `Piglin`, `AbstractSkeleton`, `WitherSkeleton`,
`Pillager`, `Drowned`, `ZombieHorse`, `AbstractHorse`, `Player`, `FoodData`, and
`PlaySoundEffect`. Compared the four preferred-weapon JSON tags and Lunge JSON
directly against the official JAR.

Release overview: https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-11

## Validation and remaining limits

Resource JSON parses, and the four new tags match the official files exactly.
The updated commit must pass the existing Fabric/NeoForge Actions build.
Compilation does not establish in-game parity; these runtime checks remain:

1. Compare spear/sword pickup when attack damage ties but attack speed differs.
2. Verify main-hand versus off-hand modifiers and all three modifier operations.
3. Verify adult piglins retain golden spears over golden swords, can replace
   crossbows with golden spears, and respect equipment-change restrictions.
4. Verify baby piglins and Wither Skeletons do not inherit adult/bow preferences.
5. Verify skeleton and pillager preferred weapons remain preferred over spears.
6. Compare Lunge at food levels 6 and 7, all three enchantment levels, mounted,
   underwater, and elytra flight, including multiplayer synchronization.

This is a bounded audit checkpoint, not a claim of complete 1:1 parity.
The branch contains Nautilus item tags and Breath of the Nautilus groundwork,
but no Nautilus or Zombie Nautilus entity implementation. Their entities,
AI, equipment, rendering, spawning, and associated content remain substantial
work. Quad-leash rendering from newer Minecraft versions is also not supplied
by the existing 1.21.1 leash renderer. Copper Horse Armor remains excluded as
previously requested; Netherite Horse Armor is part of the 1.21.11 scope.
