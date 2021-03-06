package com.github.draylar.betterbees.mixin;

import com.github.draylar.betterbees.registry.BeeTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public World world;

    @Shadow
    public abstract double getY();

    @Shadow
    @Final
    protected Random random;

    @Shadow
    private EntityDimensions dimensions;

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getZ();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"), method = "onSwimmingStart", cancellable = true)
    private void onEnterLiquid(CallbackInfo info) {
        Entity entity = (Entity) (Object) this;
        Vec3d velocity = entity.getVelocity();

        if (isInFluidTag(entity, BeeTags.HONEY)) {
            entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_STEP, 1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);

            float yPos = (float) MathHelper.floor(this.getY());

            for (int i = 0; (float) i < 1.0F + this.dimensions.width * 20.0F; ++i) {
                float particleX = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
                float particleZ = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
                this.world.addParticle(ParticleTypes.LANDING_HONEY, this.getX() + (double) particleX, (double) (yPos + 1.0F), this.getZ() + (double) particleZ, velocity.x, velocity.y - (double) (this.random.nextFloat() * 0.2F), velocity.z);
            }

            info.cancel();
        }
    }

    @Unique
    private boolean isInFluidTag(Entity entity, Tag<Fluid> tag) {
        Box box = entity.getBoundingBox().contract(0.001D);
        int startX = MathHelper.floor(box.x1);
        int endX = MathHelper.ceil(box.x2);
        int startY = MathHelper.floor(box.y1);
        int endY = MathHelper.ceil(box.y2);
        int startZ = MathHelper.floor(box.z1);
        int endZ = MathHelper.ceil(box.z2);

        if (!entity.world.isRegionLoaded(startX, startY, startZ, endX, endY, endZ)) {
            return false;
        } else {
            BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();

            for (int x = startX; x < endX; ++x) {
                for (int y = startY; y < endY; ++y) {
                    for (int z = startZ; z < endZ; ++z) {
                        pooledMutable.method_10113(x, y, z);
                        FluidState fluidState = entity.world.getFluidState(pooledMutable);

                        if (fluidState.matches(tag)) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }
}
