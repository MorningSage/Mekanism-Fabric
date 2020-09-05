package mekanism.common.network;

import java.util.function.Supplier;
import mekanism.common.content.gear.Modules;
import mekanism.common.content.gear.Modules.ModuleData;
import mekanism.common.tile.TileEntityModificationStation;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketRemoveModule {

    private final BlockPos pos;
    private final ModuleData<?> moduleType;

    public PacketRemoveModule(BlockPos pos, ModuleData<?> type) {
        this.pos = pos;
        moduleType = type;
    }

    public static void handle(PacketRemoveModule message, Supplier<Context> context) {
        PlayerEntity player = context.get().getSender();
        if (player == null) {
            return;
        }
        context.get().enqueueWork(() -> {
            TileEntityModificationStation tile = MekanismUtils.getTileEntity(TileEntityModificationStation.class, player.world, message.pos);
            if (tile != null) {
                tile.removeModule(player, message.moduleType);
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketRemoveModule pkt, PacketBuffer buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeString(pkt.moduleType.getName());
    }

    public static PacketRemoveModule decode(PacketBuffer buf) {
        return new PacketRemoveModule(buf.readBlockPos(), Modules.get(BasePacketHandler.readString(buf)));
    }
}
