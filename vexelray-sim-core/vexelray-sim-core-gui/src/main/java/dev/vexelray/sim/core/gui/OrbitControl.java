package dev.vexelray.sim.core.gui;

import dev.vexelray.gui.core.Gui;
import dev.vexelray.gui.core.Node;
import dev.vexelray.gui.core.input.DragEvent;
import dev.vexelray.gui.core.input.WheelEvent;
import dev.vexelray.gui.core.spi.Seams;

/**
 * An {@link Orbit} turned by dragging in a node and zoomed by the wheel over it.
 *
 * <p>Widget code, so it reaches the frame through {@link Seams} (vexelray-gui's {@code seams.md}): {@code onDrag}
 * for the turn, and {@code onWheel}, which hands this node the wheel over it instead of scrolling what it is inside.
 * The pointer is held for a drag and warped back, because turning is a displacement and the pointer would otherwise
 * leave the node a quarter of the way round. A touchpad's two-finger scroll arrives in small fractions of a notch,
 * which {@link Orbit#zoom} takes as they come.
 *
 * <p>Taken out of vexelray-sim-fluid's 3D view when the rigid demo needed the same camera.
 */
public final class OrbitControl implements AutoCloseable {

    /** A drag across the whole node turns the camera this far, in radians: half a turn across, a quarter down. */
    public static final double DRAG_YAW = Math.PI;
    public static final double DRAG_PITCH = Math.PI / 2;

    /**
     * Pixels in a wheel notch, as vexelray-gui's dispatcher hands the wheel to a node that takes it: three ems, down
     * positive. The dispatcher scales by the node's own em; this takes the default em of 16 pixels, so a node in a
     * larger type zooms a little faster a notch.
     */
    static final float NOTCH_PX = 3f * 16f;

    private final Orbit orbit;
    private final Node node;
    private final Seams seams;

    /** Whether the node is showing what this camera looks at: the wheel over it is this control's only then. */
    private volatile boolean active = true;

    /** Wires {@code orbit} to drags in {@code node} and the wheel over it. GUI thread, as building a widget is. */
    public OrbitControl(Gui gui, Node node, Orbit orbit) {
        this.orbit = orbit;
        this.node = node;
        this.seams = Seams.of(gui);
        seams.onDrag(node, this::drag);
        gui.dragLocksPointer(node, true);
        seams.onWheel(node, wheel -> wheel(gui, wheel));
    }

    /** The camera this moves. */
    public Orbit orbit() {
        return orbit;
    }

    /** Whether the wheel over the node is this control's; a node showing something else sets it false. */
    public void active(boolean active) {
        this.active = active;
    }

    private void drag(DragEvent e) {
        if (e.phase() != DragEvent.Phase.MOVE) {
            return;                                   // letting go is not a movement
        }
        orbit.turn(e.dx() / Math.max(1f, e.nodeW()) * DRAG_YAW, e.dy() / Math.max(1f, e.nodeH()) * DRAG_PITCH);
    }

    private void wheel(Gui gui, WheelEvent e) {
        if (e.dy() != 0 && active) {
            orbit.zoom(-e.dy() / NOTCH_PX);           // notches, up positive, as the device reported them
            gui.wakeForInput();                       // a paused simulation owes no frame, and the picture has moved
        }
    }

    /** Stops answering the wheel, and gives it back to whatever the node is inside. The drag handler goes with the node. */
    @Override
    public void close() {
        active = false;
        seams.onWheel(node, null);
    }
}
