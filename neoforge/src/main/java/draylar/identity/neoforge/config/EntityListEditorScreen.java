package draylar.identity.neoforge.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EntityListEditorScreen extends Screen {
    private final Screen parent;
    private final List<String> model; // reference to target list
    private final Consumer<Void> onChanged;

    private EntryList list;

    public EntityListEditorScreen(Screen parent, List<String> target, Runnable onChanged) {
        super(Text.literal("Manage Entities"));
        this.parent = parent;
        this.model = target;
        this.onChanged = v -> { onChanged.run();};
    }

    @Override
    protected void init() {
        int cx = width / 2;

        list = new EntryList(client, width, height - 80, 40, height - 60, 20);
        addSelectableChild(list);

        // load current items
        for (String s : new ArrayList<>(model)) list.addRow(list.new Entry(s));

        addDrawableChild(ButtonWidget.builder(Text.literal("Remove Selected"), b -> {
            EntryList.Entry sel = list.getSelectedOrNull();
            if (sel != null) {
                model.remove(sel.value);
                list.removeSelected();
                onChanged.accept(null);
            }
        }).dimensions(cx - 150, height - 30, 140, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Remove All"), b -> {
            model.clear();
            list.clearAll();
            onChanged.accept(null);
        }).dimensions(cx - 5, height - 30, 120, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> client.setScreen(parent))
                .dimensions(cx + 120, height - 30, 80, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        list.render(ctx, mouseX, mouseY, delta);
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFF);
    }

    static class EntryList extends AlwaysSelectedEntryListWidget<EntryList.Entry> {
        EntryList(MinecraftClient client, int width, int height, int top, int itemHeight, int i) {
            super(client, width, height, top, itemHeight);
        }
        public void addRow(Entry e) { super.addEntry(e); }
        public void clearAll() { super.clearEntries(); }
        public void removeSelected() {
            Entry sel = getSelectedOrNull();
            if (sel != null) removeEntry(sel);
        }
        class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            final String value;
            Entry(String v) { this.value = v; }
            @Override
            public void render(DrawContext c, int idx, int y, int x, int w, int h,
                               int mx, int my, boolean hov, float d) {
                c.drawText(client.textRenderer, value, x + 4, y + 6, 0xFFFFFF, false);
            }
            @Override
            public boolean mouseClicked(double mx, double my, int b) {
                EntryList.this.setSelected(this);
                return true;
            }
            @Override
            public Text getNarration() { return Text.literal(value); }
        }
    }
}
