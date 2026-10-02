package zzik2.zreflex.mixin;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.extensibility.IRemapper;

public class ModifyAccessTransformer implements IMixinConfigPlugin {

    private static final int ACCESS_MASK = Opcodes.ACC_PUBLIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED;
    private static final String ACCESS_DESC = "Lzzik2/zreflex/mixin/ModifyAccess;";
    private static final String NAME_DESC = "Lzzik2/zreflex/mixin/ModifyName;";

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) { return true; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        for (FieldNode field : targetClass.fields) {
            AccessRule access = accessRule(field.visibleAnnotations, field.invisibleAnnotations);
            if (access != null) field.access = applyAccess(field.access, access);

            NameRule name = nameRule(field.visibleAnnotations, field.invisibleAnnotations);
            if (name != null) {
                field.name = name.remap ? mapField(targetClassName, name.value, field.desc) : name.value;
            }
        }

        for (MethodNode method : targetClass.methods) {
            AccessRule access = accessRule(method.visibleAnnotations, method.invisibleAnnotations);
            if (access != null) method.access = applyAccess(method.access, access);

            NameRule name = nameRule(method.visibleAnnotations, method.invisibleAnnotations);
            if (name != null) {
                method.name = name.remap ? mapMethod(targetClassName, name.value, method.desc) : name.value;
            }
        }
    }

    private static int applyAccess(int original, AccessRule rule) {
        int updated = (original & ~ACCESS_MASK) | (rule.access & ACCESS_MASK);
        if (rule.removeFinal) {
            updated &= ~Opcodes.ACC_FINAL;
        } else if ((rule.access & Opcodes.ACC_FINAL) != 0) {
            updated |= Opcodes.ACC_FINAL;
        }
        return updated;
    }

    private static AccessRule accessRule(List<AnnotationNode> visible, List<AnnotationNode> invisible) {
        AnnotationNode node = find(ACCESS_DESC, visible, invisible);
        if (node == null) return null;

        int access = 0;
        boolean removeFinal = false;
        if (node.values != null) {
            for (int i = 0; i < node.values.size(); i += 2) {
                String key = (String) node.values.get(i);
                Object value = node.values.get(i + 1);
                if ("access".equals(key)) {
                    if (value instanceof List<?> list) {
                        for (Object entry : list) if (entry instanceof Integer flag) access |= flag;
                    } else if (value instanceof Integer flag) {
                        access = flag;
                    }
                } else if ("removeFinal".equals(key)) {
                    removeFinal = Boolean.TRUE.equals(value);
                }
            }
        }
        return new AccessRule(access, removeFinal);
    }

    private static NameRule nameRule(List<AnnotationNode> visible, List<AnnotationNode> invisible) {
        AnnotationNode node = find(NAME_DESC, visible, invisible);
        if (node == null) return null;

        String value = null;
        boolean remap = true;
        if (node.values != null) {
            for (int i = 0; i < node.values.size(); i += 2) {
                String key = (String) node.values.get(i);
                Object entry = node.values.get(i + 1);
                if ("value".equals(key)) value = (String) entry;
                if ("remap".equals(key)) remap = Boolean.TRUE.equals(entry);
            }
        }
        return value == null ? null : new NameRule(value, remap);
    }

    private static AnnotationNode find(String descriptor, List<AnnotationNode> visible, List<AnnotationNode> invisible) {
        AnnotationNode node = findIn(descriptor, visible);
        return node != null ? node : findIn(descriptor, invisible);
    }

    private static AnnotationNode findIn(String descriptor, List<AnnotationNode> annotations) {
        if (annotations == null) return null;
        for (AnnotationNode annotation : annotations) {
            if (descriptor.equals(annotation.desc)) return annotation;
        }
        return null;
    }

    private static String mapField(String owner, String name, String desc) {
        IRemapper remapper = MixinEnvironment.getDefaultEnvironment().getRemappers();
        return remapper.mapFieldName(owner, name, desc);
    }

    private static String mapMethod(String owner, String name, String desc) {
        IRemapper remapper = MixinEnvironment.getDefaultEnvironment().getRemappers();
        return remapper.mapMethodName(owner, name, desc);
    }

    private record AccessRule(int access, boolean removeFinal) {}
    private record NameRule(String value, boolean remap) {}
}
