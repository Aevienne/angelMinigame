# angelMinigame

Configurable minigame engine for Paper 1.21. Create arenas and game modes in-game with no command blocks or restarts. Supports spleef, TNT run, parkour, last man standing, and a script-driven custom mode.

Each arena gets its own isolated void world so games never interfere with your main world.

## Setup walkthrough

All commands require OP or the `angelminigame.admin` permission. Run them as a player in-game.

**1. Create the arena**

```
/arena create spleef1 SPLEEF
```

**2. Create the isolated world**

```
/arena createmap spleef1
```

You get teleported to a void world named `arena_spleef1` with a glass block at (0, 65, 0) to stand on. Build your arena from there. Once you are inside the arena world most commands no longer need the arena name.

**3. Build the floor**

Switch to creative and build:

```
/gamemode creative
/fill -10 64 -10 10 64 10 snow_block
```

This places a 20x20 snow floor at Y=64.

**4. Select the arena region**

```
/arena wand
```

Left click one corner of your floor, right click the opposite corner. Then:

```
/arena setregion
```

**5. Set lobby and spawns**

Stand above the arena where players should wait and run:

```
/arena setlobby
```

Go down to the floor and stand where players start. Run this for each spawn point:

```
/arena addspawn
/arena addspawn
/arena addspawn
/arena addspawn
```

**6. Configure the game**

```
/arena blocks add snow_block
/arena floor 63
/arena minmax 2 8
/arena countdown 10
/arena rules pvp false
/arena reward 100
```

**7. Check it**

```
/arena info
```

**8. Play**

```
/game join spleef1
```

When enough players join the countdown starts automatically. After the game ends there is a 5 second celebration with fireworks before everyone gets returned to their original world with their inventory restored.

## Commands

### Arena management

All commands that take a name can omit it when you are standing inside an arena world.

| Command | Description |
|---|---|
| `/arena create <name> <mode>` | Create an arena (SPLEEF, TNT_RUN, PARKOUR, LMS, CUSTOM) |
| `/arena createmap <name>` | Create an isolated void world for the arena |
| `/arena deletemap <name>` | Delete arena and its world folder |
| `/arena delete <name>` | Delete arena config only, world stays |
| `/arena edit <name>` | Teleport to arena world in creative |
| `/arena tp <name>` | Teleport to arena world |
| `/arena info [name]` | Show arena settings |
| `/arena list` | List all arenas |
| `/arena reload` | Reload arenas.yml without restarting |
| `/arena wand` | Get the region selection stick |
| `/arena setregion [name]` | Set region from wand selection |
| `/arena setlobby [name]` | Set lobby spawn at your location |
| `/arena addspawn [name]` | Add a player spawn point |
| `/arena clearspawns [name]` | Remove all player spawns |
| `/arena setspectator [name]` | Set spectator spawn |
| `/arena minmax [name] <min> <max>` | Set player limits |
| `/arena countdown [name] <seconds>` | Set countdown duration |
| `/arena floor [name] <y>` | Set the Y level players fall through |
| `/arena blocks add|remove|list [name] [material]` | Manage breakable blocks |
| `/arena rules [name] <key> <value>` | Set game rules (pvp, block-break, block-place, death-eliminates, fall-void, max-time, restore-arena) |
| `/arena allowcmd [name] add|remove <cmd>` | Allow specific commands during game |
| `/arena reward [name] <amount>` | Currency reward for winner |

### Player commands

| Command | Description |
|---|---|
| `/game join <arena>` | Join an arena queue |
| `/game leave` | Leave your current game |
| `/game list` | List active games |
| `/game start <arena>` | Admin force start |
| `/game stop <arena>` | Admin force stop |

## Custom game mode

The CUSTOM mode lets you script any minigame by editing `arenas.yml`. No Java needed. After editing run `/arena reload`.

### Triggers

```
at <seconds>              fire once at elapsed time
every <seconds>           fire repeatedly
repeat <count> every <seconds>  fire count times, once per interval
on: start                 fire at game start
on: death                 fire on every elimination
on: first_death           fire on the first elimination
on: count<N               fire when alive players hits N
on: win                   fire when the game is won
on: reset                 fire during arena reset
```

### Example: lava rising

```yaml
arenas:
  lava_rising:
    mode: CUSTOM
    world: arena_lava_rising
    region: {min: [-20, 64, -20], max: [20, 100, 20]}
    lobby: [0, 120, 0]
    spectator: [0, 130, 0]
    player-spawns:
      - [0, 70, 0]
    min-players: 2
    max-players: 16
    countdown: 10
    rules:
      pvp: false
      block-break: ALL
      block-place: ALL
      death-eliminates: true
      fall-void: true
    script:
      - at: 0
        actions:
          - fill: {region: whole, y: 64, material: LAVA}
          - title: {title: "&4Lava Rising", subtitle: "&7Build up to survive"}
          - give_item: {material: DIRT, amount: 64}
      - repeat: 10 every: 6
        actions:
          - fill: {layer: {y: 65, offset: "{iteration}"}, material: LAVA}
          - actionbar: {text: "&cLava rising. Survive"}
      - on: win
        actions:
          - title: {title: "&6{winner} survived"}
```

### Available actions

Block: `fill`, `replace`, `remove_blocks`, `set_block`, `block_decay`

Player: `title`, `actionbar`, `message`, `broadcast`, `sound`, `stop_sound`, `effect`, `clear_effects`, `heal`, `damage`, `kill`, `launch`, `teleport`, `give_item`, `clear_inv`, `gamemode`, `flight`, `flight_off`, `freeze`, `unfreeze`, `glow`, `glow_off`, `invisible`, `invisible_off`, `health`, `hunger`, `xp`

World: `set_time`, `set_weather`, `border_set`, `border_shrink`

Entity: `spawn_entity`, `entity_clear`, `lightning`

Visual: `explosion`, `particle`, `firework`

Misc: `cage`, `uncage`, `command`

The `command` action is disabled by default. Enable it in `config.yml` under `allowed-commands` only for trusted server owners.

Variables available in action parameters: `{elapsed}`, `{remaining}`, `{iteration}`, `{arena_min_x}`, `{arena_max_x}`, `{arena_name}`, `{rand_player}`, `{winner}`.

## World safety

Arena worlds are named `arena_<name>` and stored in the server root like any other world. The `/arena deletemap` command refuses to delete any world not starting with `arena_`, so your main worlds (`world`, `world_nether`, `world_the_end`) are never at risk.

## Economy integration

If angelNCore is installed, winners automatically receive the configured currency reward via the economy manager. Three events are published that other plugins can listen to: `AngelGameStartEvent`, `AngelGameEndEvent`, `AngelPlayerEliminatedEvent`.

The plugin works standalone without angelNCore, rewards just do nothing.

## Commands during games

By default players can only use `/game leave`, `/msg`, `/reply`, and similar chat commands during a game. Everything else is blocked. Admins with `angelminigame.admin` bypass this. Specific commands can be allowed per arena with `/arena allowcmd`.

## Building

Requires Java 21.

```
./gradlew jar
```

Output in `build/libs/AngelMinigame-1.0.0.jar`.

## Dependencies

- Paper 1.21.1+
- angelNCore (optional, for economy rewards and event publishing)
