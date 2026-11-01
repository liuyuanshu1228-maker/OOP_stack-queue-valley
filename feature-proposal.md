# Feature Proposal

## REQ3: Infestation and Pest Control

### The Pitch

Pests such as a slime snail, a hopping locust swarm and a burrowing mole move towards the Farmer's plants and eat them, each moving in its own way. The Farmer defends the farm with a pest trap, a jar of ladybugs or a bag of salt. Each defence works in a different way: it pulls pests in, hunts them, or pushes them out.

### The Mechanics

**How pests move**

- Each pest looks for the nearest plant within four tiles and moves towards it. Each kind moves differently:
  - **LocustSwarm** (`z`) jumps two tiles in one direction and one tile sideways, like a chess knight.
  - **SlimeSnail** (`q`) crawls to one adjacent tile.
  - **BurrowingMole** (`r`) burrows one or two tiles in a straight line: up, down, left or right.
- A pest lands on the tile that brings it closest to the plant, or on a random landing tile if no plant is near. It cannot land on a tile it cannot enter, but a jumping pest can pass over one.

**How pests eat**

- When a pest stands on a plant, it eats it. The LocustSwarm also eats when a plant is next to it.
- Eating makes the plant go back one growth stage. A plant that has no earlier stage dies, and its tile becomes Dirt, as when a crop is removed.
- **SlimeSnail:** eats the plant it stands on, and covers the bare Soil and Dirt around it with slime (`:`). Slimed tiles cannot be planted until the slime dries after five turns.
- **LocustSwarm:** makes every plant on its tile and around it go back one stage. It has four meals. After a meal it has a 30% chance to split off a smaller swarm, which takes half of the meals left. It flies away after its last meal.
- **BurrowingMole:** eats the plant it stands on, and turns the bare Soil on the four neighbouring tiles (up, down, left and right) back into Dirt.

**How the Farmer defends the farm**

The Farmer picks a defence from the menu while carrying it. Each defence is used up once used.

- **PestTrap:**
  - The Farmer sets it on the tile they stand on, if no plant grows there. The tile becomes a trap (`X`).
  - Each turn the trap pulls every pest within two tiles one tile closer to it. A pest that ends up on the trap or next to it is captured and removed from the map.
  - After two captures, or after ten turns, the trap is used up and the tile returns to what it was.
  - The Farmer, the animals and the ladybugs are not affected.
- **LadybugJar:**
  - The jar opens and releases up to three ladybugs (`e`) on the free tiles around the Farmer.
  - Each ladybug lives for five turns. On each turn it eats a pest next to it, or wanders if there is none.
- **SaltBarrier:**
  - The bag turns the bare Soil and Dirt around the Farmer into salt ground (`+`) for a few turns.
  - Pests cannot enter salt ground. Pests already standing on it are hurt and pushed out.
  - Salted tiles cannot be planted while the salt lasts.

**In the game**

- Every action tells the player what happened, with the coordinates of the tile, for example which plant was eaten or which pest was captured.
- A pest that eats nothing, and a trap that pulls and captures nothing, print nothing that turn.
- At the start of the game, the world places one pest of each kind on the farm. It also gives the Farmer one trap, one jar of ladybugs and one bag of salt.

### The Architecture

**The two new abstractions (both are interfaces):**

1. `Infestation`: what a pest does to the plant it stands on (`feedOn(Location)`).
2. `PestDeterrent`: what a defence does to the pests around it (`deterAround(Location, GameMap)`).

**The six concrete classes:**

| Abstraction | Concrete class | New or retrofitted |
|---|---|---|
| `Infestation` | `SlimeSnail` | New |
| `Infestation` | `LocustSwarm` | New |
| `Infestation` | `BurrowingMole` | New |
| `PestDeterrent` | `PestTrap` | New |
| `PestDeterrent` | `LadybugJar` | New |
| `PestDeterrent` | `SaltBarrier` | New |

All six are entirely new classes. No Assignment 1 or 2 class is retrofitted.

**Supporting classes (not counted among the six):**

- Shared by the pests: `Pest` (abstract, implements `Infestation`), `SeekPlantBehaviour` (a `Behaviour`) and `InfestAction` (an `Action`).
- Shared by the defences: `DeterrentItem` (abstract, implements `PestDeterrent`), `DeployDeterrentAction` (an `Action`) and `PestPresent` (a `TileCondition` that finds pests).
- Ladybugs: `Ladybug` (an `Actor`) and `EatPestAction` (an `Action`).
- Ground that lasts for a few turns: `TemporaryGround` (abstract), `SlimeGround`, `SaltGround` and `TrapGround`.

**Existing classes that REQ3 uses but does not change:** `WanderingActor`, `WanderBehaviour`, `TileCondition`, `Area`, `AreaFactory`, `RandomUtils`, `Soil`, `Dirt` and `Unplanted`. `RandomUtils` and `AreaFactory` are passed in through constructors.

**Existing class that REQ3 changes:** `StackQueueValleyWorld` places one pest of each kind on the farm and gives the Farmer one of each defence.

**Classes from REQ1 that REQ3 depends on:** `Plant` and `Regressible`. Pests only know the `Regressible` interface, so they never depend on a concrete crop.

### How it meets the complexity rules

- **Cross-component interaction:** an `Item` (the defence) offers an `Action` (`DeployDeterrentAction`), which changes `Ground` (trap or salt) and moves or removes `Actor`s (pests). A pest `Actor` changes a plant `Ground` through `Regressible`.
- **Spawning:** the swarm splits into a new swarm, and the jar releases ladybugs.
- **Area of effect:** the swarm sets back every plant around it; slime and salt cover every bare tile around them.
- **Terrain change:** slime, salt and the trap replace ground and later restore it; the mole turns Soil back into Dirt.
- **Forced movement:** the trap pulls pests towards itself, and salt pushes them out.

### Why it is different from the earlier requirements

- The A2 `Crow` is a state-machine creature that steals items and flees. The pests here have a simple behaviour; their complexity is in their effects on plants and terrain.
- The `Crow` flees from the Farmer by its own choice. Here the pest trap moves pests by force, pulling them towards itself, and salt pushes them out.
- The three pests move in three different ways (jump, crawl, burrow), so the same plant-seeking rule produces different paths.
- The three defences work in three different ways: they pull pests in, hunt them, and push them out.

## REQ4: Enchanted Tools

### The Pitch

The Farmer can imbue their Hoe, Watering Can or Pickaxe with magic. An enchanted tool does not just till, water or break — it also triggers a second, independent magical effect the next time it is used, then the magic is spent. Two complex effects fire from a single action: the tool's own amplified version of its job, and whichever Enchantment was attached to it.

### The Mechanics

**Enchanting a tool**

- The Farmer can enchant any Hoe, Watering Can or Pickaxe they carry with a Blaze, Bounty or Shatter Enchantment, chosen from that tool's own menu.
- A tool holds at most one Enchantment at a time; enchanting an already-enchanted tool replaces the one it carries.

**What happens the next time an enchanted tool is used**

Two effects fire together, in order:

1. The tool's own effect — fixed per tool, fires every time it is used while enchanted, independent of which Enchantment is attached:
   - **Hoe:** tilling also tills every tillable tile adjacent to the one tilled (up to the 8 surrounding tiles).
   - **Watering Can:** watering also waters one other waterable tile adjacent to it, chosen at random.
   - **Pickaxe:** breaking has a 40% chance of also breaking one random breakable tile adjacent to it.
2. The attached Enchantment's effect, fired against every tile the tool's own effect touched, then the Enchantment is spent:
   - **Blaze Enchantment:** ignites a Fire on every affected tile.
   - **Bounty Enchantment:** forces an extra growth attempt on any plant occupying an affected tile.
   - **Shatter Enchantment:** stuns any actor standing on an affected tile.

**In the game**

- An unenchanted tool behaves exactly as it always has; nothing about ordinary tilling, watering or breaking changes.
- Every outcome — the base action, the tool's own spread, and the Enchantment's effect — names the coordinates of every tile it touched.
- At the start of the game, the Farmer's Hoe, Watering Can and Pickaxe can each be enchanted with any of the three Enchantments.

### The Architecture

**The two new abstractions (both are interfaces):**

1. `Enchantable`: what a tool does to carry and fire an attached Enchantment (`enchant(Enchantment)`, `triggerEnchantment(Actor, Location)`).
2. `Enchantment`: the magical effect a tool fires once, against the tiles its own effect touched (`applyTo(List<Location>, Actor, Location)`).

**The six concrete classes:**

| Abstraction | Concrete class | New or retrofitted |
|---|---|---|
| `Enchantable` | `Hoe` | Retrofitted (A1) |
| `Enchantable` | `WateringCan` | Retrofitted (A1) |
| `Enchantable` | `Pickaxe` | Retrofitted (A2) |
| `Enchantment` | `BlazeEnchantment` | New |
| `Enchantment` | `BountyEnchantment` | New |
| `Enchantment` | `ShatterEnchantment` | New |

Three retrofitted, three entirely new, matching the "at least three new, up to three retrofitted" rule.

**Supporting classes (not counted among the six):**

- `AbstractEnchantableTool` (abstract, implements `Enchantable`): the base class `Hoe`, `WateringCan` and `Pickaxe` now extend. Owns the generic store/fire/consume logic (through `EnchantmentSlot`) and offers one `EnchantAction` per Enchantment a tool can be enchanted with, so this shared behaviour is written once instead of being repeated in all three tools.
- `EnchantmentSlot`: composition helper that stores at most one Enchantment, fires it against a list of tiles, then clears itself.
- `EnchantAction` (an `Action`): lets the Farmer attach one chosen Enchantment to one chosen Enchantable tool.
- `EnchantmentTrigger`: composition helper used by `CapabilityToolAction` (tilling/watering) and `BreakAction` (breaking) to find the specific tool instance just used among the Actor's items and fire its Enchantment, by reference equality against the matching `Enchantable` view — not `instanceof`, since `Inventory.getItemsAs` already returns a capability-narrowed view that cannot be asked for a second capability.

**Existing classes that REQ4 uses but does not change:** `AreaFactory`, `RandomUtils`, `Fire`, `StunnedStatus`, `Plant` (through `attemptGrowth`). All are passed in through constructors.

**Existing classes that REQ4 changes:** `CapabilityToolAction` and `BreakAction` each gained one line giving the tool just used a chance to fire its Enchantment (through `EnchantmentTrigger`), after their own effect has already run; `StackQueueValleyWorld` builds the three Enchantments once and offers all three to the Farmer's Hoe, Watering Can and Pickaxe.

### How it meets the complexity rules

- **Cross-component interaction:** an `Item` (the tool) offers an `Action` (`EnchantAction`) that changes the tool's own state; using the tool then fires an `Enchantment` that changes `Ground` (a Fire item), a `Plant`'s growth stage, or an `Actor`'s `Status`.
- **Spawning:** the Blaze Enchantment spawns a Fire item on every affected tile.
- **Area of effect:** every tool's own effect and every Enchantment acts on a list of affected tiles, never a single fixed one.
- **Status effect:** the Shatter Enchantment applies `StunnedStatus`.
- **Terrain modification:** the Hoe's own effect tills adjacent Dirt into Soil.

### Why it is different from the earlier requirements

- REQ3's pests act on their own turn, independently of the Farmer; REQ4's Enchantments only ever fire as a direct consequence of the Farmer's own tool use, never on their own.
- REQ3 is about defending the farm against something attacking it; REQ4 is about amplifying the Farmer's own ordinary actions.
- Where REQ1/REQ2's tools each do one fixed thing, REQ4 lets the same three tools carry one of three independent, swappable effects chosen by the player rather than fixed in the tool's own code — the Enchantment a tool carries can change from one use to the next.

## REQ5: The Wanderer (A.P.I)

<!-- Team note: the thresholds below are the agreed contract for the concrete classes.
     If the code changes a number, change it here too. Confirm the idea with the TA. -->

### The Pitch

A Wanderer roams the farm, and what it does depends on the real weather at the place on Earth where it stands. Its map position is turned into a real latitude and longitude, so every step it takes changes the weather it lives in. The feature is a new actor driven by weather data, not a weather system that changes the map by itself.

### The Mechanics

**Where the Wanderer is on Earth**

- The farm map is laid on the real world. The top-left tile is Melbourne (latitude -37.81, longitude 144.96), and each tile is 2 degrees: one step right is 2 degrees further east, one step down is 2 degrees further south.
- So the place the game asks about is decided by the Wanderer's own position, and changes as it moves.

**What the current weather makes it do**

Every 5 turns the Wanderer asks for the current weather where it stands and reacts. The first rule that matches is used:

| Current weather | Reaction | What happens |
|---|---|---|
| Wind of 10 m/s or more | **StormShelterReaction** | It builds a shelter on the free tiles around it (the ones with no actor standing on them). The tiles are blocked for a number of turns that grows with the wind, then return to what they were. |
| Rain, Drizzle or Thunderstorm | **RainDanceReaction** | It dances for rain: the bare Dirt around it turns into Soil, and every crop around it gets a free watering. The area grows with the rainfall. |
| 10°C or colder | **CampfireReaction** | It lights a campfire. The colder it is, the more tiles around it catch alight. The fire burns any actor that walks into it. |
| Anything else | none | It simply wanders. |

**What tomorrow's weather brings**

A Farmer standing next to the Wanderer can ask it for the forecast. The Wanderer asks for the weather at its position about 24 hours ahead, and an omen follows. The first rule that matches is used:

| Tomorrow's weather | Omen | What happens |
|---|---|---|
| Snow, or 2°C or colder | **FrostOmen** | Every plant around the Wanderer stops growing for a few turns. |
| Rain, Drizzle or Thunderstorm | **SowingOmen** | A Strawberry Seedling takes root on every bare Soil tile around the Wanderer. |
| Clear and 25°C or hotter | **DroughtOmen** | The Farmer is parched, and loses stamina every turn for a few turns. |
| Anything else | none | The Wanderer sees nothing unusual. |

**In the game**

- Every reaction and omen reports what it did, with the coordinates of the tiles it changed, and the weather that caused it.
- At the start of the game, the world places one Wanderer on a free tile of the farm.
- If there is no API key, no network, or the request fails, the Wanderer only wanders and the Farmer is told the sky is unreadable. The game never stops.

### The Architecture

**The two new abstractions (both are interfaces):**

1. `WeatherReaction`: what the Wanderer does to the farm because of the current weather (`reactAround(Location)`).
2. `ForecastOmen`: what tomorrow's weather brings when the Farmer asks (`bringUpon(Actor, Location)`).

Each concrete class is made with the weather reading that chose it, and uses its values to decide how strong it is.

**The six concrete classes:**

| Abstraction | Concrete class | New or retrofitted |
|---|---|---|
| `WeatherReaction` | `StormShelterReaction` | New |
| `WeatherReaction` | `RainDanceReaction` | New |
| `WeatherReaction` | `CampfireReaction` | New |
| `ForecastOmen` | `FrostOmen` | New |
| `ForecastOmen` | `SowingOmen` | New |
| `ForecastOmen` | `DroughtOmen` | New |

All six are entirely new classes.

**The two higher-level classes that depend only on the abstractions:**

1. `Wanderer` (an `Actor`): applies a `WeatherReaction` and never names a concrete reaction.
2. `AskForecastAction` (an `Action`): brings a `ForecastOmen` upon the asker and never names a concrete omen.

**Supporting classes (not counted among the six):**

- The API layer, in `game.weather`: `WeatherSource` (an interface, so the game never depends on the real API), `OpenWeatherClient` (the real one), `OpenWeatherParser` (reads the JSON), `WeatherReading` (the values the game uses), `ApiKeyLoader` (finds the key) and `MapGeography` (turns a tile into a latitude and longitude).
- `WeatherInterpreter`: the one class that names the six concrete classes. It applies the tables above to a reading.

### The Request

The current weather where the Wanderer stands, for example at tile (3, 1):

```
https://api.openweathermap.org/data/2.5/weather?lat=-39.81&lon=150.96&units=metric&appid=${OPENWEATHER_API_KEY}
```

Tomorrow's forecast at the same place, in nine three-hour steps; the last one is about 24 hours ahead:

```
https://api.openweathermap.org/data/2.5/forecast?lat=-39.81&lon=150.96&units=metric&cnt=9&appid=${OPENWEATHER_API_KEY}
```

- `lat` and `lon` come from the game state: the Wanderer's tile (x, y) gives `lat = -37.81 - 2y` and `lon = 144.96 + 2x`. Every move the Wanderer makes changes the query.
- `appid` is the API key, read from the environment or a local `.env` file, never from the code.

### The Schema

The current weather reply. The game reads the fields marked below and ignores the rest:

```json
{
  "weather": [ { "main": "Rain", "description": "light rain" } ],
  "main": { "temp": 12.3, "humidity": 81 },
  "wind": { "speed": 6.2 },
  "rain": { "1h": 2.1 },
  "name": "Melbourne"
}
```

- `weather[0].main`: the kind of weather (required).
- `main.temp`: the temperature in °C (required).
- `wind.speed`: the wind speed in m/s (0 if missing).
- `rain.1h`: the rainfall in the last hour in mm (0 if missing; it is left out when dry).

The forecast reply is a list of entries with the same fields. The game reads the last entry, and its rainfall is `rain.3h`, which it divides by 3 to get an hourly amount:

```json
{
  "list": [
    { "weather": [ { "main": "Clouds" } ], "main": { "temp": 15.0 }, "wind": { "speed": 3.0 } },
    { "weather": [ { "main": "Snow" } ], "main": { "temp": -2.5 }, "wind": { "speed": 9.0 }, "rain": { "3h": 6.0 } }
  ]
}
```

A reply that is missing a required field, or is not this shape, gives no reading, and the game carries on without the weather.

### CRITICAL SECURITY WARNING

OpenWeatherMap needs an API key. The key is never written in the code or committed:

- The game reads it from the environment variable `OPENWEATHER_API_KEY`, or from a line `OPENWEATHER_API_KEY=...` in a `.env` file in the project folder.
- `.env` is listed in `.gitignore`, so it is never committed or pushed.
- The `README.md` explains, step by step, how to get a free key, how to set it, and how to run the game.

### How it meets the complexity rules

- **Rule 1:** the query is driven by the Wanderer's map position, and the reply creates, changes or removes objects: shelter ground, fire, Soil, new seedlings and statuses. It never only prints text.
- **Rule 2:** every concrete class changes terrain, spawns something, applies a status or works on an area.
- **Rule 3:** two new abstractions, three new concrete classes each, and two higher-level classes that depend only on the abstractions.
- **Tests:** no test uses the network. `WeatherSource` lets every test use fixed readings, and the client's request is replaced in its own tests.
