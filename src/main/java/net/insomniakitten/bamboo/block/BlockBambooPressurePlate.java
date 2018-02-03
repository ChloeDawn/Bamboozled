package net.insomniakitten.bamboo.block;

import net.insomniakitten.bamboo.client.BlockModelMapper;
import net.insomniakitten.bamboo.item.ItemBlockBase;
import net.insomniakitten.bamboo.item.ItemBlockSupplier;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class BlockBambooPressurePlate extends BlockPlateBase implements ItemBlockSupplier, BlockModelMapper {

    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);

    public BlockBambooPressurePlate() {
        super(Material.WOOD, SoundType.WOOD, 0.5F, 2.5F);
    }

    @Override
    protected void playClickOnSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PRESSPLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
    }

    @Override
    protected void playClickOffSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PRESSPLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
    }

    @Override
    protected int computeRedstoneStrength(World world, BlockPos pos) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY();
        double z = pos.getZ() + 0.5D;

        double dist = Double.MAX_VALUE;

        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, PRESSURE_AABB)) {
            double d = entity.getDistanceSq(x, y, z);
            if (d < dist) dist = d;
        }

        return (15 * (int) dist) & 15;
    }

    @Override
    protected int getRedstoneStrength(IBlockState state) {
        return state.getValue(POWER);
    }

    @Override
    protected IBlockState setRedstoneStrength(IBlockState state, int strength) {
        return state.withProperty(POWER, strength);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(POWER, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWER);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, POWER);
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockBase(this, "powered=false");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IStateMapper getModelMapper() {
        return new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                //noinspection ConstantConditions
                return new ModelResourceLocation(getRegistryName(), "powered=" + (state.getValue(POWER) > 0));
            }
        };
    }

}
