package zzik2.barched.nautilus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public record ZombieNautilusVariant(ModelType model, ResourceLocation assetId, List<Selector> spawnConditions) {
    public static final ResourceKey<Registry<ZombieNautilusVariant>> REGISTRY = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("zombie_nautilus_variant"));
    public static final ResourceKey<ZombieNautilusVariant> TEMPERATE = ResourceKey.create(REGISTRY, ResourceLocation.withDefaultNamespace("temperate"));
    public static final ResourceKey<ZombieNautilusVariant> WARM = ResourceKey.create(REGISTRY, ResourceLocation.withDefaultNamespace("warm"));
    public static final Codec<ZombieNautilusVariant> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ModelType.CODEC.optionalFieldOf("model", ModelType.NORMAL).forGetter(ZombieNautilusVariant::model),
            ResourceLocation.CODEC.fieldOf("asset_id").forGetter(ZombieNautilusVariant::assetId),
            Selector.CODEC.listOf().fieldOf("spawn_conditions").forGetter(ZombieNautilusVariant::spawnConditions)
    ).apply(instance, ZombieNautilusVariant::new));
    // Spawn predicates can reference server-only registries (structures), so only appearance travels to clients.
    public static final Codec<ZombieNautilusVariant> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ModelType.CODEC.optionalFieldOf("model", ModelType.NORMAL).forGetter(ZombieNautilusVariant::model),
            ResourceLocation.CODEC.fieldOf("asset_id").forGetter(ZombieNautilusVariant::assetId)
    ).apply(instance, (model, asset) -> new ZombieNautilusVariant(model, asset, List.of())));
    public static final Codec<Holder<ZombieNautilusVariant>> CODEC = RegistryFixedCodec.create(REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ZombieNautilusVariant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(REGISTRY);

    public ResourceLocation texture() {
        return this.assetId.withPath(path -> "textures/" + path + ".png");
    }

    public static Holder<ZombieNautilusVariant> defaultVariant(RegistryAccess access) {
        Registry<ZombieNautilusVariant> registry = access.registryOrThrow(REGISTRY);
        return registry.getHolder(TEMPERATE).or(() -> registry.holders().findFirst()).orElseThrow();
    }

    public static Optional<Holder<ZombieNautilusVariant>> select(ServerLevelAccessor level, BlockPos pos) {
        record Candidate(Holder<ZombieNautilusVariant> variant, Selector selector) {}
        List<Candidate> candidates = new ArrayList<>();
        level.registryAccess().registryOrThrow(REGISTRY).holders().forEach(holder -> {
            for (Selector selector : holder.value().spawnConditions) candidates.add(new Candidate(holder, selector));
        });
        candidates.sort(Comparator.comparingInt((Candidate candidate) -> candidate.selector.priority()).reversed());
        List<Holder<ZombieNautilusVariant>> matches = new ArrayList<>();
        int priority = Integer.MIN_VALUE;
        for (Candidate candidate : candidates) {
            if (candidate.selector.priority() < priority) break;
            if (candidate.selector.condition().map(condition -> condition.test(level, pos)).orElse(true)) {
                priority = candidate.selector.priority();
                // Preserve duplicate matching selectors: vanilla selects entries, not distinct variants.
                matches.add(candidate.variant);
            }
        }
        return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(level.getRandom().nextInt(matches.size())));
    }

    public record Selector(Optional<NautilusSpawnCondition> condition, int priority) {
        public static final Codec<Selector> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                NautilusSpawnCondition.CODEC.optionalFieldOf("condition").forGetter(Selector::condition),
                Codec.INT.fieldOf("priority").forGetter(Selector::priority)
        ).apply(instance, Selector::new));
    }

    public enum ModelType implements StringRepresentable {
        NORMAL("normal"), WARM("warm");
        public static final Codec<ModelType> CODEC = StringRepresentable.fromEnum(ModelType::values);
        private final String name;
        ModelType(String name) { this.name = name; }
        @Override public String getSerializedName() { return this.name; }
    }
}
