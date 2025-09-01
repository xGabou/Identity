package draylar.identity.screen;

import draylar.identity.network.impl.VillagerProfessionPackets;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class VillagerProfessionScreen extends Screen {

    private final Identifier professionId;
    private final net.minecraft.util.math.BlockPos pos;
    private final Identifier worldId;
    private TextFieldWidget nameField;

    public VillagerProfessionScreen(Identifier professionId, net.minecraft.util.math.BlockPos pos, Identifier worldId) {
        super(Text.translatable("identity.profession.title"));
        this.professionId = professionId;
        this.pos = pos;
        this.worldId = worldId;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;
        nameField = new TextFieldWidget(textRenderer, centerX - 100, centerY - 10, 200, 20, Text.empty());
        addSelectableChild(nameField);
        addDrawableChild(ButtonWidget.builder(Text.translatable("identity.profession.confirm"), button -> {
            VillagerProfessionPackets.sendSetProfession(professionId, nameField.getText(), false, pos, worldId);
            close();
        }).dimensions(centerX - 100, centerY + 20, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("identity.profession.reset"), button -> {
            // Confirm before resetting
            MinecraftClient.getInstance().setScreen(new ConfirmScreen(confirmed -> {
                if (confirmed) {
                    VillagerProfessionPackets.sendSetProfession(professionId, nameField.getText(), true, pos, worldId);
                }
                MinecraftClient.getInstance().setScreen(null);
            }, Text.translatable("identity.profession.reset"), Text.literal("Are you sure you want to reset your current profession: " + professionId)));
        }).dimensions(centerX + 2, centerY + 20, 98, 20).build());
        setInitialFocus(nameField);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 30, 0xFFFFFF);
        nameField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(null);
    }
}