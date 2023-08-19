package vazkii.quark.content.tools.entity.rang;

import com.google.common.collect.Multimap;
import io.github.fabricators_of_create.porting_lib.event.common.PlayerEvents;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.content.mobs.entity.Toretoise;
import vazkii.quark.content.tools.config.PickarangType;
import vazkii.quark.content.tools.module.PickarangModule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class AbstractPickarang<T extends AbstractPickarang<T>> extends Projectile {

	private static final EntityDataAccessor<ItemStack> STACK = SynchedEntityData.defineId(AbstractPickarang.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(AbstractPickarang.class, EntityDataSerializers.BOOLEAN);

	protected LivingEntity owner;
	private UUID ownerId;

	protected int liveTime;
	private int slot;
	private int blockHitCount;
	private IntOpenHashSet entitiesHit;

	private static final String TAG_RETURNING = "returning";
	private static final String TAG_LIVE_TIME = "liveTime";
	private static final String TAG_BLOCKS_BROKEN = "hitCount";
	private static final String TAG_RETURN_SLOT = "returnSlot";
	private static final String TAG_ITEM_STACK = "itemStack";

	public AbstractPickarang(EntityType<? extends AbstractPickarang<?>> type, Level worldIn) {
		super(type, worldIn);
	}

	public AbstractPickarang(EntityType<? extends AbstractPickarang<?>> type, Level worldIn, LivingEntity throwerIn) {
		super(type, worldIn);
		Vec3 pos = throwerIn.position();
		this.setPos(pos.x, pos.y + throwerIn.getEyeHeight(), pos.z);
		ownerId = throwerIn.getUUID();
	}

	@Override
	@ClientOnly
	public boolean shouldRenderAtSqrDistance(double distance) {
		double d0 = this.getBoundingBox().getSize() * 4.0D;
		if (Double.isNaN(d0)) d0 = 4.0D;

		d0 = d0 * 64.0D;
		return distance < d0 * d0;
	}

	public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy) {
		float f = -Mth.sin(rotationYawIn * ((float)Math.PI / 180F)) * Mth.cos(rotationPitchIn * ((float)Math.PI / 180F));
		float f1 = -Mth.sin((rotationPitchIn + pitchOffset) * ((float)Math.PI / 180F));
		float f2 = Mth.cos(rotationYawIn * ((float)Math.PI / 180F)) * Mth.cos(rotationPitchIn * ((float)Math.PI / 180F));
		this.shoot(f, f1, f2, velocity, inaccuracy);
		Vec3 Vector3d = entityThrower.getDeltaMovement();
		this.setDeltaMovement(this.getDeltaMovement().add(Vector3d.x, entityThrower.isOnGround() ? 0.0D : Vector3d.y, Vector3d.z));
	}


	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		Vec3 vec = (new Vec3(x, y, z)).normalize().add(this.random.nextGaussian() * 0.0075F * inaccuracy, this.random.nextGaussian() * 0.0075F * inaccuracy, this.random.nextGaussian() * 0.0075F * inaccuracy).scale(velocity);
		this.setDeltaMovement(vec);
		float f = (float) vec.horizontalDistance();
		setYRot((float)(Mth.atan2(vec.x, vec.z) * (180F / (float)Math.PI)));
		setXRot((float)(Mth.atan2(vec.y, f) * (180F / (float)Math.PI)));
		this.yRotO = this.getYRot();
		this.xRotO = this.getXRot();
	}

	@Override
	@ClientOnly
	public void lerpMotion(double x, double y, double z) {
		this.setDeltaMovement(x, y, z);
		if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
			float f = (float) Math.sqrt(x * x + z * z);
			setYRot((float)(Mth.atan2(x, z) * (180F / (float)Math.PI)));
			setXRot((float)(Mth.atan2(y, f) * (180F / (float)Math.PI)));
			this.yRotO = this.getYRot();
			this.xRotO = this.getXRot();
		}

	}

	public void setThrowData(int slot, ItemStack stack) {
		this.slot = slot;
		setStack(stack.copy());
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(STACK, new ItemStack(PickarangModule.pickarang));
		entityData.define(RETURNING, false);
	}

	protected void checkImpact() {
		if(level.isClientSide)
			return;

		Vec3 motion = getDeltaMovement();
		Vec3 position = position();
		Vec3 rayEnd = position.add(motion);

		boolean doEntities = true;
		int tries = 100;

		while(isAlive() && !isReturning()) {
			if(doEntities) {
				EntityHitResult result = raycastEntities(position, rayEnd);
				if(result != null)
					onHit(result);
				else doEntities = false;
			} else {
				HitResult result = level.clip(new ClipContext(position, rayEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
				if(result.getType() == Type.MISS)
					return;

				else {
					onHit(result);
				}
			}

			if(tries-- <= 0) {
				(new RuntimeException("Pickarang hit way too much, this shouldn't happen")).printStackTrace();
				return;
			}
		}
	}

	@Nullable
	protected EntityHitResult raycastEntities(Vec3 from, Vec3 to) {
		return ProjectileUtil.getEntityHitResult(level, this, from, to, getBoundingBox().expandTowards(getDeltaMovement()).inflate(1.0D), (entity) ->
			!entity.isSpectator()
				&& entity.isAlive()
				&& (entity.isPickable() || entity instanceof AbstractPickarang)
				&& entity != getThrower()
				&& (entitiesHit == null || !entitiesHit.contains(entity.getId())));
	}

	protected boolean canDestroyBlock(BlockState state) {
		return !state.is(PickarangModule.pickarangImmuneTag);
	}

	@Override
	protected void onHit(@NotNull HitResult result) {
		LivingEntity owner = getThrower();

		if(result.getType() == Type.BLOCK && result instanceof BlockHitResult blockHitResult) {
			BlockPos hit = blockHitResult.getBlockPos();
			BlockState state = level.getBlockState(hit);

			if(getPiercingModifier() == 0 || state.getMaterial().isSolidBlocking())
				addHit();

			if(!(owner instanceof ServerPlayer player))
				return;
			//more general way of doing it instead of just checking hardness
			float progress = getBlockDestroyProgress(state,player, level, hit);
			if (progress == 0) return;

			float equivalentHardness = (1) / (progress * 100);

			if (equivalentHardness <= getPickarangType().maxHardness
					&& equivalentHardness >= 0
					&& canDestroyBlock(state)) {
				ItemStack prev = player.getMainHandItem();
				player.setItemInHand(InteractionHand.MAIN_HAND, getStack());

				if (player.gameMode.destroyBlock(hit))
					level.levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, hit, Block.getId(state));
				else
					clank();

				setStack(player.getMainHandItem());

				player.setItemInHand(InteractionHand.MAIN_HAND, prev);
			} else
				clank();
		} else if(result.getType() == Type.ENTITY && result instanceof EntityHitResult entityHitResult) {
			Entity hit = entityHitResult.getEntity();

			if(hit != owner) {
				addHit(hit);
				if (hit instanceof AbstractPickarang) {
					((AbstractPickarang<?>) hit).setReturning();
					clank();
				} else {
					ItemStack pickarang = getStack();
					Multimap<Attribute, AttributeModifier> modifiers = pickarang.getAttributeModifiers(EquipmentSlot.MAINHAND);

					if (owner != null) {
						ItemStack prev = owner.getMainHandItem();
						owner.setItemInHand(InteractionHand.MAIN_HAND, pickarang);
						owner.getAttributes().addTransientAttributeModifiers(modifiers);

						int ticksSinceLastSwing = owner.attackStrengthTicker;
						owner.attackStrengthTicker = (int) (1.0 / owner.getAttributeValue(Attributes.ATTACK_SPEED) * 20.0) + 1;

						float prevHealth = hit instanceof LivingEntity ? ((LivingEntity) hit).getHealth() : 0;

						PickarangModule.setActivePickarang(this);

						hitEntity: {
							if (hit instanceof Toretoise toretoise) {
								int ore = toretoise.getOreType();

								if(ore != 0) {
									addHit(toretoise);
									if (level instanceof ServerLevel serverLevel) {
										LootContext.Builder lootBuilder = new LootContext.Builder(serverLevel)
												.withParameter(LootContextParams.TOOL, pickarang);
										if (owner instanceof Player player)
											lootBuilder.withLuck(player.getLuck());
										toretoise.dropOre(ore, lootBuilder);
									}
									break hitEntity;
								}
							}

							if (owner instanceof Player owningSFPlayer)
								owningSFPlayer.attack(hit);
							else
								owner.doHurtTarget(hit);

							if (hit instanceof LivingEntity && ((LivingEntity) hit).getHealth() == prevHealth)
								clank();
						}


						PickarangModule.setActivePickarang(null);

						owner.attackStrengthTicker = ticksSinceLastSwing;

						setStack(owner.getMainHandItem());
						owner.setItemInHand(InteractionHand.MAIN_HAND, prev);
						owner.getAttributes().addTransientAttributeModifiers(modifiers);
					} else {
						Builder mapBuilder = new Builder();
						mapBuilder.add(Attributes.ATTACK_DAMAGE, 1);
						AttributeSupplier map = mapBuilder.build();
						AttributeMap manager = new AttributeMap(map);
						manager.addTransientAttributeModifiers(modifiers);

						ItemStack stack = getStack();
						stack.hurt(1, level.random, null);
						setStack(stack);
						hit.hurt(new IndirectEntityDamageSource("player", this, this).setProjectile(),
								(float) manager.getValue(Attributes.ATTACK_DAMAGE));
					}
				}
			}
		}
	}

	//equivalent of BlockState::getDestroyProgress
	private float getBlockDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		float f = state.getDestroySpeed(level, pos);
		if (f == -1.0F) {
			return 0.0F;
		} else {
			float i = PortingHooks.isCorrectToolForDrops(state, player) ? 30 : 100;
			float digSpeed = getPlayerDigSpeed(player, state, pos);
			return (digSpeed / f / i);
		}
	}

	//equivalent of Player::getDigSpeed but without held item stack stuff
	private float getPlayerDigSpeed(Player player, BlockState state, @Nullable BlockPos pos) {
		float f = 1;

		if (MobEffectUtil.hasDigSpeed(player)) {
			f *= 1.0F + (MobEffectUtil.getDigSpeedAmplification(player) + 1) * 0.2F;
		}

		if (player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
			float f1 = switch (player.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
				case 0 -> 0.3F;
				case 1 -> 0.09F;
				case 2 -> 0.0027F;
				default -> 8.1E-4F;
			};

			f *= f1;
		}
		if (this.isEyeInFluid(FluidTags.WATER)) {
			f /= 5.0F;
		}


		f = new PlayerEvents.BreakSpeed(player, state, f, pos).getNewSpeed();
		return f;
	}

	public void spark() {
		playSound(QuarkSounds.ENTITY_PICKARANG_SPARK, 1, 1);
		setReturning();
	}

	public void clank() {
		playSound(QuarkSounds.ENTITY_PICKARANG_CLANK, 1, 1);
		setReturning();
	}

	public void addHit(Entity entity) {
		if (entitiesHit == null)
			entitiesHit = new IntOpenHashSet(5);
		entitiesHit.add(entity.getId());
		postHit();
	}

	public void postHit() {
		if((entitiesHit == null ? 0 : entitiesHit.size()) + blockHitCount > getPiercingModifier())
			setReturning();
		else if (getPiercingModifier() > 0)
			setDeltaMovement(getDeltaMovement().scale(0.8));
	}

	public void addHit() {
		blockHitCount++;
		postHit();
	}

	protected void setReturning() {
		entityData.set(RETURNING, true);
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	@Override
	public void tick() {
		Vec3 pos = position();

		this.xOld = pos.x;
		this.yOld = pos.y;
		this.zOld = pos.z;
		super.tick();

		if(!isReturning())
			checkImpact();

		Vec3 ourMotion = this.getDeltaMovement();
		setPos(pos.x + ourMotion.x, pos.y + ourMotion.y, pos.z + ourMotion.z);

		float f = (float) ourMotion.horizontalDistance();
		setYRot((float)(Mth.atan2(ourMotion.x, ourMotion.z) * (180F / (float)Math.PI)));

		setXRot((float)(Mth.atan2(ourMotion.y, f) * (180F / (float)Math.PI)));
		while (this.getXRot() - this.xRotO < -180.0F) this.xRotO -= 360.0F;

		while(this.getXRot() - this.xRotO >= 180.0F) this.xRotO += 360.0F;

		while(this.getYRot() - this.yRotO < -180.0F) this.yRotO -= 360.0F;

		while(this.getYRot() - this.yRotO >= 180.0F) this.yRotO += 360.0F;

		setXRot(Mth.lerp(0.2F, this.xRotO, this.getXRot()));
		setYRot(Mth.lerp(0.2F, this.yRotO, this.getYRot()));


		float drag;
		if (this.isInWater()) {
			for(int i = 0; i < 4; ++i) {
				this.level.addParticle(ParticleTypes.BUBBLE, pos.x - ourMotion.x * 0.25D, pos.y - ourMotion.y * 0.25D, pos.z - ourMotion.z * 0.25D, ourMotion.x, ourMotion.y, ourMotion.z);
			}

			drag = 0.8F;
		} else drag = 0.99F;

		if(hasDrag())
			this.setDeltaMovement(ourMotion.scale(drag));

		pos = position();
		this.setPos(pos.x, pos.y, pos.z);

		if(!isAlive())
			return;

		ItemStack stack = getStack();
		emitParticles(pos, ourMotion);

		boolean returning = isReturning();
		liveTime++;

		LivingEntity owner = getThrower();
		if(owner == null || !owner.isAlive() || !(owner instanceof Player)) {
			if(!level.isClientSide) {
				while(isInWall())
					setPos(getX(), getY() + 1, getZ());

				spawnAtLocation(stack, 0);
				discard();
			}

			return;
		}

		if(!returning) {
			if(liveTime > getPickarangType().timeout)
				setReturning();
			if (!level.getWorldBorder().isWithinBounds(getBoundingBox()))
				spark();
		} else {
			noPhysics = true;

			int eff = getEfficiencyModifier();

			List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, getBoundingBox().inflate(2));
			List<ExperienceOrb> xp = level.getEntitiesOfClass(ExperienceOrb.class, getBoundingBox().inflate(2));

			Vec3 ourPos = position();
			for(ItemEntity item : items) {
				if (item.isPassenger())
					continue;
				item.startRiding(this);

				item.setPickUpDelay(5);
			}

			for(ExperienceOrb xpOrb : xp) {
				if (xpOrb.isPassenger())
					continue;
				xpOrb.startRiding(this);
			}

			Vec3 ownerPos = owner.position().add(0, 1, 0);
			Vec3 motion = ownerPos.subtract(ourPos);
			double motionMag = 3.25 + eff * 0.25;

			if(motion.lengthSqr() < motionMag) {
				Player player = (Player) owner;
				Inventory inventory = player.getInventory();
				ItemStack stackInSlot = inventory.getItem(slot);

				if(!level.isClientSide) {
					playSound(QuarkSounds.ENTITY_PICKARANG_PICKUP, 1, 1);

					if(player instanceof ServerPlayer sp && (this instanceof Flamerang) && isOnFire() && getPassengers().size() > 0)
						PickarangModule.useFlamerangTrigger.trigger(sp);

					if(!stack.isEmpty()) if (player.isAlive() && stackInSlot.isEmpty())
						inventory.setItem(slot, stack);
					else if (!player.isAlive() || !inventory.add(stack))
						player.drop(stack, false);

					if (player.isAlive()) {
						for (ItemEntity item : items)
							if(item.isAlive())
								giveItemToPlayer(player, item);

						for (ExperienceOrb xpOrb : xp)
							if(xpOrb.isAlive())
								xpOrb.playerTouch(player);

						for (Entity riding : getPassengers()) {
							if (!riding.isAlive())
								continue;

							if (riding instanceof ItemEntity)
								giveItemToPlayer(player, (ItemEntity) riding);
							else if (riding instanceof ExperienceOrb)
								riding.playerTouch(player);
						}
					}

					discard();
				}
			} else
				setDeltaMovement(motion.normalize().scale(0.7 + eff * 0.325F));
		}
	}

	public boolean isReturning() {
		return entityData.get(RETURNING);
	}

	protected void emitParticles(Vec3 pos, Vec3 ourMotion) {
		// NO-OP
	}

	public boolean hasDrag() {
		return true;
	}

	public abstract PickarangType<T> getPickarangType();

	private void giveItemToPlayer(Player player, ItemEntity itemEntity) {
		itemEntity.setPickUpDelay(0);
		itemEntity.playerTouch(player);

		if (itemEntity.isAlive()) {
			// Player could not pick up everything
			ItemStack drop = itemEntity.getItem();

			player.drop(drop, false);
			itemEntity.discard();
		}
	}

	@Nullable
	public LivingEntity getThrower() {
		if (this.owner == null && this.ownerId != null && this.level instanceof ServerLevel) {
			Entity entity = ((ServerLevel)this.level).getEntity(this.ownerId);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity)entity;
			} else {
				this.ownerId = null;
			}
		}

		return this.owner;
	}

	@Override
	protected boolean canAddPassenger(@NotNull Entity passenger) {
		return super.canAddPassenger(passenger) || passenger instanceof ItemEntity || passenger instanceof ExperienceOrb;
	}

	@Override
	public double getPassengersRidingOffset() {
		return 0;
	}

	@NotNull
	@Override
	public SoundSource getSoundSource() {
		return SoundSource.PLAYERS;
	}

	public int getEfficiencyModifier() {
		return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, getStack());
	}

	public int getPiercingModifier() {
		return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, getStack());
	}

	public ItemStack getStack() {
		return entityData.get(STACK);
	}

	public void setStack(ItemStack stack) {
		entityData.set(STACK, stack);
	}

	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag compound) {
		entityData.set(RETURNING, compound.getBoolean(TAG_RETURNING));
		liveTime = compound.getInt(TAG_LIVE_TIME);
		blockHitCount = compound.getInt(TAG_BLOCKS_BROKEN);
		slot = compound.getInt(TAG_RETURN_SLOT);

		if (compound.contains(TAG_ITEM_STACK))
			setStack(ItemStack.of(compound.getCompound(TAG_ITEM_STACK)));
		else
			setStack(new ItemStack(PickarangModule.pickarang));

		if (compound.contains("owner", 10)) {
			Tag owner = compound.get("owner");
			if (owner != null)
				this.ownerId = NbtUtils.loadUUID(owner);
		}
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag compound) {
		compound.putBoolean(TAG_RETURNING, isReturning());
		compound.putInt(TAG_LIVE_TIME, liveTime);
		compound.putInt(TAG_BLOCKS_BROKEN, blockHitCount);
		compound.putInt(TAG_RETURN_SLOT, slot);

		CompoundTag stackTag = new CompoundTag();
		getStack().save(stackTag);
		compound.put(TAG_ITEM_STACK, stackTag);
		if (this.ownerId != null)
			compound.put("owner", NbtUtils.createUUID(this.ownerId));
	}

	@NotNull
	@Override
	public Packet<?> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}

}
