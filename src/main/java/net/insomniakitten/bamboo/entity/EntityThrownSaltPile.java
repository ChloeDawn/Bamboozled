package net.insomniakitten.bamboo.entity;

import net.insomniakitten.bamboo.Bamboozled;
import net.insomniakitten.bamboo.BamboozledConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

public final class EntityThrownSaltPile extends EntityThrowable {

    public static final EntityEntryBuilder<Entity> ENTRY = EntityEntryBuilder.create()
            .entity(EntityThrownSaltPile.class)
            .id(Bamboozled.ID + ":thrown_salt_pile", 1)
            .name(Bamboozled.ID + ".thrown_salt_pile")
            .tracker(64, 1, true);

    private final boolean saltHurtsUndead;

    @SuppressWarnings("unused")
    public EntityThrownSaltPile(World world) {
        super(world);
        saltHurtsUndead = BamboozledConfig.GENERAL.saltHurtsUndead;
    }

    @SuppressWarnings("unused")
    public EntityThrownSaltPile(World world, double x, double y, double z) {
        super(world, x, y, z);
        saltHurtsUndead = BamboozledConfig.GENERAL.saltHurtsUndead;
    }

    public EntityThrownSaltPile(World world, EntityLivingBase thrower) {
        super(world, thrower);
        saltHurtsUndead = BamboozledConfig.GENERAL.saltHurtsUndead;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (saltHurtsUndead && result.typeOfHit == RayTraceResult.Type.ENTITY) {
            if (result.entityHit instanceof EntityLivingBase) {
                final EntityLivingBase living = (EntityLivingBase) result.entityHit;
                if (living.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
                    living.attackEntityFrom(DamageSource.MAGIC, 2);
                }
            }
        }
    }

}
