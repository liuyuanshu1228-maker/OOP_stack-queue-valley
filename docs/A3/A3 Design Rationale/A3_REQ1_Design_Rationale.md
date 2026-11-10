# FIT2099 Assignment 3 Design Rationale

## Poyee Tan [Student ID] – REQ 1: Second Spring

REQ1 makes plants alive. Every plant ages one turn at a time. Each turn it tries its abilities (grow, spread, produce) in order, and does at most one thing. Each young stage is its own class, and only tilled Soil accepts new plants. We had three design goals. The shared timing rule is written once. Each plant keeps its own rules in its own class. Other features (pests, enchantments, weather) act on plants without naming any crop. Abstractions were added only where the brief already shows variation between nine plants or three abilities (KISS, YAGNI).

## A. One class per growth stage

The brief says a seedling grows into a sprout, and a sprout into a crop. It also says growing creates "a brand new plant with age 0".

**Design 1:** one class per crop with a `stage` field and a `switch` that picks the character, abilities and rules.

| Pros | Cons |
|---|---|
| One class holds a crop's whole life, so a reader sees every stage in one file (KISS). | Every new stage would mean editing every `switch` on the stage field (OCP, the Switch Statements smell). |
| The class diagram gains no new classes, so the design stays small on paper. | One class would own several display characters, ability lists and harvest rules, so it changes for many reasons (SRP). |
|  | A sprout would still offer watering and harvesting, guarded by `if (stage == SPROUT)` checks scattered through the class. |

**Design 2:** one class per crop holding a separate stage object (State pattern) that it swaps as it grows.

| Pros | Cons |
|---|---|
| The crop stays the same object while it grows. Anything that holds it still sees it. | The brief says growing creates a new plant with age 0. So every swap would have to reset the age by hand (Connascence of Execution). |
| Each stage's rules still sit in their own state class, so the stage logic stays separate. | The ground object would have to change its character, name and allowed actions at run time. The engine's `Ground` is not built for this, and any caller holding the old values would be out of date. |

**Design 3 (chosen):** one `Plant` subclass per stage. `Plant` is a new abstract class between `Ground` and `Crop`, and `Crop` now extends it. Growing replaces the ground with a new object, so the new stage starts at age 0 with no reset code. The young stages extend `Plant`, not `Crop`: as `Crop`s they would inherit watering and harvesting they must refuse.

| Pros | Cons |
|---|---|
| A new crop's stages are new classes, and no existing stage changes (OCP). | There are four small classes that mostly hold constants. Acceptable because each small class removes a `switch` branch that would otherwise sit in one large class. |
| Every `Crop` really can be watered and harvested, so no sprout refuses a `Crop` method (LSP). | Stages name each other, for example `StrawberrySprout.nextStage()` returns `new Strawberry(...)` (Connascence of Name). Acceptable because the names stay inside `game.crops`, and each stage has only one next stage. |
| Each class has one character, one ability list and one set of rules (SRP). The age starts at 0 simply because a new object is made. | Inserting a new stage into an existing chain edits the stage before it, whose `nextStage()` must return the new class. Acceptable because only that one method changes, and a whole new crop needs no edit to existing stages. |

**Package placement.** The young stages live in `game.crops`, beside the crops they grow into, not in `game.plants`. Otherwise `game.plants` would import `game.crops`, which imports `game.plants`: a cycle (Acyclic Dependencies Principle). `game.plants` now depends on no crop.

**Foresight:** a Pumpkin and its Pumpkin Sprout are two new classes. No existing class changes except `StackQueueValleyWorld`, which places them.

**Connascence:** stages name their next stage (Connascence of Name), kept inside `game.crops`. The rejected State pattern would add Connascence of Execution: the age must be reset by hand on every swap. **Code smells:** avoided Switch Statements on a stage field. Also avoided Refused Bequest: a sprout does not inherit watering and harvesting it would have to refuse. Accepted: small classes that mostly hold constants.

## B. How a plant acts each turn

Every turn a plant ages, then tries its abilities in priority order and does at most one thing. Three questions shape this. How are the abilities stored? How does an ability get what it creates? How are turns counted?

**Abilities as a list.**

**Design 1:** each plant overrides `tick` with its own if-else.

| Pros | Cons |
|---|---|
| Each plant's rules sit in one place, with no shared class to learn before reading a plant (KISS). | Nine classes would each copy the rule "every N turns, roll a chance, stop at the first success" (Duplicate Code). A bug fix would need nine edits (Connascence of Algorithm). |
|  | One plant could easily break the "one thing per turn" rule. Nothing would stop it. |

**Design 2:** use the engine's `Behaviour<Plant, String>` as the ability type.

| Pros | Cons |
|---|---|
| No new interface is needed. The engine already has a type for "something that may act this turn". | It returns `null` for "nothing happened" (Connascence of Meaning). So every caller would need a `null` check. |
|  | The type is also used by actors, so a plant ability would look like an actor's behaviour. The plant rule "`""` means nothing happened" would be lost. |

**Design 3 (chosen):** `Plant` holds a `List<PlantAbility>` in priority order, passed to `super(...)`. `PlantAbility` has one main method, `tryPerform(plant, location)`. It returns a report, or `""` when nothing happened. `Plant.tick` ages the plant, then `attemptGrowth` tries the abilities from the top and stops at the first non-empty report. `PeriodicAbility` writes the timing once: its `tryPerform` is `final` (Template Method). It checks `age % interval`, rolls the chance, then calls the abstract `perform`. `Grow`, `SelfReplicate` and `Produce` write only their effect.

| Pros | Cons |
|---|---|
| The timing rule and the loop over abilities are written once. So every plant keeps the "one thing per turn" rule (DRY). | A reader opens two classes, the plant and the ability, to see one behaviour. Acceptable because each ability is short and named after what it does. |
| To add an ability, such as "wither", we write one new class. No other ability changes (OCP). | Priority is the order of the constructor arguments (Connascence of Position). Acceptable because each plant's class comment lists its abilities in priority order. |
| `Plant` only sees its abilities as `PlantAbility`, so it never names a concrete ability (DIP). The plant decides when to act. Each ability decides what happens (SRP). | `final tryPerform` stops an ability from changing the timing. Acceptable because an ability with different timing can implement `PlantAbility` directly. |
|  | To use a new ability, a plant must add it to its own list. So that plant's class changes. Acceptable because choosing a plant's abilities is that plant's job, and no other plant or ability changes. |

`PeriodicAbility` checks its numbers when it is built. It rejects an interval below 1, because an interval of 0 used to crash on `age % 0`. It also rejects a chance below 0 or above 100.

**Growth forced by magic.** REQ4's Bounty Enchantment makes a plant grow at once. At first it called `attemptGrowth`. But `attemptGrowth` goes through each ability's interval and chance. So on most turns the plant was "not due", and the magic did nothing. Now `Plant` has a second method, `forceGrowth`. It tries the same abilities, but through `forcePerform`, which skips the interval and the chance. `PeriodicAbility.forcePerform` is `final` and simply calls `perform`. Any other ability gets a default `forcePerform` that just calls `tryPerform`. The cost is that `forceGrowth` repeats the short loop in `attemptGrowth` (see Section G).

**Why each plant lists its own abilities.** The A2 marker asked us to pass the Merchant its goods from outside. We did not do the same for a plant's abilities. A Merchant that sells other goods is still a Merchant. But a Strawberry always drops strawberries every five turns. A plant that did something else would not be a Strawberry. It would be a different plant, so we would write a new class for it.

**Foresight:** a Rose that withers after 30 turns is one new `PeriodicAbility` subclass placed first in its list.

**Capabilities: `Growing`, `Spreading`, `Producing`.** Each ability must ask the plant what to create. `Grow` needs the next stage. `SelfReplicate` needs the offspring. `Produce` needs the item. The plant knows this, and already holds the `RandomUtils` and `AreaFactory` the new object needs.

**Design 1:** a `Supplier` per ability, for example `new Grow(..., StrawberrySprout::new)` (our first version).

| Pros | Cons |
|---|---|
| The ability is given exactly what it creates, so it needs no interface and no knowledge of the plant's type. | After A2 feedback, the team removed method references everywhere. This would be the only place still using them. |
|  | The supplier needed the plant's `RandomUtils` and `AreaFactory`, so each plant had to build its suppliers around its own fields. |

**Design 2:** three methods on `Plant` whose defaults throw `UnsupportedOperationException` (our draft).

| Pros | Cons |
|---|---|
| There is one abstract class to learn, and each plant overrides only the methods its abilities call. | Parsnip only produces, yet carried `nextStage()` and `offspring()` that could only throw (ISP, Refused Bequest). |
|  | The Javadoc had to say "the default throws". Callers had to know which plants support which method (Connascence of Meaning). |

**Design 3:** an abstract class per capability, such as `ProducingPlant`.

| Pros | Cons |
|---|---|
| Shared code could live in the abstract class. For example, dropping a produced item on a free tile. | A Java class has only one parent. `Strawberry` already extends `Crop`, so it could not also extend `ProducingPlant`. A plant with two capabilities could not be built at all. |
|  | Shared code would be stuck in one branch of the class tree. A sprout and a crop could not share it. |

**Design 4 (chosen):** one single-method interface per capability: `Growing` (`nextStage()`), `Spreading` (`offspring()`) and `Producing` (`produce()`). Each plant implements only what it can do. An ability finds its capability with `location.getGroundAs(Growing.class)` and does nothing if it is missing, the same way pests find `Regressible`.

| Pros | Cons |
|---|---|
| No plant carries a method it cannot honour, and nothing throws (ISP, no Refused Bequest). | There are three more interfaces, and every ability shares Connascence of Type with one of them. Acceptable because each interface has one method, and each replaces a method that could only throw. |
| `getGroundAs` is the engine's capability lookup, so abilities find what they need without any `instanceof`. | A plant given `Grow` but not `Growing` silently never grows instead of failing. Acceptable because the tests catch that mistake. Also, doing nothing is safer in play than crashing. |
| Abilities depend on three small interfaces, not on `Plant` subclasses (DIP). `Regressible` and `Plantable` work the same way, so a reader learns this once. | The capability methods must be `public`, as interface methods are. Acceptable because only the abilities call them. |

Strictly, the throwing draft did not break LSP. No path ever reached the throw. So we describe it as an ISP problem.

**Foresight:** a Sunflower that drops seeds implements `Producing` and is given `Produce`. No ability changes.

**Counting turns.** The brief says each plant's age starts at 0 when it is placed.

**Design 1:** the engine's `Status` countdown.

| Pros | Cons |
|---|---|
| It already exists and is tested, so no new counting code is needed. A status also removes itself when its time is up. | Statuses tick and expire independently, so they cannot express "try in priority order and stop at the first success". |
|  | Each ability would need its own status, and the age the brief asks for would still not be stored anywhere. |

**Design 2:** a global turn counter.

| Pros | Cons |
|---|---|
| There is one number for the whole game, so every plant reads the same clock. Two plants could never disagree about the turn. | The engine has none. Adding one would need a static field, the global-state smell A2 feedback penalised. |
|  | Every plant would depend on its value (Connascence of Value). A plant's age would have to be worked out from the turn it was placed. |

**Design 3 (chosen):** `Plant` keeps its own `age`.

| Pros | Cons |
|---|---|
| Each plant counts from 0 when placed, exactly as the brief says. | The engine updates tiles row by row. So a new plant to the right of or below its parent ages once in its first turn. Acceptable because this is engine behaviour, it shifts timing by one turn, and it breaks no rule in the brief. |
| There is no setter, so only the plant changes its age (encapsulation). |  |

Each plant gets its `RandomUtils` through the constructor. A test can pass a `FixedRandom` instead, so every run gives the same result.

**Connascence:** ability priority is the order of the constructor arguments (Connascence of Position), documented in each plant's class comment. It replaces Connascence of Algorithm across nine copies of the loop. Abilities share only Connascence of Type with the capabilities, where the rejected draft had Connascence of Meaning ("the default throws"). Ageing before trying abilities is Connascence of Execution, kept inside one method. A global counter would have spread Connascence of Value across every plant. **Code smells:** avoided Duplicate Code, Long Method (nine hand-written `tick` methods), global static state and functional style. Removed Refused Bequest (methods that could only throw). Accepted: the Connascence of Position above, and the engine's row-by-row ticking, which shifts a new plant's timing by one turn.

## C. Capabilities other features use: `Plantable` and `Regressible`

REQ1 offers two small interfaces to other features. `Plantable` says where a plant may go. `Regressible` lets REQ3's pests set a plant back.

**Where plants can grow.** The brief says only tilled Soil accepts new plants.

**Design 1:** check `instanceof Soil` wherever something is planted.

| Pros | Cons |
|---|---|
| The check is one short line. The rule "only Soil" is visible right where planting happens, so a reader sees it at once. | The same check would sit in spreading, the Merchant and the Sowing omen (Duplicate Code). |
|  | A new plantable ground would mean editing every planting path (OCP, Shotgun Surgery). A type check is also a smell. |

**Design 2 (chosen):** a `Plantable` interface with `plant(ground, location)`, and a `PlantableTile` condition for `Area.filter`. `Soil` implements it. `plant` takes a `Ground`, not a `Plant`, so `game.positions` does not depend on `game.plants` (no package cycle).

| Pros | Cons |
|---|---|
| A new plantable ground, such as fertilised soil, only implements the interface, and every planting path accepts it (OCP). | It adds one more interface and one more condition class. Acceptable because three features plant through them, so they replace three copies of the rule. |
| `SelfReplicate`, the Merchant's `Crop.plantNextTo` and REQ5's Sowing omen all plant through it, so they follow one rule (DRY). | `plant` takes a `Ground`, so only Connascence of Type stops a caller planting something odd. Acceptable because taking a `Ground` is what keeps `game.positions` free of `game.plants`, and every caller passes a plant. |
| REQ3's mole and snail find bare Soil through the same interface, so the rule is shared across features. |  |

In Assignment 2 the Merchant could plant on Dirt. Now it plants only on Soil, as the brief requires.

**Foresight:** a Greenhouse floor that accepts plants implements `Plantable`. Every planting path accepts it at once.

**Regressing, for REQ3 pests.**

**Design 1:** pests call `Crop` methods directly.

| Pros | Cons |
|---|---|
| There is no new interface to learn. A pest simply calls the crop it is standing on, so the code is easy to follow. | Pests would name every crop and stage (Connascence of Type). A new stage would mean editing every pest (OCP, Shotgun Surgery). |
|  | Pests would reach into crop internals to decide the earlier stage (Feature Envy). |

**Design 2 (chosen):** `Plant` implements `Regressible`, whose `regress` replaces the plant with `previousStage()`. By default a regressed plant dies and leaves `Dirt`, matching A2's `Crop.removeCrop`. Later stages override `previousStage()` to return their earlier stage.

| Pros | Cons |
|---|---|
| Pests depend on `Regressible`, so `game.pests` does not import `game.crops`, and only Connascence of Name (`regress`) crosses packages (DIP). | A pest cannot treat one crop differently. Acceptable because no rule in the brief or proposal treats crops differently. |
| Every pest eats a new plant that extends `Plant`, with no change to the pests (OCP). Each plant decides its own earlier stage. | `previousStage()` is `protected`, so classes in the same package could call it. Acceptable because no class other than `Plant` calls it. |

**Connascence:** only Connascence of Type on `Plantable` and Connascence of Name on `regress` cross package boundaries. **Code smells:** avoided type checks for `Soil` and Feature Envy in the pests. Also avoided Shotgun Surgery when a ground or plant is added.

## D. Pausing and printing in a plant's turn

This section covers two needs. REQ5's frost must stop a plant acting. And the plant must print what it did, although `Ground.tick` returns nothing.

**Pausing growth, for REQ5.** The proposal says that during a frost every plant around the Wanderer "stops growing".

**Design 1:** the status removes the plant's abilities and puts them back when it expires (push).

| Pros | Cons |
|---|---|
| The plant needs no check of its own. The status does all the work. | The status would reach into `Plant`'s private list and must restore exactly the same list (Connascence of Identity). |
|  | Two overlapping frosts could restore the list twice, or restore it while the other frost still holds. |

**Design 2:** `Plant.tick` checks for `GrowthPausedStatus` before calling `attemptGrowth` (our first version).

| Pros | Cons |
|---|---|
| The check sits at the start of the plant's turn, where a reader looks first. Deciding whether to act stays the plant's job (SRP). | Any other caller of `attemptGrowth` skipped the check, so the rule held only for the plant's own turn. |
|  | REQ4's Bounty did not go through `tick`. So a frozen plant still grew when an enchanted watering can touched it. The proposal says it "stops growing". |

**Design 3 (chosen):** the methods that try a plant's abilities check for `GrowthPausedStatus` themselves. There are two of them: `attemptGrowth` (the plant's own turn) and `forceGrowth` (Bounty's magic). While the plant is paused, both return `""`. `Plant.tick` still ages the plant first.

| Pros | Cons |
|---|---|
| Every way into a plant's abilities passes the check: the plant's own turn and Bounty's magic. Bounty does not need to know about frost at all. | `GrowthPausedStatus.applyEffect` is empty on purpose, which a reader may see as a do-nothing override (it looks like Refused Bequest). Acceptable because a comment says why. The status is only a flag with a countdown, and `TimedStatus` (A2) already does the countdown. |
| The status only counts down. Deciding whether to act stays the plant's job (SRP). | The check is written twice, once in each method. Acceptable because it is one line, and both copies sit in `Plant`. A test checks that Bounty does not grow a paused plant. |

While paused, the plant still ages. A test checks that Bounty does not grow a paused plant.

**Printing without a Display parameter.** `Ground.tick` returns nothing, so a plant prints its own report.

**Design 1:** a `private static final Display` (our draft).

| Pros | Cons |
|---|---|
| One object is shared by every plant, so no plant creates its own. Every plant prints through the same `Display`. | Shared static state is the global-state smell A2 feedback penalised, and every plant would then depend on one hidden object. Tests would share it too, so one test's setup could leak into another. |

**Design 2:** a `Display` injected through the constructor.

| Pros | Cons |
|---|---|
| A test could pass a `Display` that records the printed text. The plant would also stop creating its own `Display` (DIP). | Nine constructors, every `nextStage()` and `offspring()`, `StackQueueValleyWorld` and every test would gain a parameter (Tramp Data). |
|  | `Display` is the engine's console writer with no alternative to swap in, so the extra parameter would never vary. |

**Design 3:** return the report and let someone else print it.

| Pros | Cons |
|---|---|
| There is no `Display` inside `Plant` at all. The plant would only describe what happened, and someone else would print it. | Only the engine calls `Ground.tick`, and it ignores any result. So the report would be lost, and the player would never see a plant grow. |

**Design 4 (chosen):** a `private final Display` per plant.

| Pros | Cons |
|---|---|
| There is no static state. Each plant prints with one short call, and nothing is passed around (KISS). | Each plant creates its own `Display`, a small DIP gap. Acceptable because `Display` has no alternative to swap in, and the collaborators that do vary, `RandomUtils` and `AreaFactory`, are injected. |
| Plants share nothing, so there is no Connascence of Value between them. |  |

**Connascence:** avoided Connascence of Identity, because the status never puts a list back. Also avoided Connascence of Value between plants, because there is no shared static `Display`. The pause rule is Connascence of Execution (check before trying abilities), kept inside one method. **Code smells:** avoided a static field (global state) and a rule that one caller could skip. Accepted: an empty `applyEffect`, explained by a comment. Also a small DIP gap, because `Display` has nothing to swap in.

## E. Visibility, `final`, `protected` and `static`

| Choice | Where | Why | Cost |
|---|---|---|---|
| Every field is `private` | All REQ1 classes | Details stay inside the class (A2 feedback). No other class can change its data, so no class is a Data Class. Connascence on state stays inside. | Each plant keeps its own `RandomUtils` and `AreaFactory` to pass to the plants it creates. |
| Constants are `private static final` | e.g. `GROW_INTERVAL`, `PRICE` | A name explains each number. Java does not allow instance fields in `super(...)`, so the names must be static. | Tests repeat the numbers, because they cannot read private constants. |
| No static methods and no mutable static fields | Every REQ1 class | A2 feedback penalised static methods as "function thinking" and static state as global state. | `RandomUtils` and `AreaFactory` must be passed through constructors instead. |
| `protected` constructors | `Plant`, `PeriodicAbility` | They are abstract, so only subclasses construct them, and `protected` says so. | Same-package classes could call them, but Java forbids creating an abstract class anyway. |
| `protected` hooks | `perform`, `previousStage` | Subclasses override them. Callers must go through the template. | Same-package classes could call them. None does. |
| `final` methods | `tryPerform`, `forcePerform`, `plantNextTo`, `noRoomToPlant` | They fix the shared rule, so a subclass cannot change it. | A plant needing different timing implements `PlantAbility` directly. |
| `public` capability methods | `nextStage`, `offspring`, `produce`, `attemptGrowth`, `forceGrowth` | Interface methods must be public. `forceGrowth` is called by REQ4's Bounty. | Any class could ask a plant for its next stage. Only the abilities do. Both growth methods obey the pause. |
| Classes are not `final` | e.g. `Grow`, `Soil` | Not being `final` only makes a subclass possible. The design is open for extension because new classes plug into the abstractions (OCP). | A subclass could break a rule. Private fields and `final` templates limit the damage. |

**Connascence:** `private` fields keep connascence on state inside each class. **Code smells:** avoided public attributes and missing modifiers.

## F. Changes to Assignment 2

| Change | Why | Cost |
|---|---|---|
| `Crop` extends `Plant` instead of `Ground` | Every crop gets an age, abilities and `Regressible`. | `Crop` constructors take abilities. Watering and harvesting are unchanged. |
| `Unplanted` checks for any `Plant` | Watering a Cauliflower used to flood sprouts into puddles. | One condition class now depends on `Plant`, which it uses only through `getGroundAs`. |
| The Merchant plants through `PlantableTile` | The brief says only Soil accepts plants. | A Merchant surrounded by Dirt cannot sell crops. The message says so. |
| `RandomUtils` and `AreaFactory` are injected instead of static | A2 feedback. Tests pass `FixedRandom`. | Every plant constructor has two more parameters (Tramp Data). |
| Fixed farm layout with one of every plant | Every plant is visible when the game starts, so a marker can check each one. | The map no longer starts empty, so the A2 opening looks different. |
| Unique characters: Crow `b`, Cauliflower `K` | The brief gives `v` to the Strawberry Sprout, and `C` is the Cow. | Players of A2 see new characters. |

## G. Known minor violations and why we accept them

| Violation or cost | Why it is acceptable |
|---|---|
| `SelfReplicate` and `Crop.plantNextTo` repeat the two lines that find an adjacent Plantable tile and plant on it. DRY is not fully met. | They build different messages. A third use would be the signal to extract it. |
| `Crop` also builds the Merchant's "no room to plant" message, mixing trading into a ground (SRP). | It keeps one wording for both purchasable crops, Tomato and Parsnip. |
| Each plant creates its own `Display` (Section D). | Injecting it adds parameters everywhere without making anything replaceable (KISS). |
| A plant given an ability without the matching capability does nothing (Section B). | Safer than a crash. Tests check every plant's abilities. |
| The engine updates tiles row by row. So a new plant to the right of or below its parent ages once in its first turn. | Engine behaviour. It shifts timing by one turn and breaks no rule. |
| `StackQueueValleyWorld` registers the map characters with method references, `Dirt::new` (base code) and `Soil::new` (REQ1), although the team avoids functional style. | The engine's `DefaultGroundCreator.registerGround(char, Supplier<? extends Ground>)` needs a `Supplier`, and the engine cannot be changed. A named `Supplier` class for each ground would be used once, only to avoid one line (KISS). These are the only two method references in the game code. |
| `attemptGrowth` and `forceGrowth` repeat the same short loop and the same pause check. DRY is not fully met. | They differ in one call: `tryPerform` or `forcePerform`. Joining them would need a `boolean forced` parameter, which is the Flag Argument smell. Two copies of a five-line loop, side by side in one class, are easier to read. |

## Testing

`GrowTest`, `SelfReplicateTest`, `ProduceTest`, `PlantStateTest`, `PeriodicAbilityTest` and `PlantCapabilityTest` cover every plant at the right age and one turn early. They also cover a failed chance, no free tile, a paused plant and a regressed plant. Invalid cases include bad intervals and chances, and an ability on a plant that lacks the capability. `FixedRandom` makes them repeatable, and `mvn test` runs them all.

## Summary

The final design uses the chosen design of every part. Each stage is one class (A). Abilities sit in an ordered list, with the timing written once and capabilities found by interface (B). `Plantable` and `Regressible` serve other features (C). Every caller passes the pause check (D). Each abstraction answers variation the brief already shows: nine plants, three abilities and two other features that act on plants. The main cost is more, smaller types, and Connascence of Position for ability priority. In return, the design passes a real OCP test. A Pumpkin needs one new class per stage. A withering Rose needs one new `PeriodicAbility`. Neither changes `Plant`, `PeriodicAbility`, any other plant, any pest or any enchantment. Only `StackQueueValleyWorld` places them.

| Principle | Where it shows | Change it absorbs | Where it is only partly met, and why |
|---|---|---|---|
| SRP | Timing, each effect, planting, regressing and pausing each have one class. | A new timing rule edits only `PeriodicAbility`. | `Crop` holds one Merchant message (Section G). |
| OCP | Adding a stage, ability, capability or plantable ground means writing one new class. | A Pumpkin, a wither ability, a Greenhouse floor: no existing plant, ability or planting path changes. | New plants are placed in `StackQueueValleyWorld`, the composition root. |
| LSP | Every `Crop` can be watered and harvested. Abilities never check the plant's type. | A new stage works wherever `Plant` is expected. | A plant given an ability without its capability does nothing instead of failing. Tests check every plant. |
| ISP | `Growing`, `Spreading`, `Producing`, `Plantable` and `Regressible` each have one method. | Parsnip carries only `produce()`. | `PlantAbility` has two methods, `tryPerform` and `forcePerform`. The second has a default, so an ability only writes it if it has its own gate. `Plant` also offers `attemptGrowth`, `forceGrowth`, `getAge` and `regress`, each used by a different feature. |
| DIP | Abilities depend on capabilities, pests on `Regressible`, `Plant` on `PlantAbility`. `RandomUtils` and `AreaFactory` are passed in through constructors, which lets tests replace them. Both are concrete classes, so this is dependency injection rather than full DIP. | A new plant or pest needs no change to the other side. | Each plant creates its own engine `Display`. Stages create their next stage directly (one outcome each, so a factory would be a parameter nobody varies). |

**DRY and KISS.** Each rule exists once: the timing, the priority loop, the pause check, planting and the countdown. Each new interface and abstract class is used by at least two classes. **Connascence.** The connascence we found between packages is of Name and Type, through `Regressible`, `Plantable` and the capability interfaces. Stronger forms (Position for ability priority, Execution for age before abilities and the pause check) stay inside one class each.
