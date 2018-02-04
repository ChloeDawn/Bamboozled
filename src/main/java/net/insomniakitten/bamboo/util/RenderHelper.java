package net.insomniakitten.bamboo.util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.List;

public final class RenderHelper {

    private RenderHelper() {}

    public static void renderBoxes(List<AxisAlignedBB> boxes, Entity entity, BlockPos pos, float partialTicks) {
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA,
                SourceFactor.ONE, DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        double offsetX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double offsetY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double offsetZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

        for (AxisAlignedBB box : boxes) {
            AxisAlignedBB target = box.grow(0.002D).offset(pos).offset(-offsetX, -offsetY, -offsetZ);
            RenderGlobal.drawSelectionBoundingBox(target, 0.0F, 0.0F, 0.0F, 0.4F);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    public static void renderBox(AxisAlignedBB box, Entity entity, BlockPos pos, float partialTicks) {
        renderBoxes(Collections.singletonList(box), entity, pos, partialTicks);
    }

}
