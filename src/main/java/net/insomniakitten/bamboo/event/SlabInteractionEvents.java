package net.insomniakitten.bamboo.event;

import net.insomniakitten.bamboo.block.BlockSlabBase;
import net.insomniakitten.bamboo.block.BlockSlabBase.Variant;
import net.insomniakitten.bamboo.util.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class SlabInteractionEvents {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        RayTraceResult result = event.getTarget();

        if (result == null) return;
        if (result.typeOfHit != RayTraceResult.Type.BLOCK) return;

        World world = event.getPlayer().world;
        BlockPos pos = result.getBlockPos();
        IBlockState state = world.getBlockState(pos);

        if (!state.getPropertyKeys().contains(BlockSlabBase.VARIANT)) return;
        if (state.getValue(BlockSlabBase.VARIANT) != Variant.DOUBLE) return;

        AxisAlignedBB lower = Variant.LOWER.getBoundingBox();
        AxisAlignedBB upper = Variant.UPPER.getBoundingBox();
        AxisAlignedBB box;

        if (result.sideHit.getAxis().isHorizontal()) {
            box = result.hitVec.y > pos.getY() + 0.5D ? upper : lower;
        } else box = result.sideHit == EnumFacing.UP ? upper : lower;

        RenderHelper.renderBox(box, event.getPlayer(), pos, event.getPartialTicks());
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        IBlockState state = event.getState();
        World world = event.getWorld();
        BlockPos pos = event.getPos();

        if (!state.getPropertyKeys().contains(BlockSlabBase.VARIANT)) return;
        if (state.getValue(BlockSlabBase.VARIANT) != Variant.DOUBLE) return;

        RayTraceResult result = rayTrace(event.getPlayer());

        if (result == null) return;
        if (result.typeOfHit != RayTraceResult.Type.BLOCK) return;

        Variant variant = result.hitVec.y > pos.getY() + 0.5D ? Variant.LOWER : Variant.UPPER;

        world.setBlockState(pos, state.withProperty(BlockSlabBase.VARIANT, variant));

        if (!event.getPlayer().isCreative()) {
            Block.spawnAsEntity(world, pos, new ItemStack(state.getBlock()));
        }

        event.setCanceled(true);
    }

    private RayTraceResult rayTrace(EntityPlayer entity) {
        double length = entity.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        double x = entity.posX, y = entity.posY, z = entity.posZ;

        Vec3d startPos = new Vec3d(x, y + entity.getEyeHeight(), z);
        Vec3d look = entity.getLookVec();
        Vec3d endPos = startPos.addVector(look.x * length, look.y * length, look.z * length);

        return entity.world.rayTraceBlocks(startPos, endPos);
    }

}
