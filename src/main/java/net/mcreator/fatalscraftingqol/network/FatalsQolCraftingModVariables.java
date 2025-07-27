package net.mcreator.fatalscraftingqol.network;

import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.gui.components.Button;

import net.mcreator.fatalscraftingqol.FatalsQolCraftingMod;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class FatalsQolCraftingModVariables {
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		FatalsQolCraftingMod.addNetworkMessage(SavedDataSyncMessage.class, SavedDataSyncMessage::buffer, SavedDataSyncMessage::new, SavedDataSyncMessage::handler);
	}

	@Mod.EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.getEntity().level().isClientSide()) {
				SavedData mapdata = MapVariables.get(event.getEntity().level());
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (mapdata != null)
					FatalsQolCraftingMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(0, mapdata));
				if (worlddata != null)
					FatalsQolCraftingMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (!event.getEntity().level().isClientSide()) {
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (worlddata != null)
					FatalsQolCraftingMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
			}
		}
	}

	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "fatals_qol_crafting_worldvars";

		public static WorldVariables load(CompoundTag tag) {
			WorldVariables data = new WorldVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
		}

		@Override
		public CompoundTag save(CompoundTag nbt) {
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level level && !level.isClientSide())
				FatalsQolCraftingMod.PACKET_HANDLER.send(PacketDistributor.DIMENSION.with(level::dimension), new SavedDataSyncMessage(1, this));
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(e -> WorldVariables.load(e), WorldVariables::new, DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "fatals_qol_crafting_mapvars";
		public boolean ChainmailArmor = true;
		public boolean Stair = true;
		public boolean Button = true;
		public boolean Fences = true;
		public boolean PressurePlate = true;
		public boolean Trapdoor = true;
		public boolean Wood_Hyphae = true;
		public boolean SlabsToBlocks = true;
		public boolean StairsToBlocks = true;
		public boolean SmeltRawOreBlocks = true;
		public boolean Dispensers = true;
		public boolean NetherWartBlockUncraft = true;
		public boolean Cobwebs = true;
		public boolean QuartzBlockUncraft = true;
		public boolean ClayUncraft = true;
		public boolean DeadBush = true;
		public boolean ChestsFromLogs = true;
		public boolean CraftableNylium = true;
		public boolean WoolToString = false;

		public static MapVariables load(CompoundTag tag) {
			MapVariables data = new MapVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
			ChainmailArmor = nbt.getBoolean("ChainmailArmor");
			Stair = nbt.getBoolean("Stair");
			Button = nbt.getBoolean("Button");
			Fences = nbt.getBoolean("Fences");
			PressurePlate = nbt.getBoolean("PressurePlate");
			Trapdoor = nbt.getBoolean("Trapdoor");
			Wood_Hyphae = nbt.getBoolean("Wood_Hyphae");
			SlabsToBlocks = nbt.getBoolean("SlabsToBlocks");
			StairsToBlocks = nbt.getBoolean("StairsToBlocks");
			SmeltRawOreBlocks = nbt.getBoolean("SmeltRawOreBlocks");
			Dispensers = nbt.getBoolean("Dispensers");
			NetherWartBlockUncraft = nbt.getBoolean("NetherWartBlockUncraft");
			Cobwebs = nbt.getBoolean("Cobwebs");
			QuartzBlockUncraft = nbt.getBoolean("QuartzBlockUncraft");
			ClayUncraft = nbt.getBoolean("ClayUncraft");
			DeadBush = nbt.getBoolean("DeadBush");
			ChestsFromLogs = nbt.getBoolean("ChestsFromLogs");
			CraftableNylium = nbt.getBoolean("CraftableNylium");
			WoolToString = nbt.getBoolean("WoolToString");
		}

		@Override
		public CompoundTag save(CompoundTag nbt) {
			nbt.putBoolean("ChainmailArmor", ChainmailArmor);
			nbt.putBoolean("Stair", Stair);
			nbt.putBoolean("Button", Button);
			nbt.putBoolean("Fences", Fences);
			nbt.putBoolean("PressurePlate", PressurePlate);
			nbt.putBoolean("Trapdoor", Trapdoor);
			nbt.putBoolean("Wood_Hyphae", Wood_Hyphae);
			nbt.putBoolean("SlabsToBlocks", SlabsToBlocks);
			nbt.putBoolean("StairsToBlocks", StairsToBlocks);
			nbt.putBoolean("SmeltRawOreBlocks", SmeltRawOreBlocks);
			nbt.putBoolean("Dispensers", Dispensers);
			nbt.putBoolean("NetherWartBlockUncraft", NetherWartBlockUncraft);
			nbt.putBoolean("Cobwebs", Cobwebs);
			nbt.putBoolean("QuartzBlockUncraft", QuartzBlockUncraft);
			nbt.putBoolean("ClayUncraft", ClayUncraft);
			nbt.putBoolean("DeadBush", DeadBush);
			nbt.putBoolean("ChestsFromLogs", ChestsFromLogs);
			nbt.putBoolean("CraftableNylium", CraftableNylium);
			nbt.putBoolean("WoolToString", WoolToString);
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level && !world.isClientSide())
				FatalsQolCraftingMod.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SavedDataSyncMessage(0, this));
		}

		static MapVariables clientSide = new MapVariables();

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevelAccessor serverLevelAcc) {
				return serverLevelAcc.getLevel().getServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(e -> MapVariables.load(e), MapVariables::new, DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class SavedDataSyncMessage {
		private final int type;
		private SavedData data;

		public SavedDataSyncMessage(FriendlyByteBuf buffer) {
			this.type = buffer.readInt();
			CompoundTag nbt = buffer.readNbt();
			if (nbt != null) {
				this.data = this.type == 0 ? new MapVariables() : new WorldVariables();
				if (this.data instanceof MapVariables mapVariables)
					mapVariables.read(nbt);
				else if (this.data instanceof WorldVariables worldVariables)
					worldVariables.read(nbt);
			}
		}

		public SavedDataSyncMessage(int type, SavedData data) {
			this.type = type;
			this.data = data;
		}

		public static void buffer(SavedDataSyncMessage message, FriendlyByteBuf buffer) {
			buffer.writeInt(message.type);
			if (message.data != null)
				buffer.writeNbt(message.data.save(new CompoundTag()));
		}

		public static void handler(SavedDataSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				if (!context.getDirection().getReceptionSide().isServer() && message.data != null) {
					if (message.type == 0)
						MapVariables.clientSide = (MapVariables) message.data;
					else
						WorldVariables.clientSide = (WorldVariables) message.data;
				}
			});
			context.setPacketHandled(true);
		}
	}
}
