package net.insomniakitten.bamboo.entity;

import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.init.BamboozledBlocks;
import net.insomniakitten.bamboo.init.BamboozledItems;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class EntityFallingSaltBlock extends EntityFallingBlock {
    private static final DataParameter<BlockPos> ORIGIN = EntityDataManager.createKey(EntityFallingSaltBlock.class, DataSerializers.BLOCK_POS);

    private int fallTime;

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

    private boolean canDropAsItem(final BlockPos position) {
        if (this.fallTime > 100) {
            if (!this.world.isRemote) {
                final int y = position.getY();

                if (1 > y || 256 < y) {
                    return true;
                }
            }
        }
        return this.fallTime > 600;
    }

    @Override
    public void setOrigin(final BlockPos position) {
        this.dataManager.set(EntityFallingSaltBlock.ORIGIN, position);
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
            final BlockPos position = new BlockPos(this);
            final IBlockState state = this.world.getBlockState(position);

            if (BamboozledBlocks.SALT_BLOCK == state.getBlock()) {
                this.world.setBlockToAir(position);
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
            final BlockPos position = new BlockPos(this);

            if (this.onGround) {
                final IBlockState state = this.world.getBlockState(position);
                final BlockPos below = new BlockPos(this.posX, this.posY - 0.01D, this.posZ);
                final IBlockState other = this.world.getBlockState(position);

                if (Material.WATER != other.getMaterial() && this.world.isAirBlock(below)) {
                    if (BlockFalling.canFallThrough(other)) {
                        this.onGround = false;

                        return;
                    }
                }

                this.motionX *= 0.7D;
                this.motionZ *= 0.7D;
                this.motionY *= -0.5D;

                if (Blocks.PISTON_EXTENSION != state.getBlock()) {
                    this.setDead();

                    final boolean mayPlace = this.world.mayPlace(BamboozledBlocks.SALT_BLOCK, position, true, EnumFacing.UP, null);
                    final boolean isWater = this.world.getBlockState(position).getMaterial() == Material.WATER;
                    final boolean canFallThrough = BlockFalling.canFallThrough(this.world.getBlockState(position.down()));

                    if (mayPlace && (isWater || !canFallThrough)) {
                        this.world.setBlockState(position, BamboozledBlocks.SALT_BLOCK.getDefaultState(), 3);
                    } else if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                        if (Bamboozled.getConfig().saltBlockDropsItself) {
                            this.entityDropItem(new ItemStack(BamboozledBlocks.SALT_BLOCK), 0.0F);
                        } else {
                            this.entityDropItem(new ItemStack(BamboozledItems.SALT_PILE, 9), 0.0F);
                        }
                    }
                }
            } else {
                if (this.canDropAsItem(position)) {
                    if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                        if (Bamboozled.getConfig().saltBlockDropsItself) {
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
        compound.setInteger("fall_time", this.fallTime); // TODO fall_time -> FallTime ?
    }

    @Override
    protected void readEntityFromNBT(final NBTTagCompound compound) {
        this.fallTime = compound.getInteger("fall_time"); // TODO fall_time -> FallTime ?
    }

    @Override
    public void addEntityCrashInfo(final CrashReportCategory category) {
        category.addDetail("Entity Type", () -> {
            final ResourceLocation key = Objects.requireNonNull(EntityList.getKey(this), "entity key");
            return String.format("%s (%s)", key, this.getClass().getCanonicalName());
        });

        category.addCrashSection("Entity ID", this.getEntityId());
        category.addDetail("Entity Name", this::getName);

        final double x = this.posX;
        final double y = this.posY;
        final double z = this.posZ;

        category.addCrashSection("Entity's Exact location", String.format("%.2f, %.2f, %.2f", x, y, z));

        final String location = CrashReportCategory.getCoordinateInfo(new BlockPos(x, y, z));

        category.addCrashSection("Entity's Block location", location);

        final double mX = this.motionX;
        final double mY = this.motionY;
        final double mZ = this.motionZ;

        category.addCrashSection("Entity's Momentum", String.format("%.2f, %.2f, %.2f", mX, mY, mZ));

        category.addDetail("Entity's Passengers", () -> this.getPassengers().toString());
        category.addDetail("Entity's Vehicle", () -> String.valueOf(this.getRidingEntity()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire() {
        return false;
    }

    @Override
    @Nonnull
    public IBlockState getBlock() {
        return BamboozledBlocks.SALT_BLOCK.getDefaultState();
    }

    @Override
    public boolean ignoreItemEntityData() {
        return true;
    }
}
