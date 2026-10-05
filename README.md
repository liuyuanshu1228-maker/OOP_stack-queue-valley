# FIT2099 Assignment (Semester 2, 2026)

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
`game.actors.CreatureState` interface (`act`, `nextState`, `onEnter`). States
are stateless singletons (a private constructor and a public `INSTANCE`
field, the classic State-pattern/flyweight shape); any data that varies per
creature — which items it's hidden, how many consecutive turns it's felt
safe — lives on the `Crow` object itself, via two small capability
interfaces (`ItemCache`, `Composure`), not on the state objects. This is
what lets every crow on the map safely share the same four state instances.

**Where to find it in game:** run `game.Application`. The crow is shown as
`v` on the farm map; it is placed on a random free tile when the world is
created.

### The four states

| State | Ordinary turn (`act`) | Transition effect (`onEnter`) |
|---|---|---|
| **Perching** | Default/idle: hops to a random enterable adjacent tile. | If it lands on a `Crop`, its droppings count as a free watering (`Crop.recordWatering()`) — a small cross-feature hook into REQ1. |
| **Foraging** | *New behaviour.* If standing over an unclaimed item, hides it (see below); otherwise steps toward the nearest forageable item within sensing range. | Swoops in on the target item and scatters every *other* item within 1 tile of it onto random nearby tiles, as if startled by the sudden arrival. |
| **Mobbing** | *New behaviour.* Pursues the nearest actor turn after turn; once adjacent, steals one random item from that actor's inventory into the crow's **own** inventory; otherwise steps toward the target. | First strike: immediately steals one item from the nearest actor **regardless of distance**, so the theft happens the moment the crow turns on its target. |
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
Fleeing, `onEnter` empties its inventory onto the ground at its current
location, so the farmer can walk over and pick the stolen item back up. This
connects two states' effects into one coherent mechanic rather than treating
each state in isolation.

### Transition graph

Every state can reach **two** other states, on top of staying put — never
more than two, never fewer. Conditions are checked **top to bottom** and the
first one that holds wins, so the next state is always uniquely determined
(e.g. a Perching crow with exactly one actor nearby goes to Mobbing even if
food is also in range):

```
Perching  --[exactly 1 actor within sensing range]--> Mobbing
Perching  --[food within sensing range]-------------> Foraging
Perching  --[otherwise]------------------------------> Perching (stays)

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
actors within a 5-tile sensing radius, searching for the nearest forageable
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
  state machine.