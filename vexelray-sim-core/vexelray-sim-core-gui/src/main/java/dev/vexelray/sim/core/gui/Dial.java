package dev.vexelray.sim.core.gui;

import dev.vexelray.gui.core.Gui;
import dev.vexelray.gui.core.Node;
import dev.vexelray.gui.core.Timing;
import dev.vexelray.gui.core.layout.Length;
import dev.vexelray.gui.widget.Lattice;
import dev.vexelray.gui.widget.Property;
import dev.vexelray.gui.widget.Slider;
import java.util.function.DoubleFunction;

/**
 * A number on a slider, with the text beside it the application chooses.
 *
 * <p>The inspector's own range row writes the number in its own format, which has no room for a unit and no way to read
 * a speed of one eighth. This one takes the text from the caller, and can lay its track out in octaves, so that a value
 * that spans three orders of magnitude has the same travel at each end. On a log track the step is in octaves.
 *
 * <p>A {@link Property} as 0.2 has it: the value is told ({@link #show}), and what the user does leaves as a
 * {@code Double} through the row's {@link Property.Edits}, never back into the model from here. The slider is named by
 * the row's id, and its moves are heard as {@link Slider#CHANGED} at that address.
 */
public final class Dial implements Property {

    private final String id;
    private final String section;
    private final String name;
    private final double min;
    private final double max;
    private final double step;
    private final boolean log;
    private final DoubleFunction<String> text;
    private Slider slider;
    private Readout value;
    /** Kept, not just forwarded: the panel may be given its motion before this row is asked for a slider. */
    private Timing knob;

    private Dial(String id, String section, String name, double min, double max, double step, boolean log,
                 DoubleFunction<String> text) {
        this.id = id;
        this.section = section;
        this.name = name;
        this.min = min;
        this.max = max;
        this.step = step;
        this.log = log;
        this.text = text;
    }

    /** A linear track from {@code min} to {@code max}, snapped to {@code step}; at {@code min} until told. */
    public static Dial linear(String id, String section, String name, double min, double max, double step,
                              DoubleFunction<String> text) {
        return new Dial(id, section, name, min, max, step, false, text);
    }

    /** A track in octaves from {@code min} to {@code max}, snapped to {@code octaves} of them; at {@code min} until told. */
    public static Dial logarithmic(String id, String section, String name, double min, double max, double octaves,
                                   DoubleFunction<String> text) {
        return new Dial(id, section, name, min, max, octaves, true, text);
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
        this.value = value;
        // The step is in the track's own scale — octaves on a log track — so it is an even lattice of fractions
        // either way, and the slider draws it, holds the thumb on it and settles there.
        double span = scale(max) - scale(min);
        slider = new Slider(gui, 0f)
                .lattice(span > 0 && step > 0 ? Lattice.every(step / span) : Lattice.none())
                .transition(knob)
                .name(id);
        gui.mailbox(Slider.CHANGED, slider.address(), moved -> {
            double v = exact(at(moved.value()));
            edits.edited(v);
            value.show(text.apply(v));
        });
        value.show(text.apply(min));
        return slider.node().width(Length.grow(1f));
    }

    @Override
    public void show(Object told) {
        if (slider == null || !(told instanceof Number n)) {
            return;
        }
        double current = n.doubleValue();
        // show, not value: a row told its model is not the user dragging it.
        slider.show((float) fraction(current));
        value.show(text.apply(current));
    }

    private double fraction(double v) {
        double low = scale(min);
        double high = scale(max);
        return high <= low ? 0 : Math.clamp((scale(v) - low) / (high - low), 0, 1);
    }

    @Override
    public void motion(Timing timing) {
        this.knob = timing;
        if (slider != null) {
            slider.transition(timing);
        }
    }

    /** The value a fraction of the track names. */
    private double at(double fraction) {
        double low = scale(min);
        return unscale(low + fraction * (scale(max) - low));
    }

    /** The value a lattice point names, rounded back onto the step so the float dust of the fraction is gone. */
    private double exact(double v) {
        if (step <= 0) {
            return v;
        }
        double low = scale(min);
        double snapped = low + Math.round((scale(v) - low) / step) * step;
        return Math.clamp(unscale(snapped), min, max);
    }

    private double scale(double v) {
        return log ? Math.log(v) / Math.log(2) : v;
    }

    private double unscale(double s) {
        return log ? Math.pow(2, s) : s;
    }
}
