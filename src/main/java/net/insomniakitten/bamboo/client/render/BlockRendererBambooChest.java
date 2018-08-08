package net.insomniakitten.bamboo.client.render;

import lombok.experimental.var;
import lombok.val;
import net.insomniakitten.bamboo.block.BlockBambooChest;
import net.insomniakitten.bamboo.block.entity.BlockEntityBambooChest;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Calendar;

@SideOnly(Side.CLIENT)
public final class BlockRendererBambooChest extends TileEntitySpecialRenderer<BlockEntityBambooChest> {
    private static final ResourceLocation TEX_NORMAL = new ResourceLocation("bamboozled", "textures/entity/bamboo_chest.png");
    private static final ResourceLocation TEX_TRAPPED = new ResourceLocation("bamboozled", "textures/entity/trapped_bamboo_chest.png");
    private static final ResourceLocation TEX_CHRISTMAS = new ResourceLocation("bamboozled", "textures/entity/christmas_bamboo_chest.png");
    private static final ResourceLocation TEX_NORMAL_DOUBLE = new ResourceLocation("bamboozled", "textures/entity/bamboo_chest_double.png");
    private static final ResourceLocation TEX_TRAPPED_DOUBLE = new ResourceLocation("bamboozled", "textures/entity/trapped_bamboo_chest_double.png");
    private static final ResourceLocation TEX_CHRISTMAS_DOUBLE = new ResourceLocation("bamboozled", "textures/entity/christmas_bamboo_chest_double.png");

    private final ModelChest chestModel = new ModelChest();
    private final ModelChest doubleChestModel = new ModelLargeChest();

    private final boolean isChristmas;

    {
        val calendar = Calendar.getInstance();
        val month = calendar.get(Calendar.MONTH) + 1;
        val day = calendar.get(Calendar.DAY_OF_MONTH);
        this.isChristmas = month == 12 && day >= 24 && day <= 26;
    }

    @Override
    public void render(final BlockEntityBambooChest chest, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.depthMask(true);

        var facing = EnumFacing.SOUTH;

        if (chest.hasWorld() && chest.getBlockType() instanceof BlockBambooChest) {
            val world = chest.getWorld();
            val pos = chest.getPos();
            val state = world.getBlockState(pos);
            facing = state.getValue(BlockChest.FACING);
        }

        if (chest.adjacentChestZNeg != null || chest.adjacentChestXNeg != null) {
            return;
        }

        val chestModel = this.isDouble(chest) ? this.doubleChestModel : this.chestModel;

        if (destroyStage < 0) {
            this.bindTexture(this.getTexture(chest));
        } else {
            this.bindTexture(this.getDestroyStage(destroyStage));
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scale(this.isDouble(chest) ? 8.0F : 4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();

        if (destroyStage < 0) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        }

        GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);

        if (EnumFacing.NORTH == facing && chest.adjacentChestXPos != null) {
            GlStateManager.translate(1.0F, 0.0F, 0.0F);
        }

        if (EnumFacing.EAST == facing && chest.adjacentChestZPos != null) {
            GlStateManager.translate(0.0F, 0.0F, -1.0F);
        }

        GlStateManager.rotate(facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);

        var lidAngle = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * partialTicks;

        val adjZ = chest.adjacentChestZNeg;
        val adjX = chest.adjacentChestXNeg;

        if (adjZ != null) {
            var adjZLidAngle = adjZ.prevLidAngle + (adjZ.lidAngle - adjZ.prevLidAngle) * partialTicks;

            if (adjZLidAngle > lidAngle) {
                lidAngle = adjZLidAngle;
            }
        }

        if (adjX != null) {
            var adjXLigAngle = adjX.prevLidAngle + (adjX.lidAngle - adjX.prevLidAngle) * partialTicks;

            if (adjXLigAngle > lidAngle) {
                lidAngle = adjXLigAngle;
            }
        }

        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;

        chestModel.chestLid.rotateAngleX = -(lidAngle * ((float) Math.PI / 2.0F));
        chestModel.renderAll();

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (destroyStage >= 0) {
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
    }

    private boolean isDouble(final BlockEntityBambooChest chest) {
        return chest.adjacentChestZPos != null || chest.adjacentChestXPos != null;
    }

    private boolean isTrapped(final BlockEntityBambooChest chest) {
        return BlockBambooChest.TYPE_BAMBOO_TRAP == chest.getChestType();
    }

    private ResourceLocation getTexture(final BlockEntityBambooChest chest) {
        if (this.isDouble(chest)) {
            if (this.isChristmas) {
                return BlockRendererBambooChest.TEX_CHRISTMAS_DOUBLE;
            } else if (this.isTrapped(chest)) {
                return BlockRendererBambooChest.TEX_TRAPPED_DOUBLE;
            } else {
                return BlockRendererBambooChest.TEX_NORMAL_DOUBLE;
            }
        } else {
            if (this.isChristmas) {
                return BlockRendererBambooChest.TEX_CHRISTMAS;
            } else if (this.isTrapped(chest)) {
                return BlockRendererBambooChest.TEX_TRAPPED;
            } else {
                return BlockRendererBambooChest.TEX_NORMAL;
            }
        }
    }

    private ResourceLocation getDestroyStage(final int destroyStage) {
        val stages = TileEntitySpecialRenderer.DESTROY_STAGES;
        return stages[destroyStage % stages.length];
    }
}
