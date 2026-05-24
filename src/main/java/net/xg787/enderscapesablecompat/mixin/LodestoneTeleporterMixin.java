package net.xg787.enderscapesablecompat.mixin;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.bunten.enderscape.item.LodestoneTeleporter;
import net.bunten.enderscape.item.LodestoneTrackerContext;
import net.bunten.enderscape.item.component.FueledTool;
import net.bunten.enderscape.registry.EnderscapeItemSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.xg787.enderscapesablecompat.item.component.SableLodestoneTracker;
import net.xg787.enderscapesablecompat.registry.EnderscapeSableDataComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Optional;

import static net.bunten.enderscape.item.LodestoneTeleporter.*;

@Mixin(LodestoneTeleporter.class)
public class LodestoneTeleporterMixin {

    /**
     * @author Xg787
     * @reason Update target if lodestone is on sublevel
     */
    @Overwrite
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        updateLodestoneTracker(stack, level);
        enderscape_Sable_Compat$updateTarget(stack, level);
    }

    @Unique
    private static void enderscape_Sable_Compat$updateTarget(ItemStack stack, Level level) {
        if (stack.has(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER) && stack.get(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER).sable_target().isPresent()&& stack.get(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER).target().isPresent()) {
            if (stack.get(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER).sable_target().get().pos() != stack.get(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER).target().get().pos()) {
                BlockPos blockPos = stack.get(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER).sable_target().get().pos();
                SubLevel subLevel = Sable.HELPER.getContaining(level, blockPos);
                BlockPos position = blockPos;

                if (subLevel != null) {
                    Pose3dc pose = subLevel.logicalPose();
                    position = BlockPos.containing(pose.transformPosition(blockPos.getCenter()));
                }

                if (blockPos != position) {
                    stack.set(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER, new SableLodestoneTracker(Optional.of(GlobalPos.of(level.dimension(), position)), Optional.of(GlobalPos.of(level.dimension(), blockPos)), true));
                }
            }
        }
    }

    /**
     * @author Xg787
     * @reason Move to sable lodestone tracker
     */
    @Overwrite
    public static void updateLodestoneTracker(ItemStack stack, Level level) {
        if (level instanceof ServerLevel server) {
            if (stack.has(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER)) {
                SableLodestoneTracker tracker = (SableLodestoneTracker)stack.get(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER);
                SableLodestoneTracker ticked = tracker.tick(server);
                if (ticked != tracker) {
                    stack.set(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER, ticked);
                }
            }
        }
    }


    /**
     * @author Xg787
     * @reason Move to sable lodestone tracker
     */
    @Overwrite
    public static boolean isLinked(ItemStack stack) {
        if ((stack.get(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER)) != null) {
            return wasLinkedBefore(stack) && ((SableLodestoneTracker) stack.get(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER)).sable_target().isPresent();
        }
        else {
            return false;
        }
    }

    /**
     * @author Xg787
     * @reason Move to sable lodestone tracker
     */
    @Overwrite
    public static boolean wasLinkedBefore(ItemStack stack) {
        return stack.has(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER);
    }


    /**
     * @author Xg787
     * @reason Move to sable lodestone tracker
     */
    @Overwrite
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();
        if (FueledTool.is(stack) && state.is(Blocks.LODESTONE)) {
            enderscape_Sable_Compat$writeData(stack, pos, level, context.getLevel().dimension());
            level.playSound((Player)null, pos, (SoundEvent) EnderscapeItemSounds.MIRROR_LINK.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS_NO_ITEM_USED;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Unique
    private static void enderscape_Sable_Compat$writeData(ItemStack stack, BlockPos pos, Level level, ResourceKey<Level> dimension) {

        SubLevel subLevel = Sable.HELPER.getContaining(level, pos);
        BlockPos position = pos;

        if (subLevel != null) {
            Pose3dc pose = subLevel.logicalPose();
            position = BlockPos.containing(pose.transformPosition(pos.getCenter()));
        }

        stack.set(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER, new SableLodestoneTracker(Optional.of(GlobalPos.of(dimension, position)), Optional.of(GlobalPos.of(dimension, pos)), true));
    }

    /**
     * @author Xg787
     * @reason Fix unloaded sublevel teleport and move to sable lodestone tracker
     */
    @Overwrite
    public static Optional<Vec3> getTeleportPosition(LodestoneTrackerContext context) {
        ServerLevel level = context.linkedLevel();
        LivingEntity user = context.user();
        EntityDimensions dimensions = user.getDimensions(Pose.STANDING);

        BlockPos blockPos = context.stack().get(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER).sable_target().get().pos();
        Vec3 offsetPos = blockPos.above().getBottomCenter().add((double)0.0F, (double)dimensions.height() / (double)2.0F, (double)0.0F);
        VoxelShape shape = Shapes.create(AABB.ofSize(offsetPos, (double)(dimensions.width() + 1.0F), (double)(dimensions.height() + 1.0F), (double)(dimensions.width() + 1.0F)).inflate(1.0E-6));
        Optional<Vec3> freePos = level.findFreePosition(user, shape, offsetPos, (double)dimensions.width(), (double)dimensions.height(), (double)dimensions.width());
        if (freePos.isPresent()) {
            Vec3 pos = (Vec3)freePos.get();
            BlockPos.MutableBlockPos mutable = BlockPos.containing(pos).mutable();

            for(int i = 0; i < 8; ++i) {
                if (level.getBlockState(mutable.below()).isFaceSturdy(level, mutable, Direction.UP)) {
                    return Optional.of(Vec3.atLowerCornerOf(mutable));
                }

                mutable.move(Direction.DOWN);
            }
        }

        if (context.stack().get(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER).sable_target().get().pos() != context.stack().get(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER).target().get().pos()){
            blockPos = context.stack().get(EnderscapeSableDataComponents.SABLE_LODESTONE_TRACKER).target().get().pos();
            return Optional.of(new Vec3(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ()));
        }

        return Optional.empty();
    }
}
