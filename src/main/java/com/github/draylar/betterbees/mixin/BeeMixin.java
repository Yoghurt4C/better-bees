package com.github.draylar.betterbees.mixin;

import com.github.draylar.betterbees.ai.EnterApiaryGoal;
import com.github.draylar.betterbees.ai.FindApiaryGoal;
import com.github.draylar.betterbees.ai.MoveToApiaryGoal;
import com.github.draylar.betterbees.entity.ApiaryBlockEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BeeEntity.class)
public abstract class BeeMixin extends AnimalEntity implements Flutterer {

    protected BeeMixin(EntityType<? extends AnimalEntity> type, World world) {
        super(type, world);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 0, shift = At.Shift.AFTER), method = "initGoals")
    private void addFindApiaryGoal(CallbackInfo ci) {
        this.goalSelector.add(0, new FindApiaryGoal(((BeeEntity) (Object) this)));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 2, shift = At.Shift.AFTER), method = "initGoals")
    private void addEnterApiaryGoal(CallbackInfo ci) {
        this.goalSelector.add(1, new EnterApiaryGoal(((BeeEntity) (Object) this)));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 6, shift = At.Shift.AFTER), method = "initGoals")
    private void moveToApiaryGoal(CallbackInfo ci) {
        this.goalSelector.add(5, new MoveToApiaryGoal((BeeEntity) (Object) this));
    }
    
    @Inject(at = @At(value = "RETURN", ordinal = 1), cancellable = true, method = "method_23984", locals = LocalCapture.CAPTURE_FAILHARD)
    private void isHiveOnFire(CallbackInfoReturnable<Boolean> info, BlockEntity be) {
        if (!info.getReturnValueZ() && be instanceof ApiaryBlockEntity && ((ApiaryBlockEntity) be).isHiveOnFire()) {
            info.setReturnValue(true);
        }
    }
    
    @Inject(at = @At(value = "RETURN", ordinal = 1), cancellable = true, method = "isHiveValid", locals = LocalCapture.CAPTURE_FAILHARD)
    private void isHiveValid(CallbackInfoReturnable<Boolean> info, BlockEntity be) {
        if (!info.getReturnValueZ() && be instanceof ApiaryBlockEntity) {
            info.setReturnValue(true);
        }
    }
}
