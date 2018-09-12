package net.insomniakitten.bamboo.block.entity;

import lombok.val;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Objects;

public final class BlockEntityBambooCrate extends TileEntity {
    private final ItemStackHandler items = new ItemStackHandler(27) {
        @Override
        protected void onContentsChanged(final int slot) {
            super.onContentsChanged(slot);

            BlockEntityBambooCrate.this.markDirty();
        }
    };

    public IItemHandler getItems() {
        @Nullable val items = this.getCapability(this.getItemCapability(), null);

        if (items == null) {
            throw new IllegalStateException("Missing item capability instance");
        }

        return items;
    }

    @Override
    public void readFromNBT(final NBTTagCompound compound) {
        super.readFromNBT(compound);

        val items = compound.getCompoundTag("items");

        this.items.deserializeNBT(items);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound compound) {
        super.writeToNBT(compound);

        val items = this.items.serializeNBT();

        compound.setTag("items", items);

        return compound;
    }

    @Override
    public boolean shouldRefresh(final World world, final BlockPos pos, final IBlockState oldState, final IBlockState newState) {
        val oldBlock = oldState.getBlock();
        val newBlock = newState.getBlock();

        return oldBlock != newBlock;
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, @Nullable final EnumFacing face) {
        return this.getItemCapability() == capability;
    }

    @Override
    @Nullable
    public <T> T getCapability(final Capability<T> capability, @Nullable final EnumFacing face) {
        if (this.hasCapability(capability, face)) {
            return this.getItemCapability().cast(this.items);
        }

        return null;
    }

    private Capability<IItemHandler> getItemCapability() {
        return Objects.requireNonNull(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, "Forge item capability");
    }
}
