# Feature Proposal

This file lists, for each custom/creative requirement, the Pitch, the Mechanics and the Architecture (the two new abstractions and the six concrete classes that implement them, marked **New** or **Retrofitted**), as required by the REQ3/REQ4 rules and, for REQ5, the additional Request/Schema/Security items required by the REQ5 (HD) rules. AI acknowledgement is recorded separately in `docs/FoundationModelsUsed.txt`.

> **REQ3 — Infestation and Pest Control:** this section is not included here yet. Architecture summary for whoever adds it: abstractions `Infestation` (game.pests) and `PestDeterrent` (game.pestcontrol), each with 3 new concrete classes — `SlimeSnail`/`LocustSwarm`/`BurrowingMole` and `PestTrap`/`LadybugJar`/`SaltBarrier`. Full detail is already written up in `docs/A3 Design Rationale/A3_Design_Rationale.pdf`; please copy the Pitch/Mechanics/Architecture into this file before submission — the rubric says a marker is not obliged to go looking for it if it is not listed here.

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

**Abstraction 1 — `Enchantable`** (interface, `game.items.tools`): a tool that can carry an Enchantment and trigger it. Implemented by 3 concrete classes, all **Retrofitted** from Assignment 1/2 (modified to extend the new `AbstractEnchantableTool`, which implements `Enchantable`):
1. `Hoe` (`game.items.tools.Hoe`) — **Retrofitted**
2. `WateringCan` (`game.items.tools.WateringCan`) — **Retrofitted**
3. `Pickaxe` (`game.items.tools.Pickaxe`) — **Retrofitted**

**Abstraction 2 — `Enchantment`** (interface, `game.items.tools`): a magical effect a tool can carry and fire once. Implemented by 3 concrete classes, all **entirely new**:
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

No `instanceof` is used anywhere in this feature; the matching `Enchantable` view is found by reference-identity comparison against the tool already obtained from the inventory (documented in the code as a deliberate, narrow use of Connascence of Identity, not a workaround). Full SOLID/DRY/KISS/YAGNI discussion and alternatives considered are in `docs/A3 Design Rationale/A3_Design_Rationale.pdf`.

---

## REQ5 — The Wanderer (A.P.I., HD)

### The Pitch

A Wanderer roams the farm, and what it does depends on the real weather at the real-world place its current tile represents — the farm map is laid over the real world, so the Wanderer's own position decides which place the game asks the weather for. Every 5 turns it reacts to the current weather where it stands (a storm shelter, a rain dance, or a campfire); when an adjacent actor asks it for tomorrow's forecast, it reveals an omen (a frost, a sowing, or a drought warning) drawn from tomorrow's weather at that same place. Both halves change the farm or an actor directly — new ground, new items, status effects, forced plant growth — never only a printed line.

### The Mechanics

- Every 5 turns, the Wanderer converts its own `Location` into a latitude/longitude (`MapGeography`, 2 degrees per tile, anchored at Melbourne: -37.81, 144.96) and asks `OpenWeatherClient` for the real current weather there.
- `WeatherInterpreter` picks exactly one `WeatherReaction`, in a fixed priority order, and the Wanderer performs it that turn:
  - wind ≥ 10 m/s → **Storm Shelter** — blocks the free tiles around it for a duration that grows with the wind speed;
  - Rain, Drizzle or Thunderstorm → **Rain Dance** — tills the bare ground and freely waters the crops in a radius that grows with the rainfall;
  - temperature ≤ 10°C → **Campfire** — lights 1–8 adjacent tiles on fire, more the colder it is.
- When an actor stands next to the Wanderer, it is offered an "ask about tomorrow's weather" action. Choosing it asks for tomorrow's forecast at the Wanderer's own position, and `WeatherInterpreter` picks exactly one `ForecastOmen`:
  - Snow, or forecast ≤ 2°C → **Frost Omen** — pauses the growth of every Plant in a 2-tile radius for a duration that grows the colder it is;
  - Rain, Drizzle or Thunderstorm → **Sowing Omen** — a new Strawberry Seedling takes root on every bare Soil tile in a 2-tile radius;
  - Clear and forecast ≥ 25°C → **Drought Omen** — drains the asking actor's stamina over the following turns.
- If there is no API key, no network, or the request fails or takes more than 5 seconds, the Wanderer only wanders and says the sky is unreadable; the forecast action reports that tomorrow's weather is unreadable. The game never stops because of the API.
- Every message names the weather reading that caused it and the exact tiles that changed.

### The Architecture

**Abstraction 1 — `WeatherReaction`** (interface, `game.wanderer`): what the Wanderer does to the farm because of the weather it stands in right now. Implemented by 3 concrete classes, all **entirely new**:
1. `StormShelterReaction` — **New**
2. `RainDanceReaction` — **New**
3. `CampfireReaction` — **New**

**Abstraction 2 — `ForecastOmen`** (interface, `game.wanderer`): what tomorrow's forecast brings when an actor asks the Wanderer for it. Implemented by 3 concrete classes, all **entirely new**:
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
