# Plan: one repository for the simulations

*Drafted 2026-11-12. Steps 1 to 6 done the same day: the history is here, one build passes (337 tests) and both
demos run from their folders; vexelray-dist and the links elsewhere are edited but not yet committed in those
repositories. Steps 7 (GitHub) and 8 (the old checkouts) are the author's.*

`vexelray-sim-core`, `vexelray-sim-fluid` and `vexelray-sim-rigid` move into this repository, beside the coupling,
with their history. It is done before the port to VexelRay 0.2 and before the coupling work, because both of those
change all three at once, and across four repositories every round of that is a version bump, an install in order
(core, then fluid and rigid, then physics) and a build against whatever jars are in `~/.m2` at the time.

The decision of 2026-10-08 stands: the fluid and the rigid bodies do not know about each other, and the coupling
belongs to neither. Module dependencies enforce it, which is stricter than separate repositories were: Maven
refuses an import of `fluid-core` from `rigid-core`.

## Why sim-core comes too

Nothing outside the simulations depends on it. Every `pom.xml` under `GitHub/` that names `vexelray-sim-core` is in
one of the four sim repositories (checked 2026-11-12). Kept apart, it would be the one sibling the others still wait on.

## The layout

Each repository moves in **as a folder of its own, unchanged inside**:

```
vexelray-sim-physics/
  pom.xml                       the root: one version, every stack property, the physics profile
  README.md  CLAUDE.md  docs/   the front door, and the coupling's docs
  vexelray-sim-core/            was vexelray-sim-core      (-core, -core-gui)
  vexelray-sim-fluid/           was vexelray-sim-fluid     (-core, -gui, -demo, docs/, installer.json, CLAUDE.md)
  vexelray-sim-rigid/           was vexelray-sim-rigid     (-core, -gui, -demo, docs/, installer.json)
  vexelray-sim-physics-core/    as now
  vexelray-sim-physics-gui/     as now
```

Keeping each folder as it was means nothing inside it moves relative to itself:

- `installer.json`'s `source` paths (`vexelray-sim-fluid-demo/target/fluid-sim.exe`) and the demos'
  `${project.basedir}/../installer.json` stay right.
- The READMEs' links to each other (`../vexelray-sim-rigid`) stay right, because the folders are still siblings.
- `git log` on any file follows it, because the move is a subtree merge, not a copy.

Only `groupId`/`artifactId` matter to anything outside, and they do not change, so nothing else that uses the jars
notices.

## Steps

### 1. Quiet the four checkouts
- No session working in any of them. The `.claude/worktrees` folders in fluid and rigid are empty leftovers: `git
  worktree list` shows only `develop` in each.
- All four clean and on `develop`, and the three with remotes pushed. That was true on 2026-11-12.
- The `release/0.1.2` branches and the release tags stay in the old repositories, where the installers that were
  shipped point. They are not brought across.

### 2. Bring the history in
On `develop` here, once for each:

```bash
git subtree add --prefix=vexelray-sim-core  ../vexelray-sim-core  develop
git subtree add --prefix=vexelray-sim-fluid ../vexelray-sim-fluid develop
git subtree add --prefix=vexelray-sim-rigid ../vexelray-sim-rigid develop
```

Without `--squash`, so all history comes across (17, 95 and 66 commits).

### 3. One build
- **The root `pom.xml`** lists the five folders as modules and owns, once: the version (0.1.3-SNAPSHOT, the three
  repositories' version; physics moves up from 0.1.0), every stack version property (fluid's set is the superset),
  the dependency management, the compiler and surefire settings, `vexelray-build-maven-plugin`'s `isolate`, and
  the `physics` and `linux-build-output` profiles.
- **Each folder's `pom.xml`** becomes an aggregator whose parent is the root. It keeps its `<modules>` and its
  description, and loses its version, properties and plugin settings. `vexelray.sim.core.version`,
  `vexelray.sim.fluid.version` and `vexelray.sim.rigid.version` go: they are `${project.version}` now. That also
  fixes this repository's TODO about stale sibling versions.
- **Duplicate files at the folders' tops** (`LICENSE`, `LICENSE-docs`, `NOTICE`, `.gitignore`, `.gitattributes`):
  one copy at the root. `THIRD-PARTY-NOTICES.txt` stays in fluid and rigid, because their `installer.json` ships it
  from there.
- **Check:** `mvn -o install` at the root, then `-Pphysics` once. Start each demo with ottermate (`--launch`, a
  scenario, `find readout.`, close) to see that both still run.

### 4. The repository's own docs
- `README.md` becomes the front door for all of it: the table of folders, what installs, how to run each demo.
  The folders' READMEs stay as their benches' tours.
- `CLAUDE.md` at the root: the building and running half of fluid's, written for the whole repository. Fluid's own
  `CLAUDE.md` keeps what is only the fluid's.
- `vexelray-sim-rigid/TODO.md` (root level, 17 lines) folds into `vexelray-sim-rigid/docs/TODO.md`.
- This repository's `docs/TODO.md` loses the item about sibling versions. Its README loses "waits on rigid resting a
  stack", which is done.

### 5. The distribution (vexelray-dist)
This is the one real change outside the repository: dist assumes **one application per checkout, with
`installer.json` at its root** (`$root/$1/installer.json` in `build.sh`, the same in `build.cmd`).

- `build.sh`, `build.cmd`, `release.ps1`: `vexelray-sim-core` leaves `LIBS`, and `vexelray-sim-physics` takes its
  place there (its install builds and tests everything). `APPS` names the two demos as
  `vexelray-sim-physics/vexelray-sim-fluid` and `vexelray-sim-physics/vexelray-sim-rigid`. The scripts already find
  everything else from the folder they are given (`git -C`, `installer.json`, the sources under it). `--only`'s check
  and the logs' file names need to cope with a `/` in the name.
- `release.ps1` rewrites versions in each repository's poms: `vexelray-sim-physics` appears once in `$Repos`, not
  three times.
- `installer-suite.json`: the two `config` paths and `page` links.
- `README.md`: the two links.
- `preflight/BUILD.txt` is written by the build and needs nothing.

**Check:** `build.cmd --only vexelray-sim-physics/vexelray-sim-fluid`, then the same for rigid, and see that each
`installer.json` is found and its binaries staged.

### 6. Links elsewhere
Links to `github.com/sibarum/vexelray-sim-{core,fluid,rigid}` become `.../vexelray-sim-physics/tree/develop/<folder>`,
and relative links `../vexelray-sim-fluid` from sibling repositories become `../vexelray-sim-physics/vexelray-sim-fluid`.
Found on 2026-11-12:

| Repository | Files |
| --- | --- |
| vexelray-framework | `docs/index.md`, `docs/repositories.md`, `docs/TODO.md`, `docs/units.md` |
| vexelray-gui | `CLAUDE.md`, `docs/plans/scrolling.md` |
| vexelray-dist | `README.md`, `preflight/README.md` (and step 5) |
| sibarum.github.io | `index.html` (the two app cards), `technology.html` |
| kronometer | `README.md`, `docs/consumer-notes.md` |
| supirvast | `TODO.md` |
| vexelray-icons, vexelray-lean-proofs | `README.md` |

The user's global `CLAUDE.md` and the auto-memory (`sim-project-layout`) describe the four as siblings; both change
to the one repository.

### 7. GitHub (needs the author)
- This repository **has no remote yet**. `sibarum/vexelray-sim-physics` is created and `develop` pushed.
- The three old repositories each get a last commit: a README that points to the folder here. Then they are
  archived, read-only, so the installers' release tags and old links keep working.

### 8. The old checkouts
They stay on disk until the first build of the merged repository passes and dist has built from it. Then they are
deleted. Claude's per-project memory for fluid, rigid and physics merges into this repository's, and the leftover
worktree folders go with them.

## After this
1. The port to 0.2, both demos at once. Their shared panel (`Controls`, `Panel`, `Readings`) goes straight into
   `vexelray-sim-core-gui` in 0.2's shape.
2. The coupling, as `docs/TODO.md` lists it: namespaced buffers in SupirVast, the boundary contract in sim-core,
   boundaries moved from outside in `Flip3`, forces from outside in `Boxes`, then one body floating.
