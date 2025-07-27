package net.mcreator.fatalscraftingqol.network;

import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.IWorld;
import net.minecraft.world.IServerWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.client.gui.widget.button.Button;

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
			if (!event.getPlayer().world.isRemote()) {
				WorldSavedData mapdata = MapVariables.get(event.getPlayer().world);
				WorldSavedData worlddata = WorldVariables.get(event.getPlayer().world);
				if (mapdata != null)
					FatalsQolCraftingMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new SavedDataSyncMessage(0, mapdata));
				if (worlddata != null)
					FatalsQolCraftingMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new SavedDataSyncMessage(1, worlddata));
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (!event.getPlayer().world.isRemote()) {
				WorldSavedData worlddata = WorldVariables.get(event.getPlayer().world);
				if (worlddata != null)
					FatalsQolCraftingMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new SavedDataSyncMessage(1, worlddata));
			}
		}
	}

	public static class WorldVariables extends WorldSavedData {
		public static final String DATA_NAME = "fatals_qol_crafting_worldvars";

		public WorldVariables() {
			super(DATA_NAME);
		}

		public WorldVariables(String s) {
			super(s);
		}

		@Override
		public void read(CompoundNBT nbt) {
		}

		@Override
		public CompoundNBT write(CompoundNBT nbt) {
			return nbt;
		}

		public void syncData(IWorld world) {
			this.markDirty();
			if (world instanceof World && !((World) world).isRemote())
				FatalsQolCraftingMod.PACKET_HANDLER.send(PacketDistributor.DIMENSION.with(((World) world)::getDimensionKey), new SavedDataSyncMessage(1, this));
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(IWorld world) {
			if (world instanceof ServerWorld) {
				return ((ServerWorld) world).getSavedData().getOrCreate(WorldVariables::new, DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class MapVariables extends WorldSavedData {
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

		public MapVariables() {
			super(DATA_NAME);
		}

		public MapVariables(String s) {
			super(s);
		}

		@Override
		public void read(CompoundNBT nbt) {
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
		public CompoundNBT write(CompoundNBT nbt) {
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

		public void syncData(IWorld world) {
			this.markDirty();
			if (world instanceof World && !world.isRemote())
				FatalsQolCraftingMod.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SavedDataSyncMessage(0, this));
		}

		static MapVariables clientSide = new MapVariables();

		public static MapVariables get(IWorld world) {
			if (world instanceof IServerWorld) {
				return ((IServerWorld) world).getWorld().getServer().getWorld(World.OVERWORLD).getSavedData().getOrCreate(MapVariables::new, DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class SavedDataSyncMessage {
		private final int type;
		private WorldSavedData data;

		public SavedDataSyncMessage(PacketBuffer buffer) {
			this.type = buffer.readInt();
			CompoundNBT nbt = buffer.readCompoundTag();
			if (nbt != null) {
				this.data = this.type == 0 ? new MapVariables() : new WorldVariables();
				if (this.data instanceof MapVariables)
					((MapVariables) this.data).read(nbt);
				else if (this.data instanceof WorldVariables)
					((WorldVariables) this.data).read(nbt);
			}
		}

		public SavedDataSyncMessage(int type, WorldSavedData data) {
			this.type = type;
			this.data = data;
		}

		public static void buffer(SavedDataSyncMessage message, PacketBuffer buffer) {
			buffer.writeInt(message.type);
			if (message.data != null)
				buffer.writeCompoundTag(message.data.write(new CompoundNBT()));
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
