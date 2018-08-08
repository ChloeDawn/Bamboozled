package net.insomniakitten.bamboo.client.render;

import lombok.val;
import net.insomniakitten.bamboo.BamboozledItems;
import net.insomniakitten.bamboo.entity.EntityThrownSaltPile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class EntityRendererThrownSaltPile extends Render<EntityThrownSaltPile> {
    private final ItemStack stack = new ItemStack(BamboozledItems.SALT_PILE);

    public EntityRendererThrownSaltPile(final RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(final EntityThrownSaltPile entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(this.getRotationAngle(), 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        Minecraft.getMinecraft().getRenderItem().renderItem(this.stack, TransformType.GROUND);

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityThrownSaltPile entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    private float getRotationAngle() {
        val thirdPersonView = this.renderManager.options.thirdPersonView;
        val playerViewX = this.renderManager.playerViewX;
        return (float) (thirdPersonView == 2 ? -1 : 1) * playerViewX;
    }
}
