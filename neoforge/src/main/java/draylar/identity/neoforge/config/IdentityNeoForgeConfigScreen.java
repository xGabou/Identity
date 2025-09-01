package draylar.identity.neoforge.config;

import draylar.identity.neoforge.IdentityNeoForge;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Forge-side config screen for Identity (hand-rolled UI).
 * - Uses a scrollable viewport with scissor clipping so content never draws over the bottom bar.
 * - Entity pickers open a searchable list (EntityPickerScreen) and append to the target list.
 */
public class IdentityNeoForgeConfigScreen extends Screen {

    private final Screen parent;

    /** logical labels that we render ourselves (so we can clip them) */
    private final List<LabelEntry> labels = new ArrayList<>();
    /** content widgets we render/position manually (so we can clip them) */
    private final List<WidgetEntry> widgets = new ArrayList<>();

    /** total content height after laying out */
    private int contentHeight;
    /** current scroll offset (pixels) */
    private int scroll;

    // viewport (clipping) for scrollable area
    private int viewportTop = 20;
    private int viewportBottom;

    // inputs
    private TextFieldWidget hostilityTimeBox;
    private TextFieldWidget advancementsRequiredForFlightBox;
    private TextFieldWidget maxHealthBox;
    private TextFieldWidget allowedSwappersBox;
    private TextFieldWidget endermanTeleportBox;
    private TextFieldWidget flySpeedBox;
    private TextFieldWidget requiredKillsBox;
    private TextFieldWidget forcedIdentityBox;

    // fixed bottom widgets
    private ButtonWidget doneButton;

    public IdentityNeoForgeConfigScreen(Screen parent) {
        super(Text.literal("Identity Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Clear previous state on resize/reopen
        this.clearChildren();
        this.labels.clear();
        this.widgets.clear();
        this.scroll = 0;

        IdentityNeoForgeConfig config = IdentityNeoForge.CONFIG;

        int centerX = this.width / 2;
        viewportTop = 20;
        viewportBottom = this.height - 40; // bottom bar reserved area

        // sorted list of all entity ids (for pickers)
        List<Identifier> ids = Registries.ENTITY_TYPE.getIds()
                .stream()
                .sorted(Comparator.comparing(Identifier::toString))
                .collect(Collectors.toList());

        int y = viewportTop;

        // Sections with entity list pickers
        y = addEntitySection(centerX, y, "Extra Aquatic Entities", ids, config.extraAquaticEntities());
        y = addEntitySection(centerX, y, "Removed Aquatic Entities", ids, config.removedAquaticEntities());
        y = addEntitySection(centerX, y, "Extra Flying Entities", ids, config.extraFlyingEntities());
        y = addEntitySection(centerX, y, "Removed Flying Entities", ids, config.removedFlyingEntities());

        // Toggles
        y = addBoolean(centerX, y, "Overlay Identity Unlocks", config.overlayIdentityUnlocks, v -> config.overlayIdentityUnlocks = v);
        y = addBoolean(centerX, y, "Overlay Identity Revokes", config.overlayIdentityRevokes, v -> config.overlayIdentityRevokes = v);
        y = addBoolean(centerX, y, "Revoke Identity On Death", config.revokeIdentityOnDeath, v -> config.revokeIdentityOnDeath = v);
        y = addBoolean(centerX, y, "Identities Equip Items", config.identitiesEquipItems, v -> config.identitiesEquipItems = v);
        y = addBoolean(centerX, y, "Identities Equip Armor", config.identitiesEquipArmor, v -> config.identitiesEquipArmor = v);
        y = addBoolean(centerX, y, "Render Own Name Tag", config.renderOwnNameTag, v -> config.renderOwnNameTag = v);
        y = addBoolean(centerX, y, "Hostiles Ignore Hostile Identity Player", config.hostilesIgnoreHostileIdentityPlayer, v -> config.hostilesIgnoreHostileIdentityPlayer = v);
        y = addBoolean(centerX, y, "Hostiles Forget New Hostile Identity Player", config.hostilesForgetNewHostileIdentityPlayer, v -> config.hostilesForgetNewHostileIdentityPlayer = v);
        y = addBoolean(centerX, y, "Wolves Attack Identity Prey", config.wolvesAttackIdentityPrey, v -> config.wolvesAttackIdentityPrey = v);
        y = addBoolean(centerX, y, "Owned Wolves Attack Identity Prey", config.ownedWolvesAttackIdentityPrey, v -> config.ownedWolvesAttackIdentityPrey = v);
        y = addBoolean(centerX, y, "Villagers Run From Identities", config.villagersRunFromIdentities, v -> config.villagersRunFromIdentities = v);
        y = addBoolean(centerX, y, "Foxes Attack Identity Prey", config.foxesAttackIdentityPrey, v -> config.foxesAttackIdentityPrey = v);
        y = addBoolean(centerX, y, "Use Identity Sounds", config.useIdentitySounds, v -> config.useIdentitySounds = v);
        y = addBoolean(centerX, y, "Play Ambient Sounds", config.playAmbientSounds, v -> config.playAmbientSounds = v);
        y = addBoolean(centerX, y, "Hear Self Ambient", config.hearSelfAmbient, v -> config.hearSelfAmbient = v);
        y = addBoolean(centerX, y, "Enable Flight", config.enableFlight, v -> config.enableFlight = v);
        y = addBoolean(centerX, y, "Scaling Health", config.scalingHealth, v -> config.scalingHealth = v);
        y = addBoolean(centerX, y, "Enable Client Swap Menu", config.enableClientSwapMenu, v -> config.enableClientSwapMenu = v);
        y = addBoolean(centerX, y, "Enable Swaps", config.enableSwaps, v -> config.enableSwaps = v);
        y = addBoolean(centerX, y, "Show Player Nametag", config.showPlayerNametag, v -> config.showPlayerNametag = v);
        y = addBoolean(centerX, y, "Force Change New", config.forceChangeNew, v -> config.forceChangeNew = v);
        y = addBoolean(centerX, y, "Force Change Always", config.forceChangeAlways, v -> config.forceChangeAlways = v);
        y = addBoolean(centerX, y, "Log Commands", config.logCommands, v -> config.logCommands = v);
        y = addBoolean(centerX, y, "Kill For Identity", config.killForIdentity, v -> config.killForIdentity = v);
        y = addBoolean(centerX, y, "Warden Is Blinded", config.wardenIsBlinded, v -> config.wardenIsBlinded = v);
        y = addBoolean(centerX, y, "Warden Blinds Nearby", config.wardenBlindsNearby, v -> config.wardenBlindsNearby = v);

        // Inputs
        hostilityTimeBox = addIntField(centerX, y, "Hostility Time", config.hostilityTime);
        y += 40;
        advancementsRequiredForFlightBox = addListField(centerX, y, "Advancements Required For Flight", config.advancementsRequiredForFlight());
        y += 40;
        maxHealthBox = addIntField(centerX, y, "Max Health", config.maxHealth);
        y += 40;
        allowedSwappersBox = addListField(centerX, y, "Allowed Swappers", config.allowedSwappers());
        y += 40;
        endermanTeleportBox = addIntField(centerX, y, "Enderman Teleport Distance", config.endermanAbilityTeleportDistance);
        y += 40;
        flySpeedBox = addFloatField(centerX, y, "Fly Speed", config.flySpeed);
        y += 40;
        requiredKillsBox = addIntField(centerX, y, "Required Kills For Identity", config.requiredKillsForIdentity);
        y += 40;
        forcedIdentityBox = addStringField(centerX, y, "Forced Identity", config.forcedIdentity == null ? "" : config.forcedIdentity);
        y += 40;

        contentHeight = y;
        updateWidgetPositions();

        // Fixed bottom bar: Done button (rendered after scissor)
        doneButton = ButtonWidget.builder(Text.translatable("gui.done"), button -> {
            saveChanges();
            if (client != null) client.setScreen(parent);
        }).dimensions(centerX - 100, this.height - 28, 200, 20).build();
        addDrawableChild(doneButton);
    }

    /** Adds an entity picker row: label + readonly display + "Select…" button that opens EntityPickerScreen. */
    private int addEntitySection(int centerX, int y, String label, List<Identifier> ids, List<String> target) {
        labels.add(new LabelEntry(label, centerX - 100, y));

        TextFieldWidget display = new TextFieldWidget(textRenderer, centerX - 100, y + 10, 200, 20, Text.empty());
        display.setText(String.join(",", target));
        display.setEditable(false);
        addEntry(display, y + 10);

        // Select (add)
        addEntry(ButtonWidget.builder(Text.literal("Select…"), b -> {
            if (client == null) return;
            client.setScreen(new EntityPickerScreen(this, picked -> {
                String s = picked.toString();
                if (!target.contains(s)) {
                    target.add(s);
                    display.setText(String.join(",", target));
                }
            }));
        }).dimensions(centerX - 100, y + 40, 98, 20).build(), y + 40);

        // Manage (remove)
        addEntry(ButtonWidget.builder(Text.literal("Manage…"), b -> {
            if (client == null) return;
            client.setScreen(new EntityListEditorScreen(this, target,
                    () -> display.setText(String.join(",", target))));
        }).dimensions(centerX + 2, y + 40, 98, 20).build(), y + 40);

        return y + 70;
    }


    private int addBoolean(int centerX, int y, String label, boolean initial, Consumer<Boolean> setter) {
        ButtonWidget toggle = ButtonWidget.builder(Text.literal(label + ": " + (initial ? "ON" : "OFF")), btn -> {
            boolean newVal = !labelTextIsOn(btn.getMessage().getString());
            setter.accept(newVal);
            btn.setMessage(Text.literal(label + ": " + (newVal ? "ON" : "OFF")));
        }).dimensions(centerX - 100, y, 200, 20).build();
        addEntry(toggle, y);
        return y + 24;
    }

    private static boolean labelTextIsOn(String s) {
        int i = s.lastIndexOf(':');
        if (i < 0 || i + 2 >= s.length()) return false;
        return s.substring(i + 2).equalsIgnoreCase("ON");
    }

    private TextFieldWidget addIntField(int centerX, int y, String label, int value) {
        labels.add(new LabelEntry(label, centerX - 100, y));
        TextFieldWidget box = new TextFieldWidget(textRenderer, centerX - 100, y + 10, 200, 20, Text.empty());
        box.setText(Integer.toString(value));
        box.setTextPredicate(s -> s.matches("-?\\d*"));
        return addEntry(box, y + 10);
    }

    private TextFieldWidget addFloatField(int centerX, int y, String label, float value) {
        labels.add(new LabelEntry(label, centerX - 100, y));
        TextFieldWidget box = new TextFieldWidget(textRenderer, centerX - 100, y + 10, 200, 20, Text.empty());
        box.setText(Float.toString(value));
        return addEntry(box, y + 10);
    }

    private TextFieldWidget addStringField(int centerX, int y, String label, String value) {
        labels.add(new LabelEntry(label, centerX - 100, y));
        TextFieldWidget box = new TextFieldWidget(textRenderer, centerX - 100, y + 10, 200, 20, Text.empty());
        box.setText(value);
        return addEntry(box, y + 10);
    }

    private TextFieldWidget addListField(int centerX, int y, String label, List<String> list) {
        labels.add(new LabelEntry(label, centerX - 100, y));
        TextFieldWidget box = new TextFieldWidget(textRenderer, centerX - 100, y + 10, 200, 20, Text.empty());
        box.setText(String.join(",", list));
        return addEntry(box, y + 10);
    }

    /** Track a content widget and add as child so it still receives input. */
    private <T extends ClickableWidget> T addEntry(T widget, int y) {
        widgets.add(new WidgetEntry(widget, y));
        return addDrawableChild(widget);
    }

    /** Apply scroll to content widgets. */
    private void updateWidgetPositions() {
        for (WidgetEntry entry : widgets) {
            entry.widget.setY(entry.baseY - scroll);
        }
    }

    private void saveChanges() {
        IdentityNeoForgeConfig config = IdentityNeoForge.CONFIG;
        config.hostilityTime = parseInt(hostilityTimeBox.getText(), config.hostilityTime);

        config.advancementsRequiredForFlight().clear();
        splitAndAdd(advancementsRequiredForFlightBox.getText(), config.advancementsRequiredForFlight());

        config.maxHealth = parseInt(maxHealthBox.getText(), config.maxHealth);

        config.allowedSwappers().clear();
        splitAndAdd(allowedSwappersBox.getText(), config.allowedSwappers());

        config.endermanAbilityTeleportDistance = parseInt(endermanTeleportBox.getText(), config.endermanAbilityTeleportDistance);
        config.flySpeed = parseFloat(flySpeedBox.getText(), config.flySpeed);
        config.requiredKillsForIdentity = parseInt(requiredKillsBox.getText(), config.requiredKillsForIdentity);
        config.forcedIdentity = forcedIdentityBox.getText().isEmpty() ? null : forcedIdentityBox.getText();

        ConfigLoader.save(config);
    }

    private static void splitAndAdd(String csv, List<String> out) {
        if (csv == null || csv.isEmpty()) return;
        for (String s : csv.split(",")) {
            String t = s.trim();
            if (!t.isEmpty()) out.add(t);
        }
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception ignored) {
            return def;
        }
    }

    private float parseFloat(String s, float def) {
        try {
            return Float.parseFloat(s.trim());
        } catch (Exception ignored) {
            return def;
        }
    }

    // ---------- Rendering & Input (with clipping) ----------

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Background (use renderBackgroundTexture(ctx) if you want dirt everywhere)
        this.renderBackground(ctx, mouseX, mouseY, delta);

        // Clip scrollable content so it never draws over bottom bar
        ctx.enableScissor(0, viewportTop, this.width, viewportBottom);

        // Render labels inside viewport
        for (LabelEntry label : labels) {
            int ly = label.y - scroll;
            if (ly + 10 > viewportTop && ly < viewportBottom) {
                ctx.drawText(textRenderer, Text.literal(label.text), label.x, ly, 0xFFFFFF, false);
            }
        }

        // Render content widgets inside viewport
        for (WidgetEntry entry : widgets) {
            int wy = entry.widget.getY();
            if (wy + entry.widget.getHeight() > viewportTop && wy < viewportBottom) {
                entry.widget.render(ctx, mouseX, mouseY, delta);
            }
        }

        ctx.disableScissor();

        // Render fixed bottom widgets (outside scissor)
        if (doneButton != null) {
            doneButton.render(ctx, mouseX, mouseY, delta);
        }

        // Optional title
        // ctx.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, 6, 0xFFFFFF);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount,double verticalAmount) {
        // Scroll only when cursor is inside the viewport
        if (mouseY >= viewportTop && mouseY <= viewportBottom) {
            int maxScroll = Math.max(contentHeight - (viewportBottom - viewportTop), 0);
            scroll = MathHelper.clamp(scroll - (int) (horizontalAmount * 20), 0, maxScroll);
            updateWidgetPositions();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount,verticalAmount);
    }

    // Prevent off-viewport content from intercepting clicks (so the Done button works)
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseY >= viewportTop && mouseY <= viewportBottom) {
            return super.mouseClicked(mouseX, mouseY, button);
        } else {
            return doneButton != null && doneButton.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (mouseY >= viewportTop && mouseY <= viewportBottom) {
            return super.mouseReleased(mouseX, mouseY, button);
        } else {
            return doneButton != null && doneButton.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (mouseY >= viewportTop && mouseY <= viewportBottom) {
            return super.mouseDragged(mouseX, mouseY, button, dx, dy);
        }
        return false;
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        // Pause while configuring (optional; set false if you prefer live world)
        return true;
    }

    // ---------- Helpers ----------

    private static class LabelEntry {
        final String text;
        final int x;
        final int y;
        LabelEntry(String text, int x, int y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }
    }

    private static class WidgetEntry {
        final ClickableWidget widget;
        final int baseY;
        WidgetEntry(ClickableWidget widget, int baseY) {
            this.widget = widget;
            this.baseY = baseY;
        }
    }
}
