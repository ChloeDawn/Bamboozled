package net.insomniakitten.bamboo.entity;

import lombok.val;
import net.insomniakitten.bamboo.Bamboozled;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Objects;

public final class EntityThrownSaltPile extends EntityThrowable {
    public EntityThrownSaltPile(final World world) {
        super(world);
    }

    public EntityThrownSaltPile(final World world, final double x, final double y, final double z) {
        super(world, x, y, z);
    }

    public EntityThrownSaltPile(final World world, final EntityLivingBase thrower) {
        super(world, thrower);
    }

    @Override
    protected void onImpact(final RayTraceResult hit) {
        if (!Bamboozled.getConfig().isSaltUndeadDamageEnabled()) {
            return;
        }

        if (RayTraceResult.Type.ENTITY != hit.typeOfHit) {
            return;
        }

        val entity = Objects.requireNonNull(hit.entityHit, "hit entity");

        if (entity instanceof EntityLivingBase) {
            if (((EntityLivingBase) entity).isEntityUndead()) {
                entity.attackEntityFrom(DamageSource.MAGIC, 2);
            }
        }
    }
}
