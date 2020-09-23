package mekanism.common.network;

import java.util.function.Supplier;
import javax.annotation.Nonnull;
import mekanism.common.entity.EntityRobit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketRobit {

    private final RobitPacketType activeType;
    private final int entityId;
    private Text name;

    public PacketRobit(RobitPacketType type, int entityId) {
        activeType = type;
        this.entityId = entityId;
    }

    public PacketRobit(int entityId, @Nonnull Text name) {
        activeType = RobitPacketType.NAME;
        this.entityId = entityId;
        this.name = name;
    }

    public static void handle(PacketRobit message, Supplier<Context> context) {
        PlayerEntity player = BasePacketHandler.getPlayer(context);
        if (player == null) {
            return;
        }
        context.get().enqueueWork(() -> {
            EntityRobit robit = (EntityRobit) player.world.getEntityById(message.entityId);
            if (robit != null) {
                switch (message.activeType) {
                    case FOLLOW:
                        robit.setFollowing(!robit.getFollowing());
                        break;
                    case NAME:
                        robit.setCustomName(message.name);
                        break;
                    case GO_HOME:
                        robit.goHome();
                        break;
                    case DROP_PICKUP:
                        robit.setDropPickup(!robit.getDropPickup());
                        break;
                }
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void encode(PacketRobit pkt, PacketByteBuf buf) {
        buf.writeEnumConstant(pkt.activeType);
        buf.writeVarInt(pkt.entityId);
        if (pkt.activeType == RobitPacketType.NAME) {
            buf.writeText(pkt.name);
        }
    }

    public static PacketRobit decode(PacketByteBuf buf) {
        RobitPacketType activeType = buf.readEnumConstant(RobitPacketType.class);
        int entityId = buf.readVarInt();
        if (activeType == RobitPacketType.NAME) {
            return new PacketRobit(entityId, buf.readText());
        }
        return new PacketRobit(activeType, entityId);
    }

    public enum RobitPacketType {
        FOLLOW,
        NAME,
        GO_HOME,
        DROP_PICKUP
    }
}