package dev.vexelray.sim.core.gui;

import dev.vexelray.gui.core.Gui;
import dev.vexelray.gui.core.Node;
import dev.vexelray.gui.core.layout.Length;
import dev.vexelray.gui.widget.Property;
import dev.vexelray.gui.widget.Select;
import java.util.List;
import java.util.function.Function;

/**
 * One of several, behind a menu: the inspector's own choice row shows them all side by side, which eight will not fit.
 *
 * <p>A {@link Property} as 0.2 has it: the choice is told ({@link #show}), and one the user commits leaves as the
 * option's value through the row's {@link Property.Edits}. The select is named by the row's id, and its commits are
 * heard as {@link Select#COMMITTED} at that address.
 */
public final class Pick<T> implements Property {

    private final String id;
    private final String section;
    private final String name;
    private final List<T> options;
    private final Function<T, String> label;
    private Select<T> select;

    public Pick(String id, String section, String name, List<T> options, Function<T, String> label) {
        this.id = id;
        this.section = section;
        this.name = name;
        this.options = List.copyOf(options);
        this.label = label;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String section() {
        return section;
    }

    @Override
    public Node editor(Gui gui, Readout value, Edits edits) {
        select = new Select<>(gui, label).name(id);
        select.options(options);
        gui.mailbox(Select.COMMITTED, select.address(), committed -> {
            for (Object chosen : committed.chosen()) {
                edits.edited(chosen);
            }
        });
        return select.node().width(Length.grow(1f));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void show(Object told) {
        // show, not a commit: a told value is not an edit. One that is no option is ignored.
        if (select != null && !select.isOpen() && options.contains(told)) {
            select.show((T) told);
        }
    }
}
