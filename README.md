# vexelray-sim-physics

Physics for VexelRay, and **the front door to its simulations**. An application that wants physics depends on this,
not on the simulations under it.

It couples [vexelray-sim-fluid](../vexelray-sim-fluid) and [vexelray-sim-rigid](../vexelray-sim-rigid) both ways:
bodies feel buoyancy and drag, and the fluid feels the bodies as moving boundaries. Both run in one step.

This is a scaffold so far. [docs/architecture.md](docs/architecture.md) says where it sits;
[docs/TODO.md](docs/TODO.md) lists what comes first.

| Module | What it holds |
| --- | --- |
| `vexelray-sim-physics-core` | The coupling kernels, in SupirVast IR, and the passes of both simulations and the coupling ordered into one step. No engine, no window. |
| `vexelray-sim-physics-gui` | The coupled step on the stack: one runner over both simulations' resident buffers. |

## Running

The stack's siblings must be installed to the local Maven repository first: `supirvast`, `vexelray`,
`tactroller`, `vexelray-gui`, `vexelray-sim-core`, `vexelray-sim-fluid`, `vexelray-sim-rigid`.

```bash
mvn install
```

Runs the tests that take seconds. Tests that run seconds of simulated time and judge what happened run only with
`-Pphysics`.
