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

## Running the Unit Tests

The tests use JUnit 5 through Maven. `pom.xml` keeps the existing layout: the game and engine code stay in `src`, and the tests are in `test`, in the same packages as the classes they test.

Run every test with one command from the project folder:

```
mvn test
```

In IntelliJ, right-click `pom.xml` and choose **Add as Maven Project** once. After that you can run the tests from the Maven panel (**Lifecycle → test**), or right-click the `test` folder and choose **Run 'All Tests'**.

The tests do not depend on luck. `game.testsupport.FixedRandom` extends the game's `RandomUtils`, and a test passes it to the class under test through its constructor, just as `StackQueueValleyWorld` passes the real `RandomUtils`. With it, every chance either always succeeds or always fails, and every random pick takes the first option. `game.testsupport.TestFarm` builds small maps where `.` is Dirt and `,` is Soil.

| Requirement | Test classes |
|---|---|
| REQ1 Second Spring | `game.plants.PeriodicAbilityTest`, `GrowTest`, `SelfReplicateTest`, `ProduceTest`, `PlantStateTest` |
| REQ3 Pests and pest control | `game.pests.SlimeSnailTest`, `LocustSwarmTest`, `BurrowingMoleTest`, `PestTurnTest`, `PestPresentTest`; `game.pestcontrol.LadybugTest`, `SaltBarrierTest`, `SaltGroundTest`, `TrapGroundTest`, `DeployDeterrentActionTest`; `game.behaviours.SeekPlantBehaviourTest`; `game.positions.TemporaryGroundTest` |
| REQ4 Enchanted Tools | `game.enchantments.BlazeEnchantmentTest`, `BountyEnchantmentTest`, `ShatterEnchantmentTest`; `game.items.tools.EnchantmentSlotTest`, `PickaxeTest`, `WateringCanTest`; `game.actions.TillActionTest` |
| REQ5 API | Not written yet |

## A2 REQ5 — Stateful Creature: The Crow

This section describes Assignment 2's REQ5, which is still in the game. Requirement numbers in this section are Assignment 2's. It is not Assignment 3's REQ5.

Our group has three people, so we build **one** stateful creature. It is a `Crow` (`src/game/crow/Crow.java`) with exactly **four** states. Each state is its own class in `src/game/crow`. Every state implements the `game.creaturestate.CreatureState` interface, which has three methods: `act`, `nextState` and `onEnter`.

The states hold no data about one creature. A transition simply creates a new object of the next state, for example `new Mobbing()`. There is no shared instance and no static field. The state classes are not `final` and have public constructors. This lets us create a variant later, for example a subclass with a different transition rule. Every state extends `game.creaturestate.BaseCreatureState`. This base class treats two states of the same class as equal (`equals` and `hashCode`). So a new instance of a state never changes how the state machine behaves.

Data that differs between crows lives on the `Crow` object. This includes the items the crow has hidden and the number of turns it has felt safe. The crow keeps this data behind two small capability interfaces, `ItemCache` and `Composure`. The state objects never keep it. This is why a state object can be created fresh on every transition without losing anything. When a state needs the crow's randomness, area queries or sensing radius, it asks the crow at that moment through the `RandomnessSource`, `AreaFactorySource` and `SensingRange` capabilities.

**Where to find it in the game:** Run `game.Application`. The crow appears as `b` on the farm map. The game places it on a random free tile when it creates the world.

**How to read the crow's messages:** Every line the crow prints starts with a tag. `CreatureStateAction` adds the tag in one place. So you can always see the current state, even when map symbols overlap.

- `[Foraging] ...` is an ordinary turn in that state (`act`).
- `[Perching -> Mobbing] ...` is a transition on this turn. The rest of the line describes the effect of the new state (`onEnter`).

Every effect also names what it touched. It names each item, crop and actor, and it gives each tile as coordinates, for example `(5, 3) on Inherited Farm`. So you can check each step from the console alone.

### The four states

| State | Ordinary turn (`act`) | Transition effect (`onEnter`) |
|---|---|---|
| **Perching** | The default, calm state. The crow hops to a random adjacent tile that it can enter. An engine `Behaviour` (`WanderBehaviour`) chooses the tile. The Chicken, Cow and Merchant use the same one. | The crow's droppings give a free watering (`FreelyWaterable.receiveFreeWatering()`) to **every** crop on its own tile and the 8 tiles around it. This is a link to REQ1. The crow reaches the crops through the `game.positions.FreelyWaterable` capability, not through `Crop`. So `game.crow` does not depend on `game.crops` at compile time. The message lists every crop it watered, for example `... a free watering for every crop within 1 tile: [Parsnip, Melon].` |
| **Foraging** | *New behaviour.* If the crow stands on an item it has not hidden yet, it hides the item (see below). Otherwise it steps toward the nearest forageable item in sensing range. | The crow swoops onto the target item. It scatters every *other* item within 1 tile of it onto random nearby tiles, as if the sudden arrival startled them. |
| **Mobbing** | *New behaviour.* The crow chases the nearest actor, turn after turn. When it is adjacent, it steals one random item from that actor. If the item is food that the crow can eat (`Consumable.canBeConsumedBy`), the crow **eats it at once**. Any other item goes into the crow's **own** inventory. | The dive knocks **everything** the target carries onto random tiles around the target. The crow keeps none of it. The message lists each item, for example `... knocking everything they carry onto the tiles around (5, 3) on Inherited Farm: [Hoe, Watering Can, Pickaxe]!` |
| **Fleeing** | *New behaviour.* The crow steps directly away from the nearest actor (`CrowNavigation.stepAway`). It stays put if it is cornered. | The crow resets its safe-turn counter. It drops its *whole* inventory onto random nearby tiles, including anything it stole while Mobbing. It also pushes every adjacent actor one tile back. |

**Which states are new behaviour:** The spec does not count "wandering" or "attacking whatever is next to it". So we do not count Perching. It makes a random hop with the shared `WanderBehaviour`. The other three states each add a behaviour that the code did not have before REQ5:

- **Foraging** means finding the nearest item, carrying it away and hiding it somewhere else. The spec itself gives this as an example of a qualifying behaviour.
- **Fleeing** is a directed retreat. Each turn, the crow picks the adjacent tile that is *furthest* from the closest actor. Before REQ5, the code could only move an actor *toward* a target (`stepToward`) or at random. We added `stepAway` for this state.
- **Mobbing** is a chase over several turns. It ends when the crow steals an item and deals with it by what it is. The crow eats food on the spot and gets the effect through REQ1's `Consumable`. It puts anything else in its own inventory. This differs from the Merchant's Diamond theft in REQ3. That theft is a side effect of one trade with an adjacent seller, and the stolen item leaves the game. Mobbing chases its target across turns and decides what to do with the item. The farmer can recover what the crow keeps (see below).

The spec asks for at least two new behaviours among the four states. We provide three. So the requirement does not depend on any single one.

**Theft and recovery:** The crow keeps a non-food item in its own inventory. The farmer does not lose it for good. When the crow panics into Fleeing, `onEnter` empties its inventory onto the tiles around it. Then the farmer can walk over and pick the item up again. The items that the Mobbing dive knocks to the ground stay there until the farmer picks them up. The crow may also carry them away and hide them when it later Forages. So the effects of the states connect into one mechanic, and each state does not stand alone.

`ItemDropper.dropAllCarriedBy` empties an inventory onto the surrounding tiles. Mobbing and Fleeing both use it. So their two effects cannot drift apart.

### Transition graph

Every state can reach **two** other states, and it can also stay where it is. The crow checks the conditions **from top to bottom**, and the first condition that holds wins. So the next state is always clear. For example, a Perching crow with exactly one item-carrying actor nearby goes to Mobbing, even when food is also in range.

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

`CrowSensing` works out all the conditions. This public helper serves all four states. So each state's transition logic stays short, and no state repeats the tile scanning. `CrowSensing` counts the actors within the crow's 2-tile sensing radius, which it reads through the `SensingRange` capability. It finds the nearest forageable item (`CrowSensing.nearestFood()`, which uses `Area.nearestTo`). For Fleeing, it also keeps a count of consecutive safe turns through the `Composure` capability.

**The crow never uses a random number to pick the next state.** Randomness only appears inside `act()` and `onEnter()`. There it decides small things, such as which tile hides an item and which item the crow steals.

Small `TileCondition` objects narrow the tiles. Each condition is its own class, and `Area.filter(...)` takes it. The conditions are `HoldsActor` (a tile with an actor on it), `EnterableBy` (a tile an actor can enter), `BareOfItems`, `OutsideOf`, and the crow's own `ForageableItems` (a tile with an item the crow has not hidden). No state or helper loops over tiles by itself. A new kind of condition is a new class, and `Area` stays the same.

### How the crow's turn is driven

One generic `CreatureStateAction` (`src/game/actions/CreatureStateAction.java`) is the only action that a stateful creature performs. It asks the current state for `nextState()`. If the answer is a different state, the action switches to it. It compares states with `equals`, so it compares their types, not their objects. Then it calls the new state's `onEnter()` *instead of* the ordinary turn. If the state is the same, it calls `act()`.

`Crow` extends `game.actors.ConsciousActor`. The `playTurn()` of this class makes an actor with no hit points fall unconscious and do nothing. On every other turn, `Crow.playConsciousTurn()` returns a new `CreatureStateAction`. The Farmer and every `WanderingActor` share the same `ConsciousActor`. So we write the unconscious check once.

### Engine `Behaviour` for wandering

The engine's `Behaviour<Actor, Action>` lets a non-player actor decide what to do on its own turn. `game.behaviours.WanderBehaviour` implements it. It picks one adjacent tile that the actor can enter, with equal chance for each tile. Then it returns the `MoveActorAction` for that tile. It returns `null` when no tile is free. Two places share it:

- `WanderingActor` is the base class of the Chicken, Cow and Merchant. Its constructor must be given a `Behaviour`; there is no hidden default. `Animal` (the Chicken and Cow) and `Merchant` each pass a `WanderBehaviour` built with the injected `RandomUtils`.
- The crow's `Perching` state builds a `WanderBehaviour` with the crow's own randomness for its ordinary turn. So the rule "take one random step" exists once in the whole game.

The player's moves stay `Action`s. The crow's own turn is still one `CreatureStateAction`. Foraging, Mobbing and Fleeing keep `CrowNavigation` for stepping toward and away from a target. Their steps depend on the state and on what the crow senses. So a general `Behaviour` does not fit them.

### How to test it in the game

The crow senses everything within **2 tiles**. This is a 5x5 square around it (the crow's `SensingRange`). Here, "actor" means *any* actor. The Farmer, the Chicken (`c`), the Cow (`C`) and the Merchant (`$`) all count.

| To see | Do this | Expect a line like |
|---|---|---|
| **Perching → Mobbing** | Walk the Farmer to within 2 tiles of a Perching crow. The Farmer must carry at least one item, and no other actor may be within 2 tiles of the crow. The crow only mobs a lone actor who carries something, so the dive always has items to knock loose. | `[Perching -> Mobbing] Crow ... dives aggressively at Farmer Bob ..., knocking everything they carry onto the tiles around (x, y) on Inherited Farm: [Hoe, Watering Can, Pickaxe]!` The Farmer's inventory is now empty. |
| **Mobbing (ordinary turn)** | Stay within 2 tiles. | `[Mobbing] ... swoops toward Farmer Bob ... landing at (x, y) ...` When the crow is adjacent, you see `[Mobbing] ... steals Hoe from Farmer Bob ...! It now carries: [Hoe].` If the item is food, for example a Parsnip, you see `... steals Parsnip from Farmer Bob ...! Crow ... eats the Parsnip and is healed for 1 HP (...).` The crow keeps food that needs a statistic it does not have. Dried products need stamina, so the crow keeps them and does not eat them. |
| **Perching → Foraging** | Drop an item within 2 tiles of a Perching crow. Then step more than 2 tiles away. If the Farmer stays as the only actor nearby and still carries something, the crow mobs instead, because the game checks Mobbing first. | `[Perching -> Foraging] ... swoops down toward the food at (x, y) ..., scattering the other items around it: [Egg to (x, y) ...].` Or: `... but there are no other items around it to scatter.` |
| **Foraging (ordinary turn)** | Wait. | `[Foraging] ... hops toward the food at (x, y) ..., landing at (x, y) ...` Then you see `[Foraging] ... snatches the Hoe from (x, y) ... and hides it at (x, y) ...` The crow remembers a hidden item and never forages it again. |
| **Foraging → Perching** | Wait until the crow has hidden every item within 2 tiles. | `[Foraging -> Perching] ...` followed by the Perching effect. |
| **Mobbing / Foraging → Fleeing** | Put 2 or more actors within 2 tiles of the crow. For example, stand next to it while the Chicken or Cow wanders past. | `[Mobbing -> Fleeing] ... panics and takes flight at (x, y) ... (calm-down count reset to 0/3); dropping everything it carries onto the tiles around it: [Hoe]; startling actors backwards: [Farmer Bob ... to (x, y) ...]!` |
| **Fleeing (ordinary turn)** | Keep actors nearby, or move away. | `[Fleeing] ... flees from Cow ... at (x, y) ..., landing at (x, y) ...` Or: `[Fleeing] ... keeps a wary eye out, catching its breath (safe for 2/3 turns before it calms down).` |
| **Fleeing → Perching** | Keep every actor more than 2 tiles away for 3 turns in a row. | `[Fleeing -> Perching] ... settles back onto a perch at (x, y) ..., and its droppings count as a free watering for every crop within 1 tile: [Parsnip, Melon].` Or: `... with no crops within 1 tile to water.` |
| **Mobbing → Perching** | Move the Farmer more than 2 tiles away, or travel to the mines. No other actor may be nearby. | `[Mobbing -> Perching] ...` followed by the Perching effect. |
| **Fleeing → Foraging** | Make the crow safe (no actor within 2 tiles) while an item lies within 2 tiles. The items it just dropped while it panicked are a good example. | `[Fleeing -> Foraging] ... swoops down toward the food at (x, y) ...`, as for Perching → Foraging. If the only food is on the crow's own tile, you see `... starts foraging the items at (x, y) ..., without swooping anywhere, so no items are scattered.` |

**When an effect has nothing to act on, the message says so.** A transition effect depends on what is around the crow. So whenever part of an effect has nothing to work on, the message states this. It does not stay silent.

| Effect | Message when there is nothing to act on |
|---|---|
| Perching: water nearby crops | `... with no crops within 1 tile to water.` |
| Foraging: scatter items around the food | `... but there are no other items around it to scatter.` or `... without swooping anywhere, so no items are scattered.` |
| Fleeing: drop what it carries | `... it carries nothing to drop ...` |
| Fleeing: startle adjacent actors | `... no adjacent actor can be startled backwards!` |
| Mobbing: knock the target's items loose | `... is carrying nothing to knock loose.` This is only a safeguard. The crow enters Mobbing only for a lone actor who carries something, and the dive happens on that same turn. |

The ordinary-turn behaviours work in the same way. Examples are `... finds nothing worth foraging.`, `... is cornered by Cow ... and flaps in place.` and `... perches, with nowhere to go.`

**Note on "finds nothing to steal":** The Mobbing transition effect empties the target's inventory onto the ground. So on the next turns, the ordinary Mobbing turn prints `[Mobbing] ... harasses Farmer Bob ... but finds nothing to steal.` This continues until the Farmer picks at least one item up again. The pick-up option appears when the Farmer stands on the item. To see a theft, pick an item up and stay next to the crow. The same message appears when the nearest actor becomes the Chicken, Cow or Merchant while the crow is Mobbing. These actors carry nothing.

### SOLID

- **SRP:** Each `CreatureState` implementation owns the three duties of exactly one state: its own `act`, `nextState` and `onEnter`. `Crow` only holds data and hands the work to the states. `CreatureStateAction` only drives the generic transition protocol.
- **OCP:** A fifth state is one new class. We also edit the existing states that must lead to it, and any explicit transition graph needs this. `CreatureStateAction`, `StatefulCreature` and `Crow.playConsciousTurn()` never change. A second, unrelated stateful creature can reuse `CreatureStateAction`, `StatefulCreature` and `CreatureState` without any change.
- **LSP:** Any `CreatureState` can replace another wherever `CreatureStateAction` uses one. All four states follow the same three-method contract, and none of them adds a surprising precondition.
- **ISP:** `CreatureState` is a small three-method interface. `StatefulCreature`, `ItemCache`, `Composure`, `SensingRange`, `AreaFactorySource` and `RandomnessSource` are separate, small capability interfaces. So `Crow` takes on only the data that a state needs. No single large interface forces every `Actor` to implement everything.
- **DIP (wandering):** `WanderingActor` depends only on the engine's `Behaviour` interface, never on `WanderBehaviour`, and its constructor accepts any `Behaviour`. `Perching` is the exception: it creates a `WanderBehaviour` itself, because a state object is created fresh on each transition and has no constructor through which a behaviour could be injected.
- **DIP (the driver):** `CreatureStateAction` depends only on the `StatefulCreature` and `CreatureState` abstractions. It never names `Crow` or any concrete state. The state machine has no `instanceof` and no `switch` on a type. In the same way, the free watering in `Perching` depends only on the `FreelyWaterable` capability, never on the concrete `Crop` class. So `game.crow` does not depend on `game.crops`.
