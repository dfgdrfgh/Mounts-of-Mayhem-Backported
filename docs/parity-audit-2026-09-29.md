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
Code commit `fb9586732fc15f604990f055281f49fcfa140d11` passed the existing
Fabric and NeoForge build, including both artifact uploads:
https://github.com/dfgdrfgh/Yarched/actions/runs/36618022728
Compilation does not establish in-game parity; these runtime checks remain:

1. Compare spear/sword pickup when attack damage ties but attack speed differs.
2. Verify main-hand versus off-hand modifiers and all three modifier operations.
3. Verify adult piglins retain golden spears over golden swords, can replace
   crossbows with golden spears, and respect equipment-change restrictions.
4. Verify baby piglins and Wither Skeletons do not inherit adult/bow preferences.
5. Verify skeleton and pillager preferred weapons remain preferred over spears.
6. Compare Lunge at food levels 6 and 7, all three enchantment levels, mounted,
   underwater, and elytra flight, including multiplayer synchronization.

## September 30 continuation

The branch now implements Nautilus and Zombie Nautilus entities, their AI,
riding/dashing, taming/feeding, armor and saddle inventory, models, textures,
sounds, spawning, drowned riders, loot, recipes, and advancement integration.
The earlier statement that these entities were absent is superseded.

Code commit `0229d2992632c1ec8d91d4f94f2c5e34dc9b0894` passed the Fabric and
NeoForge build, including both artifact uploads:
https://github.com/dfgdrfgh/Yarched/actions/runs/36657597034

Corrections in this continuation:

- Drowned beach-seeking no longer reinstalls 1.21.1's ground-only navigator.
  The goal preserves the amphibious navigator, matching 1.21.11's goal start.
- Drowned special swimming travel now requires submersion, rather than merely
  touching water, matching 1.21.11's `travelInWater` condition.
- Nautilus body armor and saddles render their enchantment glint. The saddle
  stack is synchronized to tracking clients, replacing the boolean-only state
  that could not communicate item components. Saddle persistence still uses
  the existing `SaddleItem` save field.
- Zombie Horse armor uses the armor render type and glint buffer. Leather's
  separate overlay remains untinted and does not apply glint twice.
- Camel Husk saddle shearing is implemented with the vanilla no-passengers,
  non-sneaking and Binding Curse checks, a one-point shears durability cost,
  the saddle unequip sound, and a saddle item drop. This closes the missing
  interaction alongside the other new mounts.

Further source/resource checks:

- Compared Nautilus food, taming, movement, dash, restriction radius, effects,
  charge AI, sound selection, and built-in Zombie Nautilus variant behavior
  with the official mapped 1.21.11 classes.
- Confirmed Camel Husk rider spawning and Parched health, Weakness arrows,
  attack intervals, and Weakness immunity against the official classes.
- All packaged JSON parses. The existing tag overrides contain their relevant
  Mounts of Mayhem entries; the remaining referenced underwater-dismount tag
  is inherited from 1.21.1.
- Recipe ingredient syntax and the Lunge effect/sound codec intentionally use
  1.21.1-compatible representations. Loot differences for Copper Horse Armor,
  bundles and the Lava Chicken music disc remain outside this update's scope.
- Matching-path packaged textures match the official files except Camel Husk
  and Zombie Horse: all differing pixels occupy areas transparent in the
  official body texture, supplying the saddle regions needed by 1.21.1's
  combined body/saddle models. Do not replace them with bare body textures.
- Packaged Nautilus, Camel Husk, Parched and spear sound definitions match the
  official 1.21.11 sound definitions.

## Remaining limits

This is a source-audit and build checkpoint, not certification of complete 1:1
runtime parity. In-game startup, movement, GUI interactions, multiplayer
synchronization, rendering and the six earlier gameplay checks still require
verification. Specifically exercise Drowned water/beach/water transitions,
Nautilus saddle save/reload and remote glint visibility, and Camel Husk saddle
shearing with passengers, sneaking and Binding Curse.

The Zombie Nautilus backport represents the two built-in variants with a
synchronized flag and vanilla variant IDs in saved data; it does not implement
1.21.11's extensible variant registry/data-component system. The newer general
leash physics and quad-leash renderer are not supplied by the 1.21.1 renderer.
Copper Horse Armor remains excluded as previously requested; Netherite Horse
Armor is part of the 1.21.11 scope.
