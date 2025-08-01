
package net.mcreator.fatalscraftingqol.network;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.gui.components.Button;

import net.mcreator.fatalscraftingqol.FatalsQolCraftingMod;

import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Codec;
import com.mojang.datafixers.util.Pair;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class FatalsQolCraftingModVariables {
	public static void registerEventHandlers() {
		NeoForge.EVENT_BUS.addListener(GameEventHandler::onWorldCreate);
		NeoForge.EVENT_BUS.addListener(GameEventHandler::onWorldLoad);
	}

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

	public static class GameEventHandler {
		@SubscribeEvent
		public static void onWorldCreate(LevelEvent.CreateSpawnPosition event) {
			if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel level) {
				// Инициализация WorldVariables только при создании мира
				WorldVariables worldVariables = WorldVariables.get(level);
				worldVariables.setDirty();
				// Инициализация MapVariables только при создании мира
				MapVariables mapVariables = MapVariables.get(level);
				mapVariables.ChainmailArmor = true;
				mapVariables.Stair = true;
				mapVariables.Button = true;
				mapVariables.Fences = true;
				mapVariables.PressurePlate = true;
				mapVariables.Trapdoor = true;
				mapVariables.Wood_Hyphae = true;
				mapVariables.SlabsToBlocks = true;
				mapVariables.StairsToBlocks = true;
				mapVariables.SmeltRawOreBlocks = true;
				mapVariables.Dispensers = true;
				mapVariables.NetherWartBlockUncraft = true;
				mapVariables.Cobwebs = true;
				mapVariables.QuartzBlockUncraft = true;
				mapVariables.ClayUncraft = true;
				mapVariables.DeadBush = true;
				mapVariables.ChestsFromLogs = true;
				mapVariables.CraftableNylium = true;
				mapVariables.WoolToString = false;
				mapVariables.setDirty();
			}
		}

		@SubscribeEvent
		public static void onWorldLoad(LevelEvent.Load event) {
			// Только синхронизация при загрузке существующего мира
			if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel level) {
				WorldVariables.get(level).setDirty();
				MapVariables.get(level).setDirty();
			}
		}
	}

	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "fatals_qol_crafting_worldvars";
		public String empty_string = "";
		public static final Codec<WorldVariables> CODEC = new Codec<WorldVariables>() {
			@Override
			public <T> DataResult<Pair<WorldVariables, T>> decode(DynamicOps<T> ops, T input) {
				return ops.getMap(input).flatMap(map -> {
					Builder builder = new Builder();
					DataResult<String> emptyStringResult = ops.getStringValue(map.get("empty_string"));
					if (emptyStringResult.result().isEmpty()) {
						return DataResult.error(() -> "Missing or invalid 'empty_string' field");
					}
					builder.empty_string = emptyStringResult.result().get();
					return DataResult.success(Pair.of(builder.build(), ops.empty()));
				});
			}

			@Override
			public <T> DataResult<T> encode(WorldVariables input, DynamicOps<T> ops, T prefix) {
				RecordBuilder<T> recordBuilder = ops.mapBuilder();
				recordBuilder.add("empty_string", ops.createString(input.empty_string));
				return recordBuilder.build(prefix);
			}
		};

		private static class Builder {
			String empty_string = "";

			WorldVariables build() {
				return new WorldVariables(empty_string);
			}
		}

		public static final SavedDataType<WorldVariables> TYPE = new SavedDataType<>(DATA_NAME, ctx -> new WorldVariables(""), ctx -> CODEC, DataFixTypes.LEVEL);

		public WorldVariables(String empty_string) {
			this.empty_string = empty_string;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof ServerLevel level) {
				PacketDistributor.sendToPlayersInDimension(level, new SavedDataSyncMessage(1, this));
			}
		}

		static WorldVariables clientSide = new WorldVariables("");

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(TYPE);
			} else {
				return clientSide;
			}
		}
	}

	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "fatals_qol_crafting_mapvars";
		public boolean ChainmailArmor;
		public boolean Stair;
		public boolean Button;
		public boolean Fences;
		public boolean PressurePlate;
		public boolean Trapdoor;
		public boolean Wood_Hyphae;
		public boolean SlabsToBlocks;
		public boolean StairsToBlocks;
		public boolean SmeltRawOreBlocks;
		public boolean Dispensers;
		public boolean NetherWartBlockUncraft;
		public boolean Cobwebs;
		public boolean QuartzBlockUncraft;
		public boolean ClayUncraft;
		public boolean DeadBush;
		public boolean ChestsFromLogs;
		public boolean CraftableNylium;
		public boolean WoolToString;
		public static final Codec<MapVariables> CODEC = new Codec<MapVariables>() {
			@Override
			public <T> DataResult<Pair<MapVariables, T>> decode(DynamicOps<T> ops, T input) {
				return ops.getMap(input).flatMap(map -> {
					Builder builder = new Builder();
					Codec.BOOL.decode(ops, map.get("ChainmailArmor")).result().ifPresent(v -> builder.ChainmailArmor = v.getFirst());
					Codec.BOOL.decode(ops, map.get("Stair")).result().ifPresent(v -> builder.Stair = v.getFirst());
					Codec.BOOL.decode(ops, map.get("Button")).result().ifPresent(v -> builder.Button = v.getFirst());
					Codec.BOOL.decode(ops, map.get("Fences")).result().ifPresent(v -> builder.Fences = v.getFirst());
					Codec.BOOL.decode(ops, map.get("PressurePlate")).result().ifPresent(v -> builder.PressurePlate = v.getFirst());
					Codec.BOOL.decode(ops, map.get("Trapdoor")).result().ifPresent(v -> builder.Trapdoor = v.getFirst());
					Codec.BOOL.decode(ops, map.get("Wood_Hyphae")).result().ifPresent(v -> builder.Wood_Hyphae = v.getFirst());
					Codec.BOOL.decode(ops, map.get("SlabsToBlocks")).result().ifPresent(v -> builder.SlabsToBlocks = v.getFirst());
					Codec.BOOL.decode(ops, map.get("StairsToBlocks")).result().ifPresent(v -> builder.StairsToBlocks = v.getFirst());
					Codec.BOOL.decode(ops, map.get("SmeltRawOreBlocks")).result().ifPresent(v -> builder.SmeltRawOreBlocks = v.getFirst());
					Codec.BOOL.decode(ops, map.get("Dispensers")).result().ifPresent(v -> builder.Dispensers = v.getFirst());
					Codec.BOOL.decode(ops, map.get("NetherWartBlockUncraft")).result().ifPresent(v -> builder.NetherWartBlockUncraft = v.getFirst());
					Codec.BOOL.decode(ops, map.get("Cobwebs")).result().ifPresent(v -> builder.Cobwebs = v.getFirst());
					Codec.BOOL.decode(ops, map.get("QuartzBlockUncraft")).result().ifPresent(v -> builder.QuartzBlockUncraft = v.getFirst());
					Codec.BOOL.decode(ops, map.get("ClayUncraft")).result().ifPresent(v -> builder.ClayUncraft = v.getFirst());
					Codec.BOOL.decode(ops, map.get("DeadBush")).result().ifPresent(v -> builder.DeadBush = v.getFirst());
					Codec.BOOL.decode(ops, map.get("ChestsFromLogs")).result().ifPresent(v -> builder.ChestsFromLogs = v.getFirst());
					Codec.BOOL.decode(ops, map.get("CraftableNylium")).result().ifPresent(v -> builder.CraftableNylium = v.getFirst());
					Codec.BOOL.decode(ops, map.get("WoolToString")).result().ifPresent(v -> builder.WoolToString = v.getFirst());
					return DataResult.success(Pair.of(builder.build(), ops.empty()));
				});
			}

			@Override
			public <T> DataResult<T> encode(MapVariables input, DynamicOps<T> ops, T prefix) {
				RecordBuilder<T> recordBuilder = ops.mapBuilder();
				recordBuilder.add("ChainmailArmor", Codec.BOOL.encode(input.ChainmailArmor, ops, ops.empty()));
				recordBuilder.add("Stair", Codec.BOOL.encode(input.Stair, ops, ops.empty()));
				recordBuilder.add("Button", Codec.BOOL.encode(input.Button, ops, ops.empty()));
				recordBuilder.add("Fences", Codec.BOOL.encode(input.Fences, ops, ops.empty()));
				recordBuilder.add("PressurePlate", Codec.BOOL.encode(input.PressurePlate, ops, ops.empty()));
				recordBuilder.add("Trapdoor", Codec.BOOL.encode(input.Trapdoor, ops, ops.empty()));
				recordBuilder.add("Wood_Hyphae", Codec.BOOL.encode(input.Wood_Hyphae, ops, ops.empty()));
				recordBuilder.add("SlabsToBlocks", Codec.BOOL.encode(input.SlabsToBlocks, ops, ops.empty()));
				recordBuilder.add("StairsToBlocks", Codec.BOOL.encode(input.StairsToBlocks, ops, ops.empty()));
				recordBuilder.add("SmeltRawOreBlocks", Codec.BOOL.encode(input.SmeltRawOreBlocks, ops, ops.empty()));
				recordBuilder.add("Dispensers", Codec.BOOL.encode(input.Dispensers, ops, ops.empty()));
				recordBuilder.add("NetherWartBlockUncraft", Codec.BOOL.encode(input.NetherWartBlockUncraft, ops, ops.empty()));
				recordBuilder.add("Cobwebs", Codec.BOOL.encode(input.Cobwebs, ops, ops.empty()));
				recordBuilder.add("QuartzBlockUncraft", Codec.BOOL.encode(input.QuartzBlockUncraft, ops, ops.empty()));
				recordBuilder.add("ClayUncraft", Codec.BOOL.encode(input.ClayUncraft, ops, ops.empty()));
				recordBuilder.add("DeadBush", Codec.BOOL.encode(input.DeadBush, ops, ops.empty()));
				recordBuilder.add("ChestsFromLogs", Codec.BOOL.encode(input.ChestsFromLogs, ops, ops.empty()));
				recordBuilder.add("CraftableNylium", Codec.BOOL.encode(input.CraftableNylium, ops, ops.empty()));
				recordBuilder.add("WoolToString", Codec.BOOL.encode(input.WoolToString, ops, ops.empty()));
				return recordBuilder.build(prefix);
			}
		};

		private static class Builder {
			boolean ChainmailArmor = true;
			boolean Stair = true;
			boolean Button = true;
			boolean Fences = true;
			boolean PressurePlate = true;
			boolean Trapdoor = true;
			boolean Wood_Hyphae = true;
			boolean SlabsToBlocks = true;
			boolean StairsToBlocks = true;
			boolean SmeltRawOreBlocks = true;
			boolean Dispensers = true;
			boolean NetherWartBlockUncraft = true;
			boolean Cobwebs = true;
			boolean QuartzBlockUncraft = true;
			boolean ClayUncraft = true;
			boolean DeadBush = true;
			boolean ChestsFromLogs = true;
			boolean CraftableNylium = true;
			boolean WoolToString = false;

			MapVariables build() {
				return new MapVariables(ChainmailArmor, Stair, Button, Fences, PressurePlate, Trapdoor, Wood_Hyphae, SlabsToBlocks, StairsToBlocks, SmeltRawOreBlocks, Dispensers, NetherWartBlockUncraft, Cobwebs, QuartzBlockUncraft, ClayUncraft,
						DeadBush, ChestsFromLogs, CraftableNylium, WoolToString);
			}
		}

		public static final SavedDataType<MapVariables> TYPE = new SavedDataType<>(DATA_NAME,
				ctx -> new MapVariables(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false), ctx -> CODEC, DataFixTypes.LEVEL);

		public MapVariables(boolean ChainmailArmor, boolean Stair, boolean Button, boolean Fences, boolean PressurePlate, boolean Trapdoor, boolean Wood_Hyphae, boolean SlabsToBlocks, boolean StairsToBlocks, boolean SmeltRawOreBlocks,
				boolean Dispensers, boolean NetherWartBlockUncraft, boolean Cobwebs, boolean QuartzBlockUncraft, boolean ClayUncraft, boolean DeadBush, boolean ChestsFromLogs, boolean CraftableNylium, boolean WoolToString) {
			this.ChainmailArmor = ChainmailArmor;
			this.Stair = Stair;
			this.Button = Button;
			this.Fences = Fences;
			this.PressurePlate = PressurePlate;
			this.Trapdoor = Trapdoor;
			this.Wood_Hyphae = Wood_Hyphae;
			this.SlabsToBlocks = SlabsToBlocks;
			this.StairsToBlocks = StairsToBlocks;
			this.SmeltRawOreBlocks = SmeltRawOreBlocks;
			this.Dispensers = Dispensers;
			this.NetherWartBlockUncraft = NetherWartBlockUncraft;
			this.Cobwebs = Cobwebs;
			this.QuartzBlockUncraft = QuartzBlockUncraft;
			this.ClayUncraft = ClayUncraft;
			this.DeadBush = DeadBush;
			this.ChestsFromLogs = ChestsFromLogs;
			this.CraftableNylium = CraftableNylium;
			this.WoolToString = WoolToString;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level && !world.isClientSide()) {
				PacketDistributor.sendToAllPlayers(new SavedDataSyncMessage(0, this));
			}
		}

		static MapVariables clientSide = new MapVariables(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false);

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevelAccessor serverLevelAcc) {
				return serverLevelAcc.getLevel().getServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(TYPE);
			} else {
				return clientSide;
			}
		}
	}

	public record SavedDataSyncMessage(int dataType, SavedData data) implements CustomPacketPayload {
		public static final Type<SavedDataSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FatalsQolCraftingMod.MODID, "saved_data_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SavedDataSyncMessage> STREAM_CODEC = StreamCodec.of((buffer, message) -> {
			buffer.writeInt(message.dataType);
			if (message.data != null) {
				CompoundTag tag = switch (message.dataType) {
					case 0 -> (CompoundTag) MapVariables.CODEC.encodeStart(NbtOps.INSTANCE, (MapVariables) message.data).getOrThrow();
					case 1 -> (CompoundTag) WorldVariables.CODEC.encodeStart(NbtOps.INSTANCE, (WorldVariables) message.data).getOrThrow();
					default -> throw new IllegalArgumentException("Unknown data type");
				};
				buffer.writeNbt(tag);
			}
		}, buffer -> {
			int dataType = buffer.readInt();
			CompoundTag nbt = buffer.readNbt();
			SavedData data = switch (dataType) {
				case 0 -> MapVariables.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow();
				case 1 -> WorldVariables.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow();
				default -> null;
			};
			return new SavedDataSyncMessage(dataType, data);
		});

		@Override
		public Type<SavedDataSyncMessage> type() {
			return TYPE;
		}

		public static void handleData(final SavedDataSyncMessage message, final IPayloadContext context) {
			if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
				context.enqueueWork(() -> {
					if (message.dataType == 0) {
						MapVariables.clientSide = (MapVariables) message.data;
					} else {
						WorldVariables.clientSide = (WorldVariables) message.data;
					}
				}).exceptionally(e -> {
					context.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}
}
