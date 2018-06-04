package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledItems;
import net.minecraft.block.Block;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class BlockBambooDoor extends BlockDoor {
    protected static final ImmutableMap<EnumFacing, AxisAlignedBB> AABB_LOWER, AABB_UPPER;

    static {
        Map<EnumFacing, AxisAlignedBB> lower = new HashMap<>(), upper = new HashMap<>();

        lower.put(EnumFacing.NORTH, new AxisAlignedBB(0.0D, 0.0D, 0.8125D, 1.0D, 2.0D, 1.0D));
        lower.put(EnumFacing.SOUTH, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 2.0D, 0.1875D));
        lower.put(EnumFacing.WEST, new AxisAlignedBB(0.8125D, 0.0D, 0.0D, 1.0D, 2.0D, 1.0D));
        lower.put(EnumFacing.EAST, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.1875D, 2.0D, 1.0D));
        upper.put(EnumFacing.NORTH, new AxisAlignedBB(0.0D, -1.0D, 0.8125D, 1.0D, 1.0D, 1.0D));
        upper.put(EnumFacing.SOUTH, new AxisAlignedBB(0.0D, -1.0D, 0.0D, 1.0D, 1.0D, 0.1875D));
        upper.put(EnumFacing.WEST, new AxisAlignedBB(0.8125D, -1.0D, 0.0D, 1.0D, 1.0D, 1.0D));
        upper.put(EnumFacing.EAST, new AxisAlignedBB(0.0D, -1.0D, 0.0D, 0.1875D, 1.0D, 1.0D));

        AABB_LOWER = Maps.immutableEnumMap(lower);
        AABB_UPPER = Maps.immutableEnumMap(upper);
    }

    public BlockBambooDoor() {
        super(Material.WOOD);
        setHardness(3.0F);
        setSoundType(SoundType.WOOD);
        setCreativeTab(Bamboozled.TAB);
        disableStats();
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(HALF) == EnumDoorHalf.LOWER ? BamboozledItems.BAMBOO_DOOR : Items.AIR;
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(BamboozledItems.BAMBOO_DOOR);
    }


    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        state = state.getActualState(world, pos);

        final boolean open = state.getValue(OPEN);
        final boolean left = state.getValue(HINGE) == EnumHingePosition.LEFT;
        final boolean lower = state.getValue(HALF) == EnumDoorHalf.LOWER;

        EnumFacing facing = state.getValue(FACING);

        if (open) facing = facing.rotateYCCW();
        if (left && open) facing = facing.getOpposite();

        return (lower ? AABB_LOWER : AABB_UPPER).get(facing).offset(pos);
    }

    @Override
    public Block setUnlocalizedName(String name) {
        return super.setUnlocalizedName(Bamboozled.ID + "." + name);
    }

    @Override
    public String getUnlocalizedName() {
        return super.getUnlocalizedName().replace("tile.", "block.");
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(BamboozledItems.BAMBOO_DOOR);
    }
}

