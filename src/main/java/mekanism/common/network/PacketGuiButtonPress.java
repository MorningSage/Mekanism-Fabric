package mekanism.common.network;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeGui;
import mekanism.common.entity.EntityRobit;
import mekanism.common.inventory.container.ContainerProvider;
import mekanism.common.inventory.container.entity.robit.CraftingRobitContainer;
import mekanism.common.inventory.container.entity.robit.InventoryRobitContainer;
import mekanism.common.inventory.container.entity.robit.MainRobitContainer;
import mekanism.common.inventory.container.entity.robit.RepairRobitContainer;
import mekanism.common.inventory.container.entity.robit.SmeltingRobitContainer;
import mekanism.common.inventory.container.item.QIOFrequencySelectItemContainer;
import mekanism.common.inventory.container.tile.DigitalMinerConfigContainer;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.inventory.container.tile.MatrixStatsTabContainer;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.inventory.container.tile.QIOFrequencySelectTileContainer;
import mekanism.common.inventory.container.tile.UpgradeManagementContainer;
import mekanism.common.item.interfaces.IGuiItem;
import mekanism.common.registries.MekanismContainerTypes;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import mekanism.common.tile.multiblock.TileEntityBoilerCasing;
import mekanism.common.tile.multiblock.TileEntityInductionCasing;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * Used for informing the server that a click happened in a GUI and the gui window needs to change
 */
public class PacketGuiButtonPress {

    private final Type type;
    private ClickedItemButton itemButton;
    private ClickedTileButton tileButton;
    private ClickedEntityButton entityButton;
    private Hand hand;
    private int entityID;
    private int extra;
    private BlockPos tilePosition;

    public PacketGuiButtonPress(ClickedTileButton buttonClicked, BlockEntity tile) {
        this(buttonClicked, tile.getPos());
    }

    public PacketGuiButtonPress(ClickedTileButton buttonClicked, BlockEntity tile, int extra) {
        this(buttonClicked, tile.getPos(), extra);
    }

    public PacketGuiButtonPress(ClickedTileButton buttonClicked, BlockPos tilePosition) {
        this(buttonClicked, tilePosition, 0);
    }

    public PacketGuiButtonPress(ClickedItemButton buttonClicked, Hand hand) {
        type = Type.ITEM;
        this.itemButton = buttonClicked;
        this.hand = hand;
    }

    public PacketGuiButtonPress(ClickedTileButton buttonClicked, BlockPos tilePosition, int extra) {
        type = Type.TILE;
        this.tileButton = buttonClicked;
        this.tilePosition = tilePosition;
        this.extra = extra;
    }

    public PacketGuiButtonPress(ClickedEntityButton buttonClicked, int entityID) {
        type = Type.ENTITY;
        this.entityButton = buttonClicked;
        this.entityID = entityID;
    }

    public static void handle(PacketGuiButtonPress message, Supplier<Context> context) {
        PlayerEntity player = BasePacketHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        context.get().enqueueWork(() -> {
            if (message.type == Type.ENTITY) {
                Entity entity = player.world.getEntityByID(message.entityID);
                if (entity != null) {
                    NamedScreenHandlerFactory provider = message.entityButton.getProvider(entity);
                    if (provider != null) {
                        //Ensure valid data
                        NetworkHooks.openGui((ServerPlayerEntity) player, provider, buf -> buf.writeVarInt(message.entityID));
                    }
                }
            } else if (message.type == Type.TILE) {
                TileEntityMekanism tile = MekanismUtils.getTileEntity(TileEntityMekanism.class, player.world, message.tilePosition);
                if (tile != null) {
                    NamedScreenHandlerFactory provider = message.tileButton.getProvider(tile, message.extra);
                    if (provider != null) {
                        //Ensure valid data
                        NetworkHooks.openGui((ServerPlayerEntity) player, provider, buf -> {
                            buf.writeBlockPos(message.tilePosition);
                            buf.writeVarInt(message.extra);
                        });
                    }
                }
            } else if (message.type == Type.ITEM) {
                ItemStack stack = player.getStackInHand(message.hand);
                if (stack.getItem() instanceof IGuiItem) {
                    NamedScreenHandlerFactory provider = message.itemButton.getProvider(stack, message.hand);
                    if (provider != null) {
                        NetworkHooks.openGui((ServerPlayerEntity) player, provider, buf -> {
                            buf.writeEnumValue(message.hand);
                            buf.writeItemStack(stack);
                        });
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketGuiButtonPress pkt, PacketByteBuf buf) {
        buf.writeEnumConstant(pkt.type);
        if (pkt.type == Type.ENTITY) {
            buf.writeEnumConstant(pkt.entityButton);
            buf.writeVarInt(pkt.entityID);
        } else if (pkt.type == Type.TILE) {
            buf.writeEnumConstant(pkt.tileButton);
            buf.writeBlockPos(pkt.tilePosition);
            buf.writeVarInt(pkt.extra);
        } else if (pkt.type == Type.ITEM) {
            buf.writeEnumConstant(pkt.itemButton);
            buf.writeEnumConstant(pkt.hand);
        }
    }

    public static PacketGuiButtonPress decode(PacketByteBuf buf) {
        Type type = buf.readEnumConstant(Type.class);
        switch (type) {
            case ENTITY:
                return new PacketGuiButtonPress(buf.readEnumConstant(ClickedEntityButton.class), buf.readVarInt());
            case TILE:
                return new PacketGuiButtonPress(buf.readEnumConstant(ClickedTileButton.class), buf.readBlockPos(), buf.readVarInt());
            case ITEM:
                return new PacketGuiButtonPress(buf.readEnumConstant(ClickedItemButton.class), buf.readEnumConstant(Hand.class));
            default:
                return null;
        }
    }

    public enum ClickedItemButton {
        BACK_BUTTON((stack, hand) -> {
            if (stack.getItem() instanceof IGuiItem) {
                return ((IGuiItem) stack.getItem()).getContainerProvider(stack, hand);
            }
            return null;
        }),
        QIO_FREQUENCY_SELECT((stack, hand) -> new ContainerProvider(MekanismLang.QIO_FREQUENCY_SELECT, (i, inv, player) -> new QIOFrequencySelectItemContainer(i, inv, hand, stack)));

        private final BiFunction<ItemStack, Hand, NamedScreenHandlerFactory> providerFromItem;

        ClickedItemButton(BiFunction<ItemStack, Hand, NamedScreenHandlerFactory> providerFromItem) {
            this.providerFromItem = providerFromItem;
        }

        public NamedScreenHandlerFactory getProvider(ItemStack stack, Hand hand) {
            return providerFromItem.apply(stack, hand);
        }
    }

    public enum ClickedTileButton {
        BACK_BUTTON((tile, extra) -> {
            //Special handling to basically reset to the tiles default gui container
            Block block = tile.getBlockType();
            if (Attribute.has(block, AttributeGui.class)) {
                return Attribute.get(block, AttributeGui.class).getProvider(tile);
            }
            return null;
        }),
        QIO_FREQUENCY_SELECT((tile, extra) -> new ContainerProvider(MekanismLang.QIO_FREQUENCY_SELECT, (i, inv, player) -> new QIOFrequencySelectTileContainer(i, inv, tile))),
        UPGRADE_MANAGEMENT((tile, extra) -> new ContainerProvider(MekanismLang.UPGRADES, (i, inv, player) -> new UpgradeManagementContainer(i, inv, tile))),
        DIGITAL_MINER_CONFIG((tile, extra) -> {
            if (tile instanceof TileEntityDigitalMiner) {
                return new ContainerProvider(MekanismLang.MINER_CONFIG, (i, inv, player) -> new DigitalMinerConfigContainer(i, inv, (TileEntityDigitalMiner) tile));
            }
            return null;
        }),

        TAB_MAIN((tile, extra) -> {
            if (tile instanceof TileEntityInductionCasing) {
                return new ContainerProvider(MekanismLang.MATRIX, (i, inv, player) -> new MekanismTileContainer<>(MekanismContainerTypes.INDUCTION_MATRIX, i, inv, (TileEntityInductionCasing) tile));
            } else if (tile instanceof TileEntityBoilerCasing) {
                return new ContainerProvider(MekanismLang.BOILER, (i, inv, player) -> new MekanismTileContainer<>(MekanismContainerTypes.THERMOELECTRIC_BOILER, i, inv, (TileEntityBoilerCasing) tile));
            }
            return null;
        }),
        TAB_STATS((tile, extra) -> {
            if (tile instanceof TileEntityInductionCasing) {
                return new ContainerProvider(MekanismLang.MATRIX_STATS, (i, inv, player) -> new MatrixStatsTabContainer(i, inv, (TileEntityInductionCasing) tile));
            } else if (tile instanceof TileEntityBoilerCasing) {
                return new ContainerProvider(MekanismLang.BOILER_STATS, (i, inv, player) -> new EmptyTileContainer<>(MekanismContainerTypes.BOILER_STATS, i, inv, (TileEntityBoilerCasing) tile));
            }
            return null;
        });

        private final BiFunction<TileEntityMekanism, Integer, NamedScreenHandlerFactory> providerFromTile;

        ClickedTileButton(BiFunction<TileEntityMekanism, Integer, NamedScreenHandlerFactory> providerFromTile) {
            this.providerFromTile = providerFromTile;
        }

        public NamedScreenHandlerFactory getProvider(TileEntityMekanism tile, int extra) {
            return providerFromTile.apply(tile, extra);
        }
    }

    public enum ClickedEntityButton {
        //Entities
        ROBIT_CRAFTING(entity -> {
            if (entity instanceof EntityRobit) {
                return new ContainerProvider(MekanismLang.ROBIT_CRAFTING, (i, inv, player) -> new CraftingRobitContainer(i, inv, (EntityRobit) entity));
            }
            return null;
        }),
        ROBIT_INVENTORY(entity -> {
            if (entity instanceof EntityRobit) {
                return new ContainerProvider(MekanismLang.ROBIT_INVENTORY, (i, inv, player) -> new InventoryRobitContainer(i, inv, (EntityRobit) entity));
            }
            return null;
        }),
        ROBIT_MAIN(entity -> {
            if (entity instanceof EntityRobit) {
                return new ContainerProvider(MekanismLang.ROBIT, (i, inv, player) -> new MainRobitContainer(i, inv, (EntityRobit) entity));
            }
            return null;
        }),
        ROBIT_REPAIR(entity -> {
            if (entity instanceof EntityRobit) {
                return new ContainerProvider(MekanismLang.ROBIT_REPAIR, (i, inv, player) -> new RepairRobitContainer(i, inv, (EntityRobit) entity));
            }
            return null;
        }),
        ROBIT_SMELTING(entity -> {
            if (entity instanceof EntityRobit) {
                return new ContainerProvider(MekanismLang.ROBIT_SMELTING, (i, inv, player) -> new SmeltingRobitContainer(i, inv, (EntityRobit) entity));
            }
            return null;
        });

        private final Function<Entity, NamedScreenHandlerFactory> providerFromEntity;

        ClickedEntityButton(Function<Entity, NamedScreenHandlerFactory> providerFromEntity) {
            this.providerFromEntity = providerFromEntity;
        }

        public NamedScreenHandlerFactory getProvider(Entity entity) {
            return providerFromEntity.apply(entity);
        }
    }

    public enum Type {
        TILE,
        ITEM,
        ENTITY;
    }
}