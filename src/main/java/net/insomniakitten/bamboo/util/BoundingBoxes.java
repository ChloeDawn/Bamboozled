package net.insomniakitten.bamboo.util;

import lombok.experimental.var;
import lombok.val;
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

public final class BoundingBoxes {
    private BoundingBoxes() {}

    @SideOnly(Side.CLIENT)
    public static void renderAt(final Iterable<AxisAlignedBB> boxes, final Entity entity, final BlockPos pos, final float partialTicks) {
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
            SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA,
            SourceFactor.ONE, DestFactor.ZERO
        );
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        val x = pos.getX() - (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks);
        val y = pos.getY() - (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks);
        val z = pos.getZ() - (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks);

        for (val box : boxes) {
            RenderGlobal.drawSelectionBoundingBox(box.grow(0.002D).offset(x, y, z), 0.0F, 0.0F, 0.0F, 0.4F);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    public static RayTraceResult rayTrace(final Iterable<AxisAlignedBB> boxes, final BlockPos pos, final Vec3d start, final Vec3d end) {
        val results = new ArrayList<RayTraceResult>();
        val a = start.subtract(pos.getX(), pos.getY(), pos.getZ());
        val b = end.subtract(pos.getX(), pos.getY(), pos.getZ());

        for (val box : boxes) {
            val result = box.calculateIntercept(a, b);
            if (result != null) {
                val vec = result.hitVec.add(pos.getX(), pos.getY(), pos.getZ());
                results.add(new RayTraceResult(vec, result.sideHit, pos));
            }
        }

        RayTraceResult ret = null;
        var sqrDis = 0.0D;

        for (val result : results) {
            val newSqrDis = result.hitVec.squareDistanceTo(end);
            if (newSqrDis > sqrDis) {
                ret = result;
                sqrDis = newSqrDis;
            }
        }

        return ret;
    }
}
