package vazkii.quark.content.building.entity;

import com.mojang.authlib.GameProfile;

import io.github.fabricators_of_create.porting_lib.entity.ExtraSpawnDataEntity;
import io.github.fabricators_of_create.porting_lib.fake_players.FakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import vazkii.quark.content.building.module.GlassItemFrameModule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class GlassItemFrame extends ItemFrame implements ExtraSpawnDataEntity {

	public static final EntityDataAccessor<Boolean> IS_SHINY = SynchedEntityData.defineId(GlassItemFrame.class, EntityDataSerializers.BOOLEAN);

	private static final String TAG_SHINY = "isShiny";
	private static final GameProfile DUMMY_PROFILE = new GameProfile(UUID.randomUUID(), "ItemFrame");

	private boolean didHackery = false;
	private Integer onSignRotation = null; //not on sign

	public GlassItemFrame(EntityType<? extends GlassItemFrame> type, Level worldIn) {
		super(type, worldIn);
	}

	public GlassItemFrame(Level worldIn, BlockPos blockPos, Direction face) {
		super(GlassItemFrameModule.glassFrameEntity, worldIn);
		pos = blockPos;
		this.setDirection(face);
	}

	@NotNull
	@Override
	public InteractionResult interact(Player player, @NotNull InteractionHand hand) {
		ItemStack item = getItem();
		if(!player.isShiftKeyDown() && !item.isEmpty() && !(item.getItem() instanceof BannerItem)) {
			BlockPos behind = getBehindPos();
			BlockEntity tile = level.getBlockEntity(behind);

			if(tile != null && tile.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent()) {
				BlockState behindState = level.getBlockState(behind);
				InteractionResult result = behindState.use(level, player, hand, new BlockHitResult(new Vec3(getX(), getY(), getZ()), direction, behind, true));

				if(result.consumesAction())
					return result;
			}
		}

		var res = super.interact(player, hand);
		updateIsOnSign();
		return res;
	}

	@Override
	public void tick() {
		super.tick();

		//same update as normal frames
		if(level.getGameTime() % 100 == 0) {
			updateIsOnSign();
		}

		if(GlassItemFrameModule.glassItemFramesUpdateMaps) {
			ItemStack stack = getItem();
			if(stack.getItem() instanceof MapItem map && level instanceof ServerLevel sworld) {
				ItemStack clone = stack.copy();

				MapItemSavedData data = MapItem.getSavedData(clone, level);
				if(data != null && !data.locked) {
					var fakePlayer = new FakePlayer(sworld, DUMMY_PROFILE);

					clone.setEntityRepresentation(null);
					fakePlayer.setPos(getX(), getY(), getZ());
					fakePlayer.getInventory().setItem(0, clone);

					map.update(level, fakePlayer, data);
				}
			}
		}
	}

	private void updateIsOnSign() {
		onSignRotation = null;
		if(this.direction.getAxis() != Direction.Axis.Y){
			BlockState back = level.getBlockState(getBehindPos());
			if(back.is(BlockTags.STANDING_SIGNS)){
				onSignRotation = back.getValue(StandingSignBlock.ROTATION);
			}
		}
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();

		entityData.define(IS_SHINY, false);
	}

	@Override
	public boolean survives() {
		return isOnSign() || super.survives();
	}

	public BlockPos getBehindPos() {
		return pos.relative(direction.getOpposite());
	}

	public boolean isOnSign() {
		return onSignRotation != null;
	}

	public Integer getOnSignRotation(){
		return onSignRotation;
	}

	@Nullable
	@Override
	public ItemEntity spawnAtLocation(@NotNull ItemStack stack, float offset) {
		if (stack.getItem() == Items.ITEM_FRAME && !didHackery) {
			stack = new ItemStack(getDroppedItem());
			didHackery = true;
		}

		return super.spawnAtLocation(stack, offset);
	}

	@NotNull
	@Override
	public ItemStack getPickResult() {
		ItemStack held = getItem();
		if (held.isEmpty())
			return new ItemStack(getDroppedItem());
		else
			return held.copy();
	}

	private Item getDroppedItem() {
		return entityData.get(IS_SHINY) ? GlassItemFrameModule.glowingGlassFrame : GlassItemFrameModule.glassFrame;
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag cmp) {
		super.addAdditionalSaveData(cmp);

		cmp.putBoolean(TAG_SHINY, entityData.get(IS_SHINY));
	}

	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag cmp) {
		super.readAdditionalSaveData(cmp);

		entityData.set(IS_SHINY, cmp.getBoolean(TAG_SHINY));
	}

	@NotNull
	@Override
	public Packet<?> getAddEntityPacket() {
		return ExtraSpawnDataEntity.createExtraDataSpawnPacket(this);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(this.pos);
		buffer.writeVarInt(this.direction.get3DDataValue());
	}

	@Override
	public void readSpawnData(FriendlyByteBuf buffer) {
		this.pos = buffer.readBlockPos();
		this.setDirection(Direction.from3DDataValue(buffer.readVarInt()));
	}
}
