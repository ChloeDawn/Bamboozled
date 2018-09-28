package net.insomniakitten.bamboo.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.val;
import lombok.var;
import net.insomniakitten.bamboo.util.LazyBlockItem;
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
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public final class BlockBambooDoor extends BlockDoor {
    public static final ImmutableMap<EnumFacing, AxisAlignedBB> FACING_AABB = Maps.immutableEnumMap(
        ImmutableMap.of(
            EnumFacing.NORTH, new AxisAlignedBB(0.0D, 0.0D, 0.8125D, 1.0D, 1.0D, 1.0D),
            EnumFacing.SOUTH, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.1875D),
            EnumFacing.WEST, new AxisAlignedBB(0.8125D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D),
            EnumFacing.EAST, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.1875D, 1.0D, 1.0D)
        )
    );

    private final Supplier<Item> item = LazyBlockItem.of(this, it -> {
        val name = Objects.requireNonNull(it.getRegistryName(), "registryName");

        return ForgeRegistries.ITEMS.getValue(name);
    });

    public BlockBambooDoor() {
        super(Material.WOOD);
        this.setHardness(3.0F);
        this.setSoundType(SoundType.WOOD);
        this.disableStats();
    }

    @Override
    public Item getItemDropped(final IBlockState state, final Random random, final int fortune) {
        return EnumDoorHalf.LOWER == state.getValue(BlockDoor.HALF) ? this.item.get() : Items.AIR;
    }

    @Override
    public ItemStack getItem(final World world, final BlockPos position, final IBlockState state) {
        return new ItemStack(this.item.get());
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(final IBlockState state, final World world, final BlockPos position) {
        val actual = state.getActualState(world, position);
        val open = actual.getValue(BlockDoor.OPEN);
        val left = actual.getValue(BlockDoor.HINGE) == EnumHingePosition.LEFT;
        var facing = actual.getValue(BlockDoor.FACING);

        if (open) {
            facing = facing.rotateYCCW();
        }

        if (left && open) {
            facing = facing.getOpposite();
        }

        return BlockBambooDoor.FACING_AABB.get(facing).offset(position);
    }

    @Override
    public ItemStack getPickBlock(final IBlockState state, final RayTraceResult hit, final World world, final BlockPos position, final EntityPlayer player) {
        return new ItemStack(this.item.get());
    }
}
