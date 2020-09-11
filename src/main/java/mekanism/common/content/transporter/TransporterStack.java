package mekanism.common.content.transporter;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.NBTConstants;
import mekanism.api.math.MathUtils;
import mekanism.api.text.EnumColor;
import mekanism.common.content.network.transmitter.LogisticalTransporterBase;
import mekanism.common.content.transporter.TransporterPathfinder.Destination;
import mekanism.common.lib.inventory.TransitRequest;
import mekanism.common.lib.inventory.TransitRequest.TransitResponse;
import mekanism.common.tile.TileEntityLogisticalSorter;
import mekanism.common.tile.transmitter.TileEntityLogisticalTransporterBase;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.TransporterUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;

public class TransporterStack {

    public ItemStack itemStack = ItemStack.EMPTY;

    public int progress;

    public EnumColor color = null;

    public boolean initiatedPath = false;

    public Direction idleDir = null;
    public BlockPos originalLocation;
    public BlockPos homeLocation;
    private BlockPos clientNext;
    private BlockPos clientPrev;
    private Path pathType;
    private List<BlockPos> pathToTarget = new ArrayList<>();

    public static TransporterStack readFromNBT(CompoundTag nbtTags) {
        TransporterStack stack = new TransporterStack();
        stack.read(nbtTags);
        return stack;
    }

    public static TransporterStack readFromUpdate(CompoundTag nbtTags) {
        TransporterStack stack = new TransporterStack();
        stack.readFromUpdateTag(nbtTags);
        return stack;
    }

    public static TransporterStack readFromPacket(PacketByteBuf dataStream) {
        TransporterStack stack = new TransporterStack();
        stack.read(dataStream);
        if (stack.progress == 0) {
            stack.progress = 5;
        }
        return stack;
    }

    public void write(LogisticalTransporterBase transporter, PacketByteBuf buf) {
        buf.writeVarInt(TransporterUtils.getColorIndex(color));
        buf.writeVarInt(progress);
        buf.writeBlockPos(originalLocation);
        buf.writeEnumConstant(pathType);
        if (pathToTarget.indexOf(transporter.getTilePos()) > 0) {
            buf.writeBoolean(true);
            buf.writeBlockPos(getNext(transporter));
        } else {
            buf.writeBoolean(false);
        }
        buf.writeBlockPos(getPrev(transporter));
        buf.writeItemStack(itemStack);
    }

    public void read(PacketByteBuf dataStream) {
        color = TransporterUtils.readColor(dataStream.readVarInt());
        progress = dataStream.readVarInt();
        originalLocation = dataStream.readBlockPos();
        pathType = dataStream.readEnumConstant(Path.class);
        if (dataStream.readBoolean()) {
            clientNext = dataStream.readBlockPos();
        }
        clientPrev = dataStream.readBlockPos();
        itemStack = dataStream.readItemStack();
    }

    public void writeToUpdateTag(LogisticalTransporterBase transporter, CompoundTag updateTag) {
        updateTag.putInt(NBTConstants.COLOR, TransporterUtils.getColorIndex(color));
        updateTag.putInt(NBTConstants.PROGRESS, progress);
        updateTag.put(NBTConstants.ORIGINAL_LOCATION, NbtHelper.fromBlockPos(originalLocation));
        updateTag.putInt(NBTConstants.PATH_TYPE, pathType.ordinal());
        if (pathToTarget.indexOf(transporter.getTilePos()) > 0) {
            updateTag.put(NBTConstants.CLIENT_NEXT, NbtHelper.fromBlockPos(getNext(transporter)));
        }
        updateTag.put(NBTConstants.CLIENT_PREVIOUS, NbtHelper.fromBlockPos(getPrev(transporter)));
        itemStack.toTag(updateTag);
    }

    public void readFromUpdateTag(CompoundTag updateTag) {
        NBTUtils.setEnumIfPresent(updateTag, NBTConstants.COLOR, TransporterUtils::readColor, color -> this.color = color);
        progress = updateTag.getInt(NBTConstants.PROGRESS);
        NBTUtils.setBlockPosIfPresent(updateTag, NBTConstants.ORIGINAL_LOCATION, coord -> originalLocation = coord);
        NBTUtils.setEnumIfPresent(updateTag, NBTConstants.PATH_TYPE, Path::byIndexStatic, type -> pathType = type);
        NBTUtils.setBlockPosIfPresent(updateTag, NBTConstants.CLIENT_NEXT, coord -> clientNext = coord);
        NBTUtils.setBlockPosIfPresent(updateTag, NBTConstants.CLIENT_PREVIOUS, coord -> clientPrev = coord);
        itemStack = ItemStack.fromTag(updateTag);
    }

    public void write(CompoundTag nbtTags) {
        nbtTags.putInt(NBTConstants.COLOR, TransporterUtils.getColorIndex(color));

        nbtTags.putInt(NBTConstants.PROGRESS, progress);
        nbtTags.put(NBTConstants.ORIGINAL_LOCATION, NbtHelper.fromBlockPos(originalLocation));

        if (idleDir != null) {
            nbtTags.putInt(NBTConstants.IDLE_DIR, idleDir.ordinal());
        }
        if (homeLocation != null) {
            nbtTags.put(NBTConstants.HOME_LOCATION, NbtHelper.fromBlockPos(homeLocation));
        }
        nbtTags.putInt(NBTConstants.PATH_TYPE, pathType.ordinal());
        itemStack.toTag(nbtTags);
    }

    public void read(CompoundTag nbtTags) {
        NBTUtils.setEnumIfPresent(nbtTags, NBTConstants.COLOR, TransporterUtils::readColor, color -> this.color = color);
        progress = nbtTags.getInt(NBTConstants.PROGRESS);
        NBTUtils.setBlockPosIfPresent(nbtTags, NBTConstants.ORIGINAL_LOCATION, coord -> originalLocation = coord);
        NBTUtils.setEnumIfPresent(nbtTags, NBTConstants.IDLE_DIR, Direction::byId, dir -> idleDir = dir);
        NBTUtils.setBlockPosIfPresent(nbtTags, NBTConstants.HOME_LOCATION, coord -> homeLocation = coord);
        NBTUtils.setEnumIfPresent(nbtTags, NBTConstants.PATH_TYPE, Path::byIndexStatic, type -> pathType = type);
        itemStack = ItemStack.fromTag(nbtTags);
    }

    private void setPath(World world, List<BlockPos> path, Path type) {
        //Make sure old path isn't null
        if (pathType != Path.NONE) {
            TransporterManager.remove(world, this);
        }
        pathToTarget = path;
        pathType = type;
        if (pathType != Path.NONE) {
            TransporterManager.add(world, this);
        }
    }

    public boolean hasPath() {
        return pathToTarget != null && pathToTarget.size() >= 2;
    }

    public List<BlockPos> getPath() {
        return pathToTarget;
    }

    public Path getPathType() {
        return pathType;
    }

    public TransitResponse recalculatePath(TransitRequest request, LogisticalTransporterBase transporter, int min) {
        Destination newPath = TransporterPathfinder.getNewBasePath(transporter, this, request, min);
        if (newPath == null) {
            return request.getEmptyResponse();
        }
        idleDir = null;
        setPath(transporter.getTileWorld(), newPath.getPath(), Path.DEST);
        initiatedPath = true;
        return newPath.getResponse();
    }

    public TransitResponse recalculateRRPath(TransitRequest request, TileEntityLogisticalSorter outputter, LogisticalTransporterBase transporter, int min) {
        Destination newPath = TransporterPathfinder.getNewRRPath(transporter, this, request, outputter, min);
        if (newPath == null) {
            return request.getEmptyResponse();
        }
        idleDir = null;
        setPath(transporter.getTileWorld(), newPath.getPath(), Path.DEST);
        initiatedPath = true;
        return newPath.getResponse();
    }

    public boolean calculateIdle(LogisticalTransporterBase transporter) {
        Pair<List<BlockPos>, Path> newPath = TransporterPathfinder.getIdlePath(transporter, this);
        if (newPath == null) {
            return false;
        }
        if (newPath.getRight() == Path.HOME) {
            idleDir = null;
        }
        setPath(transporter.getTileWorld(), newPath.getLeft(), newPath.getRight());
        originalLocation = transporter.getTilePos();
        initiatedPath = true;
        return true;
    }

    public boolean isFinal(LogisticalTransporterBase transporter) {
        return pathToTarget.indexOf(transporter.getTilePos()) == (pathType == Path.NONE ? 0 : 1);
    }

    public BlockPos getNext(LogisticalTransporterBase transporter) {
        if (!transporter.isRemote()) {
            int index = pathToTarget.indexOf(transporter.getTilePos()) - 1;
            if (index < 0) {
                return null;
            }
            return pathToTarget.get(index);
        }
        return clientNext;
    }

    public BlockPos getPrev(LogisticalTransporterBase transporter) {
        if (!transporter.isRemote()) {
            int index = pathToTarget.indexOf(transporter.getTilePos()) + 1;
            if (index < pathToTarget.size()) {
                return pathToTarget.get(index);
            }
            return originalLocation;
        }
        return clientPrev;
    }

    public Direction getSide(LogisticalTransporterBase transporter) {
        Direction side = null;
        if (progress < 50) {
            BlockPos prev = getPrev(transporter);
            if (prev != null) {
                side = MekanismUtils.sideDifference(transporter.getTilePos(), prev);
            }
        } else {
            BlockPos next = getNext(transporter);
            if (next != null) {
                side = MekanismUtils.sideDifference(next, transporter.getTilePos());
            }
        }
        //sideDifference can return null
        //TODO: Look into implications further about what side should be returned.
        // This is mainly to stop a crash I randomly encountered but was unable to reproduce.
        // (I believe the difference returns null when it is the "same" transporter somehow or something)
        return side == null ? Direction.DOWN : side;
    }

    @Contract("null, _, _ -> false")
    public boolean canInsertToTransporter(BlockEntity tile, Direction from, @Nullable BlockEntity tileFrom) {
        return tile instanceof TileEntityLogisticalTransporterBase && canInsertToTransporterNN((TileEntityLogisticalTransporterBase) tile, from, tileFrom);
    }

    @Contract("null, _, _ -> false")
    public boolean canInsertToTransporter(@Nullable TileEntityLogisticalTransporterBase transporter, Direction from, @Nullable BlockEntity tileFrom) {
        return transporter != null && canInsertToTransporterNN(transporter, from, tileFrom);
    }

    public boolean canInsertToTransporterNN(@Nonnull TileEntityLogisticalTransporterBase transporter, Direction from, @Nullable BlockEntity tileFrom) {
        return canInsertToTransporterNN(transporter.getTransmitter(), from, tileFrom);
    }

    public boolean canInsertToTransporterNN(@Nonnull LogisticalTransporterBase transporter, Direction from, @Nullable BlockEntity tileFrom) {
        //If the color is valid, make sure that the connection is valid
        return (transporter.getColor() == null || transporter.getColor() == color) && transporter.canConnectMutual(from.getOpposite(), tileFrom);
    }

    public BlockPos getDest() {
        return pathToTarget.get(0);
    }

    public enum Path {
        DEST,
        HOME,
        NONE;

        private static final Path[] PATHS = values();

        public static Path byIndexStatic(int index) {
            return MathUtils.getByIndexMod(PATHS, index);
        }
    }
}