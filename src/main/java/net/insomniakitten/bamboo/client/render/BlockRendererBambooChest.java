package net.insomniakitten.bamboo.client.render;

import lombok.val;
import net.insomniakitten.bamboo.block.BlockBambooChest;
import net.insomniakitten.bamboo.block.entity.BlockEntityBambooChest;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
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

        int meta = 0;

        if (chest.hasWorld()) {
            val block = chest.getBlockType();
            meta = chest.getBlockMetadata();

            if (block instanceof BlockChest && meta == 0) {
                val world = chest.getWorld();
                val pos = chest.getPos();
                val state = world.getBlockState(pos);
                ((BlockChest) block).checkForSurroundingChests(world, pos, state);
                meta = chest.getBlockMetadata();
            }

            chest.checkForAdjacentChests();
        }

        if (chest.adjacentChestZNeg == null && chest.adjacentChestXNeg == null) {
            ModelChest chestModel;

            if (chest.adjacentChestXPos == null && chest.adjacentChestZPos == null) {
                chestModel = this.chestModel;

                if (destroyStage < 0) {
                    if (this.isChristmas) {
                        this.bindTexture(BlockRendererBambooChest.TEX_CHRISTMAS);
                    } else if (chest.getChestType() == BlockBambooChest.TYPE_BAMBOO_TRAP) {
                        this.bindTexture(BlockRendererBambooChest.TEX_TRAPPED);
                    } else {
                        this.bindTexture(BlockRendererBambooChest.TEX_NORMAL);
                    }
                } else {
                    this.bindTexture(TileEntitySpecialRenderer.DESTROY_STAGES[destroyStage]);
                    GlStateManager.matrixMode(GL11.GL_TEXTURE);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(4.0F, 4.0F, 1.0F);
                    GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
                    GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                }
            } else {
                chestModel = this.doubleChestModel;

                if (destroyStage < 0) {
                    if (this.isChristmas) {
                        this.bindTexture(BlockRendererBambooChest.TEX_CHRISTMAS_DOUBLE);
                    } else if (chest.getChestType() == BlockBambooChest.TYPE_BAMBOO_TRAP) {
                        this.bindTexture(BlockRendererBambooChest.TEX_TRAPPED_DOUBLE);
                    } else {
                        this.bindTexture(BlockRendererBambooChest.TEX_NORMAL_DOUBLE);
                    }
                } else {
                    this.bindTexture(TileEntitySpecialRenderer.DESTROY_STAGES[destroyStage]);
                    GlStateManager.matrixMode(GL11.GL_TEXTURE);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(8.0F, 4.0F, 1.0F);
                    GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
                    GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();

            if (destroyStage < 0) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
            }

            GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);

            int rotationAngle = 0;

            if (meta == 2) {
                rotationAngle = 180;
            }

            if (meta == 4) {
                rotationAngle = 90;
            }

            if (meta == 5) {
                rotationAngle = -90;
            }

            if (meta == 2 && chest.adjacentChestXPos != null) {
                GlStateManager.translate(1.0F, 0.0F, 0.0F);
            }

            if (meta == 5 && chest.adjacentChestZPos != null) {
                GlStateManager.translate(0.0F, 0.0F, -1.0F);
            }

            GlStateManager.rotate((float) rotationAngle, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            float lidAngle = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * partialTicks;

            val adjZ = chest.adjacentChestZNeg;
            val adjX = chest.adjacentChestXNeg;

            if (adjZ != null) {
                float adjZLidAngle = adjZ.prevLidAngle + (adjZ.lidAngle - adjZ.prevLidAngle) * partialTicks;

                if (adjZLidAngle > lidAngle) {
                    lidAngle = adjZLidAngle;
                }
            }

            if (adjX != null) {
                float adjXLigAngle = adjX.prevLidAngle + (adjX.lidAngle - adjX.prevLidAngle) * partialTicks;

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
    }
}
