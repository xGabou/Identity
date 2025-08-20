package draylar.identity.forge.config;

import draylar.identity.forge.IdentityForge;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class IdentityForgeConfigScreen extends Screen {
    private final Screen parent;
    private final List<LabelEntry> labels = new ArrayList<>();

    private TextFieldWidget hostilityTimeBox;
    private TextFieldWidget advancementsRequiredForFlightBox;
    private TextFieldWidget maxHealthBox;
    private TextFieldWidget allowedSwappersBox;
    private TextFieldWidget endermanTeleportBox;
    private TextFieldWidget flySpeedBox;
    private TextFieldWidget requiredKillsBox;
    private TextFieldWidget forcedIdentityBox;

    public IdentityForgeConfigScreen(Screen parent) {
        super(Text.literal("Identity Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        IdentityForgeConfig config = IdentityForge.CONFIG;
        int centerX = this.width / 2;
        int y = 20;

        List<Identifier> ids = Registries.ENTITY_TYPE.getIds()
                .stream()
                .sorted(Comparator.comparing(Identifier::toString))
                .collect(Collectors.toList());

        y = addEntitySection(centerX, y, "Extra Aquatic Entities", ids, config.extraAquaticEntities());
        y = addEntitySection(centerX, y, "Removed Aquatic Entities", ids, config.removedAquaticEntities());
        y = addEntitySection(centerX, y, "Extra Flying Entities", ids, config.extraFlyingEntities());
        y = addEntitySection(centerX, y, "Removed Flying Entities", ids, config.removedFlyingEntities());

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

        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> {
            saveChanges();
            if (client != null) {
                client.setScreen(parent);
            }
        }).dimensions(centerX - 100, this.height - 28, 200, 20).build());
    }

    private int addEntitySection(int centerX, int y, String label, List<Identifier> ids, List<String> target) {
        labels.add(new LabelEntry(label, centerX - 100, y));
        TextFieldWidget display = new TextFieldWidget(textRenderer, centerX - 100, y + 10, 200, 20, Text.empty());
        display.setText(String.join(",", target));
        display.setEditable(false);
        addDrawableChild(display);
        CyclingButtonWidget<Object> dropdown = addDrawableChild(
                CyclingButtonWidget.builder(id -> Text.literal(id.toString()))
                        .values(ids)
                        .build(centerX - 100, y + 40, 150, 20, Text.literal("Select"), (btn, value) -> {}));
        addDrawableChild(ButtonWidget.builder(Text.literal("Add"), btn -> {
            String idString = dropdown.getValue().toString();
            if (!target.contains(idString)) {
                target.add(idString);
                display.setText(String.join(",", target));
            }
        }).dimensions(centerX + 52, y + 40, 48, 20).build());
        return y + 70;
    }

    private int addBoolean(int centerX, int y, String label, boolean initial, Consumer<Boolean> setter) {
        addDrawableChild(CyclingButtonWidget.onOffBuilder(initial)
                .build(centerX - 100, y, 200, 20, Text.literal(label), (btn, value) -> setter.accept(value)));
        return y + 24;
    }

    private TextFieldWidget addIntField(int centerX, int y, String label, int value) {
        labels.add(new LabelEntry(label, centerX - 100, y));
        TextFieldWidget box = new TextFieldWidget(textRenderer, centerX - 100, y + 10, 200, 20, Text.empty());
        box.setText(Integer.toString(value));
        box.setTextPredicate(s -> s.matches("-?\\d*"));
        addDrawableChild(box);
        return box;
    }

    private TextFieldWidget addFloatField(int centerX, int y, String label, float value) {
        labels.add(new LabelEntry(label, centerX - 100, y));
        TextFieldWidget box = new TextFieldWidget(textRenderer, centerX - 100, y + 10, 200, 20, Text.empty());
        box.setText(Float.toString(value));
        addDrawableChild(box);
        return box;
    }

    private TextFieldWidget addStringField(int centerX, int y, String label, String value) {
        labels.add(new LabelEntry(label, centerX - 100, y));
        TextFieldWidget box = new TextFieldWidget(textRenderer, centerX - 100, y + 10, 200, 20, Text.empty());
        box.setText(value);
        addDrawableChild(box);
        return box;
    }

    private TextFieldWidget addListField(int centerX, int y, String label, List<String> list) {
        labels.add(new LabelEntry(label, centerX - 100, y));
        TextFieldWidget box = new TextFieldWidget(textRenderer, centerX - 100, y + 10, 200, 20, Text.empty());
        box.setText(String.join(",", list));
        addDrawableChild(box);
        return box;
    }

    private void saveChanges() {
        IdentityForgeConfig config = IdentityForge.CONFIG;
        config.hostilityTime = parseInt(hostilityTimeBox.getText(), config.hostilityTime);
        config.advancementsRequiredForFlight().clear();
        config.advancementsRequiredForFlight().addAll(split(advancementsRequiredForFlightBox.getText()));
        config.maxHealth = parseInt(maxHealthBox.getText(), config.maxHealth);
        config.allowedSwappers().clear();
        config.allowedSwappers().addAll(split(allowedSwappersBox.getText()));
        config.endermanAbilityTeleportDistance = parseInt(endermanTeleportBox.getText(), config.endermanAbilityTeleportDistance);
        config.flySpeed = parseFloat(flySpeedBox.getText(), config.flySpeed);
        config.requiredKillsForIdentity = parseInt(requiredKillsBox.getText(), config.requiredKillsForIdentity);
        config.forcedIdentity = forcedIdentityBox.getText().isEmpty() ? null : forcedIdentityBox.getText();
        ConfigLoader.save(config);
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private float parseFloat(String s, float def) {
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private List<String> split(String text) {
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        for (LabelEntry label : labels) {
            context.drawText(textRenderer, Text.literal(label.text), label.x, label.y, 0xFFFFFF, false);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }

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
}
