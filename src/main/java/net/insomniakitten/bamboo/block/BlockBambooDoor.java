package net.insomniakitten.bamboo.block;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Random;

public final class BlockBambooDoor extends BlockDoor {

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

