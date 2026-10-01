package zzik2.barched.data;

import dev.architectury.hooks.level.biome.SpawnProperties;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import static org.junit.Assert.*;

public class BarchedBiomeModificationsTest {
    @BeforeClass
    public static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    public void zombieWeightChangesWithoutReadingTheNullFabricMap() {
        FakeSpawns spawns = new FakeSpawns();
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 95, 4, 4));
        BarchedBiomeModifications.reduceMonsterSpawnWeight(spawns, EntityType.ZOMBIE, 5);
        assertEquals(1, spawns.entries.size());
        assertSpawn(spawns.entries.getFirst().data(), EntityType.ZOMBIE, 90, 4, 4);
    }

    @Test
    public void parchedAdjustmentPreservesOtherMobsAndCategories() {
        FakeSpawns spawns = new FakeSpawns();
        var zombie = new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 95, 4, 4);
        var creature = new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 7, 1, 2);
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 100, 4, 4));
        spawns.addSpawn(MobCategory.MONSTER, zombie);
        spawns.addSpawn(MobCategory.CREATURE, creature);
        BarchedBiomeModifications.reduceMonsterSpawnWeight(spawns, EntityType.SKELETON, 50);
        assertEquals(3, spawns.entries.size());
        assertTrue(spawns.entries.contains(new Entry(MobCategory.MONSTER, zombie)));
        assertTrue(spawns.entries.contains(new Entry(MobCategory.CREATURE, creature)));
        assertSpawn(spawns.entries.getLast().data(), EntityType.SKELETON, 50, 4, 4);
    }

    @Test
    public void emptyBiomesStayEmpty() {
        FakeSpawns spawns = new FakeSpawns();
        BarchedBiomeModifications.reduceMonsterSpawnWeight(spawns, EntityType.ZOMBIE, 5);
        assertTrue(spawns.entries.isEmpty());
    }

    @Test
    public void zeroAndNegativeResultingWeightsAreRemoved() {
        FakeSpawns spawns = new FakeSpawns();
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 5, 1, 1));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 3, 1, 1));
        BarchedBiomeModifications.reduceMonsterSpawnWeight(spawns, EntityType.ZOMBIE, 5);
        assertTrue(spawns.entries.isEmpty());
    }

    @Test
    public void multipleModdedEntriesKeepTheirGroupSizes() {
        FakeSpawns spawns = new FakeSpawns();
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 20, 1, 2));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 30, 3, 4));
        BarchedBiomeModifications.reduceMonsterSpawnWeight(spawns, EntityType.ZOMBIE, 5);
        assertEquals(2, spawns.entries.size());
        assertSpawn(spawns.entries.getFirst().data(), EntityType.ZOMBIE, 15, 1, 2);
        assertSpawn(spawns.entries.getLast().data(), EntityType.ZOMBIE, 25, 3, 4);
    }

    private static void assertSpawn(MobSpawnSettings.SpawnerData data, EntityType<?> type, int weight, int min, int max) {
        assertSame(type, data.type);
        assertEquals(weight, data.getWeight().asInt());
        assertEquals(min, data.minCount);
        assertEquals(max, data.maxCount);
    }

    private record Entry(MobCategory category, MobSpawnSettings.SpawnerData data) {}

    private static final class FakeSpawns implements SpawnProperties.Mutable {
        private final List<Entry> entries = new ArrayList<>();
        private boolean iterating;

        @Override public float getCreatureProbability() { return 0; }
        @Override public Map<MobCategory, List<MobSpawnSettings.SpawnerData>> getSpawners() {
            fail("Spawn adjustment must not read the unimplemented Fabric spawn map");
            return null;
        }
        @Override public Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> getMobSpawnCosts() { return Map.of(); }
        @Override public Mutable setCreatureProbability(float probability) { return this; }
        @Override public Mutable addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data) {
            assertFalse("Do not add entries while the loader is iterating", iterating);
            entries.add(new Entry(category, data));
            return this;
        }
        @Override public boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> predicate) {
            iterating = true;
            try {
                return entries.removeIf(entry -> predicate.test(entry.category(), entry.data()));
            } finally {
                iterating = false;
            }
        }
        @Override public Mutable setSpawnCost(EntityType<?> type, MobSpawnSettings.MobSpawnCost cost) { return this; }
        @Override public Mutable setSpawnCost(EntityType<?> type, double charge, double energyBudget) { return this; }
        @Override public Mutable clearSpawnCost(EntityType<?> type) { return this; }
    }
}
