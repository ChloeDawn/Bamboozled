package net.insomniakitten.bamboo.entity;

import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledBlocks;
import net.insomniakitten.bamboo.BamboozledItems;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
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
    public static final EntityEntryBuilder<Entity> ENTRY = EntityEntryBuilder
        .create()
        .entity(EntityFallingSaltBlock.class)
        .id(new ResourceLocation(Bamboozled.ID, "falling_salt_block"), 0)
        .name(Bamboozled.ID + ".falling_salt_block")
        .tracker(256, 1, true);

    private static final DataParameter<BlockPos> ORIGIN = EntityDataManager.createKey(EntityFallingSaltBlock.class, DataSerializers.BLOCK_POS);

    private int fallTime;

    @SuppressWarnings("unused")
    public EntityFallingSaltBlock(final World world) {
        super(world);
    }

    public EntityFallingSaltBlock(final World world, final double x, final double y, final double z) {
        super(world);
        this.setSize(0.98F, 0.98F);
        this.setPosition(x, y + (double) ((1.0F - this.height) / 2.0F), z);
        this.setOrigin(new BlockPos(this));
        this.preventEntitySpawning = true;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    private boolean canDropAsItem(final BlockPos pos) {
        if (this.fallTime > 100) {
            if (!this.world.isRemote) {
                if (pos.getY() < 1 || pos.getY() > 256) {
                    return true;
                }
            }
        }
        return this.fallTime > 600;
    }

    @Override
    public void setOrigin(BlockPos pos) {
        this.dataManager.set(EntityFallingSaltBlock.ORIGIN, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockPos getOrigin() {
        return this.dataManager.get(EntityFallingSaltBlock.ORIGIN);
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(EntityFallingSaltBlock.ORIGIN, BlockPos.ORIGIN);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.fallTime++ == 0) {
            val pos = new BlockPos(this);

            if (this.world.getBlockState(pos).getBlock() == BamboozledBlocks.SALT_BLOCK) {
                this.world.setBlockToAir(pos);
            } else if (!this.world.isRemote) {
                this.setDead();
                return;
            }
        }

        if (!this.hasNoGravity()) {
            this.motionY -= 0.04D;
        }

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

        if (!this.world.isRemote) {
            val pos = new BlockPos(this);

            if (this.onGround) {
                val state = this.world.getBlockState(pos);
                if (this.world.isAirBlock(new BlockPos(this.posX, this.posY - 0.01D, this.posZ)) && !(this.world
                    .getBlockState(pos)
                    .getMaterial() == Material.WATER) && BlockFalling.canFallThrough(this.world.getBlockState(new BlockPos(this.posX, this.posY - 0.01D, this.posZ)))) {
                    this.onGround = false;
                    return;
                }

                this.motionX *= 0.7D;
                this.motionZ *= 0.7D;
                this.motionY *= -0.5D;

                if (state.getBlock() != Blocks.PISTON_EXTENSION) {
                    this.setDead();

                    if (this.world.mayPlace(BamboozledBlocks.SALT_BLOCK, pos, true, EnumFacing.UP, null) && (this.world
                        .getBlockState(pos)
                        .getMaterial() == Material.WATER || !BlockFalling.canFallThrough(this.world.getBlockState(pos.down())))) {
                        this.world.setBlockState(pos, BamboozledBlocks.SALT_BLOCK.getDefaultState(), 3);
                    } else if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                        if (Bamboozled.getConfig().isSaltBlockDropsEnabled()) {
                            this.entityDropItem(new ItemStack(BamboozledBlocks.SALT_BLOCK), 0.0F);
                        } else {
                            this.entityDropItem(new ItemStack(BamboozledItems.SALT_PILE, 9), 0.0F);
                        }
                    }
                }
            } else {
                if (this.canDropAsItem(pos)) {
                    if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                        if (Bamboozled.getConfig().isSaltBlockDropsEnabled()) {
                            this.entityDropItem(new ItemStack(BamboozledBlocks.SALT_BLOCK), 0.0F);
                        } else {
                            this.entityDropItem(new ItemStack(BamboozledItems.SALT_PILE, 9), 0.0F);
                        }
                    }
                    this.setDead();
                }
            }
        }

        this.motionX *= 0.98D;
        this.motionY *= 0.98D;
        this.motionZ *= 0.98D;
    }



    @Override
    public void fall(final float distance, final float damageMultiplier) {}

    @Override
    protected void writeEntityToNBT(final NBTTagCompound compound) {
        compound.setInteger("fall_time", this.fallTime);
    }

    @Override
    protected void readEntityFromNBT(final NBTTagCompound compound) {
        this.fallTime = compound.getInteger("fall_time");
    }

    @Override
    public void addEntityCrashInfo(final CrashReportCategory category) {
        category.addDetail("Entity Type", () -> EntityList.getKey(this) + " (" + this.getClass().getCanonicalName() + ")");
        category.addCrashSection("Entity ID", this.getEntityId());
        category.addDetail("Entity Name", this::getName);
        category.addCrashSection("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.posX, this.posY, this.posZ));
        category.addCrashSection("Entity's Block location", CrashReportCategory.getCoordinateInfo(MathHelper.floor(this.posX), MathHelper
            .floor(this.posY), MathHelper.floor(this.posZ)));
        category.addCrashSection("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.motionX, this.motionY, this.motionZ));
        category.addDetail("Entity's Passengers", () -> this.getPassengers().toString());
        category.addDetail("Entity's Vehicle", () -> String.valueOf(this.getRidingEntity()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public World getWorldObj() {
        return this.world;
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
