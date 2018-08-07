package net.insomniakitten.bamboo.block;

import net.insomniakitten.bamboo.block.entity.BlockEntityBambooChest;
import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

public final class BlockBambooChest extends BlockChest {
    public static final Type TYPE_BAMBOO = EnumHelper.addEnum(Type.class, "BAMBOO", new Class[0]);
    public static final Type TYPE_BAMBOO_TRAP = EnumHelper.addEnum(Type.class, "BAMBOO_TRAP", new Class[0]);

    public BlockBambooChest(final Type type) {
        super(type);
        this.setHardness(2.5F);
        this.setSoundType(SoundType.WOOD);
    }

    @Override
    public TileEntity createNewTileEntity(final World world, final int meta) {
        return new BlockEntityBambooChest();
    }
}
