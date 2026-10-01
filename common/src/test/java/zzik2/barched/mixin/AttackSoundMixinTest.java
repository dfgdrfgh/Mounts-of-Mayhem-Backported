package zzik2.barched.mixin;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

/** Check real target bytecode without bootstrapping Minecraft or applying mixins. */
public class AttackSoundMixinTest {
    @Test
    public void shadowsResolveAgainstMinecraft1211Owners() throws IOException {
        verifyShadows("zzik2/barched/mixin/entity/LivingEntityMixin", "net/minecraft/world/entity/LivingEntity");
        verifyShadows("zzik2/barched/mixin/entity/MobMixin", "net/minecraft/world/entity/Mob");
    }

    @Test
    public void attackSoundIsDeclaredOnMobInsteadOfLivingEntity() throws IOException {
        assertFalse(declaresMethod(read("net/minecraft/world/entity/LivingEntity"), "playAttackSound", "()V"));
        assertTrue(declaresMethod(read("net/minecraft/world/entity/Mob"), "playAttackSound", "()V"));
    }

    private static void verifyShadows(String mixin, String target) throws IOException {
        for (MethodNode method : read(mixin).methods) {
            if (hasShadow(method.visibleAnnotations) || hasShadow(method.invisibleAnnotations)) {
                assertTrue(mixin + " shadows missing method " + target + "." + method.name + method.desc,
                        resolves(target, method.name, method.desc));
            }
        }
    }

    private static boolean hasShadow(List<AnnotationNode> annotations) {
        return annotations != null && annotations.stream()
                .anyMatch(annotation -> annotation.desc.equals("Lorg/spongepowered/asm/mixin/Shadow;"));
    }

    private static boolean resolves(String owner, String name, String descriptor) throws IOException {
        while (owner != null) {
            ClassNode node = read(owner);
            if (declaresMethod(node, name, descriptor)) return true;
            owner = node.superName;
        }
        return false;
    }

    private static boolean declaresMethod(ClassNode node, String name, String descriptor) {
        return node.methods.stream().anyMatch(method -> method.name.equals(name) && method.desc.equals(descriptor));
    }

    private static ClassNode read(String name) throws IOException {
        try (InputStream stream = AttackSoundMixinTest.class.getClassLoader().getResourceAsStream(name + ".class")) {
            assertNotNull("Missing class " + name, stream);
            ClassNode node = new ClassNode();
            new ClassReader(stream).accept(node, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return node;
        }
    }
}
