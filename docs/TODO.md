# TODO

- [ ] **The port to 0.2 waits on a GPU route** that 0.2 does not have yet: vexelray-gui's
      [device-feed.md](../../vexelray-gui/docs/plans/device-feed.md). With the stack at `0.2.0-SNAPSHOT`, everything
      but the two demos compiles; the demos fail on `@MainThread`/`@BeforeFrame` drawing, the `GuiApp`, a raw
      `Atchung`, `Topic.of`, and `ShownRing`, `PhysicsNews` and the `Dilated` clock shared through `@Provides`
      (checked 2026-11-12). The physics becomes a `@Source`, the views techniques, `ShownRing`/`KeptSlots`/`AppCompute`
      a device feed and a lent queue.
- [ ] **Namespaced buffers** in SupirVast's pass types (`dev.supirvast.vastir.pass`) and `PassRunner`, so two
      simulations' passes can be recorded into one sequence without their names colliding.
- [ ] **The boundary contract** in `vexelray-sim-core`: a solid as the fluid sees it, and the force and torque the
      fluid gives back. The fluid takes a boundary moved from outside, and the bodies take forces from outside.
- [ ] **One body in water**: a box that floats at the depth its density says, and settles. Judged against
      Archimedes, so it is a test before it is a picture.
- [ ] **Static SDF geometry both simulations meet**, such as an endless tunnel system: a tiled layout (3D Truchet
      tiles or a gyroid) carved with fractal detail (a Menger sponge, inversion fractals, layered noise).
- [ ] **The demo**: the pirate ship.
