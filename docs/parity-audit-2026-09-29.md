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


## Late September 30 parity pass

Additional non-weapon parity corrections completed after the earlier checkpoint:

- Zombie Horse default natural-spawn biomes now match the final generated
  1.21.11 biome data exactly: Plains, Sunflower Plains, Snowy Plains, Savanna,
  Savanna Plateau and Windswept Savanna. Ice Spikes remains excluded.
- Zombie Horse spawning now reduces the biome's existing Zombie spawn entries
  by the configured Zombie Horse weight while preserving each entry's existing
  group sizes. The vanilla defaults therefore reproduce the 1.21.11 95 -> 90
  Zombie weight shift and add Zombie Horse at weight 5 without overwriting
  datapack/modded spawn-group values.
- Parched's Desert replacement applies the same preservation logic to existing
  Skeleton entries. Vanilla remains Skeleton 50 plus Parched 50, while modified
  Skeleton group sizes are no longer reset to hard-coded values.
- Camel Husk's natural rider chance now uses the existing config value instead
  of an unconditional 10 percent literal. The default remains the vanilla
  1.21.11 10 percent chance.
- The advancement data-generation path now includes Zombie Nautilus in
  Monsters Hunted. The packaged advancement JSON already contained it; both
  generated and shipped paths now agree.
- Added the missing `minecraft:nautilus_one_cm` custom statistic with the
  distance formatter and award it from the 1.21.1 ServerPlayer riding-stat path
  whenever the player moves while riding an AbstractNautilus.
- Added the 1.21.11 Nautilus riding loop on the client. It starts on a successful
  local mount, follows the mount's sound source, is audible underwater, scales
  with Nautilus movement speed, and stops on dismount/removal.
- Added the Nautilus Armor empty-slot icon to the Netherite Upgrade smithing
  template in the same position as 1.21.11. Both Nautilus inventory/smithing
  slot sprites are byte-for-byte identical to the 1.21.11 resources.
- Verified the new iron/golden Nautilus armor recycling inputs, piglin-loved
  membership for Golden Nautilus Armor, all-effects advancement data, entity
  loot, armor values, smithing inputs, chest-loot weights, creative-tab
  placement and new mount entity/item/biome tags against 1.21.11.

The dedicated four-point Zombie Horse leash offsets in 1.21.11 are part of the
newer quad-leash system. Quad connections are only used when the leash holder
supports quad leashing (for example newer Ghast/Happy Ghast holder behavior),
which is outside this 1.21.1 Mounts of Mayhem backport scope. The existing
single-leash behavior remains the observable behavior for the scoped entities.

Latest validation commit: `bdf6b886faae2c0a639539d45a087434bb5e82a6`.
GitHub Actions run 36661636609 completed successfully: combined Fabric and
NeoForge Gradle build passed, and both release-JAR artifact uploads succeeded.
https://github.com/dfgdrfgh/Yarched/actions/runs/36661636609

## Remaining limits

This is a source-audit and build checkpoint, not certification of complete 1:1
runtime parity. In-game startup, movement, GUI interactions, multiplayer
synchronization, rendering and the six earlier gameplay checks still require
verification. Specifically exercise Drowned water/beach/water transitions,
Nautilus saddle save/reload and remote glint visibility, and Camel Husk saddle
shearing with passengers, sneaking and Binding Curse.

The Zombie Nautilus backport represents the two built-in variants with a
synchronized flag and vanilla variant IDs in saved data; it does not implement
1.21.11's extensible variant registry/data-component system. The newer general quad-leash physics/renderer are not supplied by 1.21.1; the
Zombie Horse quad offsets are not exercised by the scoped 1.21.1 leash holders.
Copper Horse Armor remains excluded as previously requested; Netherite Horse
Armor is part of the 1.21.11 scope.


## Final non-weapon compatibility sweep

A later compatibility sweep found and closed two more 1.21.1 adaptation gaps:

- Nautilus Armor now has a dedicated dispenser behavior on 1.21.1. Vanilla
  1.21.1 already knows how to dispense Saddles onto any Saddleable, but its
  armor dispenser path only understands the older horse-armor system. The
  backport now equips any of the five Nautilus Armor tiers onto a valid adult,
  tamed Nautilus or Zombie Nautilus with an empty BODY slot, consumes one item,
  preserves the guaranteed-drop behavior supplied by the mount's BODY-slot
  setter, and marks the equipped mount persistent like 1.21.11's generic
  equipment dispenser.
- Nautilus entities no longer auto-board boats, and the rejected boarding path
  now applies the normal boat collision push. The first backport prevented
  startRiding but did not reproduce 1.21.11's else-branch push because 1.21.1
  ignores startRiding's return value in that loop.

Additional final-release checks:

- The 1.21.1 entity-tag graph already defines UNDEAD as SKELETONS + ZOMBIES and
  CAN_BREATHE_UNDER_WATER, IGNORES_POISON_AND_REGEN,
  INVERTED_HEALING_AND_HARM, WITHER_FRIENDS and SENSITIVE_TO_SMITE in terms of
  UNDEAD. Therefore the additive Parched/Zombie Horse/Camel Husk/Zombie
  Nautilus tag entries automatically inherit the intended final behavior.
- 1.21.1 Pufferfish already reads NOT_SCARY_FOR_PUFFERFISH. The additive
  Nautilus and Zombie Nautilus entries therefore work without another code
  hook.
- The safe land-dismount algorithm in AbstractNautilus is behavior-identical to
  the final 1.21.11 Animal implementation added before release.
- Adult and baby Nautilus model geometry, Nautilus Armor geometry and Nautilus
  Saddle geometry match the final 1.21.11 values, including the late
  head/shell clipping adjustment.
- Zombie Nautilus is forced non-baby in both implementations, covering the
  negative-age hitbox fix.
- Drowned-created Zombie Nautilus is marked persistent for structure spawns,
  matching the release-candidate persistence fix.
- The Zombie Horse daylight routine delegates to 1.21.1's isSunBurnTick, which
  already requires actual daytime before sky/brightness checks and therefore
  does not reproduce the late 1.21.11 light-source false-positive.
- Zombie Horse natural placement, Parched/Camel Husk surface placement and
  Nautilus water placement match the final SpawnPlacements table. Zombie
  Nautilus intentionally has no standalone natural SpawnPlacement entry.
- The final Zombie Horse biome set was rechecked against both the Java
  generators and the generated biome JSON: Plains, Sunflower Plains, Snowy
  Plains and all three Savanna variants use Zombie 90 / Zombie Villager 5 /
  Zombie Horse 5. Ice Spikes keeps Zombie 95 / Zombie Villager 5 with no
  Zombie Horse entry.

Latest code validation for this sweep is commit
`1b8b1c8041a9e39de4a73b10dbbb8cd23d43ae68`.
GitHub Actions run 36663901605 completed successfully: Fabric and NeoForge
compiled and both release-JAR artifact uploads succeeded.
https://github.com/dfgdrfgh/Yarched/actions/runs/36663901605


## Final compatibility verification after generated-data cross-check

The final generated 1.21.11 data was checked directly in addition to the Java
generators. This corrected one earlier inference and verified several
1.21.1-specific compatibility shims:

- Zombie Horse natural spawning uses the final generated biome set: Plains,
  Sunflower Plains, Snowy Plains, Savanna, Savanna Plateau and Windswept
  Savanna. Those biomes use Zombie 90 / Zombie Villager 5 / Zombie Horse 5.
  Ice Spikes retains Zombie 95 / Zombie Villager 5 and has no Zombie Horse.
- Nautilus Armor has one bootstrap-time dispenser implementation for all five
  armor tiers. The older duplicate item-constructor registration was removed.
  Dispenser-equipped Nautilus armor consumes one item, equips only a valid
  empty BODY slot, remains guaranteed-drop equipment and marks the mount
  persistent.
- The 1.21.1 Saddle dispenser was adapted so a dispenser-saddled Nautilus is
  also marked persistent, matching 1.21.11's generic equipment dispenser.
- The Nautilus boat compatibility hook now reproduces both halves of the final
  behavior: Nautilus/Zombie Nautilus do not auto-board, and the boat still
  collision-pushes them when boarding is rejected.
- The backport now registers a single compatibility DataFixer schema at the
  1.21.1 data version for Camel Husk, Parched, Nautilus and Zombie Nautilus.
  This avoids registering two different schemas at the same version key, where
  DataFixerUpper would replace the earlier schema.
- Final 1.21.11 marks Parched not-in-peaceful at the EntityType layer, but
  1.21.1 has no equivalent EntityType.Builder method. No extra shim is needed:
  Parched extends the 1.21.1 Monster path, whose hostile spawn rules reject
  Peaceful and whose shouldDespawnInPeaceful implementation returns true.
- A Git-blob comparison covered 27 matching new-mob/equipment PNG and OGG
  assets. 26 are byte-for-byte identical to final 1.21.11. The sole difference
  is the already-documented Camel Husk body texture adaptation needed by the
  older 1.21.1 camel model. All five Nautilus Armor textures and the Nautilus
  saddle texture are exact matches.
- Nautilus swimming animation keyframes and Zombie Nautilus coral geometry
  match final 1.21.11. The coral visibility rule also matches: coral is hidden
  while BODY armor is equipped.
- The old 1.21.1 horse/camel saddle system only synchronized a boolean
  saddled flag. The backport now also synchronizes the actual saddle ItemStack
  through AbstractHorse so equipment components such as foil are available on
  remote clients and after save/reload.
- Camel Husk's compatibility saddle layer now renders foil from that synced
  saddle stack. Undead horses use a saddle-only glint overlay over the older
  baked saddle geometry, reproducing 1.21.11 equipment-renderer foil without
  replacing their 1.21.1 base model.
- Nautilus and Zombie Nautilus loot tables and the all-effects advancement are
  exact JSON matches. The smithing recipe advancements are also exact; the
  smithing recipes themselves use 1.21.1's older ingredient-object syntax with
  equivalent inputs/results.
- The Shipwreck Map armor pool matches final 1.21.11 exactly: Copper 20, Iron
  10, Gold 5 and Diamond 2, each with count 1.

Latest code validation is commit
`36708e9cc58f620c086929f2803d1dd03c539141`.
GitHub Actions run 36666735571 completed successfully: Fabric and NeoForge
compiled and both release-JAR artifact uploads succeeded.
https://github.com/dfgdrfgh/Yarched/actions/runs/36666735571


## Mount equipment-system parity pass

The 1.21.11 equipment rewrite changes more than the newly added Nautilus
inventory, so the 1.21.1 compatibility layer was extended across the existing
mounts where those changes are observable:

- AbstractHorse now synchronizes the actual Saddle ItemStack instead of only
  the legacy saddled boolean. This preserves item components for remote-client
  rendering while continuing to use the normal 1.21.1 SaddleItem save field.
- Pig and Strider now preserve the real Saddle ItemStack as synchronized data
  and in save data. Existing 1.21.1 worlds containing only the legacy saddle
  boolean migrate to a normal Saddle stack. Death drops preserve saddle
  components and respect PREVENT_EQUIPMENT_DROP.
- Successful saddle shearing now follows the final shared rules for the scoped
  mounts: shears, no secondary-use, no passengers, Binding Curse protection
  unless creative, one durability point, SHEAR game event, real ItemStack drop
  and the Saddle unequip sound.
- Horse Armor is explicitly shearable in final 1.21.11. The horseArmor
  Equippable definition sets can_be_sheared=true and uses HORSE_ARMOR_UNEQUIP.
  The compatibility hook therefore removes EQUESTRIAN BODY armor before the
  Saddle, honors passenger/secondary-use/Binding-Curse restrictions, emits the
  normal BODY-slot UNEQUIP event plus SHEAR, drops the real stack, plays
  HORSE_ARMOR_UNEQUIP and costs one shears durability.
- Nautilus Armor remains armor-first then saddle. Clearing its BODY slot emits
  the normal BODY-slot UNEQUIP game event through LivingEntity, while the
  shared shearing hook explicitly plays ARMOR_UNEQUIP_NAUTILUS, matching the
  final Equippable shearing sound.
- Saddle foil/glint now uses the synchronized saddle stack on Camel Husk,
  Zombie/Skeleton Horse, Pig, Strider, Horse, Donkey, Mule and Camel. The
  compatibility render layers render only the older model's saddle geometry,
  preserving the 1.21.1 body model while reproducing 1.21.11's stack-based
  equipment foil.
- The shared Saddle dispenser now marks every equipped Mob persistent, matching
  1.21.11's EquipmentDispenseItemBehavior rather than doing so only for
  Nautilus.
- Mob auto-equipping now rejects Nautilus Armor on non-Nautilus entities and
  rejects every EQUESTRIAN AnimalArmorItem on entities other than Horse and
  Zombie Horse. This reproduces the final CAN_WEAR_NAUTILUS_ARMOR and
  CAN_WEAR_HORSE_ARMOR allowed-entity sets and prevents 1.21.1's BODY-to-
  MAINHAND fallback on unrelated mobs.
- 1.21.1 has no EquipmentSlot.SADDLE or EquipmentSlotGroup.SADDLE, so its
  generic EnchantmentHelper equipment iteration cannot treat the preserved
  Saddle stack as a native equipment slot. Vanilla 1.21.11 does not place
  Saddles in the normal enchantable tags; the observable vanilla cases used by
  this backport are handled explicitly (Binding/shearing protection,
  PREVENT_EQUIPMENT_DROP, item components, save/load and foil). A
  command/datapack-added active enchantment whose slot rule is ANY could still
  execute from a native 1.21.11 Saddle slot but not from the 1.21.1 legacy
  saddle bridge. Fully emulating that would require inventing the newer Saddle
  equipment-slot enum/codec across the older engine and is retained as a
  structural compatibility limit rather than using an inaccurate BODY proxy.
- Final 1.21.11 also introduced the generic player_sheared_equipment criterion
  as part of the broader equipment migration and changed the pre-existing
  Remove Wolf Armor advancement to use it. The 1.21.1 base advancement still
  uses player_interacted_with_entity; importing the new criterion completely
  would also require unrelated Wolf/Llama/Happy-Ghast equipment-system changes,
  so it remains outside this Mounts of Mayhem compatibility scope.

Latest code validation for this pass is commit
`83ab8a2fe17b6a7371b3238919611ea7c481e71f`.
GitHub Actions run 36669483539 completed successfully: Fabric and NeoForge
compiled and both release-JAR artifact uploads succeeded.
https://github.com/dfgdrfgh/Yarched/actions/runs/36669483539


## Unified mount equipment shearing verification

- The shared mount-equipment shearing hook now handles Horse Armor, Nautilus
  Armor and Saddles in the same interaction stage as final 1.21.11.
- BODY equipment is checked before the Saddle, matching the final equipment
  slot order. Binding Curse blocks removal unless the player is in creative,
  ridden mounts cannot be sheared, secondary-use skips shearing, one shears
  durability is consumed, the real ItemStack is dropped at the average
  passenger attachment height, and the SHEAR game event is emitted.
- Horse Armor uses HORSE_ARMOR_UNEQUIP and Nautilus Armor uses
  ARMOR_UNEQUIP_NAUTILUS. BODY-slot clearing also emits the inherited UNEQUIP
  game event through the 1.21.1 LivingEntity equipment callback.
- Saddles use SADDLE_UNEQUIP. The shared SaddleItemBridge now covers
  AbstractHorse descendants, Pig, Strider and Nautilus, so the same removal
  rules apply across the scoped saddleable mounts.
- Pig and Strider preserve the full Saddle ItemStack in synced/save data rather
  than only the legacy boolean, preserving components, foil and Binding/drop
  behavior. Existing boolean-only saves migrate to a normal Saddle stack.
- Successful shearing returns InteractionResult.SUCCESS on both sides, matching
  final 1.21.11 rather than the older sided-success convention.

Latest code validation is commit
`e54ed6365723048c8e43490ea051a6d3fe9578de`.
GitHub Actions run 36675164787 completed successfully: Fabric and NeoForge
compiled and both release-JAR artifact uploads succeeded.
https://github.com/dfgdrfgh/Yarched/actions/runs/36675164787


## Final dispenser eligibility cross-check

A final comparison against the 1.21.11 generic equipment dispenser found two
observable 1.21.1 compatibility differences and both are now corrected:

- Horse Armor dispensing now uses the final Horse/Zombie Horse eligibility
  rule for every horse-armor tier: the BODY slot must be valid and empty and
  the horse must be tamed **or** allowed to pick up loot. This replaces the old
  1.21.1 tame-only dispenser check while retaining the final
  CAN_WEAR_HORSE_ARMOR entity set (Horse and Zombie Horse only). Leather, Iron,
  Golden, Diamond and Netherite Horse Armor share the same compatibility path.
- Saddle dispensing now preserves the final AbstractHorse dispenser gate for
  Horse and Zombie Horse (tamed or can-pick-up-loot) instead of relying only on
  1.21.1 Saddleable.isSaddleable(). Other saddleable mounts continue to use
  their normal saddleability checks, and the existing compatibility path still
  marks equipped mobs persistent.
- 1.21.1 Mob.setBodyArmorItem already delegates to
  setItemSlotAndDropWhenKilled, so dispenser-equipped Horse Armor remains
  guaranteed-drop equipment and marks the mount persistent without an extra
  compatibility write.
- The old Netherite Horse Armor constructor-level tame-only dispenser
  registration was removed. All five horse-armor tiers now use the single
  shared final-rule compatibility registration, eliminating load-order
  dependence between the legacy and backported behaviors.
- Saddle item interaction now reproduces the 1.21.11 Equippable path for Horse,
  Zombie Horse and Skeleton Horse rather than always inheriting 1.21.1
  SaddleItem's isSaddleable gate. This closes the regular Horse difference
  introduced by the equipment-component migration and preserves direct-call
  semantics for all three equines whose final canUseSlot implementation is
  unconditional. The full Saddle stack, equip event and old-engine saddle
  inventory/drop behavior are retained. Zombie Horse still rejects non-food
  items while untamed and Skeleton Horse still returns PASS while untamed in
  their own mobInteract methods, exactly as the final 1.21.11 classes do.
- Skeleton Horse dispenser eligibility now also matches the final combination
  of unconditional SADDLE canUseSlot plus AbstractHorse's tame-or-pickup-loot
  dispenser gate. This covers the rare command/datapack-visible case where an
  untamed Skeleton Horse is allowed to pick up loot.

The same final-source pass rechecked Nautilus adult/baby geometry, Nautilus
Armor and Saddle geometry, Zombie Nautilus coral geometry/visibility, Nautilus
swim animation inputs, Camel/Camel Husk synchronized Saddle ItemStack access,
Horse/Nautilus armor material values, and shared mount-equipment shearing. No
additional non-structural mismatch was found in those areas.

Current code head for this cross-check is
`6c197a92cb5c20432c13ae799c277cd463c4a66a`.
GitHub Actions run 36678016565 completed successfully: the combined Fabric and
NeoForge Gradle build passed, and both Fabric and NeoForge JAR artifact uploads
succeeded.
https://github.com/dfgdrfgh/Yarched/actions/runs/36678016565
