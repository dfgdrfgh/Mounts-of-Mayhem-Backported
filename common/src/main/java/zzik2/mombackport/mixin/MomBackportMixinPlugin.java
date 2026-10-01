package zzik2.mombackport.mixin;

import zzik2.zreflex.mixin.ModifyAccessTransformer;

public final class MomBackportMixinPlugin extends ModifyAccessTransformer {

    private static final String COMPAT_MIXIN_PACKAGE = MomBackportMixinPlugin.class.getPackageName() + ".compat.";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return super.shouldApplyMixin(targetClassName, mixinClassName) && (!mixinClassName.startsWith(COMPAT_MIXIN_PACKAGE) || isClassPresent(targetClassName));
    }

    private static boolean isClassPresent(String className) {
        String resourceName = className.replace('.', '/') + ".class";
        return MomBackportMixinPlugin.class.getClassLoader().getResource(resourceName) != null;
    }
}
