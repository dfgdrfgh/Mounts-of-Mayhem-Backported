package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;

@Environment(EnvType.CLIENT)
public class UndeadHorseArmorLayer extends RenderLayer<AbstractHorse, HorseModel<AbstractHorse>> {

   private static final ResourceLocation LEATHER = ResourceLocation.withDefaultNamespace("textures/entity/equipment/horse_body/leather.png");
   private static final ResourceLocation LEATHER_OVERLAY = ResourceLocation.withDefaultNamespace("textures/entity/equipment/horse_body/leather_overlay.png");
   private static final ResourceLocation IRON = ResourceLocation.withDefaultNamespace("textures/entity/equipment/horse_body/iron.png");
   private static final ResourceLocation GOLD = ResourceLocation.withDefaultNamespace("textures/entity/equipment/horse_body/gold.png");
   private static final ResourceLocation DIAMOND = ResourceLocation.withDefaultNamespace("textures/entity/equipment/horse_body/diamond.png");

   private final HorseModel<AbstractHorse> model;

   public UndeadHorseArmorLayer(RenderLayerParent<AbstractHorse, HorseModel<AbstractHorse>> renderLayerParent, EntityModelSet entityModelSet) {
      super(renderLayerParent);
      this.model = new HorseModel(entityModelSet.bakeLayer(ModelLayers.HORSE_ARMOR));
   }

   public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AbstractHorse horse, float f, float g, float h, float j, float k, float l) {
      ItemStack itemStack = horse.getBodyArmorItem();
      Item var13 = itemStack.getItem();
      if (var13 instanceof AnimalArmorItem) {
         AnimalArmorItem animalArmorItem = (AnimalArmorItem)var13;
         if (animalArmorItem.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN) {
            ((HorseModel)this.getParentModel()).copyPropertiesTo(this.model);
            this.model.prepareMobModel(horse, f, g, h);
            this.model.setupAnim(horse, f, g, j, k, l);
            int m;
            if (itemStack.is(ItemTags.DYEABLE)) {
               m = FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(itemStack, -6265536));
            } else {
               m = -1;
            }

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

            VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource, RenderType.armorCutoutNoCull(texture), itemStack.hasFoil());
            this.model.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, m);
            if (itemStack.is(Items.LEATHER_HORSE_ARMOR)) {
               VertexConsumer overlay = multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(LEATHER_OVERLAY));
               this.model.renderToBuffer(poseStack, overlay, i, OverlayTexture.NO_OVERLAY, -1);
            }
            return;
         }
      }

   }
}
