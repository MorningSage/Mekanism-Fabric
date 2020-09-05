package mekanism.common.content.network;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import mekanism.api.Coord4D;
import mekanism.common.MekanismLang;
import mekanism.common.content.network.transmitter.LogisticalTransporterBase;
import mekanism.common.content.transporter.PathfinderCache;
import mekanism.common.content.transporter.TransporterManager;
import mekanism.common.content.transporter.TransporterStack;
import mekanism.common.lib.inventory.TransitRequest;
import mekanism.common.lib.inventory.TransitRequest.TransitResponse;
import mekanism.common.lib.transmitter.DynamicNetwork;
import mekanism.common.util.MekanismUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class InventoryNetwork extends DynamicNetwork<IItemHandler, InventoryNetwork, LogisticalTransporterBase> {

    public InventoryNetwork() {
    }

    public InventoryNetwork(UUID networkID) {
        super(networkID);
    }

    public InventoryNetwork(Collection<InventoryNetwork> networks) {
        for (InventoryNetwork net : networks) {
            if (net != null) {
                adoptTransmittersAndAcceptorsFrom(net);
                net.deregister();
            }
        }
        register();
    }

    public List<AcceptorData> calculateAcceptors(TransitRequest request, TransporterStack stack, Long2ObjectMap<IChunk> chunkMap) {
        List<AcceptorData> toReturn = new ArrayList<>();
        for (Entry<BlockPos, Map<Direction, LazyOptional<IItemHandler>>> entry : acceptorCache.getAcceptorEntrySet()) {
            BlockPos pos = entry.getKey();
            if (!pos.equals(stack.homeLocation)) {
                TileEntity acceptor = MekanismUtils.getTileEntity(getWorld(), chunkMap, pos);
                if (acceptor == null) {
                    continue;
                }
                AcceptorData data = null;
                Coord4D position = Coord4D.get(acceptor);
                for (Entry<Direction, LazyOptional<IItemHandler>> acceptorEntry : entry.getValue().entrySet()) {
                    Optional<IItemHandler> handler = MekanismUtils.toOptional(acceptorEntry.getValue());
                    if (handler.isPresent()) {
                        Direction side = acceptorEntry.getKey();
                        //TODO: Figure out how we want to best handle the color check, as without doing it here we don't
                        // actually need to even query the TE
                        TransitResponse response = TransporterManager.getPredictedInsert(acceptor, position, handler.get(), stack.color, request, side);
                        if (!response.isEmpty()) {
                            Direction opposite = side.getOpposite();
                            if (data == null) {
                                toReturn.add(data = new AcceptorData(pos, response, opposite));
                            } else {
                                data.sides.add(opposite);
                            }
                        }
                    }
                }
            }
        }
        return toReturn;
    }

    @Override
    public void commit() {
        super.commit();
        // update the cache when the network has been changed (called when transmitters are added)
        PathfinderCache.onChanged(this);
    }

    @Override
    public void deregister() {
        super.deregister();
        // update the cache when the network has been removed (when transmitters are removed)
        PathfinderCache.onChanged(this);
    }

    @Override
    public String toString() {
        return "[InventoryNetwork] " + transmitters.size() + " transmitters, " + getAcceptorCount() + " acceptors.";
    }

    @Override
    public ITextComponent getTextComponent() {
        return MekanismLang.NETWORK_DESCRIPTION.translate(MekanismLang.INVENTORY_NETWORK, transmitters.size(), getAcceptorCount());
    }

    public static class AcceptorData {

        private final BlockPos location;
        private final TransitResponse response;
        private final Set<Direction> sides;

        protected AcceptorData(BlockPos pos, TransitResponse ret, Direction side) {
            location = pos;
            response = ret;
            sides = EnumSet.of(side);
        }

        public TransitResponse getResponse() {
            return response;
        }

        public BlockPos getLocation() {
            return location;
        }

        public Set<Direction> getSides() {
            return sides;
        }
    }
}