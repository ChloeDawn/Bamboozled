package net.insomniakitten.bamboo.event;

import net.insomniakitten.bamboo.BamboozledObjects;
import net.insomniakitten.bamboo.block.BlockBamboo;
import net.insomniakitten.bamboo.util.RenderHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;

public final class BambooRenderEvents {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        if (event.getTarget() == null) return;
        if (event.getTarget().typeOfHit != RayTraceResult.Type.BLOCK) return;

        BlockPos pos = event.getTarget().getBlockPos();
        EntityPlayer player = event.getPlayer();
        World world = player.world;
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() != BamboozledObjects.BAMBOO_BLOCK) return;

        List<AxisAlignedBB> boxes = new LinkedList<>();

        ((BlockBamboo) state.getBlock()).getCollisionBoxes(
                state.getActualState(world, pos), world, pos, boxes);

        RenderHelper.renderBoxes(boxes, player, pos, event.getPartialTicks());

        event.setCanceled(true);
    }


}
