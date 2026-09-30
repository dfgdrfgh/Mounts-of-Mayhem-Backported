package zzik2.barched.nautilus;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.Bootstrap;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZombieNautilusVariantTest {
    private static RegistryOps<JsonElement> registries;

    @BeforeClass
    public static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        HolderLookup.Provider lookup = VanillaRegistries.createLookup();
        registries = RegistryOps.create(JsonOps.INSTANCE, lookup);
    }

    @Test
    public void temperateDefinitionDefaultsToNormalModel() {
        ZombieNautilusVariant variant = decode("""
                {"asset_id":"minecraft:entity/nautilus/zombie_nautilus","spawn_conditions":[{"priority":0}]}
                """);
        assertEquals(ZombieNautilusVariant.ModelType.NORMAL, variant.model());
        assertEquals("minecraft:textures/entity/nautilus/zombie_nautilus.png", variant.texture().toString());
        assertTrue(variant.spawnConditions().getFirst().condition().isEmpty());
        assertEquals(0, variant.spawnConditions().getFirst().priority());
    }

    @Test
    public void customAppearanceAndBiomeConditionRoundTrip() {
        ZombieNautilusVariant variant = decode("""
                {"model":"warm","asset_id":"example:nautilus/reef","spawn_conditions":[
                  {"priority":7,"condition":{"type":"minecraft:biome","biomes":["minecraft:warm_ocean"]}}
                ]}
                """);
        assertEquals(ZombieNautilusVariant.ModelType.WARM, variant.model());
        assertEquals("example:textures/nautilus/reef.png", variant.texture().toString());
        assertTrue(variant.spawnConditions().getFirst().condition().orElseThrow() instanceof NautilusSpawnCondition.BiomeCheck);
        JsonElement encoded = ZombieNautilusVariant.DIRECT_CODEC.encodeStart(registries, variant).getOrThrow();
        assertEquals(variant, ZombieNautilusVariant.DIRECT_CODEC.parse(registries, encoded).getOrThrow());
    }

    @Test
    public void networkAppearanceDoesNotRequireServerOnlyStructureRegistry() {
        ZombieNautilusVariant variant = decode("""
                {"model":"warm","asset_id":"example:nautilus/fortress","spawn_conditions":[
                  {"priority":3,"condition":{"type":"minecraft:structure","structures":"minecraft:fortress"}}
                ]}
                """);
        JsonElement packet = ZombieNautilusVariant.NETWORK_CODEC.encodeStart(JsonOps.INSTANCE, variant).getOrThrow();
        assertFalse(packet.getAsJsonObject().has("spawn_conditions"));
        ZombieNautilusVariant client = ZombieNautilusVariant.NETWORK_CODEC.parse(JsonOps.INSTANCE, packet).getOrThrow();
        assertEquals(variant.model(), client.model());
        assertEquals(variant.texture(), client.texture());
        assertTrue(client.spawnConditions().isEmpty());
    }

    @Test
    public void moonBrightnessRangeRetainsBounds() {
        ZombieNautilusVariant variant = decode("""
                {"asset_id":"example:nautilus/moon","spawn_conditions":[
                  {"priority":-2,"condition":{"type":"moon_brightness","range":{"min":0.5,"max":1.0}}}
                ]}
                """);
        var condition = (NautilusSpawnCondition.MoonBrightnessCheck) variant.spawnConditions().getFirst().condition().orElseThrow();
        assertFalse(condition.range().matches(0.25));
        assertTrue(condition.range().matches(0.5));
        assertTrue(condition.range().matches(1.0));
        assertFalse(condition.range().matches(1.25));
    }

    @Test
    public void invalidConditionAndModelAreRejected() {
        assertTrue(ZombieNautilusVariant.DIRECT_CODEC.parse(registries, JsonParser.parseString("""
                {"asset_id":"example:nautilus/test","spawn_conditions":[
                  {"priority":1,"condition":{"type":"example:missing"}}
                ]}
                """)).isError());
        assertTrue(ZombieNautilusVariant.DIRECT_CODEC.parse(registries, JsonParser.parseString("""
                {"model":"missing","asset_id":"example:nautilus/test","spawn_conditions":[]}
                """)).isError());
    }

    private static ZombieNautilusVariant decode(String json) {
        return ZombieNautilusVariant.DIRECT_CODEC.parse(registries, JsonParser.parseString(json)).getOrThrow();
    }
}
