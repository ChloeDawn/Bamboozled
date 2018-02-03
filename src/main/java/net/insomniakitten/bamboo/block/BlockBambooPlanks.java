package net.insomniakitten.bamboo.block;

import net.insomniakitten.bamboo.item.ItemBlockSupplier;
import net.insomniakitten.bamboo.item.ItemSubBlockBase;
import net.insomniakitten.bamboo.util.OreEntrySupplier;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Locale;

public final class BlockBambooPlanks extends BlockBase implements ItemBlockSupplier, OreEntrySupplier {

    private static final PropertyEnum<Orientation> ORIENTATION = PropertyEnum.create("orientation", Orientation.class);

    public BlockBambooPlanks() {
        super(Material.WOOD, SoundType.WOOD, 2.0F, 15.0F);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ORIENTATION).ordinal();
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ORIENTATION, Orientation.VALUES[meta]);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ORIENTATION);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(this, 1, getMetaFromState(state)));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, getMetaFromState(state));
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        world.setBlockState(pos, world.getBlockState(pos).cycleProperty(ORIENTATION));
        return true;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(ORIENTATION, meta > 0 ? Orientation.VERTICAL : Orientation.HORIZONTAL);
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemSubBlockBase(this) {
            @Override
            public String getUnlocalizedName(ItemStack stack) {
                String name = getUnlocalizedName();
                if (stack.getMetadata() > 0) {
                    name += "_vertical";
                }
                return name;
            }

            @Override
            public void getModels(List<ModelResourceLocation> models) {
                //noinspection ConstantConditions
                models.add(0, new ModelResourceLocation(getRegistryName(), "orientation=horizontal"));
                models.add(1, new ModelResourceLocation(getRegistryName(), "orientation=vertical"));
            }
        };
    }

    @Override
    public void getOreEntries(OreCollection oreEntries) {
        oreEntries.put(new ItemStack(this, 1, 0), "plankWood", "plankBamboo");
        oreEntries.put(new ItemStack(this, 1, 1), "plankWood", "plankBamboo",
                "plankWoodVertical", "plankBambooVertical");
    }

    private enum Orientation implements IStringSerializable {
        HORIZONTAL, VERTICAL;

        private static final Orientation[] VALUES = values();

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
