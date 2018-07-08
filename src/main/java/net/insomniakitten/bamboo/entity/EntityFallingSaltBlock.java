package net.insomniakitten.bamboo.entity;

import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledBlocks;
import net.insomniakitten.bamboo.BamboozledItems;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class EntityFallingSaltBlock extends EntityFallingBlock {
    public static final EntityEntryBuilder ENTRY = EntityEntryBuilder.create()
        .entity(EntityFallingSaltBlock.class)
        .id(new ResourceLocation(Bamboozled.ID, "falling_salt_block"), 0)
        .name(Bamboozled.ID + ".falling_salt_block")
        .tracker(256, 1, true);

    private static final DataParameter<BlockPos> ORIGIN = EntityDataManager.createKey(
        EntityFallingSaltBlock.class, DataSerializers.BLOCK_POS
    );

    public int fallTime;

    @SuppressWarnings("unused")
    public EntityFallingSaltBlock(World world) {
        super(world);
    }

    public EntityFallingSaltBlock(World world, double x, double y, double z) {
        super(world);
        setSize(0.98F, 0.98F);
        setPosition(x, y + (double) ((1.0F - height) / 2.0F), z);
        setOrigin(new BlockPos(this));
        preventEntitySpawning = true;
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    public void setOrigin(BlockPos pos) {
        dataManager.set(ORIGIN, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockPos getOrigin() {
        return dataManager.get(ORIGIN);
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void entityInit() {
        dataManager.register(ORIGIN, BlockPos.ORIGIN);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isDead;
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (fallTime++ == 0) {
            val pos = new BlockPos(this);

            if (world.getBlockState(pos).getBlock() == BamboozledBlocks.SALT_BLOCK) {
                world.setBlockToAir(pos);
            } else if (!world.isRemote) {
                setDead();
                return;
            }
        }

        if (!hasNoGravity()) {
            motionY -= 0.04D;
        }

        move(MoverType.SELF, motionX, motionY, motionZ);

        if (!world.isRemote) {
            val pos = new BlockPos(this);

            if (!onGround) {
                if (fallTime > 100 && !world.isRemote && (pos.getY() < 1 || pos.getY() > 256) || fallTime > 600) {
                    if (shouldDropItem && world.getGameRules().getBoolean("doEntityDrops")) {
                        if (Bamboozled.getConfig().isSaltBlockDropsEnabled()) {
                            entityDropItem(new ItemStack(BamboozledBlocks.SALT_BLOCK), 0.0F);
                        } else entityDropItem(new ItemStack(BamboozledItems.SALT_PILE, 9), 0.0F);
                    }
                    setDead();
                }
            } else {
                val state = world.getBlockState(pos);
                if (world.isAirBlock(new BlockPos(posX, posY - 0.01D, posZ))
                    && !(world.getBlockState(pos).getMaterial() == Material.WATER)
                    && BlockFalling.canFallThrough(world.getBlockState(new BlockPos(posX, posY - 0.01D, posZ)))) {
                    onGround = false;
                    return;
                }

                motionX *= 0.7D;
                motionZ *= 0.7D;
                motionY *= -0.5D;

                if (state.getBlock() != Blocks.PISTON_EXTENSION) {
                    setDead();

                    if (world.mayPlace(BamboozledBlocks.SALT_BLOCK, pos, true, EnumFacing.UP, null)
                        && (world.getBlockState(pos).getMaterial() == Material.WATER
                        || !BlockFalling.canFallThrough(world.getBlockState(pos.down())))) {
                        world.setBlockState(pos, BamboozledBlocks.SALT_BLOCK.getDefaultState(), 3);
                    } else if (shouldDropItem && world.getGameRules().getBoolean("doEntityDrops")) {
                        if (Bamboozled.getConfig().isSaltBlockDropsEnabled()) {
                            entityDropItem(new ItemStack(BamboozledBlocks.SALT_BLOCK), 0.0F);
                        } else entityDropItem(new ItemStack(BamboozledItems.SALT_PILE, 9), 0.0F);
                    }
                }
            }
        }

        motionX *= 0.98D;
        motionY *= 0.98D;
        motionZ *= 0.98D;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("fall_time", fallTime);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        fallTime = compound.getInteger("fall_time");
    }

    @Override
    public void addEntityCrashInfo(CrashReportCategory category) {
        category.addDetail("Entity Type", () -> EntityList.getKey(this) + " (" + getClass().getCanonicalName() + ")");
        category.addCrashSection("Entity ID", getEntityId());
        category.addDetail("Entity Name", this::getName);
        category.addCrashSection("Entity's Exact location", String.format("%.2f, %.2f, %.2f", posX, posY, posZ));
        category.addCrashSection("Entity's Block location", CrashReportCategory.getCoordinateInfo(
            MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ)));
        category.addCrashSection("Entity's Momentum", String.format("%.2f, %.2f, %.2f", motionX, motionY, motionZ));
        category.addDetail("Entity's Passengers", () -> getPassengers().toString());
        category.addDetail("Entity's Vehicle", () -> getRidingEntity().toString());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public World getWorldObj() {
        return world;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire() {
        return false;
    }

    @Override
    public IBlockState getBlock() {
        return BamboozledBlocks.SALT_BLOCK.getDefaultState();
    }

    @Override
    public boolean ignoreItemEntityData() {
        return true;
    }
}
