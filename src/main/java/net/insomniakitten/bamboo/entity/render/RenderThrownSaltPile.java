package net.insomniakitten.bamboo.entity.render;

import lombok.val;
import net.insomniakitten.bamboo.BamboozledItems;
import net.insomniakitten.bamboo.entity.EntityThrownSaltPile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraft.client.renderer.GlStateManager.*;

@SideOnly(Side.CLIENT)
public final class RenderThrownSaltPile extends Render<EntityThrownSaltPile> {
    public RenderThrownSaltPile(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(EntityThrownSaltPile entity, double x, double y, double z, float entityYaw, float partialTicks) {
        pushMatrix();
        translate(x, y, z);
        enableRescaleNormal();
        rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        rotate(getRotationAngle(), 1.0F, 0.0F, 0.0F);
        rotate(180.0F, 0.0F, 1.0F, 0.0F);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        if (renderOutlines) {
            enableColorMaterial();
            enableOutlineMode(this.getTeamColor(entity));
        }

        Minecraft.getMinecraft().getRenderItem().renderItem(
                new ItemStack(BamboozledItems.SALT_PILE),
                ItemCameraTransforms.TransformType.GROUND
        );

        if (renderOutlines) {
            disableOutlineMode();
            disableColorMaterial();
        }

        disableRescaleNormal();
        popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityThrownSaltPile entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    private float getRotationAngle() {
        val thirdPerson = renderManager.options.thirdPersonView;
        val viewX = renderManager.playerViewX;
        return (float) (thirdPerson == 2 ? -1 : 1) * viewX;
    }
}
