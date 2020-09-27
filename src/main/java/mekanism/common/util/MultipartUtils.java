package mekanism.common.util;

import java.util.Collection;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

public final class MultipartUtils {

    /* taken from MCMP */
    public static Pair<Vec3d, Vec3d> getRayTraceVectors(Entity entity) {
        float pitch = entity.pitch;
        float yaw = entity.yaw;
        Vec3d start = new Vec3d(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
        float f1 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = -MathHelper.cos(-pitch * 0.017453292F);
        float f4 = MathHelper.sin(-pitch * 0.017453292F);
        float f5 = f2 * f3;
        float f6 = f1 * f3;
        double d3 = 5.0D;
        if (entity instanceof ServerPlayerEntity) {
            d3 = ((ServerPlayerEntity) entity).getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue();
        }
        Vec3d end = start.add(f5 * d3, f4 * d3, f6 * d3);
        return Pair.of(start, end);
    }

    public static AdvancedRayTraceResult collisionRayTrace(BlockPos pos, Vec3d start, Vec3d end, Collection<VoxelShape> boxes) {
        double minDistance = Double.POSITIVE_INFINITY;
        AdvancedRayTraceResult hit = null;
        int i = -1;
        for (VoxelShape shape : boxes) {
            if (shape != null) {
                BlockHitResult result = shape.raycast(start, end, pos);
                if (result != null) {
                    // ToDo: These don't exist in Fabric as they are patched in by Forge.
                    //result.subHit = i;
                    //result.hitInfo = null;
                    AdvancedRayTraceResult advancedResult = new AdvancedRayTraceResult(result, shape);
                    double d = advancedResult.squareDistanceTo(start);
                    if (d < minDistance) {
                        minDistance = d;
                        hit = advancedResult;
                    }
                }
            }
            i++;
        }
        return hit;
    }

    public static class AdvancedRayTraceResult {

        public final VoxelShape bounds;
        public final HitResult hit;

        public AdvancedRayTraceResult(HitResult mop, VoxelShape shape) {
            hit = mop;
            bounds = shape;
        }

        public boolean valid() {
            return hit != null && bounds != null;
        }

        public double squareDistanceTo(Vec3d vec) {
            return hit.getPos().squaredDistanceTo(vec);
        }
    }
}