package draylar.identity.forge.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EntityPickerScreen extends Screen {

    private final Screen parent;
    private final Consumer<Identifier> onPick;
    private TextFieldWidget searchBox;
    private EntityList list;

    public EntityPickerScreen(Screen parent, Consumer<Identifier> onPick) {
        super(Text.literal("Select Entity"));
        this.parent = parent;
        this.onPick = onPick;
    }

    @Override
    protected void init() {
        int cx = width / 2;


        searchBox = new TextFieldWidget(textRenderer, cx - 150, 20, 300, 20, Text.empty());
        searchBox.setPlaceholder(Text.literal("Search entities…"));
        searchBox.setChangedListener(s -> refresh());
        addSelectableChild(searchBox);
        list = new EntityList(client, width, height - 80, 50, height - 60, 20);
        list.setRenderBackground(false);
        list.setRenderHorizontalShadows(false);
        addSelectableChild(list);

        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"),
                b -> client.setScreen(parent)).dimensions(cx - 150, height - 30, 120, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Add Selected"), b -> {
            EntityList.Entry sel = list.getSelectedEntry();
            if (sel != null) {
                onPick.accept(sel.id);
                client.setScreen(parent);
            }
        }).dimensions(cx + 30, height - 30, 120, 20).build());

        refresh();
    }

    private void refresh() {
        String q = searchBox.getText().toLowerCase(Locale.ROOT);
        List<Identifier> ids = Registries.ENTITY_TYPE.getIds().stream()
                .sorted(Comparator.comparing(Identifier::toString))
                .filter(id -> {
                    if (q.isEmpty()) return true;
                    EntityType<?> type = Registries.ENTITY_TYPE.get(id);
                    String name = type == null ? "" : Text.translatable(type.getTranslationKey()).getString();
                    String s = id.toString().toLowerCase(Locale.ROOT);
                    return s.contains(q) || name.toLowerCase(Locale.ROOT).contains(q);
                })
                .collect(Collectors.toList());

        list.clearAll();
        for (Identifier id : ids) list.addRow(list.new Entry(id));
    }
    @Override
    public boolean shouldPause() {
        return true;
    }
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx);
        list.render(ctx, mouseX, mouseY, delta);
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 6, 0xFFFFFF);
    }

    // ---- List widget with public wrappers ----
    static class EntityList extends AlwaysSelectedEntryListWidget<EntityList.Entry> {
        EntityList(MinecraftClient mc, int w, int h, int top, int bottom, int itemH) {
            super(mc, w, h, top, bottom, itemH);
        }

        // public wrappers around protected methods
        public void addRow(Entry e) { super.addEntry(e); }
        public void clearAll() { super.clearEntries(); }
        public Entry getSelectedEntry() { return super.getSelectedOrNull(); }
        public void select(Entry e) { super.setSelected(e); }

        // non-static so it can access parent list (no getList() needed)
        class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            final Identifier id;
            Entry(Identifier id) { this.id = id; }

            @Override
            public void render(DrawContext ctx, int idx, int y, int x, int w, int h,
                               int mouseX, int mouseY, boolean hovered, float delta) {
                EntityType<?> type = Registries.ENTITY_TYPE.get(id);
                String nice = type == null ? id.toString()
                        : Text.translatable(type.getTranslationKey()).getString() + " (" + id + ")";
                ctx.drawText(MinecraftClient.getInstance().textRenderer, nice, x + 4, y + 6, 0xFFFFFF, false);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                // select on click
                EntityList.this.select(this);
                return true;
            }

            @Override
            public Text getNarration() {
                // Minimal narration text
                return Text.literal(id.toString());
            }

        }

    }
}
