package net.insomniakitten.bamboo.tile;

import net.minecraft.tileentity.TileEntity;

public interface TileEntitySupplier {

    Class<? extends TileEntity> getTileClass();

    String getTileKey();

}
