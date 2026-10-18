# TODO

- [ ] **Update all version numbers** in the root `pom.xml` to the siblings' current versions, which have moved on
      since: `vexelray.gui.version`, `vexelray.sim.core.version`, `vexelray.sim.fluid.version` and
      `vexelray.sim.rigid.version` are 0.1.0-SNAPSHOT (now 0.1.1-SNAPSHOT), and `supirvast.version` is
      0.2.0-SNAPSHOT (now 0.2.1-SNAPSHOT). Until then the build uses stale jars from `~/.m2`.
- [ ] **Namespaced buffers** in SupirVast's pass types (`dev.supirvast.vastir.pass`) and `PassRunner`, so two
      simulations' passes can be recorded into one sequence without their names colliding.
- [ ] **The boundary contract** in `vexelray-sim-core`: a solid as the fluid sees it, and the force and torque the
      fluid gives back. The fluid takes a boundary moved from outside, and the bodies take forces from outside.
- [ ] **One body in water**: a box that floats at the depth its density says, and settles. Judged against
      Archimedes, so it is a test before it is a picture.
- [ ] **Static SDF geometry both simulations meet**, such as an endless tunnel system: a tiled layout (3D Truchet
      tiles or a gyroid) carved with fractal detail (a Menger sponge, inversion fractals, layered noise).
- [ ] **The demo**: the pirate ship.
