package zzik2.barched.client.nautilus;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.junit.Test;

import static org.junit.Assert.*;

public class NautilusEquipmentLayeringTest {
    @Test
    public void inventoryArmorMovesForwardWithoutShrinkingOrSliding() {
        Matrix4f projection = new Matrix4f().setOrtho(0, 1920, 1080, 0, 1000, 21000);
        Matrix4f modelView = new Matrix4f().translation(0, 0, -11000);
        Vector3f shell = new Vector3f(52, 40, 50);
        Vector3f base = modelView.transformPosition(new Vector3f(shell));
        NautilusEquipmentRenderType.applyLayering(modelView, projection);
        Vector3f armor = modelView.transformPosition(new Vector3f(shell));
        assertEquals(base.x, armor.x, 0.0F);
        assertEquals(base.y, armor.y, 0.0F);
        assertTrue("Armor must be in front of the coplanar shell", armor.z > base.z);
        assertEquals(1.0F / 512.0F, armor.z - base.z, 0.00001F);
    }

    @Test
    public void worldEquipmentKeepsVanillaPerspectiveOffset() {
        Matrix4f projection = new Matrix4f().setPerspective(1, 1.6F, 0.05F, 1000);
        Matrix4f initial = new Matrix4f().translation(3, 4, -8).rotateY(0.7F);
        Matrix4f expected = new Matrix4f(initial).scale(0.99975586F);
        NautilusEquipmentRenderType.applyLayering(initial, projection);
        assertTrue(expected.equals(initial, 0.000001F));
    }
}
