package net.minecraft.world.entity.animal.nautilus;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.nautilus.NautilusAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerListener;
import zzik2.barched.Barched;
import zzik2.barched.nautilus.NautilusInventoryBridge;
import zzik2.barched.item.NautilusArmorItem;
import zzik2.zreflex.mixin.ModifyName;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractNautilus
extends TamableAnimal
implements HasCustomInventoryScreen,
PlayerRideableJumping, Saddleable, ContainerListener {
    public static final int INVENTORY_SLOT_OFFSET = 500;
    public static final int INVENTORY_ROWS = 3;
    public static final int SMALL_RESTRICTION_RADIUS = 16;
    public static final int LARGE_RESTRICTION_RADIUS = 32;
    public static final int RESTRICTION_RADIUS_BUFFER = 8;
    private static final int EFFECT_DURATION = 60;
    private static final int EFFECT_REFRESH_RATE = 40;
    private static final double NAUTILUS_WATER_RESISTANCE = 0.9;
    private static final float IN_WATER_SPEED_MODIFIER = 0.011f;
    private static final float RIDDEN_SPEED_MODIFIER_IN_WATER = 0.0325f;
    private static final float RIDDEN_SPEED_MODIFIER_ON_LAND = 0.02f;
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(AbstractNautilus.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DASH = SynchedEntityData.defineId(AbstractNautilus.class, EntityDataSerializers.BOOLEAN);
    private static final int DASH_COOLDOWN_TICKS = 40;
    private static final int DASH_MINIMUM_DURATION_TICKS = 5;
    private static final float DASH_MOMENTUM_IN_WATER = 1.2f;
    private static final float DASH_MOMENTUM_ON_LAND = 0.5f;
    private int dashCooldown = 0;
    protected float playerJumpPendingScale;
    protected SimpleContainer inventory;
    private static final double BUBBLE_SPREAD_FACTOR = 0.8;
    private static final double BUBBLE_DIRECTION_SCALE = 1.1;
    private static final double BUBBLE_Y_OFFSET = 0.25;
    private static final double BUBBLE_PROBABILITY_MULTIPLIER = 2.0;
    private static final float BUBBLE_PROBABILITY_MIN = 0.15f;
    private static final float BUBBLE_PROBABILITY_MAX = 1.0f;

    protected AbstractNautilus(EntityType<? extends AbstractNautilus> $$0, Level $$1) {
        super((EntityType<? extends TamableAnimal>)$$0, $$1);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.011f, 0.0f, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.setPathfindingMalus(PathType.WATER, 0.0f);
        this.createInventory();
    }

    @Override
    public boolean isFood(ItemStack $$0) {
        return this.isTame() || this.isBaby() ? $$0.is(Barched.ItemTags.NAUTILUS_FOOD) : $$0.is(Barched.ItemTags.NAUTILUS_TAMING_ITEMS);
    }

    @Override
    protected void usePlayerItem(Player $$0, InteractionHand $$1, ItemStack $$2) {
        if ($$2.is(Barched.ItemTags.NAUTILUS_BUCKET_FOOD)) {
            $$0.setItemInHand($$1, ItemUtils.createFilledResult($$2, $$0, new ItemStack(Items.WATER_BUCKET)));
        } else {
            super.usePlayerItem($$0, $$1, $$2);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 15.0).add(Attributes.MOVEMENT_SPEED, 1.0).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.KNOCKBACK_RESISTANCE, 0.3f);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected PathNavigation createNavigation(Level $$0) {
        return new WaterBoundPathNavigation(this, $$0);
    }

    @Override
    public float getWalkTargetValue(BlockPos $$0, LevelReader $$1) {
        return 0.0f;
    }

    public static boolean checkNautilusSpawnRules(EntityType<? extends AbstractNautilus> $$0, LevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        int $$5 = $$1.getSeaLevel();
        int $$6 = $$5 - 25;
        return $$3.getY() >= $$6 && $$3.getY() <= $$5 - 5 && $$1.getFluidState($$3.below()).is(FluidTags.WATER) && $$1.getBlockState($$3.above()).is(Blocks.WATER);
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader $$0) {
        return $$0.isUnobstructed(this);
    }





    @Override
    protected boolean canAddPassenger(Entity $0) {
        return !this.isVehicle();
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        Direction direction = this.getMotionDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.getDismountLocationForPassenger(passenger);
        }

        int[][] offsets = DismountHelper.offsetsForDirection(direction);
        BlockPos origin = this.blockPosition();
        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();

        for (Pose pose : passenger.getDismountPoses()) {
            AABB bounds = passenger.getLocalBoundsForPose(pose);
            for (int[] offset : offsets) {
                candidate.set(origin.getX() + offset[0], origin.getY(), origin.getZ() + offset[1]);
                double floorHeight = this.level().getBlockFloorHeight(candidate);
                if (!DismountHelper.isBlockFloorValid(floorHeight)) {
                    continue;
                }

                Vec3 location = Vec3.upFromBottomCenterOf(candidate, floorHeight);
                if (DismountHelper.canDismountTo(this.level(), passenger, bounds.move(location))) {
                    passenger.setPose(pose);
                    return location;
                }
            }
        }

        return super.getDismountLocationForPassenger(passenger);
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        Entity $$0 = this.getFirstPassenger();
        if (this.isSaddled() && $$0 instanceof Player) {
            Player $$1 = (Player)$$0;
            return $$1;
        }
        return super.getControllingPassenger();
    }

    @Override
    protected Vec3 getRiddenInput(Player $$0, Vec3 $$1) {
        float $$2 = $$0.xxa;
        float $$3 = 0.0f;
        float $$4 = 0.0f;
        if ($$0.zza != 0.0f) {
            float $$5 = Mth.cos($$0.getXRot() * ((float)Math.PI / 180));
            float $$6 = -Mth.sin($$0.getXRot() * ((float)Math.PI / 180));
            if ($$0.zza < 0.0f) {
                $$5 *= -0.5f;
                $$6 *= -0.5f;
            }
            $$4 = $$6;
            $$3 = $$5;
        }
        return new Vec3($$2, $$4, $$3);
    }

    protected Vec2 getRiddenRotation(LivingEntity $$0) {
        return new Vec2($$0.getXRot() * 0.5f, $$0.getYRot());
    }

    @Override
    protected void tickRidden(Player $$0, Vec3 $$1) {
        super.tickRidden($$0, $$1);
        Vec2 $$2 = this.getRiddenRotation($$0);
        float $$3 = this.getYRot();
        float $$4 = Mth.wrapDegrees($$2.y - $$3);
        float $$5 = 0.5f;
        this.setRot($$3 += $$4 * 0.5f, $$2.x);
        this.yBodyRot = this.yHeadRot = $$3;
        this.yRotO = this.yHeadRot;
        if (this.isControlledByLocalInstance()) {
            if (this.playerJumpPendingScale > 0.0f && !this.jumping) {
                this.executeRidersJump(this.playerJumpPendingScale, $$0);
            }
            this.playerJumpPendingScale = 0.0f;
        }
    }



    @Override
    protected float getRiddenSpeed(Player $$0) {
        return this.isInWater() ? 0.0325f * (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) : 0.02f * (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    protected void doPlayerRide(Player $$0) {
        if (!this.level().isClientSide()) {
            $$0.startRiding(this);
            if (!this.isVehicle()) {
                this.clearRestriction();
            }
        }
    }

    private int getNautilusRestrictionRadius() {
        if (!this.isBaby() && !this.isSaddled()) {
            return 32;
        }
        return 16;
    }

    protected void checkRestriction() {
        if (this.isLeashed() || this.isVehicle() || !this.isTame()) {
            return;
        }
        int $$0 = this.getNautilusRestrictionRadius();
        if (this.hasRestriction() && this.getRestrictCenter().closerThan(this.blockPosition(), $$0 + 8) && $$0 == this.getRestrictRadius()) {
            return;
        }
        this.restrictTo(this.blockPosition(), $$0);
    }

    @Override
    protected void customServerAiStep() {
        this.checkRestriction();
        super.customServerAiStep();
    }

    private void applyEffects(Level $$0) {
        Entity $$1 = this.getFirstPassenger();
        if ($$1 instanceof Player) {
            boolean $$4;
            Player $$2 = (Player)$$1;
            boolean $$3 = $$2.hasEffect(Barched.MobEffects.BREATH_OF_THE_NAUTILUS);
            boolean bl = $$4 = $$0.getGameTime() % 40L == 0L;
            if (!$$3 || $$4) {
                $$2.addEffect(new MobEffectInstance(Barched.MobEffects.BREATH_OF_THE_NAUTILUS, 60, 0, true, true, true));
            }
        }
    }

    private void spawnBubbles() {
        double $$0 = this.getDeltaMovement().length();
        double $$1 = Mth.clamp($$0 * 2.0, (double)0.15f, 1.0);
        if ((double)this.random.nextFloat() < $$1) {
            float $$2 = this.getYRot();
            float $$3 = Mth.clamp(this.getXRot(), -10.0f, 10.0f);
            Vec3 $$4 = this.calculateViewVector($$3, $$2);
            double $$5 = this.random.nextDouble() * 0.8 * (1.0 + $$0);
            double $$6 = ((double)this.random.nextFloat() - 0.5) * $$5;
            double $$7 = ((double)this.random.nextFloat() - 0.5) * $$5;
            double $$8 = ((double)this.random.nextFloat() - 0.5) * $$5;
            this.level().addParticle(ParticleTypes.BUBBLE, this.getX() - $$4.x * 1.1, this.getY() - $$4.y + 0.25, this.getZ() - $$4.z * 1.1, $$6, $$7, $$8);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            this.applyEffects(this.level());
        }
        if (this.isDashing() && this.dashCooldown < 35) {
            this.setDashing(false);
        }
        if (this.dashCooldown > 0) {
            --this.dashCooldown;
            if (this.dashCooldown == 0) {
                this.makeSound(this.getDashReadySound());
            }
        }
        if (this.isInWater()) {
            this.spawnBubbles();
        }
    }

    @Override
    public boolean canJump() {
        return this.isSaddled();
    }

    @Override
    public void onPlayerJump(int $$0) {
        if (!this.isSaddled() || this.dashCooldown > 0) {
            return;
        }
        this.playerJumpPendingScale = ($$0 >= 90 ? 1.0F : 0.4F + 0.4F * (float)Math.max(0, $$0) / 90.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder $$0) {
        super.defineSynchedData($$0);
        $$0.define(DASH, false);
        $$0.define(SADDLED, false);
    }

    public boolean isDashing() {
        return this.entityData.get(DASH);
    }

    public void setDashing(boolean $$0) {
        this.entityData.set(DASH, $$0);
    }

    protected void executeRidersJump(float $$0, Player $$1) {
        this.addDeltaMovement($$1.getLookAngle().scale((double)((this.isInWater() ? 1.2f : 0.5f) * $$0) * this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (double)this.getBlockSpeedFactor()));
        this.dashCooldown = 40;
        this.setDashing(true);
        this.hasImpulse = true;
    }

    @Override
    public void handleStartJump(int $$0) {
        this.makeSound(this.getDashSound());
        this.gameEvent(GameEvent.ENTITY_ACTION);
        this.setDashing(true);
    }

    @Override
    public int getJumpCooldown() {
        return this.dashCooldown;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> $$0) {
        if (!this.firstTick && DASH.equals($$0)) {
            this.dashCooldown = this.dashCooldown == 0 ? 40 : this.dashCooldown;
        }
        super.onSyncedDataUpdated($$0);
    }

    @Override
    public void handleStopJump() {
    }

    @Override
    protected void playStepSound(BlockPos $$0, BlockState $$1) {
    }

    protected @Nullable SoundEvent getDashSound() {
        return null;
    }

    protected @Nullable SoundEvent getDashReadySound() {
        return null;
    }

    @ModifyName("interact")
    public InteractionResult interact0(Player $$0, InteractionHand $$1) {
        this.setPersistenceRequired();
        return super.interact($$0, $$1);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.SHEARS) && !player.isSecondaryUseActive() && !this.isVehicle()) {
            ItemStack armor = this.getBodyArmorItem();
            if (!armor.isEmpty() && (player.isCreative() || !net.minecraft.world.item.enchantment.EnchantmentHelper.has(armor, net.minecraft.world.item.enchantment.EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE))) {
                if (!this.level().isClientSide()) {
                    ItemStack removed = armor.copy();
                    this.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
                    this.spawnAtLocation(removed, (float) this.getAttachments().get(EntityAttachment.PASSENGER, 0, 0.0F).y);
                    this.gameEvent(GameEvent.SHEAR, player);
                    stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
            if (this.isSaddled() && (player.isCreative() || !net.minecraft.world.item.enchantment.EnchantmentHelper.has(this.inventory.getItem(0), net.minecraft.world.item.enchantment.EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE))) {
                if (!this.level().isClientSide()) {
                    ItemStack removed = this.inventory.getItem(0).copy();
                    this.inventory.setItem(0, ItemStack.EMPTY);
                    this.spawnAtLocation(removed, (float) this.getAttachments().get(EntityAttachment.PASSENGER, 0, 0.0F).y);
                    this.gameEvent(GameEvent.SHEAR, player);
                    this.playSound(Barched.SoundEvents.SADDLE_UNEQUIP);
                    stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
        }
        if (this.isBaby()) {
            return this.interactAsAnimal(player, hand);
        }
        if (this.isTame() && player.isSecondaryUseActive()) {
            this.openCustomInventoryScreen(player);
            return InteractionResult.SUCCESS;
        }
        if (!stack.isEmpty()) {
            if (this.isTame() && stack.getItem() instanceof NautilusArmorItem && this.getBodyArmorItem().isEmpty()) {
                if (!this.level().isClientSide()) {
                    this.setItemSlot(EquipmentSlot.BODY, stack.copyWithCount(1));
                    stack.consume(1, player);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
            if (!this.level().isClientSide() && !this.isTame() && this.isFood(stack)) {
                this.usePlayerItem(player, hand, stack);
                this.tryToTame(player);
                return InteractionResult.CONSUME;
            }
            if (this.isFood(stack) && this.getHealth() < this.getMaxHealth()) {
                FoodProperties food = stack.get(DataComponents.FOOD);
                this.heal(food != null ? (float)(2 * food.nutrition()) : 1.0f);
                this.usePlayerItem(player, hand, stack);
                this.playEatingSound();
                return InteractionResult.SUCCESS;
            }
            InteractionResult itemInteraction = stack.interactLivingEntity(player, this, hand);
            if (itemInteraction.consumesAction()) {
                return itemInteraction;
            }
        }
        if (this.isTame() && !player.isSecondaryUseActive() && !this.isFood(stack)) {
            this.doPlayerRide(player);
            return InteractionResult.SUCCESS;
        }
        return this.interactAsAnimal(player, hand);
    }

    private InteractionResult interactAsAnimal(Player player, InteractionHand hand) {
        boolean feeding = this.isFood(player.getItemInHand(hand)) && (this.isBaby() || this.getAge() == 0 && this.canFallInLove());
        InteractionResult result = super.mobInteract(player, hand);
        if (feeding && result.consumesAction() && !this.level().isClientSide()) this.playEatingSound();
        return result;
    }

    private void tryToTame(Player $$0) {
        if (this.random.nextInt(3) == 0) {
            this.tame($$0);
            this.navigation.stop();
            this.level().broadcastEntityEvent(this, (byte)7);
        } else {
            this.level().broadcastEntityEvent(this, (byte)6);
        }
        this.playEatingSound();
    }

    @Override
    public boolean removeWhenFarAway(double $$0) {
        return true;
    }

    @Override
    public boolean hurt(DamageSource $$1, float $$2) {
        Entity entity;
        boolean $$3 = super.hurt($$1, $$2);
        if ($$3 && (entity = $$1.getEntity()) instanceof LivingEntity) {
            LivingEntity $$4 = (LivingEntity)entity;
            if (this.level() instanceof ServerLevel serverLevel) NautilusAi.setAngerTarget(serverLevel, this, $$4);
        }
        return $$3;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance $$0) {
        if ($$0.getEffect() == MobEffects.POISON) {
            return false;
        }
        return super.canBeAffected($$0);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor $$0, DifficultyInstance $$1, MobSpawnType $$2, @Nullable SpawnGroupData $$3) {
        RandomSource $$4 = $$0.getRandom();
        NautilusAi.initMemories(this, $$4);
        return super.finalizeSpawn($$0, $$1, $$2, $$3);
    }















    protected boolean isMobControlled() {
        return this.getFirstPassenger() instanceof Mob;
    }

    protected boolean isAggravated() {
        return this.getBrain().hasMemoryValue(MemoryModuleType.ANGRY_AT) || this.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET);
    }

    @Override
    public boolean canUseSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.BODY ? this.isSaddleable() : super.canUseSlot(slot);
    }

    @Override
    public boolean isSaddleable() { return this.isAlive() && !this.isBaby() && this.isTame(); }

    @Override
    public boolean isSaddled() { return this.entityData.get(SADDLED); }

    @Override
    public void equipSaddle(ItemStack saddle, @Nullable SoundSource source) {
        this.inventory.setItem(0, saddle.copyWithCount(1));

    }

    @Override
    public void travel(Vec3 input) {
        if (this.isControlledByLocalInstance() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), input);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            this.calculateEntityAnimation(false);
        } else {
            super.travel(input);
        }
    }

    protected void playEatingSound() {}

    private void createInventory() {
        this.inventory = new SimpleContainer(2);
        this.inventory.addListener(this);
    }

    public SimpleContainer getInventory() { return this.inventory; }

    @Override
    public @Nullable SlotAccess getSlot(int slot) {
        int inventorySlot = slot - INVENTORY_SLOT_OFFSET;
        return inventorySlot >= 0 && inventorySlot < this.inventory.getContainerSize()
                ? SlotAccess.forContainer(this.inventory, inventorySlot)
                : super.getSlot(slot);
    }

    public boolean hasInventoryChanged(Container oldInventory) {
        return this.inventory != oldInventory;
    }

    @Override
    public void containerChanged(Container container) {
        boolean saddled = this.inventory.getItem(0).is(Items.SADDLE);
        boolean wasSaddled = this.isSaddled();
        this.entityData.set(SADDLED, saddled);
        if (saddled && !wasSaddled && this.tickCount > 20) {
            this.playSound(this.isUnderWater() ? Barched.SoundEvents.NAUTILUS_SADDLE_UNDERWATER_EQUIP : Barched.SoundEvents.NAUTILUS_SADDLE_EQUIP);
        }
        ItemStack armor = this.inventory.getItem(1);
        if (armor != this.getBodyArmorItem()) this.setItemSlot(EquipmentSlot.BODY, armor);
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        ItemStack previous = this.getItemBySlot(slot);
        super.setItemSlot(slot, stack);
        if (slot == EquipmentSlot.BODY && this.inventory != null) {
            if (!stack.isEmpty()) this.setDropChance(EquipmentSlot.BODY, 2.0F);
            if (this.inventory.getItem(1) != stack) this.inventory.setItem(1, stack);
            if (this.tickCount > 20 && !ItemStack.isSameItemSameComponents(previous, stack)) {
                this.playSound(stack.isEmpty() ? Barched.SoundEvents.NAUTILUS_ARMOR_UNEQUIP : Barched.SoundEvents.NAUTILUS_ARMOR_EQUIP);
            }
        }
    }

    @Override
    public boolean isBodyArmorItem(ItemStack stack) { return stack.getItem() instanceof NautilusArmorItem; }

    @Override
    public void openCustomInventoryScreen(Player player) {
        if (!this.level().isClientSide() && (!this.isVehicle() || this.hasPassenger(player)) && this.isTame() && player instanceof NautilusInventoryBridge bridge) {
            bridge.barched$openNautilusInventory(this);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (!this.inventory.getItem(0).isEmpty()) tag.put("SaddleItem", this.inventory.getItem(0).save(this.registryAccess()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.inventory.setItem(0, ItemStack.parseOptional(this.registryAccess(), tag.getCompound("SaddleItem")));
        this.inventory.setItem(1, this.getBodyArmorItem());
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled() && !net.minecraft.world.item.enchantment.EnchantmentHelper.has(this.inventory.getItem(0), net.minecraft.world.item.enchantment.EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) this.spawnAtLocation(this.inventory.getItem(0));
        this.inventory.setItem(0, ItemStack.EMPTY);
    }
}
