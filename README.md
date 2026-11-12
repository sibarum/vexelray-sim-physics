# vexelray-sim-physics

Physics for VexelRay, and **the front door to its simulations**. An application that wants physics depends on this.

It holds every simulation on the stack, each in a folder of its own, and the coupling between them: bodies feel
buoyancy and drag, and the fluid feels the bodies as moving boundaries, both in one step. The simulations are written
in SupirVast IR, so one source runs on the GPU and the CPU.

| Folder | What it is |
| --- | --- |
| [vexelray-sim-core](vexelray-sim-core) | What every simulation shares: the kernel body, a step as data, the clock; on the stack, the lent compute queue, the ring of finished steps, the camera and the panel's rows. Infrastructure, never technique. |
| [vexelray-sim-fluid](vexelray-sim-fluid) | The fluid solver and its experiments: shallow water, FLIP, two-fluid MLS-MPM in 2D and 3D, heat, surface tension, slime. Its demo is **Fluid Sim**. |
| [vexelray-sim-rigid](vexelray-sim-rigid) | The rigid-body solver and its experiments: spheres and boxes, XPBD and Gauss–Seidel contact, friction, a grid broad phase. Its demo is **Rigid Sim**. |
| `vexelray-sim-physics-core` | The coupling kernels, in SupirVast IR, and the passes of both simulations and the coupling ordered into one step. No engine, no window. |
| `vexelray-sim-physics-gui` | The coupled step on the stack: one runner over both simulations' resident buffers. |

The fluid and the rigid bodies do not know about each other: neither depends on the other, and the build would refuse
it. Each folder has its own README, `docs/architecture.md` and `docs/TODO.md`, and each was a repository of its own
until 2026-11-12, whose history came with it. [docs/](docs) here is the coupling's.

**Try it:** Fluid Sim and Rigid Sim install on Windows with the other VexelRay applications, from
[sibarum.github.io](https://sibarum.github.io/).

## Status

The two simulations are built and measured; their READMEs say how far. **The coupling is a scaffold**: its modules are
empty. What it needs first, in order, is in [docs/TODO.md](docs/TODO.md): namespaced buffers in SupirVast, the
boundary contract in sim-core, a fluid that takes boundaries moved from outside and bodies that take forces from
outside, and then one box floating at the depth Archimedes says. [docs/architecture.md](docs/architecture.md) says why
it is shaped this way.

## Running

The stack's other repositories must be installed to the local Maven repository first: `supirvast`, `vexelray`,
`kronometer`, `tactroller`, `atchung`, `vexelray-gui`, `vexelray-framework`, `vexelray-codec`.

```bash
mvn install
```

Builds everything here and runs the tests that take seconds. Tests that run seconds of simulated time and judge what
happened run only with `-Pphysics`.

```bash
mvn -pl vexelray-sim-fluid/vexelray-sim-fluid-demo exec:exec
```

```bash
mvn -pl vexelray-sim-rigid/vexelray-sim-rigid-demo exec:exec
```

Open the two demos.
