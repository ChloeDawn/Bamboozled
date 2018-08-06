package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableMap;
import lombok.experimental.var;
import lombok.val;
import net.insomniakitten.bamboo.BamboozledItems;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public final class BlockBambooDoor extends BlockDoor {
    private static final ImmutableMap<EnumFacing, AxisAlignedBB> AABB = ImmutableMap.of(
        EnumFacing.NORTH, new AxisAlignedBB(0.0D, 0.0D, 0.8125D, 1.0D, 1.0D, 1.0D),
        EnumFacing.SOUTH, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.1875D),
        EnumFacing.WEST, new AxisAlignedBB(0.8125D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D),
        EnumFacing.EAST, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.1875D, 1.0D, 1.0D)
    );

    public BlockBambooDoor() {
        super(Material.WOOD);
        this.setHardness(3.0F);
        this.setSoundType(SoundType.WOOD);
        this.disableStats();
    }

    @Override
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER ? BamboozledItems.BAMBOO_DOOR : Items.AIR;
    }

    @Override
    public ItemStack getItem(final World world, final BlockPos pos, final IBlockState state) {
        return new ItemStack(BamboozledItems.BAMBOO_DOOR);
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(final IBlockState state, final World world, final BlockPos pos) {
        val actual = state.getActualState(world, pos);
        val open = actual.getValue(BlockDoor.OPEN);
        val left = actual.getValue(BlockDoor.HINGE) == EnumHingePosition.LEFT;
        var facing = actual.getValue(BlockDoor.FACING);

        if (open) {
            facing = facing.rotateYCCW();
        }

        if (left && open) {
            facing = facing.getOpposite();
        }

        return BlockBambooDoor.AABB.get(facing).offset(pos);
    }

    @Override
    public ItemStack getPickBlock(final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player) {
        return new ItemStack(BamboozledItems.BAMBOO_DOOR);
    }
}

