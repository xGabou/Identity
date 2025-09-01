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

    public static int VERTICAL_OFFSET = 30;
    private static int BASE_Y_OFFSET = 10;

    private IdentityType<T> type;
    private T entity;
    private int size;
    private boolean active;
    private boolean starred;
    private IdentityScreen parent;

    public EntityWidget(
            int x, int y, int width, int height,
            IdentityType<T> type,
            T entity,
            IdentityScreen parent,
            boolean starred,
            boolean current
    ) {
        super(x, y, width, height, Text.of(""));
        this.type = type;
        this.entity = entity;
        this.parent = parent;
        this.starred = starred;
        this.active = current;

        float baseSizeFactor = (float) (25F / Math.max(entity.getWidth(), entity.getHeight()));
        double scaleFactor = parent.getScaleFactor();
        int guiScale = parent.getGuiScale();
        double finalScale = scaleFactor / (guiScale == 0 ? 1 : guiScale);
        this.size = Math.max(1, (int) (baseSizeFactor * finalScale));

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

    public Text getHoverName() {
        return type.createTooltipText(entity);
    }

    // 1.21.1: do not override render, override renderWidget instead
    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();

        // draw debug box
        ctx.fill(x, y, x + w, y + h, 0x60204080);
        ctx.drawBorder(x, y, w, h, 0xFFFFFFFF);

        // normalize entity size
        float unit = Math.max(entity.getWidth(), entity.getHeight());
//        int scale  = Math.max(1, Math.round((Math.min(w, h) * 0.8f) / unit))/2;
        int scale = getSize();
        // clip entity to this cell
        int x1 = x;
        int y1 = y;
        int x2 = x + w;
        int y2 = y + h;

        // vertical offset so mobs stand in the middle
        float yOffset = 0.25f; // tweakable, 0.0 = feet at bottom, 0.5 = more centered

        try {
            InventoryScreen.drawEntity(
                    ctx,
                    x1, y1, x2, y2,
                    scale,
                    0,
                    mouseX, mouseY,
                    entity
            );
        } catch (Exception e) {
            Identity.LOGGER.warn("Failed to render " + type.getEntityType().getTranslationKey(), e);
        }

        RenderSystem.disableDepthTest();
        if (starred) {
            ctx.drawTexture(Identity.id("textures/gui/star.png"), x, y, 0, 0, 15, 15, 15, 15);
        }
        if (active) {
            ctx.drawTexture(Identity.id("textures/gui/selected.png"), x, y, w, h, 0, 0, 48, 32, 48, 32);
        }

        this.hovered = mouseX >= x && mouseY >= y && mouseX < x + w && mouseY < y + h;
    }







    private int getSize() {
        int rawGui = parent.getGuiScale();
        int clampedGui = (rawGui == 0) ? 3 : Math.min(rawGui, 5);

        float baseSizePerBlock = 25F / Math.max(entity.getWidth(), entity.getHeight());

        double windowScale = parent.getScaleFactor();
        double effectiveScale = windowScale / clampedGui;

        return Math.max(1, (int) (baseSizePerBlock * effectiveScale));
    }

    @Override
    public void onPress() { }

    public void setActive(boolean a) {
        this.active = a;
    }

    public void dispose() {
        if (entity != null) {
            try {
                entity.discard();
            } catch (Exception ignored) { }
            entity = null;
        }
        type = null;
        parent = null;
    }
}
