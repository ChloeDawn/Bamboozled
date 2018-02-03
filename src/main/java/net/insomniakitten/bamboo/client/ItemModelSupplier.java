package net.insomniakitten.bamboo.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import java.util.List;

public interface ItemModelSupplier {

    void getModels(List<ModelResourceLocation> models);

}
