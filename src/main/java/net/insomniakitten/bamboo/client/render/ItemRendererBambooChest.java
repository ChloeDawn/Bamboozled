package net.insomniakitten.bamboo.client.render;

import lombok.val;
import net.insomniakitten.bamboo.block.BlockBambooChest;
import net.insomniakitten.bamboo.block.entity.BlockEntityBambooChest;
import net.minecraft.block.BlockChest;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ItemRendererBambooChest extends TileEntityItemStackRenderer {
    @Override
    public void renderByItem(final ItemStack stack, final float partialTicks) {
        val item = stack.getItem();
        if (item instanceof ItemBlock) {
            val block = ((ItemBlock) item).getBlock();
            if (block instanceof BlockBambooChest) {
                val type = ((BlockChest) block).chestType;
                val te = new BlockEntityBambooChest(type);
                TileEntityRendererDispatcher.instance.render(te, 0.0D, 0.0D, 0.0D, partialTicks, 1.0F);
            }
        }
    }
}
