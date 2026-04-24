# GameScene

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A powerful **Minecraft Bukkit/Spigot/Paper** plugin for creating and managing immersive, scriptable game scenes. GameScene provides a complete toolkit for building mini-games, dungeons, RPG events, and structured PvE/PvP experiences through YAML-driven configuration and an in-game setup system.

> **Server Version:** Paper 1.12.2  
> **Java:** 8+

---

## Features

- **Scene System** – Create isolated game instances with their own locations, areas, variables, and players.
- **Function Engine** – Script game logic with 30+ built-in functions (teleport, heal, broadcast, give items, potion effects, sounds, titles, etc.). Supports delays, wildcards, and conditional branching.
- **Game Objects** – Place interactive world objects:
  - `PlayerSpawn` – Scene spawn points
  - `Billboard` / `TemporaryBillboard` – Holographic displays
  - `Aura` – Area-of-effect zones that trigger functions
  - `LootChest` – Configurable loot containers
  - `ItemOnGround` – Dropped item spawners
  - `UnlockGate` – Progression gates
- **Schedulers** – Run timed function sequences automatically within scenes.
- **Event System** – Hook into player actions (join, quit, enter scene, leave scene) to trigger functions.
- **Areas** – Define **Radius** or **Cuboid** zones for scene boundaries and aura effects.
- **Loot Tables** – Weighted dice-roll loot systems with random ranges.
- **Journals & Conversations** – Build NPC dialogue trees with hovering text or chat-based prompts.
- **In-Game Setup** – Visual block-selector tools for setting coordinates without touching config files.
- **External Library API** – Other plugins can register custom functions, game objects, and item providers.
- **Integrations** –
  - [DungeonPlus](https://github.com/) (soft dependency)
  - [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI)
  - [HolographicDisplays](https://github.com/filoghost/HolographicDisplays)
  - LightAPI (server-side dynamic lighting)

---

## Installation

1. Download the latest `GameScene-*.jar` from [Releases](../../releases).
2. Place the jar in your server's `plugins/` folder.
3. Install optional dependencies if needed: **PlaceholderAPI**, **HolographicDisplays**, **DungeonPlus**.
4. Start the server. Configuration folders will be generated automatically:
   ```
   plugins/GameScene/
   ├── GameScenes/          # Scene configurations
   ├── Journals/            # Conversation scripts
   └── globalSettings.yml   # Plugin-wide settings
   ```

---

## Quick Start

### 1. Create a Scene Folder
Inside `plugins/GameScene/GameScenes/`, create a new folder named after your scene (e.g., `Tutorial`).

### 2. Add Configuration Files
Each scene can contain:

| File | Purpose |
|------|---------|
| `Settings.yml` | Scene options (auto-join, cleanup timer) |
| `Locations.yml` | Named spawn and warp points |
| `Areas.yml` | Radius or cuboid zones |
| `GameObjects.yml` | Interactive objects in the world |
| `Schedulers.yml` | Timed task sequences |
| `EventListeners.yml` | Event-driven functions |
| `LootTable.yml` | Drop tables for chests / mobs |
| `Variables.yml` | Scene-specific variables |

### 3. Example: Simple Scheduler
`Schedulers.yml`
```yaml
Schedulers:
  WelcomeTimer:
    CounterTime: 10  # seconds
    Functions:
      - 'broadcastScene(msg: &aWelcome to the scene!)'
      - 'wait(val: 5)'
      - 'broadcastScene(msg: &eGame starts in 5 seconds...)'
```

### 4. Example: Game Object
`GameObjects.yml`
```yaml
Objects:
  SpawnPoint:
    type: "PlayerSpawn"
    options:
      location: "LobbySpawn"
      functions:
        - 'sendMessage(player:$player$, msg:&aYou spawned here!)'
        - 'heal(player:$player$, val:20)'
```

### 5. Load & Run
Use in-game commands or console:
```
/gs load Tutorial
/gs run Tutorial
```

---

## Function Reference

Functions are the scripting language of GameScene. Arguments use `key:value` syntax and support wildcards.

| Function | Description | Example |
|----------|-------------|---------|
| `broadcast` | Message to entire server | `broadcast(msg:&aHello World)` |
| `broadcastScene` | Message to players in scene | `broadcastScene(msg:&aWave 1!)` |
| `broadcastNearby` | Message to nearby players | `broadcastNearby(msg:&cDanger!, radius:10)` |
| `sendMessage` | Message to one player | `sendMessage(player:$player$, msg:&aHi)` |
| `title` | Send title/subtitle | `title(player:$player$, title:&aWin!, subtitle:&7Good job)` |
| `chat` | Force player to say something | `chat(player:$player$, msg:Hello)` |
| `teleport` | Teleport player | `teleport(player:$player$, location:SpawnA)` |
| `heal` / `modifyHealth` | Heal or damage | `modifyHealth(player:$player$, operator:heal, val:10)` |
| `modifyHunger` | Change food level | `modifyHunger(player:$player$, val:20)` |
| `giveItems` | Give parsed items | `giveItems(player:$player$, items:itemKey1;itemKey2)` |
| `clearInventory` | Clear player inventory | `clearInventory(player:$player$)` |
| `potionEffect` | Apply effects | `potionEffect(player:$player$, type:SPEED, duration:60, level:1)` |
| `removeEffect` | Remove effects | `removeEffect(player:$player$, type:SPEED)` |
| `soundPlayer` | Play sound to player | `soundPlayer(player:$player$, sound:ENTITY_PLAYER_LEVELUP)` |
| `soundNearby` | Play sound in area | `soundNearby(location:BossRoom, sound:ENTITY_WITHER_SPAWN)` |
| `command` | Run console command | `command(cmd:kick $player$)` |
| `applyVelocity` / `applyVelocityDelayed` | Launch player | `applyVelocity(player:$player$, x:0, y:1, z:0)` |
| `hidePlayer` / `showPlayer` | Visibility control | `hidePlayer(player:$player$, target:$all$)` |
| `setGameMode` | Change gamemode | `setGameMode(player:$player$, mode:ADVENTURE)` |
| `disconnect` | Kick player | `disconnect(player:$player$, reason:&cYou died)` |
| `addProperty` / `removeProperty` / `clearProperties` | Scene player tags | `addProperty(player:$player$, property:completed)` |
| `activateScheduler` / `resetScheduler` | Control timers | `activateScheduler(scheduler:Wave2)` |
| `all` | Execute for all players in scene | `all(fn:sendMessage(msg:&aEveryone gets this))` |
| `wait` / `waitTick` | Delay execution | `wait(val:3)` |
| `log` | Log to console | `log(msg:Debug info)` |
| `shutdown` | Shutdown server | `shutdown()` |

### Wildcards
- `$player$` / `$p$` / `$pl$` – Player name
- `$scene$` / `$gs$` – Scene name
- `$random$` – Random number (use with `randomMin` / `randomMax`)
- `$TIME$` – Current timestamp
- `%placeholderapi%` – PlaceholderAPI variables (if installed)

---

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/gs load <scene>` | `gamescene.admin` | Load a scene into memory |
| `/gs run <scene>` | `gamescene.admin` | Start a loaded scene |
| `/gs unload <scene>` | `gamescene.admin` | Stop and unload a scene |
| `/gs setup <scene>` | `gamescene.setup` | Enter visual setup mode |
| `/gs function ...` | `gamescene.admin` | Debug and run functions manually |
| `/journal <player> <file>` | `gamescene.admin` | Start a conversation |

---

## Building from Source

```bash
# Clone the repository
git clone https://github.com/ringotypowriter/GameScene.git
cd GameScene

# Build with Maven
mvn clean package
```

The shaded jar will be in `target/GameScene-*.jar`.

### Dependencies
- Paper API `1.12.2-R0.1-SNAPSHOT`
- [ACF (Aikar Command Framework)](https://github.com/aikar/commands)
- [Caffeine](https://github.com/ben-manes/caffeine) (shaded)
- [Intake](https://github.com/KariakiDec/IntakeSk) (shaded)
- Lombok (compile-only)

---

## API for Developers

GameScene exposes an `ExternalLibrary` interface so your own plugin can extend it:

```java
public class MyPlugin extends JavaPlugin implements ExternalLibrary {
    @Override
    public void onLoadFunctions() {
        FunctionManager.register(new MyCustomFunction());
    }

    @Override
    public void onLoadGameObjects() {
        GObjectManager.register("MyObject", MyObject.class);
    }

    @Override
    public ItemProvider getItemProvider() {
        return new MyItemProvider();
    }
}
```

---

## License

This project is licensed under the **Apache License 2.0** – see the [LICENSE](LICENSE) file for details.

---

## Contributing

Contributions, issues, and feature requests are welcome! Feel free to open a pull request or issue on GitHub.

---

*Made with ❤️ for the Minecraft server community.*
