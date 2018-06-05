package net.insomniakitten.bamboo.util;

import lombok.experimental.UtilityClass;
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
import java.util.List;

@UtilityClass
public class BoundingBoxes {
    @SideOnly(Side.CLIENT)
    public void renderAt(List<AxisAlignedBB> boxes, Entity entity, BlockPos pos, float partialTicks) {
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA,
                SourceFactor.ONE, DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        val offsetX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        val offsetY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        val offsetZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

        for (val box : boxes) {
            val target = box.grow(0.002D).offset(pos).offset(-offsetX, -offsetY, -offsetZ);
            RenderGlobal.drawSelectionBoundingBox(target, 0.0F, 0.0F, 0.0F, 0.4F);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    public RayTraceResult rayTrace(List<AxisAlignedBB> boxes, BlockPos pos, Vec3d start, Vec3d end) {
        val results = new ArrayList<RayTraceResult>();
        val x = pos.getX();
        val y = pos.getY();
        val z = pos.getZ();
        val a = start.subtract(x, y, z);
        val b = end.subtract(x, y, z);

        for (val box : boxes) {
            val result = box.calculateIntercept(a, b);
            if (result != null) {
                val vec = result.hitVec.addVector(x, y, z);
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
