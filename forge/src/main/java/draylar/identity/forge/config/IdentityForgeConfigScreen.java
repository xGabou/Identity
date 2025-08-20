package draylar.identity.forge.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import draylar.identity.forge.IdentityForge;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class IdentityForgeConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget extraAquaticBox;
    private TextFieldWidget removedAquaticBox;
    private TextFieldWidget extraFlyingBox;
    private TextFieldWidget removedFlyingBox;

    public IdentityForgeConfigScreen(Screen parent) {
        super(Text.literal("Identity Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        IdentityForgeConfig config = IdentityForge.CONFIG;
        int centerX = this.width / 2;
        int y = 40;

        extraAquaticBox = new TextFieldWidget(textRenderer, centerX - 100, y, 200, 20, Text.empty());
        extraAquaticBox.setText(String.join(",", config.extraAquaticEntities()));
        addDrawableChild(extraAquaticBox);
        y += 30;

        removedAquaticBox = new TextFieldWidget(textRenderer, centerX - 100, y, 200, 20, Text.empty());
        removedAquaticBox.setText(String.join(",", config.removedAquaticEntities()));
        addDrawableChild(removedAquaticBox);
        y += 30;

        extraFlyingBox = new TextFieldWidget(textRenderer, centerX - 100, y, 200, 20, Text.empty());
        extraFlyingBox.setText(String.join(",", config.extraFlyingEntities()));
        addDrawableChild(extraFlyingBox);
        y += 30;

        removedFlyingBox = new TextFieldWidget(textRenderer, centerX - 100, y, 200, 20, Text.empty());
        removedFlyingBox.setText(String.join(",", config.removedFlyingEntities()));
        addDrawableChild(removedFlyingBox);

        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> {
            saveChanges();
            if (client != null) {
                client.setScreen(parent);
            }
        }).dimensions(centerX - 100, this.height - 28, 200, 20).build());
    }

    private void saveChanges() {
        IdentityForgeConfig config = IdentityForge.CONFIG;
        config.extraAquaticEntities().clear();
        config.extraAquaticEntities().addAll(split(extraAquaticBox.getText()));

        config.removedAquaticEntities().clear();
        config.removedAquaticEntities().addAll(split(removedAquaticBox.getText()));

        config.extraFlyingEntities().clear();
        config.extraFlyingEntities().addAll(split(extraFlyingBox.getText()));

        config.removedFlyingEntities().clear();
        config.removedFlyingEntities().addAll(split(removedFlyingBox.getText()));

        ConfigLoader.save(config);
    }

    private List<String> split(String text) {
        return Arrays.stream(text.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        int centerX = this.width / 2;
        context.drawText(textRenderer, Text.literal("Extra Aquatic Entities"), centerX - 100, 30, 0xFFFFFF, false);
        context.drawText(textRenderer, Text.literal("Removed Aquatic Entities"), centerX - 100, 60, 0xFFFFFF, false);
        context.drawText(textRenderer, Text.literal("Extra Flying Entities"), centerX - 100, 90, 0xFFFFFF, false);
        context.drawText(textRenderer, Text.literal("Removed Flying Entities"), centerX - 100, 120, 0xFFFFFF, false);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }
}

