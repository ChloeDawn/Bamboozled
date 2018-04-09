package net.insomniakitten.bamboo.entity.render;

import net.insomniakitten.bamboo.BamboozledItems;
import net.insomniakitten.bamboo.entity.EntityThrownSaltPile;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderThrownSaltPile extends Render<EntityThrownSaltPile> {

    public RenderThrownSaltPile(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(EntityThrownSaltPile entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(getRotationAngle(), 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        if (renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        FMLClientHandler.instance().getClient().getRenderItem().renderItem(
                new ItemStack(BamboozledItems.SALT_PILE),
                ItemCameraTransforms.TransformType.GROUND
        );

        if (renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityThrownSaltPile entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    private float getRotationAngle() {
        final int thirdPerson = renderManager.options.thirdPersonView;
        final float viewX = renderManager.playerViewX;
        return (float) (thirdPerson == 2 ? -1 : 1) * viewX;
    }

}
