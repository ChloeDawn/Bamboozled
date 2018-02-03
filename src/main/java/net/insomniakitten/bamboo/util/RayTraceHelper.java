package net.insomniakitten.bamboo.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public final class RayTraceHelper {

    private RayTraceHelper() {}

    public static RayTraceResult rayTraceMultiAABB(List<AxisAlignedBB> boxes, BlockPos pos, Vec3d start, Vec3d end) {
        List<RayTraceResult> results = new ArrayList<>();

        double x = pos.getX(), y = pos.getY(), z = pos.getZ();
        Vec3d a = start.subtract(x, y, z);
        Vec3d b = end.subtract(x, y, z);

        for (AxisAlignedBB box : boxes) {
            RayTraceResult result = box.calculateIntercept(a, b);
            if (result != null) {
                Vec3d vec = result.hitVec.addVector(x, y, z);
                results.add(new RayTraceResult(vec, result.sideHit, pos));
            }
        }

        RayTraceResult ret = null;
        double sqrDis = 0.0D;

        for (RayTraceResult result : results) {
            double newSqrDis = result.hitVec.squareDistanceTo(end);
            if (newSqrDis > sqrDis) {
                ret = result;
                sqrDis = newSqrDis;
            }
        }

        return ret;
    }

}
