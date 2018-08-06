package net.insomniakitten.bamboo.entity;

import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

public final class EntityThrownSaltPile extends EntityThrowable {
    public static final EntityEntryBuilder<Entity> ENTRY = EntityEntryBuilder.create()
        .entity(EntityThrownSaltPile.class)
        .id(new ResourceLocation(Bamboozled.ID, "thrown_salt_pile"), 1)
        .name(Bamboozled.ID + ".thrown_salt_pile")
        .tracker(64, 1, true);

    @SuppressWarnings("unused")
    public EntityThrownSaltPile(final World world) {
        super(world);
    }

    @SuppressWarnings("unused")
    public EntityThrownSaltPile(final World world, final double x, final double y, final double z) {
        super(world, x, y, z);
    }

    public EntityThrownSaltPile(final World world, final EntityLivingBase thrower) {
        super(world, thrower);
    }

    @Override
    protected void onImpact(final RayTraceResult result) {
        if (Bamboozled.getConfig().isSaltUndeadDamageEnabled() && result.typeOfHit == RayTraceResult.Type.ENTITY) {
            if (result.entityHit instanceof EntityLivingBase) {
                val living = (EntityLivingBase) result.entityHit;
                if (living.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
                    living.attackEntityFrom(DamageSource.MAGIC, 2);
                }
            }
        }
    }
}
