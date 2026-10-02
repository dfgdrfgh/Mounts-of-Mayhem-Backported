"""Reject release JARs that omit recovered content, mixins, or the approved icon."""
import json
from pathlib import Path
import struct
import zlib
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[2]
RESOURCES = ROOT / "common/src/main/resources"


def verify_png(data, name):
    assert data[:8] == b"\x89PNG\r\n\x1a\n", f"Invalid PNG: {name}"
    offset = 8
    compressed = bytearray()
    ended = False
    while offset < len(data):
        size = struct.unpack_from(">I", data, offset)[0]
        kind = data[offset + 4:offset + 8]
        payload = data[offset + 8:offset + 8 + size]
        crc = struct.unpack_from(">I", data, offset + 8 + size)[0]
        assert zlib.crc32(kind + payload) & 0xffffffff == crc, f"PNG checksum: {name}"
        if kind == b"IDAT":
            compressed.extend(payload)
        if kind == b"IEND":
            ended = True
        offset += size + 12
    assert ended and compressed, f"Incomplete PNG: {name}"
    zlib.decompress(compressed)


def verify(jar, platform):
    with ZipFile(jar) as archive:
        assert archive.testzip() is None, f"Damaged JAR: {jar}"
        names = set(archive.namelist())
        required = {
            "zzik2/barched/minecraft/world/entity/animal/nautilus/Nautilus.class",
            "zzik2/barched/minecraft/world/entity/animal/nautilus/ZombieNautilus.class",
            "zzik2/barched/minecraft/world/entity/animal/CamelHusk.class",
            "zzik2/barched/minecraft/world/entity/monster/Parched.class",
            "zzik2/barched/item/NautilusArmorItem.class",
            "zzik2/barched/item/BackportedHorseArmorItem.class",
            "zzik2/barched/nautilus/ZombieNautilusVariant.class",
            "data/minecraft/zombie_nautilus_variant/temperate.json",
            "data/minecraft/zombie_nautilus_variant/warm.json",
            "data/minecraft/tags/worldgen/biome/spawns_coral_variant_zombie_nautilus.json",
            "data/minecraft/tags/entity_type/can_wear_horse_armor.json",
            "data/minecraft/tags/entity_type/can_wear_nautilus_armor.json",
            "data/minecraft/recipe/netherite_horse_armor_smithing.json",
            "data/minecraft/recipe/netherite_nautilus_armor_smithing.json",
            "data/minecraft/enchantment/lunge.json",
            "data/minecraft/advancement/adventure/spear_many_mobs.json",
            "data/minecraft/advancement/adventure/kill_a_mob.json",
            "data/minecraft/advancement/adventure/kill_all_mobs.json",
            "data/minecraft/advancement/husbandry/bred_all_animals.json",
            "data/minecraft/advancement/nether/all_effects.json",
            "data/minecraft/loot_table/entities/nautilus.json",
            "data/minecraft/loot_table/entities/zombie_nautilus.json",
            "data/minecraft/loot_table/entities/camel_husk.json",
            "data/minecraft/loot_table/entities/parched.json",
            "data/minecraft/loot_table/entities/zombie_horse.json",
            "assets/minecraft/textures/mob_effect/breath_of_the_nautilus.png",
            "assets/minecraft/textures/item/zombie_horse_spawn_egg.png",
            "assets/mombackport/models/item/zombie_horse_spawn_egg.json",
        }
        for item in ["netherite_horse_armor", "nautilus_spawn_egg", "zombie_nautilus_spawn_egg",
                     "camel_husk_spawn_egg", "parched_spawn_egg"]:
            required.add(f"assets/minecraft/models/item/{item}.json")
        for material in ["copper", "iron", "golden", "diamond", "netherite"]:
            required.add(f"assets/minecraft/models/item/{material}_nautilus_armor.json")
            required.add(f"assets/minecraft/textures/item/{material}_nautilus_armor.png")
        assert not required - names, f"Missing recovered content: {sorted(required - names)}"
        assert not any("copper_horse_armor" in name for name in names), "Copper Horse Armor is outside this backport's scope"
        forbidden_classes = {
            "zzik2/zreflex/internal/UnsafeAccess.class",
            "zzik2/zreflex/enumeration/ZEnumTool.class",
            "zzik2/zreflex/enumeration/ZEnumTool$EnumException.class",
        }
        assert not forbidden_classes & names, f"Unsafe ZReflex classes packaged: {sorted(forbidden_classes & names)}"
        forbidden_markers = (
            b"sun.misc.Unsafe",
            b"sun/misc/Unsafe",
            b"jdk.internal.misc.Unsafe",
            b"jdk/internal/misc/Unsafe",
            b"IMPL_LOOKUP",
            b"theUnsafe",
        )
        for class_name in (name for name in names if name.endswith(".class")):
            payload = archive.read(class_name)
            for marker in forbidden_markers:
                assert marker not in payload, f"Unsafe reflection marker {marker!r} found in {class_name}"
        for name in names:
            if name.endswith(".json"):
                json.loads(archive.read(name))
            elif name.endswith(".png"):
                verify_png(archive.read(name), name)
        config = json.loads(archive.read("barched.mixins.json"))
        for mixin in config.get("mixins", []) + config.get("client", []):
            class_name = (config["package"] + "." + mixin).replace(".", "/") + ".class"
            assert class_name in names, f"Missing registered mixin: {class_name}"
        zombie_horse_egg = "assets/minecraft/textures/item/zombie_horse_spawn_egg.png"
        assert archive.read(zombie_horse_egg) == (RESOURCES / zombie_horse_egg).read_bytes(), "Zombie Horse Spawn Egg texture differs from the 1.21.11 source asset"
        icon = "assets/barched/icon.png"
        assert archive.read(icon) == (RESOURCES / icon).read_bytes(), "Packaged icon differs from approved source"
        if platform == "fabric":
            metadata = json.loads(archive.read("fabric.mod.json"))
            assert metadata["id"] == "mombackport" and metadata["name"] == "MoM Backport"
            assert metadata["icon"] == icon
        else:
            metadata = archive.read("META-INF/neoforge.mods.toml").decode()
            assert 'modId = "mombackport"' in metadata and 'displayName = "MoM Backport"' in metadata
            assert f'logoFile = "{icon}"' in metadata
    print(f"Verified recovered content, resources, mixins, identity, icon, and safe reflection packaging: {jar.name}")


if __name__ == "__main__":
    for platform in ["fabric", "neoforge"]:
        jars = [p for p in (ROOT / platform / "build/libs").glob("MoMBackport-*.jar")
                if not p.name.endswith(("-sources.jar", "-all.jar", "-dev-shadow.jar"))]
        assert len(jars) == 1, f"Expected one release JAR for {platform}, got {jars}"
        verify(jars[0], platform)
