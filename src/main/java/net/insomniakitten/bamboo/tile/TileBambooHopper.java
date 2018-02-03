package net.insomniakitten.bamboo.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.items.ItemStackHandler;

public final class TileBambooHopper extends TileEntity implements ITickable, IWorldNameable {

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };

    private int transferCooldown = -1;
    private String customName;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        transferCooldown = compound.getInteger("transfer_cooldown");
        if (compound.hasKey("custom_name")) {
            customName = compound.getString("custom_name");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inventory", inventory.serializeNBT());
        compound.setInteger("transfer_cooldown", transferCooldown);
        if (hasCustomName()) {
            compound.setString("custom_name", customName);
        }
        return compound;
    }

    @Override
    public ITextComponent getDisplayName() {
        String name = getBlockType().getUnlocalizedName();
        if (hasCustomName()) name = getName();
        return new TextComponentTranslation(name + ".name");
    }

    @Override
    public void update() {
        if (world != null && !world.isRemote) {
            --transferCooldown;

            if (!isOnTransferCooldown()) {
                setTransferCooldown(0);
                updateHopper();
            }
        }
    }

    private void updateHopper() {
        // TODO
    }

    private boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }


    private void setTransferCooldown(int ticks) {
        this.transferCooldown = ticks;
    }

    @Override
    public String getName() {
        return customName;
    }

    @Override
    public boolean hasCustomName() {
        return customName != null && !customName.isEmpty();
    }

}
