package net.insomniakitten.bamboo.block;

import net.insomniakitten.bamboo.Bamboozled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public abstract class BlockPlateBase extends BlockBasePressurePlate {

    private String name;

    public BlockPlateBase(Material material, MapColor mapColor, SoundType sound, float hardness, float resistance) {
        super(material, mapColor);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(sound);
        setCreativeTab(Bamboozled.TAB);
    }

    public BlockPlateBase(Material material, SoundType sound, float hardness, float resistance) {
        this(material, material.getMaterialMapColor(), sound, hardness, resistance);
    }

    @Override
    public final Block setUnlocalizedName(String name) {
        return super.setUnlocalizedName(Bamboozled.ID + "." + name);
    }

    @Override
    public final String getUnlocalizedName() {
        if (name == null) {
            name = super.getUnlocalizedName()
                    .replace("tile.", "block.");
        }
        return name;
    }

}
