# angelMinigame

Configurable minigame engine for Paper 1.21. Admins create arenas and game modes entirely in-game — no command blocks, no restarts.

## Modes

- **SPLEEF** — Break blocks under opponents, last standing wins
- **TNT_RUN** — Blocks vanish when stepped on
- **PARKOUR** — Checkpoint race, first to finish wins
- **LMS** — Last Man Standing PvP arena
- **CUSTOM** — Script-driven game engine with 36 action types (lava rising, border shrink, zombie waves, etc.)

## Quick Start

```
/arena create spleef1 SPLEEF
/arena wand
/arena setregion spleef1
/arena setlobby spleef1
/arena addspawn spleef1
/arena blocks add spleef1 SNOW_BLOCK
/arena floor spleef1 64
/arena minmax spleef1 2 8
/game join spleef1
```

## Custom Games

Edit `arenas.yml` and run `/arena reload`. Scripts use a timeline of triggers (`at`, `every`, `repeat`, `on: death`, `on: win`) and 36 built-in actions — no Java required.

## Dependencies

- Paper 1.21.1+
- angelNCore (optional — enables economy rewards and cross-plugin events)

## Building

```bash
./gradlew jar
```

Requires Java 21.
