package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import game.utils.Resources;
import game.utils.Skins;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import shared.model.Spell;

public class SpellSlot extends ImageButton {

    static final int SIZE = 64;
    private static final float ICON_ALPHA = 0.5f;
    private static final Drawable selection = Skins.COMODORE_SKIN.getDrawable("slot-selected2");
    private final SpellView spellView;
    private final ClickListener clickListener;
    private Spell spell;
    private Tooltip<?> tooltip;

    SpellSlot(SpellView spellView, Spell spell) {
        super(Skins.COMODORE_SKIN, "icon-container");
        this.spellView = spellView;
        clickListener = new ClickListener() {

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                onClick();
            }
        };
        addListener(clickListener);
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
        if (spell == null) {
            return;
        }
        if (tooltip != null) {
            removeListener(tooltip);
        }
        tooltip = getTooltip(spell);
        addListener(tooltip);
    }

    @NotNull
    private Tooltip<?> getTooltip(Spell spell) {
        Actor content = createTooltipContent(spell);
        return new Tooltip<>(content);
    }

    @NotNull
    private Actor createTooltipContent(@NotNull Spell spell) {
        String name = spell.getName();
        String desc = spell.getDesc();
        int minhp = spell.getMinHP();
        int maxhp = spell.getMaxHP();
        int requiredMana = spell.getRequiredMana();
        int requiredSkills = spell.getMinSkill();

        Table table = WidgetFactory.createWindow();

        table.pad(0, 10, 10, 0);
        table.add //    LabelNombre
                (WidgetFactory.createTitleLabel(name))
                .left().pad(10, 15, 10, 10).row();
        table.add //    LabelSkills
                (WidgetFactory.createDescLabel("Requiere " + requiredSkills + " puntos de Magia."))
                .pad(0, 20, 0, 10).left().row();
        table.add //    LabelMana
                (WidgetFactory.createDescLabel("Requiere " + requiredMana + " puntos de Maná."))
                .pad(0, 20, 0, 10).left().row();
        table.add //    LabelDaño TODO Llamar daño base desde el character
                (WidgetFactory.createDescLabel("Inflinge entre " + minhp + " (+DañoBase)" + "/" + maxhp + " (+DañoBase)"))
                .pad(0, 20, 0, 10).left().row();
        table.add //    LabelDescripcion TODO hacer que el texto se ajuste a un tamaño fijo
                (WidgetFactory.createDescLabel(desc))
                .pad(10, 20, 0, 10).row();
        return table;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (spell == null) {
            return;
        }
        drawSpell(batch);
        spellView.selected.filter(sp -> sp.equals(spell)).ifPresent(sp -> drawSelection(batch));
    }

    private void drawSelection(Batch batch) {
        selection.draw(batch, getX(), getY(), SIZE, SIZE);
    }

    private void drawSpell(@NotNull Batch batch) {
        Texture graphic = getSpellIcon();
        Color current = new Color(batch.getColor());
        batch.setColor(current.r, current.g, current.b, ICON_ALPHA);
        batch.draw(graphic, getX() + 1, getY() + 1);
        batch.setColor(current);
    }

    private void onClick() {
        spellView.selected(spell);
    }

    @Override
    public boolean isOver() {
        return clickListener != null && clickListener.isOver();
    }

    @NotNull
    @Contract(" -> new")
    private Texture getSpellIcon() {
        return new Texture(Gdx.files.local(Resources.GAME_SPELL_ICONS_PATH + spell.getId() + ".png"));
    }
}
