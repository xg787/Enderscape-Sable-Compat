package net.xg787.enderscapesablecompat.registry;

import net.bunten.enderscape.Enderscape;
import net.bunten.enderscape.registry.RegistryHelper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.xg787.enderscapesablecompat.item.component.SableLodestoneTracker;

import java.util.function.UnaryOperator;

public class EnderscapeSableDataComponents {
    public static final DataComponentType<SableLodestoneTracker> SABLE_LODESTONE_TRACKER = register("sable_lodestone_tracker", builder -> builder.persistent(SableLodestoneTracker.CODEC).networkSynchronized(SableLodestoneTracker.STREAM_CODEC).cacheEncoding());

    private static <T> DataComponentType<T> register(String string, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        var type = unaryOperator.apply(DataComponentType.builder()).build();
        RegistryHelper.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Enderscape.id(string), () -> type);
        return type;
    }
}
