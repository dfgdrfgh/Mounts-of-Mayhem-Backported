package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import zzik2.barched.client.nautilus.NautilusEquipmentRenderType;

@Environment(EnvType.CLIENT)
public class UndeadHorseArmorLayer<T extends AbstractHorse, M extends HorseModel<T>> extends RenderLayer<T, M> {

   private static final ResourceLocation LEATHER = ResourceLocation.withDefaultNamespace("textures/entity/equipment/horse_body/leather.png");
   private static final ResourceLocation LEATHER_OVERLAY = ResourceLocation.withDefaultNamespace("textures/entity/equipment/horse_body/leather_overlay.png");
   private static final ResourceLocation IRON = ResourceLocation.withDefaultNamespace("textures/entity/equipment/horse_body/iron.png");
   private static final ResourceLocation GOLD = ResourceLocation.withDefaultNamespace("textures/entity/equipment/horse_body/gold.png");
   private static final ResourceLocation DIAMOND = ResourceLocation.withDefaultNamespace("textures/entity/equipment/horse_body/diamond.png");

   private final HorseModel<T> model;

   public UndeadHorseArmorLayer(RenderLayerParent<T, M> renderLayerParent, EntityModelSet entityModelSet) {
      super(renderLayerParent);
      this.model = new HorseModel<>(entityModelSet.bakeLayer(ModelLayers.HORSE_ARMOR));
   }

   @Override
   public void render(
           PoseStack poseStack,
           MultiBufferSource multiBufferSource,
           int packedLight,
           T horse,
           float limbSwing,
           float limbSwingAmount,
           float partialTick,
           float ageInTicks,
           float netHeadYaw,
           float headPitch
   ) {
      ItemStack itemStack = horse.getBodyArmorItem();
      if (!(itemStack.getItem() instanceof AnimalArmorItem animalArmorItem)
              || animalArmorItem.getBodyType() != AnimalArmorItem.BodyType.EQUESTRIAN) {
         return;
      }

      this.getParentModel().copyPropertiesTo(this.model);
      this.model.prepareMobModel(horse, limbSwing, limbSwingAmount, partialTick);
      this.model.setupAnim(horse, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

      int color = itemStack.is(ItemTags.DYEABLE)
              ? FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(itemStack, -6265536))
              : -1;

      ResourceLocation texture = animalArmorItem.getTexture();
      if (itemStack.is(Items.LEATHER_HORSE_ARMOR)) {
         texture = LEATHER;
      } else if (itemStack.is(Items.IRON_HORSE_ARMOR)) {
         texture = IRON;
      } else if (itemStack.is(Items.GOLDEN_HORSE_ARMOR)) {
         texture = GOLD;
      } else if (itemStack.is(Items.DIAMOND_HORSE_ARMOR)) {
         texture = DIAMOND;
      }

      // 1.21.1's armor RenderType uses a perspective scale for depth layering.
      // Under the mount inventory's orthographic camera that visibly shrinks/slides
      // the equipment away from the horse. Reuse the 1.21.11-style projection-aware
      // equipment layer already used by Nautilus armor.
      VertexConsumer base = multiBufferSource.getBuffer(NautilusEquipmentRenderType.armor(texture));
      this.model.renderToBuffer(poseStack, base, packedLight, OverlayTexture.NO_OVERLAY, color);

      if (itemStack.hasFoil()) {
         VertexConsumer glint = multiBufferSource.getBuffer(NautilusEquipmentRenderType.glint());
         this.model.renderToBuffer(poseStack, glint, packedLight, OverlayTexture.NO_OVERLAY, -1);
      }

      if (itemStack.is(Items.LEATHER_HORSE_ARMOR)) {
         VertexConsumer overlay = multiBufferSource.getBuffer(NautilusEquipmentRenderType.armor(LEATHER_OVERLAY));
         this.model.renderToBuffer(poseStack, overlay, packedLight, OverlayTexture.NO_OVERLAY, -1);
      }
   }
}
