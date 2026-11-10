# FIT2099 Assignment 3 Design Rationale

## Jingyu Zhang 37033956, Yuanshu Liu 36226173, Poyee Tan 36641162 – REQ 5: The Wanderer (API, HD)

The Wanderer's tile is turned into a real latitude and longitude, and OpenWeatherMap's weather there decides what it does. Every 5 turns, today's weather triggers a `WeatherReaction` (storm shelter, rain dance or campfire). A Farmer next to it can ask for tomorrow's forecast, which brings a `ForecastOmen` (frost, sowing or drought). We had four design goals. The game does not depend on the weather API. The game keeps running when no weather can be read. Each part of a request has one class. Adding a weather effect means writing one new class. We only added an abstraction where it was needed. Either the proposal names six effects, or the network must stay out of tests (KISS, YAGNI).

## A. The game asks an interface, and "no reading" is a normal answer

The Wanderer never talks to OpenWeatherMap directly, and a failed request must not stop the game.

**Keeping the game independent of the weather API.**

**Design 1:** `Wanderer` and `AskForecastAction` build the URL, send the request and read the JSON themselves.

| Pros | Cons |
|---|---|
| There is one interface and one class fewer to read, and the whole weather path sits in one place (KISS). | Game classes would depend on `HttpClient` and on OpenWeatherMap's JSON shape. Another company controls that shape, and it can change at any time (DIP, Connascence of Algorithm). |
|  | Every test of the Wanderer would need the network or a fragile HTTP mock, so tests would not be repeatable. |
|  | Changing the weather provider would mean editing `Wanderer` and `AskForecastAction`. Neither has anything to do with HTTP (Shotgun Surgery). |

**Design 2 (chosen):** a `WeatherSource` interface with `currentWeatherAt(lat, lon)` and `tomorrowWeatherAt(lat, lon)`, both returning `Optional<WeatherReading>`. `OpenWeatherClient` is the real one and is injected through the constructor. Tests inject `FixedWeatherSource`.

| Pros | Cons |
|---|---|
| Game classes depend on an interface the game owns, not on the HTTP library (DIP). A change in the library stays inside `game.weather`. It does not reach the game classes, as long as the four values stay the same. | There is one more interface and one more class to learn before the feature makes sense. Acceptable because they are what keep the network out of the game and its tests. |
| `FixedWeatherSource` replaces the real client, so no REQ5 test uses the network (LSP). | The interface only carries the four values the game uses, so extra data needs a change to `WeatherReading`. Acceptable because a small interface is what lets any provider fit it. The proposal's rules only use these four values. |
| Another provider, or a cached one, needs no change to `Wanderer`, `AskForecastAction` or `WeatherInterpreter` (OCP). |  |

The package `game.weather` imports nothing from the rest of the game, so it could be reused by any feature. **Foresight:** a weather source that replays saved readings for a demo needs one new class.

**When the sky cannot be read.** With no key, no network or a bad reply, there is no reading.

**Design 1:** throw a checked exception.

| Pros | Cons |
|---|---|
| It is normal Java, and the compiler makes every caller handle the failure. The exception can also say why the reading failed. | No reading is a normal case, not an error. Yet every caller would need its own `try/catch` for it (Duplicate Code). |

**Design 2:** return `null`.

| Pros | Cons |
|---|---|
| It is short, needs no wrapper type, and every Java reader already knows the pattern (KISS). | A forgotten check crashes the game, and scattered `null` checks are a known smell. |
|  | The rule "`null` means no reading" is invisible to the compiler and lives only in comments (Connascence of Meaning). |

**Design 3 (chosen):** return `Optional<WeatherReading>`.

| Pros | Cons |
|---|---|
| "No reading" is part of the return type, so every caller sees it. Every source keeps the same contract (LSP). | A caller that calls `get()` without checking still fails. So the risk is smaller, but not gone. Acceptable because there are only two callers, and both check `isEmpty()` first. |
| The game handles it in one place per caller with `isEmpty()`, with no exception handling. | `Optional` invites functional chains, which the team avoids. Acceptable because we use it only with `isEmpty()` and `get()`, with no chained calls. |

When there is no reading, the Wanderer just wanders. `UnreadableSkyAction` reports "The sky is unreadable, and the Wanderer only wanders." `AskForecastAction` reports that tomorrow's weather is unreadable. A missing reading does not stop the game, and the player is told what happened.

**Connascence:** Connascence of Algorithm with OpenWeatherMap's JSON layout stays inside `game.weather`. The rest of the game shares only Connascence of Type on `WeatherSource`. Returning `Optional` avoids the Connascence of Meaning a `null` result would need. **Code smells:** avoided Shotgun Surgery (changing provider would otherwise edit game classes), network calls in tests and scattered `null` checks.

## B. The API layer: place, key and reply each have one class

Behind `WeatherSource`, three small classes each own one part of a request. One says where to ask. One finds the key. One reads the reply.

**Turning the Wanderer's tile into a real place.** The brief says the request cannot be a fixed URL. The game state must change the query.

**Design 1:** always ask about one fixed place, such as Melbourne.

| Pros | Cons |
|---|---|
| It is easy to write and needs no extra class. The same answer for every tile also makes testing easy (KISS). | It breaks the brief's rule against a fixed URL. The game state would not change the query. |
|  | The coordinates would be magic numbers inside a URL string, with nothing to say which number is which. |

**Design 2 (chosen):** `MapGeography` turns a tile's (x, y) into a latitude and longitude. The top-left tile is Melbourne (-37.81, 144.96), and each tile is 2 degrees. `Wanderer` and `AskForecastAction` ask it about the tile the Wanderer stands on, so every move changes the query. Latitude stops at the poles and longitude wraps past 180, so any tile gives a valid place.

| Pros | Cons |
|---|---|
| The query is driven by the game state, as the brief requires. | It is one more small class, and its constructor takes three `double`s in a fixed order (Connascence of Position). Acceptable because the constructor is called once, in `StackQueueValleyWorld`. |
| One class knows how map tiles match the real world (SRP). Moving the farm only changes its constructor arguments. | Two degrees per tile puts many tiles over the sea, so many readings come from ocean weather. Acceptable because the scale is one constant to change, and ocean weather still varies. |
| `latitudeOf` and `longitudeOf` name the two numbers, so callers cannot swap them. |  |

**Keeping the API key out of the code.**

**Design 1:** `OpenWeatherClient` calls `System.getenv("OPENWEATHER_API_KEY")` itself.

| Pros | Cons |
|---|---|
| There is one class fewer, and reading the key is one line (KISS). | The client would also decide where secrets live. Moving the key would mean editing the network class (SRP, Divergent Change). |
|  | A test could not control the key without changing the real environment, so tests would depend on external state. |

**Design 2 (chosen):** `ApiKeyLoader` takes the key's name, an environment map and a `.env` path. It checks the environment first, then a `NAME=value` line in `.env`, and returns `Optional<String>`. The client receives a loader through its constructor.

| Pros | Cons |
|---|---|
| The loader finds the key and the client calls the API, so each changes for one reason (SRP). | It is one more class and one more constructor parameter for the client. Acceptable because keeping a secret out of the code is worth one small class. |
| A test passes a plain map and a temporary file, with no real environment. | The key's name must match in the loader, the `.env` file and the README (Connascence of Name across files). Acceptable because `StackQueueValleyWorld` holds the name once. |
| The key is never in the source, and `.env` is in `../../../.gitignore`, so it cannot be committed by mistake (security). |  |

**Reading the reply.**

**Design 1:** read the JSON inside `OpenWeatherClient`.

| Pros | Cons |
|---|---|
| There is one class fewer, and the request and its reply sit together (KISS). | The client would both talk to the network and know the JSON layout. Two different kinds of change would edit the same class (SRP, Divergent Change). |
|  | The JSON could only be tested through a network call, or by overriding the client, instead of with saved text. |

**Design 2 (chosen):** `OpenWeatherParser` turns a reply into a `WeatherReading`. The weather condition and the temperature are required. Wind and rain default to 0, because OpenWeatherMap leaves out `rain` on dry days. For the forecast, it reads the last of nine 3-hour steps, about 24 hours ahead. It divides `rain.3h` by 3 to get rain per hour. A reply missing a required field gives no reading. `WeatherReading` is a small value object with `private final` fields. Its constructor rejects a blank condition, a value that is not a finite number, and negative wind or rain.

| Pros | Cons |
|---|---|
| Only the parser knows the JSON layout (SRP). It is tested with saved JSON text, with no network. | Gson is an extra library, and the parser still knows the field names (Connascence of Algorithm with the API format). Acceptable because Gson comes through Maven, and the field names are confined to one class. |
| A bad reading cannot be created at all. So the reactions do not repeat these checks. | A reply with finite but odd values, such as 500°C, is still accepted. Acceptable because each effect has a time limit (below). An odd value only makes an effect longer or shorter, within that limit. The tests cover extreme values. |
| `WeatherReading` is not a passive Data Class, because it validates and describes itself. | The checks live in the constructor, so a test cannot build a broken reading to see how an effect reacts. Acceptable because a broken value takes the real path instead. The parser turns it into "no reading", and the tests cover that path. |

**Bad values.** The parser catches a rejected reading and turns it into "no reading". The game then takes the normal unreadable-sky path. `StormShelterReaction` and `FrostOmen` turn the wind or the cold into extra turns. Each adds at most 100 turns, far more than any real weather needs. Without the cap, a huge value would overflow `int` and make a negative duration. Tests cover infinite values and extreme finite ones.

**Connascence:** `latitudeOf` and `longitudeOf` name the two numbers, reducing Connascence of Position, though the three-`double` constructor still has it. The key's name is written in one place (Connascence of Name). Only the parser depends on the API's format (Connascence of Algorithm). **Code smells:** avoided a static URL, magic numbers, a secret in the source code and Divergent Change in the client. Also avoided Primitive Obsession (four loose numbers) and a passive Data Class, because `WeatherReading` validates and describes itself.

## C. From a reading to an effect: one interpreter, two interfaces

A reading must become a reaction today or an omen tomorrow, without the Wanderer knowing the rules.

**Choosing which effect a reading triggers.** The proposal fixes six rules. One is "wind of 10 m/s or more builds a shelter".

**Design 1:** an if-chain inside `Wanderer.playConsciousTurn` that also performs each effect.

| Pros | Cons |
|---|---|
| There is no extra class, and the rules sit where they are used (KISS). | The actor would also hold every weather rule (SRP), and a new reaction would edit `Wanderer` (OCP). |
|  | A reader would have to guess what the bare numbers mean (Connascence of Meaning). The forecast action would also need a second copy of the rules. |

**Design 2:** a configurable rule engine: a list of (condition, effect) pairs, perhaps read from a file.

| Pros | Cons |
|---|---|
| Rules could change without recompiling, and a new rule would edit no class (OCP). | The six rules are fixed by the proposal, so the flexibility would never be used (YAGNI). |
|  | A general list is harder to read than a few `if`s. It solves a problem we do not have (Speculative Generality, KISS). |
|  | Conditions read from a file would lose compile-time checking, so a typo would only show up while playing. |

**Design 3 (chosen):** `WeatherInterpreter` with `reactionFor(reading)` and `omenFor(forecast)`. It is the only class that creates the six concrete classes, so it works as a simple factory.

| Pros | Cons |
|---|---|
| `Wanderer` sees only `WeatherReaction`, and the rules have one home (SRP, DIP). | A new rule adds one `if` here, so OCP is only partly met. Acceptable because this is the one place that creates effects. Everything else stays closed. A few named `if`s are easier to check than a rule engine. |
| Each threshold is a named constant, written only here. A reader can check every rule against the proposal in one class (DRY). | This one class names all six concrete classes (Connascence of Name with each). Acceptable because it is the only class that creates them, so no other class names them. |
| Today's and tomorrow's rules share helpers such as `isRainy`, so a condition is written once. |  |

**Thresholds passed in, not repeated.** `CampfireReaction` and `FrostOmen` grow with the cold. They measure how far the temperature is below their threshold.

**Design 1:** each of the two classes keeps its own copy of the threshold (our first version).

| Pros | Cons |
|---|---|
| Each class reads alone, with the number it needs written beside the code that uses it. | 10°C and 2°C would be written twice (Connascence of Value across classes). |
| The two effect constructors stay one parameter shorter, so creating an effect needs one value less. | Changing a threshold in `WeatherInterpreter` would leave the effect sized against the old number, and nothing would warn us. |

**Design 2 (chosen):** `WeatherInterpreter` passes the threshold into their constructors.

| Pros | Cons |
|---|---|
| Each number has one source, so changing a rule changes the effect's size with it (DRY). | Each of the two constructors has one more parameter. Acceptable because only `WeatherInterpreter` calls them. |
| The effect classes stay free of rule constants, so they only describe what the effect does. | A caller could pass a different threshold from the one that chose the effect. Acceptable because `WeatherInterpreter` passes the same constant it tested. |

**Two interfaces, not one.**

**Design 1:** one interface for both sides, with `reactAround(Location)` and `bringUpon(Actor, Location)`.

| Pros | Cons |
|---|---|
| There is one interface for every weather effect. The interpreter could return the same type for today and tomorrow (KISS). | Every reaction would carry an omen method it cannot use, and every omen a reaction method (ISP). |
|  | The unused bodies would do nothing or throw, so a caller could not trust every implementation (LSP risk). This is the Refused Bequest smell. |

**Design 2 (chosen):** `WeatherReaction` (`reactAround(Location)`) and `ForecastOmen` (`bringUpon(Actor, Location)`). Both are chosen in the same `WeatherInterpreter`. Picking an effect for a reading is one job, whether the reading is for today or tomorrow.

| Pros | Cons |
|---|---|
| Each class implements only the method it needs, so no effect carries a method it cannot honour (ISP). | There are two interfaces that look alike, which a reader must learn apart. Acceptable because they really differ: a reaction acts on a place, while an omen acts on the actor who asked. |
| `AskForecastAction` only sees a `ForecastOmen`. `Wanderer` and `ReactToWeatherAction` only see a `WeatherReaction`. None of them names a concrete effect class (DIP). | Both depend on `WeatherReading` (Connascence of Type), so a new field there touches both. Acceptable because `WeatherReading` is a small value object that rarely changes. |

**Effects that last several turns.** The drought omen makes the asker lose 2 stamina a turn for 3 turns. It uses `DroughtWearinessStatus`, a `TimedStatus` (A2).

**Design 1:** spend stamina with `StatisticAdjuster.trySpend` and print nothing (our first version).

| Pros | Cons |
|---|---|
| It reuses an existing, tested method. The status is only a few lines, and no A2 class changes. | `trySpend` is all-or-nothing, so a Farmer with 1 stamina lost nothing, against the proposal's "loses stamina every turn". |
|  | The player saw no sign of the drain, because a status has no return value to report through. |

**Design 2 (chosen):** a new `StatisticAdjuster.decrease`, the mirror of `increase`, drains what is there and stops at zero. The status overrides `TimedStatus.describeTurn` to print how much was drained and where.

| Pros | Cons |
|---|---|
| The rule matches the proposal at every stamina level, because the drain stops at zero instead of being skipped. | `StatisticAdjuster`, an A2 class, gained one method. Acceptable because the new method only adds behaviour beside `increase`, and no existing method changed. |
| `decrease` returns a `StatisticChange`, so the report is worded the same way as every other stat change (DRY). | The status keeps the last change in a field between `applyEffect` and `describeTurn`. Acceptable because the field is private and overwritten each turn. |
| `describeTurn` is the hook `BurningStatus` already uses, so `TimedStatus` did not change (OCP). |  |

**Foresight:** a heatwave reaction above 35°C needs one new class, `HeatwaveReaction`, and one new `if` in `WeatherInterpreter`. `Wanderer` and the other reactions do not change.

**Connascence:** named constants avoid Connascence of Meaning. Passing the thresholds in removed Connascence of Value between the interpreter and two effect classes. Each action has Connascence of Type with one interface only. **Code smells:** avoided Switch Statements inside `Wanderer`, and magic numbers. Also avoided Speculative Generality (a rule engine) and Refused Bequest (reactions with an omen method). Accepted: one factory class names the six concrete classes.

## D. Letting the Farmer ask about tomorrow

**Design 1:** give `Farmer` a method that searches the map for a Wanderer and asks it.

| Pros | Cons |
|---|---|
| There is no new action class, and the Farmer's options sit in the Farmer's class. | The data (`WeatherSource`, `MapGeography`, `WeatherInterpreter`) belongs to the Wanderer, not the Farmer (Feature Envy). |
|  | Any other actor that wanted to ask would copy the same search (DRY). The copies would have to stay the same (Connascence of Algorithm). |

**Design 2 (chosen):** `Wanderer.allowableActions` offers an `AskForecastAction` to whoever stands next to it. This is the engine's normal way for one actor to offer an action to another. `ReactToWeatherAction` and `AskForecastAction` only pass the turn on to the reaction or omen they hold. The engine only offers actions from actors on the map. So `AskForecastAction` never needs to check that the Wanderer is still there.

| Pros | Cons |
|---|---|
| It reuses the engine's protocol for one actor offering an action to another, like every other interactive entity (DRY). | Any adjacent actor is offered the action, not only the Farmer. Acceptable because only the player picks menu actions. |
| Each action has one job and names no concrete effect class (SRP). | `AskForecastAction` holds the same three collaborators as `Wanderer` (a small Data Clump). Acceptable because they are passed once, in `allowableActions`. |
|  | `ReactToWeatherAction` only passes the turn on, close to a Middle Man. Acceptable because the engine needs an `Action` object for every turn. |

**Connascence:** only Connascence of Name with the engine's existing API. **Code smells:** avoided Feature Envy (a Farmer method using the Wanderer's data). Accepted: `ReactToWeatherAction` and `UnreadableSkyAction` are close to Middle Men, because the engine needs an `Action` object.

## E. Visibility, `final`, `protected` and `static`

| Choice | Where | Why | Cost |
|---|---|---|---|
| Every class is `public` | All REQ5 classes | `game.weather` is meant to be reused by other features. Being `public` only makes reuse possible. Openness for extension comes from the interfaces and hooks, not from the modifier. | A wider public API, so more classes could depend on them (more connascence across packages). |
| Every field is `private`, and `final` where it never changes | All classes. `WeatherReading` is fully immutable | A reading is shared by the interpreter and the effect it creates, so it must not change. | `Wanderer.turnsSinceLastReaction` and `DroughtWearinessStatus.lastDrain` are the only changing fields. Each is changed by one method. |
| `protected` method | `OpenWeatherClient.fetch(URI)` | The one seam a test overrides to return saved replies without the network. | A subclass could replace the network call. The class comment says it exists for tests. |
| `protected` hook override | `DroughtWearinessStatus.describeTurn` | It overrides the `TimedStatus` hook, so only the template calls it. | Same-package classes could call it. None does. |
| `private` helpers | e.g. `OpenWeatherParser.readingFrom`, `WeatherInterpreter.isRainy` | They are steps, not contracts, so their signatures can change freely. | A subclass cannot reuse a single step. It must override the public method around it. |
| Constants are `private static final` | e.g. `STORM_WIND_THRESHOLD_MPS`, `MAX_EXTRA_TURNS`, `TIMEOUT` | A name explains each number, avoiding magic numbers (Connascence of Meaning). No public constants (A2 feedback). | Tests repeat the numbers. |
| No static methods | Every REQ5 class | A2 feedback penalised static methods as "function thinking". | `MapGeography` and `ApiKeyLoader` are objects passed in, not static utilities, so constructors take more parameters. |

**Connascence:** `private` fields keep connascence on state inside each class. `WeatherReading` is immutable. **Code smells:** avoided public attributes and missing modifiers.

## F. Changes to the earlier design

REQ5 is new in A3. It builds on earlier classes and changes only three of them: `StackQueueValleyWorld`, `StatisticAdjuster` and `Plant`.

| Earlier class | How REQ5 uses or changes it |
|---|---|
| `WanderingActor` (A2) | `Wanderer` extends it, so the unconscious check and wandering are reused. |
| `TemporaryGround` (REQ3) | `StormShelterGround` extends it, so the shelter restores the old ground with no new countdown code. |
| `Fire` (A2) | `CampfireReaction` lights the same `Fire` item. |
| `GrowthPausedStatus` (REQ1), `TimedStatus` (A2) | `FrostOmen` pauses plants. `DroughtWearinessStatus` reuses the countdown and the `describeTurn` hook. |
| `Plant.attemptGrowth` and `Plant.forceGrowth` (REQ1), changed | The pause check moved out of `Plant.tick` into both methods. So the frost also stops REQ4's Bounty (REQ1, Section D). |
| `StatisticAdjuster` (A2), changed | Gained `decrease`, the mirror of `increase`, for the drought. No existing method changed. |
| `Plantable`, `StrawberrySeedling` (REQ1) | `SowingOmen` plants through the same rule as everything else. |
| `StackQueueValleyWorld`, changed | Builds the client, the key loader and `MapGeography`, and places one Wanderer. |

## G. Known minor violations and why we accept them

| Violation or cost | Why it is acceptable |
|---|---|
| The Wanderer can be burnt by its own campfire if it later walks onto a burning tile. | On purpose: `Fire` burns any actor and never asks who lit it, so it needs no type check. A Wanderer that stays somewhere cold pays for its fires, and it has 50 hit points. |
| `OpenWeatherClient` creates its own `HttpClient`, a small DIP gap. | Tests replace `fetch`, not the `HttpClient`, so injecting it would add a parameter nobody varies. |
| The weather condition is a `String` such as `"Rain"`, so the code depends on OpenWeatherMap's names (Connascence of Meaning). | The names are named constants in `WeatherInterpreter`, and the parser is the only class that reads them from JSON. |
| `AskForecastAction` holds the same three collaborators as `Wanderer` (a small Data Clump). | They are passed once, in `allowableActions`. A holder class for three fields would be a class used once. |
| Any actor next to the Wanderer is offered `AskForecastAction`, not only the Farmer. | Only the player picks menu actions. Other actors never choose it. |
| `WeatherInterpreter` names all six concrete classes, so a new reaction edits it. OCP is only partly met. | It is the one factory point (Section C). Everything else stays closed. |
| `ApiKeyLoader` and `OpenWeatherParser` turn errors into an empty result. | The game must keep running without weather. Tests cover the missing key, the missing file, the broken JSON and infinite values. |

## Summary

The final design uses the chosen design of every part. The game owns `WeatherSource`, and "no reading" is a normal answer (A). The place, the key and the reply each have one class (B). One interpreter picks between two narrow effect interfaces (C). Asking about tomorrow uses the engine's own protocol (D). Each abstraction either keeps the network out of the game and its tests, or separates effects the proposal already names. The main cost is more, smaller classes on the API side, and one factory class that names every effect. In return, the design passes a concrete OCP test: a Heatwave reaction needs one new class and one `if`. A different weather provider needs one new `WeatherSource` class. Neither changes `Wanderer`, `AskForecastAction`, the other effects or the parser's callers. Only `WeatherInterpreter` or `StackQueueValleyWorld` gains one line.

| Principle | Where it shows | Change it absorbs | Where it is only partly met, and why |
|---|---|---|---|
| SRP | Each job has one class. `OpenWeatherClient` calls the API. `OpenWeatherParser` reads the JSON. `ApiKeyLoader` finds the key. `MapGeography` finds the place. `WeatherInterpreter` holds the rules. | A new JSON layout edits only the parser. | `Wanderer` both wanders and times its reactions. The timing is one counter, so a separate class would be used once. |
| OCP | Adding a reaction, omen or weather provider means writing one new class. | A Heatwave reaction or a cached weather source. | `WeatherInterpreter` gains one `if` per new rule (Section G). |
| LSP | Each of the six effects works in its action. `FixedWeatherSource` stands in for the real client in the Wanderer tests. Each effect has its own test class. | `FixedWeatherSource` replaces the real client in every test. | Every source must return an empty `Optional`, never `null`. The interface Javadoc says a missing reading is empty. |
| ISP | `WeatherReaction` and `ForecastOmen` each have one method. `WeatherSource` has the two the Wanderer needs. | A new omen implements one method and never sees reactions. | `WeatherSource` joins today and tomorrow. Every provider we know offers both, so splitting would add a type nobody uses alone. |
| DIP | `Wanderer` depends on `WeatherSource` and `WeatherReaction`. `AskForecastAction` on `ForecastOmen`. The client gets an `ApiKeyLoader` through its constructor, so tests can replace it. It is a concrete class, so this is injection, not full DIP. | A different provider that returns the same four values needs no change to the game classes. | `OpenWeatherClient` creates its own `HttpClient` (Section G). |

**DRY and KISS.** The rules live in one interpreter. The countdowns reuse `TemporaryGround` and `TimedStatus`. The effects reuse `Fire`, `GrowthPausedStatus`, `Plantable` and `StatisticChange`. Six fixed rules are a few `if`s, not a rule engine. **Connascence.** The connascence we found between packages is of Name and Type, through `WeatherSource`, `WeatherReaction` and `ForecastOmen`. Each threshold is written once, in `WeatherInterpreter`. It passes the cold and frost thresholds to the effects that need them.

**Testing.** Each reaction, omen, action and API-layer class has its own test class, for example `WeatherInterpreterTest`, `WandererTest`, `OpenWeatherParserTest` and `ApiKeyLoaderTest`. They cover readings that meet or miss each rule, and which rule wins. They also cover extreme wind and cold, and a drought that drains the last stamina. Invalid input includes a missing key, broken JSON, negative wind and infinite values. No test uses the network: `FixedWeatherSource` replaces the client, and `OpenWeatherClientTest` overrides `fetch`. `mvn test` runs them all. `WandererStressHarness` is a separate tool, not part of `mvn test`. It plays many games with fake weather to look for rare crashes.
