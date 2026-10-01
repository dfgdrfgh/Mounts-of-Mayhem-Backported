package zzik2.barched.client.nautilus;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.Optional;
import java.util.function.Function;

/** Keep vanilla equipment states, with 1.21.11's projection-aware depth offset. */
public final class NautilusEquipmentRenderType extends RenderType {
    private static final Function<ResourceLocation, RenderType> ARMOR = Util.memoize(
            texture -> new NautilusEquipmentRenderType(RenderType.armorCutoutNoCull(texture)));
    private static final RenderType GLINT = new NautilusEquipmentRenderType(RenderType.armorEntityGlint());
    private final RenderType delegate;

    private NautilusEquipmentRenderType(RenderType delegate) {
        super("mombackport_nautilus_" + delegate, delegate.format(), delegate.mode(), delegate.bufferSize(),
                delegate.affectsCrumbling(), false, () -> setup(delegate), delegate::clearRenderState);
        this.delegate = delegate;
    }

    private static void setup(RenderType delegate) {
        Matrix4f projection = RenderSystem.getProjectionMatrix();
        boolean orthographic = projection.m33() != 0.0F;
        Matrix4f previous = orthographic ? new Matrix4f(RenderSystem.getModelViewMatrix()) : null;
        delegate.setupRenderState();
        if (previous != null) {
            // The delegate pushed a matrix and applied 1.21.1's perspective
            // scale. Replace that transform for the inventory's orthographic
            // projection; clearRenderState still owns the matching pop.
            applyLayering(previous, projection);
            RenderSystem.getModelViewStack().set(previous);
            RenderSystem.applyModelViewMatrix();
        }
    }

    static void applyLayering(Matrix4f modelView, Matrix4f projection) {
        if (projection.m33() != 0.0F) {
            modelView.translate(0.0F, 0.0F, 1.0F / 512.0F);
        } else {
            modelView.scale(1.0F - 1.0F / 4096.0F);
        }
    }

    public static RenderType armor(ResourceLocation texture) {
        return ARMOR.apply(texture);
    }

    public static RenderType glint() {
        return GLINT;
    }

    @Override
    public Optional<RenderType> outline() {
        return this.delegate.outline();
    }
}
