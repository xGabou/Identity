package draylar.identity.screen;

import draylar.identity.network.impl.VillagerProfessionPackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class VillagerProfessionScreen extends Screen {

    private final Identifier professionId;
    private TextFieldWidget nameField;

    public VillagerProfessionScreen(Identifier professionId) {
        super(Text.translatable("identity.profession.title"));
        this.professionId = professionId;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;
        nameField = new TextFieldWidget(textRenderer, centerX - 100, centerY - 10, 200, 20, Text.empty());
        addSelectableChild(nameField);
        addDrawableChild(ButtonWidget.builder(Text.translatable("identity.profession.confirm"), button -> {
            VillagerProfessionPackets.sendSetProfession(professionId, nameField.getText(), false);
            close();
        }).dimensions(centerX - 100, centerY + 20, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("identity.profession.reset"), button -> {
            VillagerProfessionPackets.sendSetProfession(professionId, "", true);
            close();
        }).dimensions(centerX + 2, centerY + 20, 98, 20).build());
        setInitialFocus(nameField);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 30, 0xFFFFFF);
        nameField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(null);
    }
}
