# Feature Proposal

This file lists, for each custom/creative requirement, the Pitch, the Mechanics and the Architecture (the two new abstractions and the six concrete classes that implement them, marked **New** or **Retrofitted**), as required by the REQ3/REQ4 rules and, for REQ5, the additional Request/Schema/Security items required by the REQ5 (HD) rules. AI acknowledgement is recorded separately in `docs/A3/A3_FoundationModelsUsed.txt`.

## REQ3 — Infestation and Pest Control

### The Pitch

Three pests — a slime snail, a hopping locust swarm, and a burrowing mole — move towards the Farmer's plants and feed on them, each moving and feeding in its own way. The Farmer fights back with three single-use defences that each work in a different way: a trap that pulls pests in and captures them, a jar of ladybugs that hunts them down, and a bag of salt that pushes them out.

### The Mechanics

- Each pest looks for the nearest plant within 4 tiles and lands on whichever reachable tile brings it closest, or a random reachable tile if none is near:
  - **Slime Snail** (`q`) crawls to one of the 8 adjacent tiles.
  - **Locust Swarm** (`z`) jumps like a chess knight (two tiles one way, one tile sideways).
  - **Burrowing Mole** (`r`) burrows 1 or 2 tiles in a straight line, up, down, left or right.
- Feeding sets the plant back one growth stage, through the `Regressible` interface the pest never names a concrete crop class to use:
  - **Slime Snail:** feeds on the plant under it, then covers the bare Soil/Dirt around it with slime (`:`) for 5 turns — nothing can be planted there until it dries.
  - **Locust Swarm:** feeds on every plant on and around its tile at once. It starts with 4 meals; after a meal, if it still has at least 2 meals left, it has a 30% chance to split off a second swarm carrying half its remaining meals; it flies off the map once its meals run out.
  - **Burrowing Mole:** feeds on the plant under it, then caves its 4 straight neighbours of bare Soil back into Dirt.
- Each defence is picked from the Farmer's menu while carrying it, and is consumed the moment it is used:
  - **Pest Trap** (item `[`, becomes ground `X`): sets on the tile the Farmer stands on, only if no plant grows there. Each turn it first pulls every pest within 2 tiles one step closer, then captures every pest on it or next to it, up to 2 captures, before reverting to the ground it covered — after 10 turns, or sooner once its captures are used up.
  - **Jar of Ladybugs** (`j`): releases up to 3 ladybugs (`e`) onto the free tiles around the Farmer. Each ladybug lives 5 turns, losing 1 hit point a turn, and eats an adjacent pest each turn instead of wandering.
  - **Bag of Salt** (`f`): turns the bare Soil/Dirt around the Farmer into Salt ground (`+`) for 8 turns; any pest already standing on a tile it salts takes 2 damage and is pushed out to a free neighbouring tile.
- Every message names the coordinates of every tile it touched. A pest that feeds on nothing, and a trap that pulls and captures nothing, print nothing that turn.
- At the start of the game, the world places one pest of each kind on a free tile of the farm, and gives the Farmer one of each defence.

### The Architecture

**Abstraction 1 — `Infestation`** (interface, `game.pests`): `feedOn(Location here)` — what a pest does to the plant it stands on. Implemented by 3 concrete classes, all **entirely new**:
1. `SlimeSnail` (`game.pests.SlimeSnail`) — **New**
2. `LocustSwarm` (`game.pests.LocustSwarm`) — **New**
3. `BurrowingMole` (`game.pests.BurrowingMole`) — **New**

**Abstraction 2 — `PestDeterrent`** (interface, `game.pestcontrol`): `deterAround(Location here, GameMap map)` — what a defence does to the pests around it. Implemented by 3 concrete classes, all **entirely new**:
1. `PestTrap` (`game.pestcontrol.PestTrap`) — **New**
2. `LadybugJar` (`game.pestcontrol.LadybugJar`) — **New**
3. `SaltBarrier` (`game.pestcontrol.SaltBarrier`) — **New**

That is 6 entirely-new concrete classes implementing the 2 abstractions; none is retrofitted from Assignment 1/2.

**Supporting, uncounted classes** that integrate the feature into the engine, without themselves being one of the 2 abstractions or 6 concrete classes above:
- `Pest` (abstract class, `game.pests`, implements `Infestation`) — the shared wandering/seeking/feeding turn logic the 3 pests extend.
- `SeekPlantBehaviour` (`game.behaviours`, a `Behaviour`) — lands a pest on the reachable tile nearest a plant within its sensing radius, or falls back to ordinary wandering.
- `InfestAction` (`game.pests`, extends the engine's `Action`) — the pest's own feeding action on its turn; depends only on `Infestation`.
- `DeterrentItem` (abstract class, `game.pestcontrol`, implements `PestDeterrent`) — the shared base the 3 defence items extend.
- `DeployDeterrentAction` (`game.pestcontrol`, extends the engine's `Action`) — uses a deterrent where the actor stands and consumes the item it came from; depends only on `PestDeterrent`.
- `PestPresent` (`game.pests`, a `TileCondition`) — finds the pest standing on a tile; reused by `TrapGround`, `SaltBarrier` and `Ladybug`.
- `Ladybug` (`game.pestcontrol`, extends the engine's `WanderingActor`) and `EatPestAction` (`game.pestcontrol`, extends the engine's `Action`) — the ladybug released from a jar, and the action it uses to eat a pest next to it.
- `TemporaryGround` (abstract class, `game.positions`) — ground that covers another ground for a fixed number of turns, then restores it; extended by `SlimeGround` (`game.pests`), `SaltGround` and `TrapGround` (both `game.pestcontrol`).

**Existing classes REQ3 uses but does not change:** `WanderingActor`, `WanderBehaviour`, `TileCondition`, `Area`/`AreaFactory`, `RandomUtils`, `Soil`, `Dirt`, `Unplanted`, `Plantable`, `Tillable`. `RandomUtils` and `AreaFactory` are passed in through constructors, never looked up.

**Existing class REQ3 changes:** `StackQueueValleyWorld` — places one pest of each kind on the farm and gives the Farmer one of each defence.

**Classes from REQ1 that REQ3 depends on:** `Plant` and `Regressible` — a pest only ever asks a tile for its `Regressible` view, so it never depends on a concrete crop class.

No `instanceof` is used anywhere in this feature; a pest or a defence is found on a tile purely through `getGroundAs`/`getActor().asCapability(...)`-style capability lookups. Full SOLID/DRY/KISS/YAGNI discussion and alternatives considered are in `docs/A3/A3 Design Rationale/A3_Design_Rationale.pdf`.

### How it meets the complexity rules

- **Cross-component interaction:** an `Item` (a defence) offers an `Action` (`DeployDeterrentAction`) that changes `Ground` (salt or a trap) and moves or removes `Actor`s (pests); a pest `Actor` changes a plant `Ground` through `Regressible` on its own turn.
- **Spawning:** the Locust Swarm splits into a second swarm; the Jar of Ladybugs releases up to 3 ladybug `Actor`s.
- **Area of effect:** the swarm sets back every plant on and around its tile; slime and salt cover every bare tile around them; the trap pulls every pest within 2 tiles.
- **Terrain change:** slime, salt and the trap each replace `Ground` and later restore it; the mole turns Soil back into Dirt.
- **Forced movement:** the trap pulls pests towards itself; salt pushes them out to a free neighbouring tile.

### Why REQ3 is different from REQ4 and REQ5

- Pests act on their own turn, driven by `SeekPlantBehaviour`, independently of the Farmer — unlike REQ4, whose Enchantments only ever fire as a direct consequence of the Farmer's own tool use, and unlike REQ5, whose Wanderer is driven by real-world weather data rather than by seeking a target.
- REQ3 is about the farm being attacked and defended; REQ4 is about amplifying the Farmer's own ordinary actions; REQ5 is about an actor whose behaviour is driven entirely by external, real-world data.
- The 3 pests move in 3 different ways (jump, crawl, burrow) and the 3 defences act in 3 different ways (pull, hunt, push), so the same 2 abstractions produce 6 genuinely different behaviours, not 6 variations on one idea.

---

## REQ4 — Enchanted Tools

### The Pitch

The Hoe, Watering Can and Pickaxe can each be enchanted with one of three Enchantments. The next time an enchanted tool is used for its ordinary job, two things happen in the same turn: the tool's own fixed effect (a spread, a chain, or similar — always the same shape regardless of which Enchantment is attached), and then the attached Enchantment's own effect, applied to whichever tiles that first effect actually touched, after which the Enchantment is spent. An unenchanted tool behaves exactly as it did in Assignment 1/2.

### The Mechanics

- Each of the Hoe, Watering Can and Pickaxe offers an "enchant [tool] with a [Enchantment]" action for every Enchantment the game knows, alongside its ordinary tilling/watering/breaking action.
- Choosing it attaches that Enchantment to that one tool *instance*, replacing whatever was attached before. Two tools of the same kind (e.g. two Hoes in the same inventory) are enchanted independently.
- The next time that same tool instance is used for its own purpose:
  - an enchanted **Hoe** also tills *every* tillable tile adjacent to the one just tilled;
  - an enchanted **Watering Can** also waters *one random* waterable tile adjacent to the one just watered;
  - an enchanted **Pickaxe** has a 40% chance of the break chaining to *one random* breakable tile adjacent to the one just broken.
- The attached Enchantment then fires once against exactly the tiles that spread/chain touched (never the original tile, which the tool's own action already reports on), and is consumed:
  - **Blaze Enchantment** ignites a Fire on every touched tile (burns for 5 turns; anyone standing in it takes 5 damage/turn for 3 turns);
  - **Bounty Enchantment** forces an extra, immediate growth attempt on any Plant standing on a touched tile;
  - **Shatter Enchantment** stuns, for 2 turns, whichever Actor is standing on a touched tile.
- Even when the tool's own effect touches nothing that turn (no free adjacent tile to spread to, the chain roll fails, nothing to chain to), the Enchantment still fires against an empty tile list and reports exactly that, rather than printing nothing.

### The Architecture

**Abstraction 1 — `Enchantable`** (interface, `game.items.tools`): `enchant(Enchantment)` / `triggerEnchantment(Actor, Location)` — a tool that can carry an Enchantment and trigger it. Implemented by 3 concrete classes, all **Retrofitted** (modified to extend the new `AbstractEnchantableTool`, which implements `Enchantable`):
1. `Hoe` (`game.items.tools.Hoe`) — **Retrofitted**
2. `WateringCan` (`game.items.tools.WateringCan`) — **Retrofitted**
3. `Pickaxe` (`game.items.tools.Pickaxe`) — **Retrofitted**

**Abstraction 2 — `Enchantment`** (interface, `game.items.tools`): `applyTo(List<Location>, Actor, Location)` — a magical effect a tool can carry and fire once. Implemented by 3 concrete classes, all **entirely new**:
1. `BlazeEnchantment` (`game.enchantments.BlazeEnchantment`) — **New**
2. `BountyEnchantment` (`game.enchantments.BountyEnchantment`) — **New**
3. `ShatterEnchantment` (`game.enchantments.ShatterEnchantment`) — **New**

That is 3 entirely-new classes (the Enchantments) and 3 retrofitted classes (the tools) — 6 concrete classes total implementing the 2 abstractions, as required.

**Supporting, uncounted classes** that integrate the feature into the engine, without themselves being one of the 2 abstractions or 6 concrete classes above:
- `AbstractEnchantableTool` (abstract class, `game.items.tools`) — the shared base the 3 tools extend; owns the Enchantments a tool can be enchanted with, and offers one `EnchantAction` per Enchantment via `allowableActions`.
- `EnchantmentSlot` (`game.items.tools`) — composition helper that stores at most one `Enchantment`, fires it, then clears itself.
- `EnchantmentTrigger` (`game.items.tools`) — finds the `Enchantable` view of the specific tool instance an Action just used (by reference identity against the narrowed `Inventory.getItemsAs` view, since the engine gives no other way to ask an already-narrowed reference for a second capability) and fires it.
- `EnchantAction` (`game.items.tools`, extends the engine's `Action`) — attaches one specific Enchantment to one specific tool; depends only on `Enchantable` and `Enchantment`, never on a concrete tool or Enchantment class.
- `CapabilityToolAction` (`game.actions`, retrofitted) and `BreakAction` (`game.actions`, retrofitted) — the existing Actions behind tilling/watering and breaking, each now also calling `new EnchantmentTrigger().fire(actor, tool, location)` once their own effect has run, so neither Action needs to know whether the tool it used happens to be enchantable.

**Existing classes REQ4 uses but does not change:** `AreaFactory`, `RandomUtils`, `Fire`, and `StunnedStatus`. All are passed in through constructors or obtained from the tile, never looked up globally.

**Existing classes REQ4 changes:** `Plant` and `PeriodicAbility` each gained a method for a forced attempt: `Plant.forceGrowth(Location)` and `PeriodicAbility.forcePerform(...)`. The Bounty Enchantment calls `Plant.forceGrowth(Location)` directly. It skips the interval and chance of a plant's own turn, so the forced attempt is never wasted. A plant whose growth is paused still refuses it. `CapabilityToolAction` and `BreakAction` each gained one line firing the used tool's Enchantment (through `EnchantmentTrigger`) after their own effect has already run; `StackQueueValleyWorld` builds the 3 Enchantments once and gives the Farmer's starting Hoe, Watering Can and Pickaxe all 3 to choose from.

No `instanceof` is used anywhere in this feature; the matching `Enchantable` view is found by reference-identity comparison against the tool already obtained from the inventory (documented in the code as a deliberate, narrow use of Connascence of Identity, not a workaround). Full SOLID/DRY/KISS/YAGNI discussion and alternatives considered are in `docs/A3/A3 Design Rationale/A3_Design_Rationale.pdf`.

---

## REQ5 — The Wanderer (A.P.I., HD)

### The Pitch

A Wanderer roams the farm, and what it does depends on the real weather at the real-world place its current tile represents — the farm map is laid over the real world, so the Wanderer's own position decides which place the game asks the weather for. Every 5 turns it reacts to the current weather where it stands (a storm shelter, a rain dance, or a campfire); when an adjacent actor asks it for tomorrow's forecast, it reveals an omen (a frost, a sowing, or a drought warning) drawn from tomorrow's weather at that same place. Both halves change the farm or an actor directly — new ground, new items, status effects, forced plant growth — never only a printed line.

### The Mechanics

- Every 5 turns, the Wanderer converts its own `Location` into a latitude/longitude (`MapGeography`, 2 degrees per tile, anchored at Melbourne: -37.81, 144.96) and asks `OpenWeatherClient` for the real current weather there.
- `WeatherInterpreter` picks at most one `WeatherReaction`, in a fixed priority order, and the Wanderer performs it that turn. If no rule below matches (for example Clear, 22°C and wind 3 m/s), it picks none and the Wanderer takes its normal wandering step:
  - wind ≥ 10 m/s → **Storm Shelter** — blocks the free tiles around it for a duration that grows with the wind speed;
  - Rain, Drizzle or Thunderstorm → **Rain Dance** — tills the bare ground and freely waters the crops in a radius that grows with the rainfall;
  - temperature ≤ 10°C → **Campfire** — lights 1–8 adjacent tiles on fire, more the colder it is.
- When an actor stands next to the Wanderer, it is offered an "ask about tomorrow's weather" action. Choosing it asks for tomorrow's forecast at the Wanderer's own position, and `WeatherInterpreter` picks at most one `ForecastOmen`. If no rule below matches, the Wanderer shrugs and says tomorrow looks like an ordinary day:
  - Snow, or forecast ≤ 2°C → **Frost Omen** — pauses the growth of every Plant in a 2-tile radius for a duration that grows the colder it is;
  - Rain, Drizzle or Thunderstorm → **Sowing Omen** — a new Strawberry Seedling takes root on every bare Soil tile in a 2-tile radius;
  - Clear and forecast ≥ 25°C → **Drought Omen** — drains the asking actor's stamina over the following turns.
- If there is no API key, no network, or the request fails or takes more than 5 seconds, the Wanderer only wanders and says the sky is unreadable; the forecast action reports that tomorrow's weather is unreadable. The game never stops because of the API.
- Every message names the weather reading that caused it and the exact tiles that changed.

### The Architecture

**Abstraction 1 — `WeatherReaction`** (interface, `game.wanderer`): `reactAround(Location here)` — what the Wanderer does to the farm because of the weather it stands in right now. Implemented by 3 concrete classes, all **entirely new**:
1. `StormShelterReaction` — **New**
2. `RainDanceReaction` — **New**
3. `CampfireReaction` — **New**

**Abstraction 2 — `ForecastOmen`** (interface, `game.wanderer`): `bringUpon(Actor asker, Location here)` — what tomorrow's forecast brings when an actor asks the Wanderer for it. Implemented by 3 concrete classes, all **entirely new**:
1. `FrostOmen` — **New**
2. `SowingOmen` — **New**
3. `DroughtOmen` — **New**

**Higher-level classes that depend strictly on the two abstractions above, never on a concrete reaction or omen** (Dependency Inversion):
1. `Wanderer` (`game.wanderer.Wanderer`, extends the engine's `WanderingActor`) — depends on `WeatherReaction` only through `WeatherInterpreter.reactionFor(...)`.
2. `AskForecastAction` (`game.wanderer.AskForecastAction`, extends the engine's `Action`) — depends only on `ForecastOmen`.
3. `ReactToWeatherAction` (`game.wanderer.ReactToWeatherAction`, extends the engine's `Action`) — depends only on `WeatherReaction` (a third class, beyond the 2 required by the rule).

**Supporting, uncounted layer** that connects the two abstractions above to the real OpenWeatherMap API, so none of the 8 classes listed above ever touches HTTP or JSON directly:
- `WeatherSource` (interface, `game.weather`) — `currentWeatherAt`/`tomorrowWeatherAt`, returning `Optional<WeatherReading>`.
- `OpenWeatherClient` (`game.weather`) — the one production implementation of `WeatherSource`; builds the request, sends it, and hands the body to `OpenWeatherParser`.
- `OpenWeatherParser` (`game.weather`) — turns the JSON reply into a `WeatherReading`.
- `WeatherReading` (`game.weather`) — the validated values the rest of the feature uses (condition, temperature, wind speed, rainfall).
- `ApiKeyLoader` (`game.weather`) — finds the API key from an environment variable or a local `.env` file, so it is never written in the code.
- `MapGeography` (`game.weather`) — converts a `Location` into a latitude/longitude.
- `WeatherInterpreter` (`game.wanderer`) — the single class allowed to construct a concrete `WeatherReaction`/`ForecastOmen`, so every other class above depends only on the interface.

### The Request

`OpenWeatherClient` builds a plain HTTPS GET request. The latitude/longitude are **not** constant — `MapGeography.latitudeOf(here)`/`longitudeOf(here)` derive them from the Wanderer's own `Location` (its live `x`/`y` on the map) every time it is asked, so the query genuinely changes as the Wanderer moves. For example:

Current weather, asked every 5 turns at the Wanderer's own tile (here, the farm's top-left tile, Melbourne itself):
```
GET https://api.openweathermap.org/data/2.5/weather?lat=-37.81&lon=144.96&units=metric&appid=<OPENWEATHER_API_KEY>
```

Tomorrow's forecast, asked when an actor requests it, from a Wanderer that has since moved to tile (3, 1) — `&cnt=9` selects the entry roughly 24 hours ahead, at this endpoint's 3-hour step:
```
GET https://api.openweathermap.org/data/2.5/forecast?lat=-39.81&lon=150.96&units=metric&cnt=9&appid=<OPENWEATHER_API_KEY>
```

`lat`/`lon` are computed from `tile.y()`/`tile.x()` (2 degrees per tile south/east of the Melbourne origin), so they satisfy Rule 1: the query is driven by the current game state (the Wanderer's own map coordinates), never a static URL.

### The Schema

Only the fields the game actually reads (`OpenWeatherParser.readingFrom`) are shown; every other field OpenWeatherMap's real reply contains is ignored.

Current weather endpoint — the shape `parseCurrent` expects:
```json
{
  "weather": [ { "main": "Rain" } ],
  "main":    { "temp": 12.3 },
  "wind":    { "speed": 6.2 },
  "rain":    { "1h": 2.1 }
}
```

Forecast endpoint — the shape `parseLastForecast` expects; the **last** entry of `"list"` is the one used:
```json
{
  "list": [
    { "weather": [ { "main": "Clear" } ], "main": { "temp": 9.4 },  "wind": { "speed": 3.1 } },
    { "weather": [ { "main": "Snow"  } ], "main": { "temp": -1.2 }, "wind": { "speed": 4.0 }, "rain": { "3h": 1.5 } }
  ]
}
```

`weather[0].main` and `main.temp` must be present, or the reading is discarded entirely (`Optional.empty()`, the Wanderer then reports the sky as unreadable). `wind.speed` and `rain.1h` / `rain.3h` default to `0` when OpenWeatherMap leaves them out, which it does on a dry/calm day.

### CRITICAL SECURITY WARNING

The OpenWeatherMap API key is **never** written in the source code. `OpenWeatherClient` is handed an already-constructed `ApiKeyLoader`, which looks first for an `OPENWEATHER_API_KEY` environment variable and, failing that, for a line `OPENWEATHER_API_KEY=...` in a local `.env` file in the project root. `.env` is listed in `.gitignore`, so Git never commits it. Step-by-step instructions for obtaining a free key and installing it (via `.env`, an IntelliJ run configuration, or a shell environment variable) are in `README.md`, under "A3 REQ5 — The Wanderer (API, HD)".
