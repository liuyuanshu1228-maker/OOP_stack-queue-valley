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
`game.creaturestate.CreatureState` interface (`act`, `nextState`, `onEnter`). States
are stateless singletons (a private constructor and a public `INSTANCE`
field, the classic State-pattern/flyweight shape); any data that varies per
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
| **Perching** | Default/idle: hops to a random enterable adjacent tile. | Its droppings count as a free watering (`FreelyWaterable.receiveFreeWatering()`) for **every** crop on its own tile and the 8 tiles around it — a cross-feature hook into REQ1, reached through a `game.positions.FreelyWaterable` capability rather than a direct dependency on `Crop`, so `game.crow` has no compile-time dependency on `game.crops`. The message lists every crop watered, e.g. `... a free watering for every crop within 1 tile: [Parsnip, Melon].` |
| **Foraging** | *New behaviour.* If standing over an unclaimed item, hides it (see below); otherwise steps toward the nearest forageable item within sensing range. | Swoops in on the target item and scatters every *other* item within 1 tile of it onto random nearby tiles, as if startled by the sudden arrival. |
| **Mobbing** | *New behaviour.* Pursues the nearest actor turn after turn; once adjacent, steals one random item from that actor's inventory into the crow's **own** inventory; otherwise steps toward the target. | The dive knocks **everything** the target is carrying onto random tiles around the target; unlike stealing, the crow keeps none of it. The message lists every item knocked loose, e.g. `... knocking everything they carry onto the tiles around (5, 3) on Inherited Farm: [Hoe, Watering Can, Pickaxe]!` |
| **Fleeing** | *New behaviour.* Steps directly away from the nearest actor (`LocationUtils.stepAway`), or stays put if cornered. | Resets its safe-turn counter, drops its *entire* inventory (including anything stolen while Mobbing) onto random nearby tiles, and startles every adjacent actor one tile backwards. |

**Which states introduce genuinely new behaviour:** the spec explicitly
disqualifies "wandering" and "attacking whatever is next to it", which is
why Perching (random wander) is *not* counted. The other three each add a
behaviour that did not exist anywhere in the codebase before REQ5:

- **Foraging** — seek out the nearest item, then carry it off and hide it
  somewhere else. This is the spec's own example of a qualifying behaviour.
- **Fleeing** — directed retreat: every turn, pick the adjacent tile that
  moves the crow *furthest* from the closest actor. Before REQ5 the codebase
  could only move an actor *toward* a target (`stepToward`) or at random;
  `stepAway` was added for this state.
- **Mobbing** — a multi-turn pursuit of one actor that ends in taking an item
  into the crow's own inventory. This is deliberately different from the
  Merchant's Diamond theft in REQ3: that is a one-off side effect of a trade
  with an adjacent seller, and the stolen item is removed from the game,
  whereas Mobbing chases its target across turns and keeps what it takes, so
  the item can later be recovered (see below).

The spec requires at least two of the four states to be new behaviours;
three are provided, so the requirement does not hinge on any single one.

**Theft → recovery loop:** an item Mobbing steals sits in the crow's own
inventory, not the farmer's loss forever — the moment the crow panics into
Fleeing, `onEnter` empties its inventory onto the tiles around it, so the
farmer can walk over and pick the stolen item back up. Likewise, the items
Mobbing's dive knocks to the ground stay there until the farmer picks them
up — or until the crow, once Foraging, carries them off and hides them. This
connects the states' effects into one coherent mechanic rather than treating
each state in isolation.

Emptying an inventory onto the surrounding tiles (used by both Mobbing's and
Fleeing's transition effects) is a single shared helper,
`LocationUtils.dropInventoryNearby`, so the two effects never drift apart.

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
actors within a 2-tile sensing radius (`Crow.SENSING_RADIUS`), searching for the nearest forageable
item via `LocationUtils.nearestWhere`, and (for Fleeing) a consecutive
"safe turns" counter kept through the `Composure` capability. **No
`Random` is ever consulted to pick the next state** — randomness is only
used *within* `act()`/`onEnter()` for flavour (which tile to hide an item
on, which item to steal), never to decide which state comes next.

### How it's driven

A single, generic `CreatureStateAction` (`src/game/actions/CreatureStateAction.java`)
is the only Action a stateful creature ever performs: it asks the creature's
current state for `nextState()`; if that differs from the current state, it
switches to it and calls the new state's `onEnter()` *instead of* that
turn's ordinary behaviour; otherwise it calls `act()`. `Crow.playTurn()`
simply returns `new CreatureStateAction()` every turn (unless it has fallen
unconscious).

### How to test it in game

The crow senses everything within **2 tiles** (a 5x5 square around it,
`Crow.SENSING_RADIUS`); "actor" means *any* actor — the Farmer, the
Chicken (`n`), the Cow (`B`) and the Merchant (`$`) all count.

| To see | Do this | Expect a line like |
|---|---|---|
| **Perching → Mobbing** | Walk the Farmer, carrying at least one item, to within 2 tiles of a Perching crow while no other actor is within 2 tiles of it. The crow only mobs a lone actor who carries something, so the dive always has items to knock loose. | `[Perching -> Mobbing] Crow ... dives aggressively at Farmer Bob ..., knocking everything they carry onto the tiles around (x, y) on Inherited Farm: [Hoe, Watering Can, Pickaxe]!` — the Farmer's inventory is now empty. |
| **Mobbing (ordinary turn)** | Stay within 2 tiles. | `[Mobbing] ... swoops toward Farmer Bob ... landing at (x, y) ...`; once adjacent, `[Mobbing] ... steals Hoe from Farmer Bob ...! It now carries: [Hoe].` |
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

**Simulation check:** we drove the real `StackQueueValleyWorld` for 20 games
of 100 turns each, with the Chicken, Cow and Merchant wandering as normal and
the Farmer wandering at random and picking up any item it stepped on. All
eight transitions in the graph occurred, all four states appeared in 19 of
the 20 games, and every entry into Mobbing (35 of 35) knocked items loose.
With the original 5-tile radius, the 10x7 farm was almost entirely inside
the crow's range, so the animals and Merchant always counted as a crowd and
the crow never left Fleeing — which is why the radius is 2.

### SOLID

- **SRP** — each `CreatureState` implementation owns exactly one state's
  three responsibilities (its own `act`/`nextState`/`onEnter`); `Crow`
  itself only holds data and delegates; `CreatureStateAction`'s only job is
  to drive the generic transition protocol.
- **OCP** — adding a fifth state means writing one new class and wiring the
  relevant *existing* states' `nextState()` to recognise it (unavoidable for
  any explicit transition graph); `CreatureStateAction`, `StatefulCreature`
  and `Crow.playTurn()` never change. A second, unrelated stateful creature
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
  - **DIP** — `CreatureStateAction` depends only on the `StatefulCreature`/
    `CreatureState` abstractions, never on `Crow` or on any concrete state by
    name. There is no `instanceof`/`switch`-on-type dispatch anywhere in the
    state machine. Likewise, `Perching`'s free-watering effect depends only on
    the `FreelyWaterable` capability, never on the concrete `Crop` class,
    keeping `game.crow` and `game.crops` fully decoupled.