package net.xg787.enderscapesablecompat.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;

import java.util.Optional;

public record SableLodestoneTracker(Optional<GlobalPos> target,Optional<GlobalPos> sable_target, boolean tracked) {
    public static final Codec<SableLodestoneTracker> CODEC = RecordCodecBuilder.create(
            p_337949_ -> p_337949_.group(
                            GlobalPos.CODEC.optionalFieldOf("target").forGetter(SableLodestoneTracker::target),
                            GlobalPos.CODEC.optionalFieldOf("sable_target").forGetter(SableLodestoneTracker::sable_target),
                            Codec.BOOL.optionalFieldOf("tracked", Boolean.valueOf(true)).forGetter(SableLodestoneTracker::tracked)
                    )
                    .apply(p_337949_, SableLodestoneTracker::new)
    );
    public static final StreamCodec<ByteBuf, SableLodestoneTracker> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional), SableLodestoneTracker::target,GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional), SableLodestoneTracker::sable_target, ByteBufCodecs.BOOL, SableLodestoneTracker::tracked, SableLodestoneTracker::new
    );

    public SableLodestoneTracker tick(ServerLevel level) {
        if (this.tracked && !this.sable_target.isEmpty()) {
            if (this.sable_target.get().dimension() != level.dimension()) {
                return this;
            } else {
                BlockPos blockpos = this.sable_target.get().pos();
                return level.isInWorldBounds(blockpos) && level.getPoiManager().existsAtPosition(PoiTypes.LODESTONE, blockpos)
                        ? this
                        : new SableLodestoneTracker(Optional.empty(),Optional.empty(), true);
            }
        } else {
            return this;
        }
    }
}
