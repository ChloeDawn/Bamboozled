package net.insomniakitten.bamboo.block.base;

import lombok.val;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFence extends net.minecraft.block.BlockFence {
    public BlockFence(final Material material, final MapColor mapColor, final SoundType sound, final float hardness, final float resistance) {
        super(material, mapColor);
        this.setSoundType(sound);
        this.setHardness(hardness);
        this.setResistance(resistance);
    }

    public BlockFence(final Material material, final SoundType sound, final float hardness, final float resistance) {
        super(material, material.getMaterialMapColor());
        this.setSoundType(sound);
        this.setHardness(hardness);
        this.setResistance(resistance);
    }

    @Override
    public boolean doesSideBlockRendering(final IBlockState state, final IBlockAccess access, final BlockPos position, final EnumFacing face) {
        val offset = position.offset(face);
        val other = access.getBlockState(offset);

        return this == other.getBlock();
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(final IBlockState state, final IBlockAccess access, final BlockPos position, final EnumFacing face) {
        val offset = position.offset(face);
        val other = access.getBlockState(offset);

        return this != other.getBlock() && !other.doesSideBlockRendering(access, offset, face.getOpposite());
    }
}
