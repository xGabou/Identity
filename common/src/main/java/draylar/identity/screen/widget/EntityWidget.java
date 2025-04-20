package draylar.identity.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import draylar.identity.Identity;
import draylar.identity.api.variant.IdentityType;
import draylar.identity.network.impl.FavoritePackets;
import draylar.identity.network.impl.SwapPackets;
import draylar.identity.screen.IdentityScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

public class EntityWidget<T extends LivingEntity> extends PressableWidget {

    private final IdentityType<T> type;
    private final T              entity;
    private final int            size;
    private       boolean        active;
    private       boolean        starred;
    private final IdentityScreen parent;

    public EntityWidget(
            int x, int y, int width, int height,
            IdentityType<T> type,
            T entity,
            IdentityScreen parent,
            boolean starred,
            boolean current
    ) {
        super(x, y, width, height, Text.of(""));
        this.type    = type;
        this.entity  = entity;
        this.parent  = parent;
        this.starred = starred;
        this.active  = current;

        // ---- new GUI‑scale–aware sizing ----
        // baseSizeFactor ≃ 25 pixels “zoom” per block‑unit
        float baseSizeFactor = (float)(25F / Math.max(entity.getWidth(), entity.getHeight()));
        double scaleFactor   = parent.getScaleFactor();
        int    guiScale      = parent.getGuiScale();
        double finalScale    = scaleFactor / (guiScale == 0 ? 1 : guiScale);
        this.size            = Math.max(1, (int)(baseSizeFactor * finalScale));
        // ------------------------------------

        entity.setGlowing(true);
        setTooltip(Tooltip.of(type.createTooltipText(entity)));
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) { }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        boolean hit = mx >= getX() && mx < getX() + getWidth()
                && my >= getY() && my < getY() + getHeight();

        if (hit) {
            if (button == 0) {
                SwapPackets.sendSwapRequest(type);
                parent.disableAll();
                active = true;
            } else if (button == 1) {
                starred = !starred;
                FavoritePackets.sendFavoriteRequest(type, starred);
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // 1) No super.render() here (we want transparent background)
        // 2) Compute your already‑scaled “base” pixel size per block‑unit:
        float baseSizePerBlock = 25F / Math.max(entity.getWidth(), entity.getHeight());
        double guiScaleFactor = parent.getScaleFactor();
        int   guiScale       = parent.getGuiScale();
        double finalFactor   = guiScale == 0 ? guiScaleFactor : (guiScaleFactor / guiScale);
        int    baseSize      = Math.max(1, (int)(baseSizePerBlock * finalFactor));

        // 3) Now measure how tall that would draw:
        int pixelHeight = (int)(entity.getHeight() * baseSize);

        // 4) If that’s taller than the cell, clamp it:
        int cellH = getHeight();
        if(pixelHeight > cellH) {
            // shrink so the *entire* height fits
            double shrink = (double)cellH / pixelHeight;
            baseSize = Math.max(1, (int)(baseSize * shrink));
            pixelHeight = cellH;
        }

        // 5) Compute center‑X and bottom‑Y so the model sits flush inside the box
        int cx      = getX() + getWidth() / 2;
        int bottomY = getY() + cellH - 2; // 2px of bottom padding

        // 6) Draw
        try {
            InventoryScreen.drawEntity(
                    ctx,
                    cx,
                    bottomY,
                    baseSize,
                    -10, -10,
                    entity
            );
        } catch(Exception ex) {
            Identity.LOGGER.warn("Failed to render "+ type.getEntityType().getTranslationKey(), ex);
        }

        // 7) Star & selection overlay
        if(starred) {
            ctx.drawTexture(Identity.id("textures/gui/star.png"),
                    getX(), getY(), 0,0, 15,15, 15,15);
        }
        if(active) {
            ctx.drawTexture(Identity.id("textures/gui/selected.png"),
                    getX(), getY(), getWidth(), getHeight(),
                    0,0, 48,32, 48,32);
        }
    }


    // completely suppress the default pressable background:
    @Override
    protected void renderButton(DrawContext ctx, int mouseX, int mouseY, float delta) { }

    @Override
    public void onPress() { /* no-op */ }

    public void setActive(boolean a) {
        this.active = a;
    }

    public void dispose() {
        if (entity != null) {
            entity.discard();
        }
    }
}
