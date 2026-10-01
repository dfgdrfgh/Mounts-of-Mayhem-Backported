package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.camel.Camel;

@Environment(EnvType.CLIENT)
public class CamelHuskModel<T extends Camel> extends CamelModel<T> {

    public CamelHuskModel(ModelPart root) {
        super(root);

        ModelPart body = root.getChild("body");
        ModelPart head = body.getChild("head");
        body.getChild("saddle").skipDraw = true;
        head.getChild("bridle").skipDraw = true;
        head.getChild("reins").skipDraw = true;
    }
}
