# TODO

- [ ] **The port to 0.2 waits on a GPU route** that 0.2 does not have yet: vexelray-gui's
      [device-feed.md](../../vexelray-gui/docs/plans/device-feed.md) (steps 2 and 4; the port is its step 5). The stack is
      at `0.2.0-SNAPSHOT` since 2026-11-14, and **the two demos are out of the default build until then**: the root
      pom's `demos` profile holds them, `mvn install -Pdemos` builds them (and fails), and moving them back into
      `vexelray-sim-fluid`'s and `vexelray-sim-rigid`'s `<modules>` is part of the port. Everything but the two demos
      compiles; the demos fail on `@MainThread`/`@BeforeFrame` drawing, the `GuiApp`, a raw
      `Atchung`, `Topic.of`, and `ShownRing`, `PhysicsNews` and the `Dilated` clock shared through `@Provides`
      (checked 2026-11-12). The physics becomes a `@Source`, the views techniques, `ShownRing`/`KeptSlots`/`AppCompute`
      a device feed and a lent queue. sim-core-gui's `Dial` and `Pick` are already 0.2 `Property` rows (an id, told values,
      edits through `Edits`), so the panels that made them with a getter and a setter tell them values and hear
      `Inspector.EDITED` instead; `OrbitControl` and `FluidView3` take the wheel through `Seams.onWheel`.
- [ ] **Namespaced buffers** in SupirVast's pass types (`dev.supirvast.vastir.pass`) and `PassRunner`, so two
      simulations' passes can be recorded into one sequence without their names colliding.
- [ ] **The boundary contract** in `vexelray-sim-core`: a solid as the fluid sees it, and the force and torque the
      fluid gives back. The fluid takes a boundary moved from outside, and the bodies take forces from outside.
- [ ] **One body in water**: a box that floats at the depth its density says, and settles. Judged against
      Archimedes, so it is a test before it is a picture.
- [ ] **Static SDF geometry both simulations meet**, such as an endless tunnel system: a tiled layout (3D Truchet
      tiles or a gyroid) carved with fractal detail (a Menger sponge, inversion fractals, layered noise).
- [ ] **The demo**: the pirate ship.
