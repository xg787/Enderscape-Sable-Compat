package net.xg787.enderscapesablecompat;

import com.google.common.reflect.Reflection;
import net.xg787.enderscapesablecompat.registry.EnderscapeSableDataComponents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(EnderscapeSableCompat.MODID)
public class EnderscapeSableCompat {
    public static final String MODID = "enderscapesablecompat";
    public EnderscapeSableCompat(IEventBus modEventBus, ModContainer modContainer) {
        Reflection.initialize(
                EnderscapeSableDataComponents.class
        );
    }
}
