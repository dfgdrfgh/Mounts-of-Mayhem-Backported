package zzik2.barched.client.nautilus;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import zzik2.barched.nautilus.NautilusInventoryMenu;

public class NautilusInventoryScreen extends AbstractContainerScreen<NautilusInventoryMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/nautilus.png");
    public NautilusInventoryScreen(NautilusInventoryMenu menu, Inventory inventory) { super(menu, inventory, menu.nautilus.getDisplayName()); }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        if (this.menu.nautilus.isSaddleable()) {
            graphics.blitSprite(ResourceLocation.withDefaultNamespace("container/slot"), this.leftPos + 7, this.topPos + 17, 18, 18);
            graphics.blitSprite(ResourceLocation.withDefaultNamespace("container/slot"), this.leftPos + 7, this.topPos + 35, 18, 18);
        }
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, this.leftPos + 26, this.topPos + 18, this.leftPos + 78, this.topPos + 70, 17, 0.25F, mouseX, mouseY, this.menu.nautilus);
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
