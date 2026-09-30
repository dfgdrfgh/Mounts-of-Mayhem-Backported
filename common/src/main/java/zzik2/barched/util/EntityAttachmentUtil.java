package zzik2.barched.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.phys.Vec3;

public final class EntityAttachmentUtil {
    private EntityAttachmentUtil() {
    }

    public static float averageY(Entity entity, EntityAttachment attachment) {
        double y = 0.0D;
        int count = 0;
        Vec3 point;
        while ((point = entity.getAttachments().getNullable(attachment, count, 0.0F)) != null) {
            y += point.y;
            count++;
        }
        if (count == 0) {
            throw new IllegalStateException("No attachment points of type: " + attachment);
        }
        return (float) (y / count);
    }
}
