package net.insomniakitten.bamboo.client.render;

import lombok.val;
import net.insomniakitten.bamboo.entity.EntityThrownSaltPile;
import net.insomniakitten.bamboo.init.BamboozledItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

@SideOnly(Side.CLIENT)
public final class EntityRendererThrownSaltPile extends Render<EntityThrownSaltPile> {
    private static final ResourceLocation TEXTURE_ATLAS = new ResourceLocation("textures/atlas/blocks.png");

    private final ItemStack itemStack = new ItemStack(BamboozledItems.SALT_PILE);

    public EntityRendererThrownSaltPile(final RenderManager manager) {
        super(manager);

        if (this.itemStack.isEmpty()) {
            throw new IllegalStateException("Empty item stack for 'BamboozledItems.SALT_PILE'");
        }
    }

    @Override
    public void doRender(final EntityThrownSaltPile entity, final double x, final double y, final double z, final float yaw, final float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotate(-this.getEntityRenderer().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(this.getRotationAngle(), 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        this.bindEntityTexture(entity);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        Minecraft.getMinecraft().getRenderItem().renderItem(this.itemStack, TransformType.GROUND);

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityThrownSaltPile entity) {
        return EntityRendererThrownSaltPile.TEXTURE_ATLAS;
    }

    private float getRotationAngle() {
        val renderer = this.getEntityRenderer();
        val options = renderer.options;

        return renderer.playerViewX * (2 == options.thirdPersonView ? -1 : 1);
    }

    private RenderManager getEntityRenderer() {
        return Objects.requireNonNull(this.renderManager, "this.renderManager");
    }
}
