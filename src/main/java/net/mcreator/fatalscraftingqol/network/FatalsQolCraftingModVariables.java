package net.mcreator.fatalscraftingqol.network;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.gui.components.Button;

import net.mcreator.fatalscraftingqol.FatalsQolCraftingMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class FatalsQolCraftingModVariables {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FatalsQolCraftingMod.MODID);

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		FatalsQolCraftingMod.addNetworkMessage(SavedDataSyncMessage.ID, SavedDataSyncMessage::new, SavedDataSyncMessage::handleData);
	}

	@Mod.EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			if (event.getEntity() instanceof ServerPlayer player) {
				SavedData mapdata = MapVariables.get(event.getEntity().level());
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (mapdata != null)
					PacketDistributor.PLAYER.with(player).send(new SavedDataSyncMessage(0, mapdata));
				if (worlddata != null)
					PacketDistributor.PLAYER.with(player).send(new SavedDataSyncMessage(1, worlddata));
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (event.getEntity() instanceof ServerPlayer player) {
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (worlddata != null)
					PacketDistributor.PLAYER.with(player).send(new SavedDataSyncMessage(1, worlddata));
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
				PacketDistributor.DIMENSION.with(level.dimension()).send(new SavedDataSyncMessage(1, this));
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(WorldVariables::new, WorldVariables::load), DATA_NAME);
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
				PacketDistributor.ALL.noArg().send(new SavedDataSyncMessage(0, this));
		}

		static MapVariables clientSide = new MapVariables();

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevelAccessor serverLevelAcc) {
				return serverLevelAcc.getLevel().getServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(new SavedData.Factory<>(MapVariables::new, MapVariables::load), DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class SavedDataSyncMessage implements CustomPacketPayload {
		public static final ResourceLocation ID = new ResourceLocation(FatalsQolCraftingMod.MODID, "saved_data_sync");
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

		@Override
		public void write(final FriendlyByteBuf buffer) {
			buffer.writeInt(type);
			if (data != null)
				buffer.writeNbt(data.save(new CompoundTag()));
		}

		@Override
		public ResourceLocation id() {
			return ID;
		}

		public static void handleData(final SavedDataSyncMessage message, final PlayPayloadContext context) {
			if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
				context.workHandler().submitAsync(() -> {
					if (message.type == 0)
						MapVariables.clientSide.read(message.data.save(new CompoundTag()));
					else
						WorldVariables.clientSide.read(message.data.save(new CompoundTag()));
				}).exceptionally(e -> {
					context.packetHandler().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}
}
