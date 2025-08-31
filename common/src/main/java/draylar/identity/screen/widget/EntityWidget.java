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
    private  IdentityType<T> type;
    private  T              entity;
    private  int            size;
    private       boolean        active;
    private       boolean        starred;
    private  IdentityScreen parent;

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
    // in EntityWidget<T>
    public Text getHoverName() {
        return type.createTooltipText(entity);
    }


    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // 1) transparent background, no super.render()

        // 2) clamp GUI‑scale to [1..5], default Auto→3
        int rawGui = parent.getGuiScale();    // 0 == Auto, otherwise 1–5
        int clampedGui = (rawGui == 0) ? 3 : Math.min(rawGui, 5);

        // 3) compute how “big” a block unit is in pixels
        float baseSizePerBlock = 25F / Math.max(entity.getWidth(), entity.getHeight());

        // 4) apply inverse scaling by GUI‑scale
        double windowScale = parent.getScaleFactor();
        double effectiveScale = windowScale / clampedGui;

        // 5) final pixel size for our model
        int size = Math.max(1, (int)(baseSizePerBlock * effectiveScale));

        // 6) figure out its pixel‐height and vertical center
        int pixelHeight = (int)(entity.getHeight() * size);
        int slotCX      = getX() + getWidth()  / 2;
        int slotCY      = getY() + getHeight() / 2;
        int bottomY     = slotCY + (pixelHeight / 2);

        // 7) draw it (with your -10, -10 offsets)
        try {
            InventoryScreen.drawEntity(
                    ctx,
                    slotCX, bottomY - size,    // top-left (x1,y1)
                    slotCX + size, bottomY,    // bottom-right (x2,y2)
                    size,
                    0.0F,                      // rotation angle (use 0F if not needed)
                    -10, -10,                  // mouseX, mouseY
                    entity
            );

        } catch (Exception e) {
            Identity.LOGGER.warn("Failed to render " + type.getEntityType().getTranslationKey(), e);
        }

        // 8) star & outline on top
        if (starred) {
            ctx.drawTexture(
                    Identity.id("textures/gui/star.png"),
                    getX(), getY(), 0, 0, 15, 15, 15, 15
            );
        }
        if (active) {
            ctx.drawTexture(
                    Identity.id("textures/gui/selected.png"),
                    getX(), getY(), getWidth(), getHeight(),
                    0, 0, 48, 32, 48, 32
            );
        }
    }


    // default button background is suppressed by not calling super.renderWidget

    @Override
    public void onPress() { /* no-op */ }

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
