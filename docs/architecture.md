# Architecture

## Where this sits

| Folder | What it is |
| --- | --- |
| `vexelray-sim-core` | What every simulation shares: the kernel body, a step as passes over named buffers, the clock, the work budget, the camera, the panel's rows. Infrastructure, never technique. |
| `vexelray-sim-fluid` | The fluid solver and its experiments. |
| `vexelray-sim-rigid` | The rigid-body solver and its experiments. |
| `vexelray-sim-physics-core`, `-gui` | This: the two coupled, and the front door. |

All four are in this repository, each in its folder. Dependencies point downward only. The fluid and the rigid bodies do not know about each other, so the coupling has
nowhere to live but here.

## The front door

An application asks for physics by depending on this, and gets the fluid, the bodies and the coupling as one thing.
What is behind the door can change, whether a solver is replaced or a third simulation joins, without the
application changing. The fluid and rigid repos stay what they are, benches for their own experiments, each with
its own demo of readings. The demo people actually open is this one.

## One step

A step in every simulation is data: passes over named buffers (SupirVast's `dev.supirvast.vastir.pass`), run by
SupirVast's `PassRunner`. So the coupled step is the fluid's passes and the rigid step's, with the coupling's passes
between them. The whole thing is recorded as one dispatch sequence, and the buffers they share are bound by name.

Two things follow:

- **Buffer names need a namespace per simulation.** Both will have a `params`. Prefixing them, or binding them by
  owner and name, is the first thing a coupled step needs. Nothing about it is a simulation's, so the fix belongs
  in SupirVast's pass types.
- **The contract between the two belongs in `vexelray-sim-core`**: what a solid is to the fluid (where its surface
  is, and how fast it moves), and what the fluid gives back (the force and torque on it). Neither side should have
  to import the other to meet it.
