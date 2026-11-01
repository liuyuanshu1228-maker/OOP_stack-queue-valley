# FIT2099 Assignment 3 Design Rationale

## Jingyu Zhang 37033956 – REQ 3: Infestation and Pest Control

Class diagram: `../A3_UML/A3_REQ3_Infestation_UML.drawio`. Feature proposal: `feature-proposal.md`.

Pests move towards plants and eat them. The Farmer fights back with three defences: one pulls pests in, one hunts them, and one pushes them out. Every effect changes the map or an actor, never only prints text.

| Class | Abstraction | Effect | Game state it changes |
|---|---|---|---|
| `SlimeSnail` | `Infestation` (`feedOn`) | Sets the plant back and slimes the bare ground around it | Plant stage; ground over an area, restored later |
| `LocustSwarm` | `Infestation` | Sets back every plant on and around it; may split; leaves when full | Plant stages over an area; spawns and removes actors |
| `BurrowingMole` | `Infestation` | Sets the plant back and caves the four straight neighbours into Dirt | Plant stage; ground |
| `PestTrap` | `PestDeterrent` (`deterAround`) | Becomes a trap that pulls pests closer and captures them | Ground; forced movement; removes actors |
| `LadybugJar` | `PestDeterrent` | Releases up to three ladybugs that eat pests | Spawns actors |
| `SaltBarrier` | `PestDeterrent` | Salts the bare ground around the Farmer; burns and pushes out pests on it | Ground over an area; hit points; forced movement |

Each decision compares rejected and chosen designs, with pros, cons and a future example. Sections H and J list every place a reader may see as a weakness, and say why we chose it.

## A. Two interfaces, and one pest turn written once

**Why interfaces. Rejected:** `Infestation` and `PestDeterrent` as abstract classes.

| Pros | Cons |
|---|---|
| One type fewer on each side. | Java has single inheritance. `Pest` already extends `WanderingActor` (from A2) and `DeterrentItem` extends the engine's `Item`, so neither could also extend an abstract `Infestation` or `PestDeterrent`. |

**Chosen:** both are one-method interfaces. A pest is an actor and an `Infestation` at once, and defences find it through `asCapability(Infestation.class)`, not through the `Pest` class (DIP, ISP). **Foresight:** a cursed scarecrow that is not a `Pest` could implement `Infestation`, and every trap, jar and salt bag would find it unchanged.

**The pest turn. Rejected:** one `Pest` class with a `PestKind` enum and a `switch` in `feedOn`.

| Pros | Cons |
|---|---|
| KISS: one file holds every pest. | OCP: a new pest edits the switch, and every other switch on the enum. |
| A reader sees all pest rules side by side. | SRP: one method mixes slime, area damage and cave-ins. This is the "switch statement" smell. |
| | Each case would need its own fields (meals left only for the swarm), so the class carries fields most pests never use. |

**Chosen:** `Pest` is an abstract class (Template Method). Its turn is written once: if the pest can feed here, it returns an `InfestAction`; otherwise its `SeekPlantBehaviour` moves it. A subclass gives only its landing offsets and its `feedOn`. One hook, `canFeedAt(Location)`, is true when a plant is underfoot. `LocustSwarm` overrides it, because a swarm also feeds on the plants next to it.

| Pros | Cons |
|---|---|
| OCP: a new pest is one new class. `Pest`, `InfestAction` and the defences do not change. | A reader must open two classes to see one pest's full turn. |
| LSP: the turn never asks which pest it is. The hook replaces `if (this instanceof LocustSwarm)`, which would make the parent know its children. | A subclass could override `canFeedAt` badly, for example return true where `feedOn` finds nothing. `feedOn` then returns an empty report, so the game does not break. |
| DRY: "feed or move" exists once, and the A2 unconscious check in `WanderingActor` is reused. | |

**Why `feedOn` is not a template.** We considered a template on `Pest` where each pest gives only a per-tile step. But the three effects touch different tiles (the area around the swarm, the mole's four straight neighbours, the bare ground around the snail), do different things to them, and only the swarm changes itself (meals, splitting). A template would wrap three different effects without removing repeated code (KISS).

**Connascence:** `InfestAction` knows a pest only through `Infestation` (Connascence of Name). **Foresight:** an Aphid that hops three tiles and poisons the soil is one class: a new offset table and a new `feedOn`.

## B. Movement as data, not as classes

| Option | Pros | Cons |
|---|---|---|
| Each pest overrides `move()` | Easy to read in one class. | DRY: "find the nearest plant, pick a landing, fall back" is copied three times. A bug fix needs three edits (Connascence of Algorithm). |
| Three classes (`KnightMove`, `CrawlMove`, `BurrowMove`) | Each move has a name. | Three classes that differ only in a table of numbers. The A2 marker called one class per variant "architectural bloat". |
| **One `SeekPlantBehaviour` with offsets (chosen)** | DRY: one copy of the search. OCP: a new movement is one new array. KISS: no new type per pest. | The offsets are a raw `int[][]`, a mild "primitive obsession" smell. |

We accept the smell because the array never leaves its pest class, and the behaviour validates and copies it in its constructor. So a pest cannot pass `{1}` or change the table later. The fallback is typed as the engine's `Behaviour`, not `WanderBehaviour` (DIP).

Running the game led to two tested fixes: the mole moves one or two tiles, so it can reach a plant next to it, and the swarm also feeds from the tiles next to it.

**Foresight:** a grasshopper that leaps three tiles in a straight line is one new offset table; `SeekPlantBehaviour` stays the same.

## C. Pests depend on `Regressible`, not on crops

Pests never name a crop. They ask the ground for `Regressible` (from REQ1) with `getGroundAs` and call `regress`.

| Pros | Cons |
|---|---|
| DIP: `game.pests` does not import `game.crops`, so there is no package cycle. | A pest cannot treat one crop differently, for example a snail that prefers Strawberries. That would need a new method on `Regressible`. |
| OCP: a new plant that can regress is eaten without any change to the pests. | |
| No `instanceof`: `getGroundAs` is the engine's capability lookup. | |

## D. One deploy action for all defences

**Rejected:** three actions, `SetTrapAction`, `OpenJarAction` and `ThrowSaltAction`.

| Pros | Cons |
|---|---|
| Each action can have its own menu text. | DRY: each repeats "remove the item, act, report". If the rule for using an item changed, three classes change (Connascence of Algorithm). |
| | OCP: each new defence also needs a new action. |

**Chosen:** `DeterrentItem` is an abstract item. While the Farmer carries it, it offers one `DeployDeterrentAction`, which calls `deterAround` and removes the item. The action holds a `PestDeterrent`, not a `PestTrap` (DIP). The hook `canDeployAt(Location)` hides the option where it cannot work: a trap is not offered on a plant, so a failed attempt never wastes the item.

| Pros | Cons |
|---|---|
| OCP/DIP: a new defence is one subclass. The action never changes. | The action keeps two fields for one object: the `Item` (the engine's inventory only removes an `Item`) and the `PestDeterrent`. They must be the same object (Connascence of Identity). `DeterrentItem` passes `this` twice in one place, so callers cannot mix them up. |
| SRP: the item says what it does; the action says how an item is used. | |

Rejected fixes for the two fields: a cast breaks type safety; a generic `<T extends Item & PestDeterrent>` adds a type parameter that only removes one field.

**Foresight:** a Scarecrow that scares pests away for ten turns is one `DeterrentItem` subclass plus one `TemporaryGround` subclass.

## E. Finding pests without `instanceof`

The trap, the salt and the ladybug must tell a pest from any other actor. `PestPresent`, a `TileCondition`, checks `actor.asCapability(Infestation.class)`, which only pests have.

| Option | Pros | Cons |
|---|---|---|
| `instanceof Pest` in each defence | Short. | Each defence names the pest class. |
| A list of pest classes | Explicit. | Every new pest edits the list (OCP). |
| **`PestPresent` (chosen)** | DRY: one check, used in three places. OCP: a new pest is found by every defence at once. | One more small class. |

## F. Temporary ground

Slime, salt and the trap replace a tile's ground for a while and then put the old ground back.

**Rejected:** each ground keeps its own counter.

| Pros | Cons |
|---|---|
| No abstract class to learn. | DRY: the same countdown in three classes. A change such as "rain shortens every temporary ground" edits three classes. |
| | A ground can forget to restore the old ground, a bug the compiler cannot catch. |

**Chosen:** `TemporaryGround` is abstract. Its `tick` is `final`: run `tickEffect` (empty by default), stop if the effect already replaced this ground, then count down and restore the original ground.

| Pros | Cons |
|---|---|
| The order "effect, check, count down" is fixed in one place (Connascence of Execution kept local). | `final` stops a subclass from changing the countdown itself, for example a ground that never expires. Such a ground would extend `Ground` directly. |
| KISS: `SlimeGround` and `SaltGround` are only constants. | |

**Package placement.** `TemporaryGround` sits in `game.positions`; each concrete ground sits beside the feature that creates it (`game.pests` or `game.pestcontrol`). Otherwise `game.positions` would import `game.pests`, which imports `game.positions`: a cycle (Acyclic Dependencies Principle).

## G. The three defences

**PestTrap and TrapGround (pull in).** Each turn the trap pulls every pest within two tiles one step closer, then captures pests on or next to it, up to two. Grounds tick before actors move. In our first version the trap captured first; a pulled mole then walked away on its own turn, and the trap never caught it. Pulling first fixed this (Connascence of Timing, explained in the class comment and locked in by a test).

A ground's `tick` returns nothing, so the trap cannot hand its report to the engine.

| Option | Pros | Cons |
|---|---|---|
| A `private final Display` field, as `Plant` does | Matches REQ1. | Every trap holds a `Display` it rarely uses, and the diagram gains an association with no state. |
| A `Display` passed into the constructor | Testable. | The `Display` would travel through the item and the action, which have nothing to print. |
| Make the trap an actor | Uses the engine's normal message path. | A trap is not a creature; it changes the TA-approved design. |
| **A local `Display` when there is news (chosen)** | No static state; a dependency, not an association. | Tests check the map, not the printed text. |

**LadybugJar and Ladybug (hunt).** A ladybug eats a pest next to it, or wanders. It loses one hit point a turn, so it lives five turns, and the existing `ConsciousActor` removes it at zero (DRY). The cost: lifespan and health are one number, so healing would extend its life. Nothing in the game heals it.

**SaltBarrier and SaltGround (push out).** The bag salts bare Soil and Dirt around the Farmer, burns any pest on those tiles and pushes it out. All of this happens once, inside `deterAround`. After that, `SaltGround` only refuses pests in `canActorEnter` (KISS).

**Why an item and a ground for the trap and the salt.** This looks like a "parallel hierarchy" smell. But the engine keeps carried things (`Item`) and map tiles (`Ground`) as separate types, and one object cannot be both. The item lives in the inventory; the ground lives on the map with its own timer.

## H. Visibility, `final`, `protected` and `static`

A2 feedback said package-private helpers (`CrowSensing`, `CrowNavigation`) can be good encapsulation, but the rationale must say so. Here is each choice in REQ3.

| Choice | Where | Why | Cost and when we would change it |
|---|---|---|---|
| Every class is `public` | All 20 REQ3 types, including the small `PestPresent`, `InfestAction` and `EatPestAction` | A3 is the last assignment, so package-private would be safe. We still chose `public` so a feature in another package, for example a frog in `game.actors`, can reuse `PestPresent` and `EatPestAction`. | A wider public API. `InfestAction` could be package-private (only `Pest` uses it), but one rule for the whole feature is easier to check. |
| Every field is `private` | All classes | Implementation details stay inside (A2 feedback). Subclasses use hooks, not fields. | `SlimeSnail`, `BurrowingMole` and `LocustSwarm` each keep their own `AreaFactory`, although `Pest` also receives it. A `protected` getter on `Pest` would remove three fields but expose a tool that `Pest` itself does not use. |
| Constants are `private static final` | e.g. `LocustSwarm.SPLIT_CHANCE_PERCENT`, `SlimeSnail.HIT_POINTS` | A fixed rule belongs to the class, not to one object, and a name explains the number (no magic numbers). Java does not allow instance fields in `super(...)`, so names, characters and hit points must be static to be named. | A test cannot read them, so tests repeat the number. |
| No static methods, no `public` constants and no mutable static fields | — | The A2 marker penalised static methods ("function thinking") and `public` constants (they leak details). A `private static final` constant cannot change and is not seen outside the class. | — |
| `protected` constructors | `Pest`, `DeterrentItem`, `TemporaryGround` | They are abstract; only subclasses construct them. | — |
| `protected` hooks | `canFeedAt`, `canDeployAt`, `tickEffect` | Subclasses must override them; callers must not skip the template. | Classes in the same package can also call them. No class does. |
| `protected final` | `TemporaryGround.revertAt` | A subclass may end its ground early (the trap after two captures) but cannot change how it ends. | — |
| `final` method | `TemporaryGround.tick` | Fixes the order of the countdown. | See Section F. |
| Classes are not `final` | e.g. `LadybugJar`, `SaltGround` | Open for extension (OCP), e.g. a larger jar. | A subclass could break a rule. Private fields and the final `tick` limit the damage. |
| `private` constructor | `LocustSwarm(int meals, ...)` | Only a split may create a smaller swarm. The public constructor always gives four meals, so a half-empty swarm cannot be built by mistake. | A future "hungry swarm" difficulty would need a new public constructor. |
| `private` helpers | e.g. `pullPests`, `splitAt`, `copyOf` | They are steps, not contracts. Making them `protected` would freeze their signatures for every subclass. | A subclass that wants a different pull must override `tickEffect`. |
| Non-final fields | `mealsLeft`, `capturesLeft`, `remainingTurns` | These values change every turn. | — |

## I. Changes to the earlier design

**Statics replaced by injection.** In A2, REQ3 called `Area.within` and `RandomUtils.rollPercent` as static methods. Following A2 feedback, the team now creates one `RandomUtils` and one `AreaFactory` in `StackQueueValleyWorld.initialise` and passes them in through constructors.

| Pros | Cons |
|---|---|
| DIP: a pest depends on the objects it is given, not on global classes. Tests pass `FixedRandom` directly. | Every pest and defence constructor gained one or two parameters, and `Pest` must pass them on (a "tramp data" smell). |
| No hidden global state, so two tests cannot affect each other. | Connascence of Position in constructors, with at most two such parameters. |

**Other changes.** Only `StackQueueValleyWorld` changed: it places the pests and gives the Farmer the defences. `SaltBarrier` moved from `k` to `f`, because earlier display characters are fixed.

## J. Known minor violations and why we accept them

| Violation or cost | Why it is acceptable |
|---|---|
| `SlimeSnail` and `SaltBarrier` both ask "is this tile bare Soil or Dirt?" (two lines each). DRY is not fully met. | Rule of three: two short copies are cheaper to read than a new class. A third use would be the signal to extract a `BareFarmGround` condition. |
| `LadybugJar` catches `GameEngineException` and moves on. An empty catch is a smell. | The tile is checked with `canActorEnter` first; the catch only covers a tile taken in between. A comment says so. |
| `Ladybug`, `LadybugJar` and `SaltBarrier` use `getExits()`, while other classes use `AreaFactory`. | `getExits()` is the engine's own list of neighbours and needs no extra dependency. `AreaFactory` is used where we filter or search an area. |
| `TrapGround` moves actors, which is more than most grounds do. | Acting on nearby pests each turn is the trap's job, and the logic stays in one class. |
| Pests (except the swarm) never leave on their own. | On purpose: the defences are the counter to the pests. A meal limit could be added in `feedOn` only. |

## Summary

| Principle | Where it shows | Change it absorbs | Where it is only partly met, and why |
|---|---|---|---|
| SRP | Moving, the pest turn, finding pests, using an item and timing ground each have one class. | A new landing or countdown rule edits one class. | `TrapGround` pulls, captures and prints. `LocustSwarm` feeds, splits and leaves. Each is one rule from the brief, and splitting them would add classes used once. |
| OCP | A new pest, defence or temporary ground is one new class. | An Aphid, a Scarecrow or a Frost ground: no existing class changes. | The new class must still be placed in `StackQueueValleyWorld`. That is the composition root, so this is expected. |
| LSP | Every pest works wherever `Pest` or `Infestation` is expected; every defence wherever `PestDeterrent` is. Hooks replace type checks. | A new pest is fed, found and trapped with no new branch. | `canFeedAt` and `feedOn` must agree; if not, `feedOn` returns an empty report. `final tick` protects the countdown. |
| ISP | `Infestation`, `PestDeterrent` and `Regressible` each have one method. Defences never see a pest's movement or health. | A pest that moves differently does not touch any defence. | `PestPresent` uses `Infestation` only to recognise a pest and never calls `feedOn`. This is not a marker interface: `InfestAction` calls `feedOn`. |
| DIP | `DeployDeterrentAction` depends on `PestDeterrent`, pests on `Regressible`, `SeekPlantBehaviour` on `Behaviour`; `RandomUtils` and `AreaFactory` are injected. | A new crop, defence or fallback needs no change to its users. | `Pest` creates `SeekPlantBehaviour` and `WanderBehaviour`; `SlimeSnail` creates `SlimeGround`; `LadybugJar` creates `Ladybug`. Each has one outcome, so an injected factory would be a parameter nobody varies (YAGNI). |

**DRY and KISS.** Each rule exists once: the pest turn, the landing search, finding a pest and the countdown. We added no layer that only one class uses. **Connascence.** Between packages it is only of Name and Type. Stronger forms (Timing, Identity, Execution) stay inside one class each, with a comment and a test.

**Testing.** Every class has normal, edge and invalid tests. `FixedRandom` makes them repeatable, and `mvn test` runs them all.
