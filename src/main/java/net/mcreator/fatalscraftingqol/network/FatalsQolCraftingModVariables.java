package net.mcreator.fatalscraftingqol.network;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.client.gui.components.Button;

import net.mcreator.fatalscraftingqol.FatalsQolCraftingMod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class FatalsQolCraftingModVariables {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FatalsQolCraftingMod.MODID);

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		FatalsQolCraftingMod.addNetworkMessage(SavedDataSyncMessage.TYPE, SavedDataSyncMessage.STREAM_CODEC, SavedDataSyncMessage::handleData);
	}

	@EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			if (event.getEntity() instanceof ServerPlayer player) {
				SavedData mapdata = MapVariables.get(event.getEntity().level());
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (mapdata != null)
					PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(0, mapdata));
				if (worlddata != null)
					PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(1, worlddata));
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (event.getEntity() instanceof ServerPlayer player) {
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (worlddata != null)
					PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(1, worlddata));
			}
		}
	}

	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "fatals_qol_crafting_worldvars";

		public static WorldVariables load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
			WorldVariables data = new WorldVariables();
			data.read(tag, lookupProvider);
			return data;
		}

		public void read(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
		}

		@Override
		public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof ServerLevel level)
				PacketDistributor.sendToPlayersInDimension(level, new SavedDataSyncMessage(1, this));
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

		public static MapVariables load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
			MapVariables data = new MapVariables();
			data.read(tag, lookupProvider);
			return data;
		}

		public void read(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
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
		public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
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
				PacketDistributor.sendToAllPlayers(new SavedDataSyncMessage(0, this));
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

	public record SavedDataSyncMessage(int dataType, SavedData data) implements CustomPacketPayload {
		public static final Type<SavedDataSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FatalsQolCraftingMod.MODID, "saved_data_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SavedDataSyncMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, SavedDataSyncMessage message) -> {
			buffer.writeInt(message.dataType);
			if (message.data != null)
				buffer.writeNbt(message.data.save(new CompoundTag(), buffer.registryAccess()));
		}, (RegistryFriendlyByteBuf buffer) -> {
			int dataType = buffer.readInt();
			CompoundTag nbt = buffer.readNbt();
			SavedData data = null;
			if (nbt != null) {
				data = dataType == 0 ? new MapVariables() : new WorldVariables();
				if (data instanceof MapVariables mapVariables)
					mapVariables.read(nbt, buffer.registryAccess());
				else if (data instanceof WorldVariables worldVariables)
					worldVariables.read(nbt, buffer.registryAccess());
			}
			return new SavedDataSyncMessage(dataType, data);
		});

		@Override
		public Type<SavedDataSyncMessage> type() {
			return TYPE;
		}

		public static void handleData(final SavedDataSyncMessage message, final IPayloadContext context) {
			if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
				context.enqueueWork(() -> {
					if (message.dataType == 0)
						MapVariables.clientSide.read(message.data.save(new CompoundTag(), context.player().registryAccess()), context.player().registryAccess());
					else
						WorldVariables.clientSide.read(message.data.save(new CompoundTag(), context.player().registryAccess()), context.player().registryAccess());
				}).exceptionally(e -> {
					context.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}
}
