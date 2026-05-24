package net.xg787.enderscapesablecompat.mixin;

import net.bunten.enderscape.item.ItemStackContext;
import net.bunten.enderscape.item.LodestoneTrackerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.xg787.enderscapesablecompat.item.component.SableLodestoneTracker;
import net.xg787.enderscapesablecompat.registry.EnderscapeSableDataComponents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(LodestoneTrackerContext.class)
public class LodestoneTrackerContextMixin extends ItemStackContext {
    public LodestoneTrackerContextMixin(ItemStack stack, Level level, LivingEntity user) {
        super(stack, level, user);
    }

    /**
     * @author Xg787
     * @reason use SableLodestoneTracker
     */
    @Overwrite
    public @Nullable BlockPos linkedPos() {
        if (this.stack().has(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER)) {
            SableLodestoneTracker tracker = (SableLodestoneTracker)this.stack().get(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER);
            if (tracker.sable_target().isPresent()) {
                return ((GlobalPos)tracker.target().get()).pos();
            }
        }

        return null;
    }

    /**
     * @author Xg787
     * @reason use SableLodestoneTracker
     */
    @Overwrite
    public @Nullable ResourceKey<Level> linkedDimension() {
        if (this.stack().has(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER)) {
            SableLodestoneTracker tracker = (SableLodestoneTracker)this.stack().get(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER);
            if (tracker.sable_target().isPresent()) {
                return ((GlobalPos)tracker.target().get()).dimension();
            }
        }

        return null;
    }
}
