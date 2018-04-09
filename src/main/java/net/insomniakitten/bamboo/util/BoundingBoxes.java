package net.insomniakitten.bamboo.util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BoundingBoxes {

    private BoundingBoxes() {}

    @SideOnly(Side.CLIENT)
    public static void renderAt(List<AxisAlignedBB> boxes, Entity entity, BlockPos pos, float partialTicks) {
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA,
                SourceFactor.ONE, DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        final double offsetX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        final double offsetY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        final double offsetZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

        for (AxisAlignedBB box : boxes) {
            final AxisAlignedBB target = box
                    .grow(0.002D)
                    .offset(pos)
                    .offset(-offsetX, -offsetY, -offsetZ);
            RenderGlobal.drawSelectionBoundingBox(target, 0.0F, 0.0F, 0.0F, 0.4F);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    @SideOnly(Side.CLIENT)
    public static void renderAt(AxisAlignedBB box, Entity entity, BlockPos pos, float partialTicks) {
        renderAt(Collections.singletonList(box), entity, pos, partialTicks);
    }

    public static RayTraceResult rayTrace(List<AxisAlignedBB> boxes, BlockPos pos, Vec3d start, Vec3d end) {
        final List<RayTraceResult> results = new ArrayList<>();

        final double x = pos.getX(), y = pos.getY(), z = pos.getZ();
        final Vec3d a = start.subtract(x, y, z);
        final Vec3d b = end.subtract(x, y, z);

        for (AxisAlignedBB box : boxes) {
            final RayTraceResult result = box.calculateIntercept(a, b);
            if (result != null) {
                final Vec3d vec = result.hitVec.addVector(x, y, z);
                results.add(new RayTraceResult(vec, result.sideHit, pos));
            }
        }

        RayTraceResult ret = null;
        double sqrDis = 0.0D;

        for (RayTraceResult result : results) {
            final double newSqrDis = result.hitVec.squareDistanceTo(end);
            if (newSqrDis > sqrDis) {
                ret = result;
                sqrDis = newSqrDis;
            }
        }

        return ret;
    }

}
