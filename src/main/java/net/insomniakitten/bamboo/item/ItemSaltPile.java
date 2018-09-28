package net.insomniakitten.bamboo.item;

import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.entity.EntityThrownSaltPile;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.Objects;

public final class ItemSaltPile extends ItemBlock {
    public ItemSaltPile(final Block block) {
        super(block);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        final ItemStack stack = player.getHeldItem(hand);

        if (!Bamboozled.getConfig().throwableSaltPiles) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        if (Bamboozled.getConfig().throwRequiresSneaking && !player.isSneaking()) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }

        player.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 0.5F, 0.4F / (Item.itemRand.nextFloat() * 0.4F + 0.8F));
        player.getCooldownTracker().setCooldown(this, 10);

        if (!world.isRemote) {
            final EntityThrownSaltPile entity = new EntityThrownSaltPile(world, player);

            entity.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            world.spawnEntity(entity);
        }

        player.addStat(Objects.requireNonNull(StatList.getObjectUseStats(this), "StatList.getObjectUseStats(this)"));

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
