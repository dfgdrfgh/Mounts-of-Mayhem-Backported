package zzik2.barched.stat;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public final class BarchedStats {
    public static final ResourceLocation NAUTILUS_ONE_CM = ResourceLocation.withDefaultNamespace("nautilus_one_cm");

    private BarchedStats() {
    }

    public static void register() {
        if (!BuiltInRegistries.CUSTOM_STAT.containsKey(NAUTILUS_ONE_CM)) {
            Registry.register(BuiltInRegistries.CUSTOM_STAT, NAUTILUS_ONE_CM, NAUTILUS_ONE_CM);
        }
        Stats.CUSTOM.get(NAUTILUS_ONE_CM, StatFormatter.DISTANCE);
    }
}
