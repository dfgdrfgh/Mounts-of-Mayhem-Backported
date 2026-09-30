package zzik2.barched.nautilus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

/** The three vanilla spawn-condition types used by data-driven mob variants. */
public interface NautilusSpawnCondition {
    Codec<NautilusSpawnCondition> CODEC = ResourceLocation.CODEC.partialDispatch(
            "type", condition -> DataResult.success(condition.type()), id -> {
                if (id.equals(ResourceLocation.withDefaultNamespace("biome"))) return DataResult.success(BiomeCheck.CODEC);
                if (id.equals(ResourceLocation.withDefaultNamespace("structure"))) return DataResult.success(StructureCheck.CODEC);
                if (id.equals(ResourceLocation.withDefaultNamespace("moon_brightness"))) return DataResult.success(MoonBrightnessCheck.CODEC);
                return DataResult.error(() -> "Unknown spawn condition type: " + id);
            });

    ResourceLocation type();
    boolean test(ServerLevelAccessor level, BlockPos pos);

    record BiomeCheck(HolderSet<Biome> biomes) implements NautilusSpawnCondition {
        static final MapCodec<BiomeCheck> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(BiomeCheck::biomes)
        ).apply(instance, BiomeCheck::new));

        @Override public ResourceLocation type() { return ResourceLocation.withDefaultNamespace("biome"); }
        @Override public boolean test(ServerLevelAccessor level, BlockPos pos) { return this.biomes.contains(level.getBiome(pos)); }
    }

    record StructureCheck(HolderSet<Structure> structures) implements NautilusSpawnCondition {
        static final MapCodec<StructureCheck> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RegistryCodecs.homogeneousList(Registries.STRUCTURE).fieldOf("structures").forGetter(StructureCheck::structures)
        ).apply(instance, StructureCheck::new));

        @Override public ResourceLocation type() { return ResourceLocation.withDefaultNamespace("structure"); }
        @Override public boolean test(ServerLevelAccessor level, BlockPos pos) {
            return level.getLevel().structureManager().getStructureWithPieceAt(pos, this.structures).isValid();
        }
    }

    record MoonBrightnessCheck(MinMaxBounds.Doubles range) implements NautilusSpawnCondition {
        static final MapCodec<MoonBrightnessCheck> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                MinMaxBounds.Doubles.CODEC.fieldOf("range").forGetter(MoonBrightnessCheck::range)
        ).apply(instance, MoonBrightnessCheck::new));

        @Override public ResourceLocation type() { return ResourceLocation.withDefaultNamespace("moon_brightness"); }
        @Override public boolean test(ServerLevelAccessor level, BlockPos pos) {
            // 1.21.1 derives the vanilla moon phase from dimension time, before environment attributes existed.
            return this.range.matches(level.getMoonBrightness());
        }
    }
}
