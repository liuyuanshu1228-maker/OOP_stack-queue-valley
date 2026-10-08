# FIT2099 Assignment (Semester 2, 2026)

## Contribution Log

https://docs.google.com/spreadsheets/d/14k_CRD-zLqx8Qtg6DL1X_qB89g8ZwoPe-r2XALmmoUY/edit?gid=0#gid=0

```
   ,-,--.  ,--.--------.   ,---.       _,.----.  ,--.-.,-.                
 ,-.'-  _\/==/,  -   , -\.--.'  \    .' .' -   \/==/- |\  \               
/==/_ ,_.'\==\.-.  - ,-./\==\-/\ \  /==/  ,  ,-'|==|_ `/_ /,--.--------.  
\==\  \    `--`\==\- \   /==/-|_\ | |==|-   |  .|==| ,   //==/,  -   , -\ 
 \==\ -\        \==\_ \  \==\,   - \|==|_   `-' \==|-  .| \==\.-.  - ,-./ 
 _\==\ ,\       |==|- |  /==/ -   ,||==|   _  , |==| _ , \ `--`--------`  
/==/\/ _ |      |==|, | /==/-  /\ - \==\.       /==/  '\  |               
\==\ - , /      /==/ -/ \==\ _.\=\.-'`-.`.___.-'\==\ /\=\.'               
 `--`---'       `--`--`  `--`                    `--`                     
    _,.---._                     ,----.                  ,----.           
  ,-.' - ,  `.   .--.-. .-.-. ,-.--` , \ .--.-. .-.-. ,-.--` , \          
 /==/ ,    -  \ /==/ -|/=/  ||==|-  _.-`/==/ -|/=/  ||==|-  _.-`          
|==| - .=.  ,  ||==| ,||=| -||==|   `.-.|==| ,||=| -||==|   `.-.          
|==|  : ;=:  - ||==|- | =/  /==/_ ,    /|==|- | =/  /==/_ ,    /          
|==|,  '='  ,  ||==|,  \/ - |==|    .-' |==|,  \/ - |==|    .-'           
 \==\ _   -    ;|==|-   ,   /==|_  ,`-._|==|-   ,   /==|_  ,`-._          
  '.='.  ,  ; -\/==/ , _  .'/==/ ,     //==/ , _  .'/==/ ,     /          
    `--`--'' `--`--`..---'  `--`-----`` `--`..---'  `--`-----``           
        ,-.-.  ,---.                              ,----.                  
 ,--.-./=/ ,/.--.'  \       _.-.      _.-.     ,-.--` , \ ,--.-.  .-,--.  
/==/, ||=| -|\==\-/\ \    .-,.'|    .-,.'|    |==|-  _.-`/==/- / /=/_ /   
\==\,  \ / ,|/==/-|_\ |  |==|, |   |==|, |    |==|   `.-.\==\, \/=/. /    
 \==\ - ' - /\==\,   - \ |==|- |   |==|- |   /==/_ ,    / \==\  \/ -/     
  \==\ ,   | /==/ -   ,| |==|, |   |==|, |   |==|    .-'   |==|  ,_/      
  |==| -  ,//==/-  /\ - \|==|- `-._|==|- `-._|==|_  ,`-._  \==\-, /       
  \==\  _ / \==\ _.\=\.-'/==/ - , ,/==/ - , ,/==/ ,     /  /==/._/        
   `--`--'   `--`        `--`-----'`--`-----'`--`-----``   `--`-`         
```

## REQ5 (HD) — Stateful Creature: The Crow

A group of three, so this requirement is satisfied with **one** creature — a
`Crow` (`src/game/crow/Crow.java`) — with exactly **four** states. Each state
is its own class under `src/game/crow`, implementing a shared
`game.creaturestate.CreatureState` interface (`act`, `nextState`, `onEnter`).
States hold no per-creature data, so the game shares one public `INSTANCE` of each
(the State-pattern/flyweight shape). The state classes are not `final` and have
public constructors, so a variant (for example a subclass with a different
transition rule) can be created without changing them. Every state extends
`game.creaturestate.BaseCreatureState`, which makes two states of the same class
equal (`equals`/`hashCode`), so creating another instance of a state never changes
how the state machine behaves. Any data that varies per
creature — which items it's hidden, how many consecutive turns it's felt
safe — lives on the `Crow` object itself, via two small capability
interfaces (`ItemCache`, `Composure`), not on the state objects. This is
what lets every crow on the map safely share the same four state instances.

**Where to find it in game:** run `game.Application`. The crow is shown as
`v` on the farm map; it is placed on a random free tile when the world is
created.

**Reading the crow's messages:** every line the crow prints starts with a
tag, added in one place (`CreatureStateAction`), so the current state is
always visible even when symbols overlap on the map:

- `[Foraging] ...` — an ordinary turn in that state (`act`).
- `[Perching -> Mobbing] ...` — a transition this turn; the rest of the line
  is the new state's transition effect (`onEnter`).

Every effect also prints exactly what it touched — each item, crop and actor
by name, and every tile by its coordinates (e.g. `(5, 3) on Inherited Farm`)
— so the result of each step can be checked from the console alone.

### The four states

| State | Ordinary turn (`act`) | Transition effect (`onEnter`) |
|---|---|---|
| **Perching** | Default/idle: hops to a random enterable adjacent tile, chosen by an engine `Behaviour` (`WanderBehaviour`, the same one the Chicken, Cow and Merchant use). | Its droppings count as a free watering (`FreelyWaterable.receiveFreeWatering()`) for **every** crop on its own tile and the 8 tiles around it — a cross-feature hook into REQ1, reached through a `game.positions.FreelyWaterable` capability rather than a direct dependency on `Crop`, so `game.crow` has no compile-time dependency on `game.crops`. The message lists every crop watered, e.g. `... a free watering for every crop within 1 tile: [Parsnip, Melon].` |
| **Foraging** | *New behaviour.* If standing over an unclaimed item, hides it (see below); otherwise steps toward the nearest forageable item within sensing range. | Swoops in on the target item and scatters every *other* item within 1 tile of it onto random nearby tiles, as if startled by the sudden arrival. |
| **Mobbing** | *New behaviour.* Pursues the nearest actor turn after turn; once adjacent, steals one random item from that actor's inventory: if it is food the crow can eat (`Consumable.canBeConsumedBy`), the crow **eats it at once**; any other item goes into the crow's **own** inventory; otherwise steps toward the target. | The dive knocks **everything** the target is carrying onto random tiles around the target; unlike stealing, the crow keeps none of it. The message lists every item knocked loose, e.g. `... knocking everything they carry onto the tiles around (5, 3) on Inherited Farm: [Hoe, Watering Can, Pickaxe]!` |
| **Fleeing** | *New behaviour.* Steps directly away from the nearest actor (`CrowNavigation.stepAway`), or stays put if cornered. | Resets its safe-turn counter, drops its *entire* inventory (including anything stolen while Mobbing) onto random nearby tiles, and startles every adjacent actor one tile backwards. |

**Which states introduce genuinely new behaviour:** the spec explicitly
disqualifies "wandering" and "attacking whatever is next to it", which is
why Perching (random wander, done by the shared `WanderBehaviour`) is *not* counted. The other three each add a
behaviour that did not exist anywhere in the codebase before REQ5:

- **Foraging** — seek out the nearest item, then carry it off and hide it
  somewhere else. This is the spec's own example of a qualifying behaviour.
- **Fleeing** — directed retreat: every turn, pick the adjacent tile that
  moves the crow *furthest* from the closest actor. Before REQ5 the codebase
  could only move an actor *toward* a target (`stepToward`) or at random;
  `stepAway` was added for this state.
- **Mobbing** — a multi-turn pursuit of one actor that ends in taking an item
  and dealing with it by what it is: food is eaten on the spot (the crow gets
  the effect, through REQ1's `Consumable`), anything else goes into the
  crow's own inventory. This is deliberately different from the Merchant's
  Diamond theft in REQ3: that is a one-off side effect of a trade with an
  adjacent seller, and the stolen item is removed from the game, whereas
  Mobbing chases its target across turns and decides what to do with the
  item, and what the crow keeps can later be recovered (see below).

The spec requires at least two of the four states to be new behaviours;
three are provided, so the requirement does not hinge on any single one.

**Theft → recovery loop:** a non-food item Mobbing steals sits in the crow's own
inventory, not the farmer's loss forever — the moment the crow panics into
Fleeing, `onEnter` empties its inventory onto the tiles around it, so the
farmer can walk over and pick the stolen item back up. Likewise, the items
Mobbing's dive knocks to the ground stay there until the farmer picks them
up — or until the crow, once Foraging, carries them off and hides them. This
connects the states' effects into one coherent mechanic rather than treating
each state in isolation.

Emptying an inventory onto the surrounding tiles (used by both Mobbing's and
Fleeing's transition effects) is a single shared helper,
`ItemDropper.dropAllCarriedBy`, so the two effects never drift apart.

### Transition graph

Every state can reach **two** other states, on top of staying put — never
more than two, never fewer. Conditions are checked **top to bottom** and the
first one that holds wins, so the next state is always uniquely determined
(e.g. a Perching crow with exactly one item-carrying actor nearby goes to
Mobbing even if food is also in range):

```
Perching  --[exactly 1 actor within sensing range, carrying >=1 item]--> Mobbing
Perching  --[food within sensing range]-------------------------------> Foraging
Perching  --[otherwise]------------------------------------------------> Perching (stays)

Foraging  --[crowded: >=2 actors within sensing range]--> Fleeing
Foraging  --[no food left within sensing range]----------> Perching
Foraging  --[otherwise]-----------------------------------> Foraging (stays)

Mobbing   --[crowded: >=2 actors within sensing range]--> Fleeing
Mobbing   --[0 actors within sensing range]--------------> Perching
Mobbing   --[otherwise]-----------------------------------> Mobbing (stays)

Fleeing   --[safe (0 actors nearby) for >=3 consecutive turns]--> Perching
Fleeing   --[safe this turn, and food is within range]----------> Foraging
Fleeing   --[otherwise]-------------------------------------------> Fleeing (stays)
```

All conditions are computed by `CrowSensing` (package-private helper shared
by all four states, so each state's transition logic stays a one-line
expression instead of repeating the same neighbour-tile scanning): counting
actors within a 2-tile sensing radius (`Crow.SENSING_RADIUS`), searching for the
nearest forageable item (`CrowSensing.nearestFood()`, which uses
`Area.nearestTo`), and (for Fleeing) a consecutive "safe turns" counter kept
through the `Composure` capability. **No `Random` is ever consulted to pick the
next state** — randomness is only used *within* `act()`/`onEnter()` for flavour
(which tile to hide an item on, which item to steal), never to decide which
state comes next.

Tiles are narrowed with small `TileCondition` objects, one class per condition,
passed to `Area.filter(...)`: `HoldsActor` (a tile with an actor on it),
`EnterableBy` (a tile an actor can enter), `BareOfItems`, `OutsideOf` and the
crow's own `ForageableItems` (a tile holding an item the crow has not already
hidden). No state or helper loops over tiles itself, and a new kind of condition
is a new class, without changing `Area`.

### How it's driven

A single, generic `CreatureStateAction` (`src/game/actions/CreatureStateAction.java`)
is the only Action a stateful creature ever performs: it asks the creature's
current state for `nextState()`; if that is a different state (compared with
`equals`, so by state type rather than by object), it switches to it and calls the
new state's `onEnter()` *instead of* that turn's ordinary behaviour; otherwise it
calls `act()`. `Crow` extends `game.actors.ConsciousActor`, whose `playTurn()`
makes an actor with no hit points fall unconscious and do nothing; on every other
turn `Crow.playConsciousTurn()` simply returns `new CreatureStateAction()`. The
same `ConsciousActor` base is shared by the Farmer and every `WanderingActor`, so
the unconscious check is written once.

### Engine `Behaviour` for wandering

The engine's `Behaviour<Actor, Action>` is how a non-player actor decides what
to do on its own turn. `game.behaviours.WanderBehaviour` implements it: it picks,
uniformly at random, one adjacent tile the actor is able to enter and returns
the `MoveActorAction` for it (or `null` if there is none). It is shared by:

- `WanderingActor`, the base class of the Chicken, Cow and Merchant, which
  receives its behaviour when it is created (a `WanderBehaviour` by default,
  or any other `Behaviour` through the second constructor);
- the crow's `Perching` state, which uses it for its ordinary turn, so the
  "take one random step" rule is written once for the whole game.

The player's moves stay `Action`s, and the crow's own turn is still one
`CreatureStateAction`. Foraging, Mobbing and Fleeing keep `CrowNavigation` for
stepping toward and away from a target, because those steps depend on which
state the crow is in and on what it senses.

### How to test it in game

The crow senses everything within **2 tiles** (a 5x5 square around it,
`Crow.SENSING_RADIUS`); "actor" means *any* actor — the Farmer, the
Chicken (`n`), the Cow (`B`) and the Merchant (`$`) all count.

| To see | Do this | Expect a line like |
|---|---|---|
| **Perching → Mobbing** | Walk the Farmer, carrying at least one item, to within 2 tiles of a Perching crow while no other actor is within 2 tiles of it. The crow only mobs a lone actor who carries something, so the dive always has items to knock loose. | `[Perching -> Mobbing] Crow ... dives aggressively at Farmer Bob ..., knocking everything they carry onto the tiles around (x, y) on Inherited Farm: [Hoe, Watering Can, Pickaxe]!` — the Farmer's inventory is now empty. |
| **Mobbing (ordinary turn)** | Stay within 2 tiles. | `[Mobbing] ... swoops toward Farmer Bob ... landing at (x, y) ...`; once adjacent, `[Mobbing] ... steals Hoe from Farmer Bob ...! It now carries: [Hoe].` If the item is food, e.g. a Parsnip: `... steals Parsnip from Farmer Bob ...! Crow ... eats the Parsnip and is healed for 1 HP (...).` Food that needs a statistic the crow lacks (Dried products need stamina) is kept, not eaten. |
| **Perching → Foraging** | Drop an item within 2 tiles of a Perching crow, then step more than 2 tiles away (if the Farmer stays as the only nearby actor and still carries something, the crow mobs instead — Mobbing is checked first). | `[Perching -> Foraging] ... swoops down toward the food at (x, y) ..., scattering the other items around it: [Egg to (x, y) ...].`, or `... but there are no other items around it to scatter.` |
| **Foraging (ordinary turn)** | Wait. | `[Foraging] ... hops toward the food at (x, y) ..., landing at (x, y) ...`, then `[Foraging] ... snatches the Hoe from (x, y) ... and hides it at (x, y) ...`. A hidden item is remembered and never foraged again. |
| **Foraging → Perching** | Wait until every item within 2 tiles has been hidden. | `[Foraging -> Perching] ...` with the Perching effect below. |
| **Mobbing / Foraging → Fleeing** | Have 2 or more actors within 2 tiles of the crow, e.g. stand next to it while the Chicken or Cow wanders past. | `[Mobbing -> Fleeing] ... panics and takes flight at (x, y) ... (calm-down count reset to 0/3); dropping everything it carries onto the tiles around it: [Hoe]; startling actors backwards: [Farmer Bob ... to (x, y) ...]!` |
| **Fleeing (ordinary turn)** | Keep actors nearby, or move away. | `[Fleeing] ... flees from Cow ... at (x, y) ..., landing at (x, y) ...`, or `[Fleeing] ... keeps a wary eye out, catching its breath (safe for 2/3 turns before it calms down).` |
| **Fleeing → Perching** | Keep every actor more than 2 tiles away for 3 turns in a row. | `[Fleeing -> Perching] ... settles back onto a perch at (x, y) ..., and its droppings count as a free watering for every crop within 1 tile: [Parsnip, Melon].`, or `... with no crops within 1 tile to water.` |
| **Mobbing → Perching** | Move the Farmer more than 2 tiles away (or travel to the mines) with no other actor nearby. | `[Mobbing -> Perching] ...` with the Perching effect above. |
| **Fleeing → Foraging** | Let the crow be safe (no actor within 2 tiles) while an item lies within 2 tiles — e.g. the items it just dropped while panicking. | `[Fleeing -> Foraging] ... swoops down toward the food at (x, y) ...`, as for Perching → Foraging. If the only food is on the crow's own tile: `... starts foraging the items at (x, y) ..., without swooping anywhere, so no items are scattered.` |

**When an effect has nothing to act on, it says so.** A transition effect
depends on what is around the crow, so whenever part of it has nothing to
work on, the message states that explicitly instead of staying silent:

| Effect | Message when there is nothing to act on |
|---|---|
| Perching — water nearby crops | `... with no crops within 1 tile to water.` |
| Foraging — scatter items around the food | `... but there are no other items around it to scatter.` / `... without swooping anywhere, so no items are scattered.` |
| Fleeing — drop what it carries | `... it carries nothing to drop ...` |
| Fleeing — startle adjacent actors | `... no adjacent actor can be startled backwards!` |
| Mobbing — knock the target's items loose | `... is carrying nothing to knock loose.` (a safeguard only: Mobbing is entered only for a lone actor who carries something, and the dive happens on that same turn) |

The ordinary-turn behaviours do the same, e.g. `... finds nothing worth
foraging.`, `... is cornered by Cow ... and flaps in place.`, `... perches,
with nowhere to go.`

**Note — "finds nothing to steal":** Mobbing's transition effect empties the
target's inventory onto the ground, so on the following turns the crow's
ordinary Mobbing behaviour prints `[Mobbing] ... harasses Farmer Bob ... but
finds nothing to steal.` until the Farmer picks at least one item back up
(the pick-up option appears when standing on the item). To see a theft,
pick an item up and stay next to the crow. The same message appears if,
while Mobbing, the nearest actor becomes the Chicken, Cow or Merchant, who
carry nothing.

### SOLID

- **SRP** — each `CreatureState` implementation owns exactly one state's
  three responsibilities (its own `act`/`nextState`/`onEnter`); `Crow`
  itself only holds data and delegates; `CreatureStateAction`'s only job is
  to drive the generic transition protocol.
- **OCP** — adding a fifth state means writing one new class and wiring the
  relevant *existing* states' `nextState()` to recognise it (unavoidable for
  any explicit transition graph); `CreatureStateAction`, `StatefulCreature`
  and `Crow.playConsciousTurn()` never change. A second, unrelated stateful creature
  could reuse `CreatureStateAction`/`StatefulCreature`/`CreatureState`
  completely unmodified.
- **LSP** — any `CreatureState` can substitute for another everywhere
  `CreatureStateAction` uses one; all four honour the same three-method
  contract with no surprising preconditions.
- **ISP** — `CreatureState` is a tight three-method interface;
  `StatefulCreature`, `ItemCache` and `Composure` are each separate, small
  capability interfaces, so `Crow` only takes on exactly the per-creature
  data a particular state actually needs, rather than one fat interface
  every `Actor` would have to implement in full.
- **DIP** — `Perching` and `WanderingActor` depend on the engine's `Behaviour`
  interface, not on `WanderBehaviour`, which is only their default; any other
  `Behaviour` can be passed in through their constructors.
- **DIP** — `CreatureStateAction` depends only on the `StatefulCreature`/
  `CreatureState` abstractions, never on `Crow` or on any concrete state by
  name. There is no `instanceof`/`switch`-on-type dispatch anywhere in the
  state machine. Likewise, `Perching`'s free-watering effect depends only on
  the `FreelyWaterable` capability, never on the concrete `Crop` class,
  keeping `game.crow` and `game.crops` fully decoupled.