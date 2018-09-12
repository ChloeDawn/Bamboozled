package net.insomniakitten.bamboo.util.intrinsics;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Nonnull
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierDefault({ ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER })
public @interface ValuesAreNonNullByDefault {

}
