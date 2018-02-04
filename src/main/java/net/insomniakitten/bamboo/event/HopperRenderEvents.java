package net.insomniakitten.bamboo.event;

import net.insomniakitten.bamboo.BamboozledObjects;
import net.insomniakitten.bamboo.block.BlockBambooHopper;
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class HopperRenderEvents {

    private static final AxisAlignedBB AABB_BOWL = new AxisAlignedBB(
            0.125D, 0.6875D, 0.125D, 0.875D, 1.0D, 0.875D
    ).shrink(0.004D);

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        if (event.getTarget() == null) return;
        if (event.getTarget().typeOfHit != RayTraceResult.Type.BLOCK) return;

        BlockPos pos = event.getTarget().getBlockPos();
        EntityPlayer player = event.getPlayer();
        World world = player.world;
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() != BamboozledObjects.BAMBOO_HOPPER) return;

        List<AxisAlignedBB> boxes = new LinkedList<>();

        Collections.addAll(boxes, AABB_BOWL, BlockBambooHopper.AABB_UPPER, BlockBambooHopper.AABB_LOWER);
        boxes.add(BlockBambooHopper.AABB_JOINTS.get(state.getValue(BlockBambooHopper.CONNECT)));

        RenderHelper.renderBoxes(boxes, player, pos, event.getPartialTicks());

        event.setCanceled(true);
    }

}
