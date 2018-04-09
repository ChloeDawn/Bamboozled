package net.insomniakitten.bamboo.item;

import net.insomniakitten.bamboo.BamboozledConfig;
import net.insomniakitten.bamboo.entity.EntityThrownSaltPile;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.Objects;

public final class ItemSaltPile extends ItemBlock {

    private final boolean throwableSaltPiles;
    private final boolean throwRequiresSneaking;

    public ItemSaltPile(Block block) {
        super(block);
        throwableSaltPiles = BamboozledConfig.GENERAL.throwableSaltPiles;
        throwRequiresSneaking = BamboozledConfig.GENERAL.throwRequiresSneaking;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!throwableSaltPiles || (throwRequiresSneaking && !player.isSneaking())) {
            return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
        }

        final ItemStack stack = player.getHeldItem(hand);
        if (!player.isCreative()) stack.shrink(1);

        player.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        player.getCooldownTracker().setCooldown(this, 10);

        if (!world.isRemote) {
            final EntityThrownSaltPile saltPile = new EntityThrownSaltPile(world, player);
            saltPile.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            world.spawnEntity(saltPile);
        }

        player.addStat(Objects.requireNonNull(StatList.getObjectUseStats(this)));

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

}
