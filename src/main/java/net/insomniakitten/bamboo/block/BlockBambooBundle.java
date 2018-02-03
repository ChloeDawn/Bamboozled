package net.insomniakitten.bamboo.block;

import net.insomniakitten.bamboo.BamboozledConfig;
import net.insomniakitten.bamboo.item.ItemBlockSupplier;
import net.insomniakitten.bamboo.item.ItemSubBlockBase;
import net.insomniakitten.bamboo.util.OreEntrySupplier;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public final class BlockBambooBundle extends BlockBase implements ItemBlockSupplier, OreEntrySupplier {

    private static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);
    private static final PropertyInteger DRIED = PropertyInteger.create("dried", 0, 3);

    private final boolean isDryingEnabled;

    public BlockBambooBundle() {
        super(Material.PLANTS, SoundType.WOOD, 1.0F, 5.0F);
        isDryingEnabled = BamboozledConfig.GENERAL.inWorldBambooDrying;
        setHarvestLevel("axe", 0);
        setTickRandomly(true);
    }

    private boolean isDry(IBlockState state) {
        return state.getValue(DRIED) == 3;
    }

    private boolean isDry(int meta) {
        return meta == 1;
    }

    public boolean isDryingEnabled() {
        return isDryingEnabled;
    }

    public int getDryProgress(IBlockState state) {
        return state.getValue(DRIED);
    }

    @Override
    @Deprecated
    public Material getMaterial(IBlockState state) {
        return isDry(state) ? Material.WOOD : Material.PLANTS;
    }

    @Override
    @Deprecated
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return isDry(state) ? MapColor.WOOD : MapColor.GREEN;
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing.Axis axis = EnumFacing.Axis.values()[meta & 3];
        return getDefaultState().withProperty(AXIS, axis).withProperty(DRIED, meta >> 2);
    }

    @Override
    @Deprecated
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (state.getValue(AXIS)) {
                    case X:
                        return state.withProperty(AXIS, EnumFacing.Axis.Z);
                    case Z:
                        return state.withProperty(AXIS, EnumFacing.Axis.X);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        boolean hasSunlight = world.isDaytime() && world.canBlockSeeSky(pos.up());
        if (isDryingEnabled && !world.isRemote && !isDry(state) && hasSunlight) {
            world.setBlockState(pos, state.cycleProperty(DRIED));
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS, DRIED);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(this, 1, isDry(state) ? 1 : 0));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, isDry(state) ? 1 : 0);
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return isDry(state) ? SoundType.WOOD : SoundType.PLANT;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(AXIS, side.getAxis()).withProperty(DRIED, isDry(meta) ? 3 : 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int axis = state.getValue(AXIS).ordinal();
        int dried = state.getValue(DRIED) << 2;
        return axis | dried;
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemSubBlockBase(this) {
            @Override
            public void getModels(List<ModelResourceLocation> models) {
                models.add(0, new ModelResourceLocation(getRegistryName(), "axis=y,dried=0"));
                models.add(1, new ModelResourceLocation(getRegistryName(), "axis=y,dried=3"));
            }

            @Override
            @SideOnly(Side.CLIENT)
            public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
                if (isDry(stack.getMetadata()) || isDryingEnabled) {
                    super.addInformation(stack, world, tooltip, flag);
                }
            }

            @Override
            public int getItemBurnTime(ItemStack stack) {
                return isDry(stack.getMetadata()) ? 288 : 0;
            }

            @Override
            public String getUnlocalizedName(ItemStack stack) {
                String name = super.getUnlocalizedName(stack);
                if (isDry(stack.getMetadata())) name += "_dried";
                return name;
            }
        };
    }

    @Override
    public void getOreEntries(OreCollection oreEntries) {
        oreEntries.put(new ItemStack(this, 1, 0), "blockBamboo");
        oreEntries.put(new ItemStack(this, 1, 1), "blockBambooDried");
    }

}
