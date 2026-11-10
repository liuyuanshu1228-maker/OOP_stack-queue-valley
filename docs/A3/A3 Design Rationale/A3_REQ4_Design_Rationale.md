# FIT2099 Assignment 3 Design Rationale

## Yuanshu Liu 36226173 – REQ 4: Enchanted Tools

The Farmer can enchant a Hoe, a Watering Can or a Pickaxe. Each of these tools is now `Enchantable`. The Enchantments are Blaze, Bounty and Shatter, each a new `Enchantment` class. The next use of the tool fires its own spread and then the Enchantment on every tile the spread touched. The Enchantment is then spent. We had three design goals. Any tool can carry any Enchantment, and the actions never name a tool. The store-fire-clear bookkeeping is written once. Existing effects are reused instead of building new ones. Abstractions were added only where three tools or two actions already share code (KISS, YAGNI).

## A. Finding and firing the tool that was used, without `instanceof`

After tilling, watering or breaking, the action must fire the Enchantment of the exact tool it used. This needs a capability, a shared lookup and a way to recognise that one tool.

**A capability, not a type check.** Two actions use tools: `CapabilityToolAction` (tilling and watering) and `BreakAction`. Each must fire the Enchantment of the tool it just used.

**Design 1:** check the tool's class, for example `if (tool instanceof Hoe)`, and read its enchantment directly.

| Pros | Cons |
|---|---|
| The check is short and needs no new interface or helper (KISS). | Every new enchantable tool adds a branch to two actions, the Switch Statements smell spread over two classes (OCP). |
| A reader sees in the action itself which tools can carry an Enchantment. | Both actions would name every tool class (Connascence of Type). Renaming or adding a tool would mean editing both actions (Shotgun Surgery). |
|  | The tests of both actions would have to cover every tool. A forgotten case would only fail while playing. |

**Design 2 (chosen):** a capability interface, `Enchantable`, with `enchant(Enchantment)` and `triggerEnchantment(Actor, Location)`.

| Pros | Cons |
|---|---|
| Both actions reach the tool's Enchantment only through `Enchantable`, so they compile without knowing any tool class (DIP). | It is one more interface to read. Acceptable because it follows the existing `Tillable` and `Waterable` pattern, so a reader already knows the shape. |
| A new enchantable tool that implements `Enchantable` needs no change to either action (OCP). | The actions still need a way to find the tool's `Enchantable` view, because the engine hands them a narrower type. Acceptable because the shared lookup below solves this once for both actions. |
| The tool, which owns the Enchantment, decides whether it has anything to fire (encapsulation). | A new tool must provide both methods of `Enchantable`, even if it only wants to fire. Acceptable because extending `AbstractEnchantableTool` already provides `enchant()`, so a tool writes only `triggerEnchantment()`. |

**One shared lookup.** After their own effect, both actions need the same step. They find the tool just used among the actor's `Enchantable` items, and fire it.

**Design 1:** write the lookup loop inside each action.

| Pros | Cons |
|---|---|
| There is no new class. The lookup sits next to the code that needs it, so each action reads alone. | The same loop would sit in two unrelated actions (Duplicate Code). A third action that uses a tool would copy it again. |
|  | If the lookup changed, both copies would have to change together (Connascence of Algorithm). |

**Design 2 (chosen):** `EnchantmentTrigger.fire(Actor, Object usedTool, Location)`, used by both actions. In the first version only `CapabilityToolAction` needed the lookup. When `BreakAction` needed the same loop, we moved it into one class. So the class was made for a real second user, not "just in case".

| Pros | Cons |
|---|---|
| One place knows how to go from a tool in hand to its `Enchantable` view (SRP). | The `usedTool` parameter is typed `Object`, so a wrong argument fails silently instead of at compile time. Acceptable because the two callers hold tools of unrelated types (Section F). A narrower type would need a new interface just for this. |
| A future action that uses a tool, such as a scything action, reuses it with no new code (OCP). | A new `EnchantmentTrigger` object is created on each use. Acceptable because it holds no state, so creating it only reads oddly and costs nothing. |
| The loop exists once, so a fix to the lookup reaches both actions (DRY). |  |

**Recognising the exact tool.** The engine's `Inventory.getItemsAs(Class)` returns views narrowed to one capability. A tool held as a `TillingTool` cannot be asked for a second capability, and the engine cannot be changed. So `EnchantmentTrigger` asks for a second view, `getItemsAs(Enchantable.class)`. Then it looks for the entry that is the same object as the tool in hand.

**Design 1:** `instanceof Enchantable` plus a cast.

| Pros | Cons |
|---|---|
| It is short and familiar to Java readers, and needs no second inventory view. | It asks "what class is this?", which is the wrong question. It also brings back the Connascence of Type that the interface removed. |
|  | A type check plus a cast is the smell polymorphism should replace. If the check is ever wrong, the cast fails at run time, with an error far from its cause. |

**Design 2:** compare with `equals`.

| Pros | Cons |
|---|---|
| It is familiar, reads like an ordinary value comparison, and would keep working if tools later defined their own equality. | Tools do not override `equals`, so it would quietly mean `==` anyway. A reader would wrongly expect two equal-looking tools to match (Connascence of Meaning). A later `equals` override, added for another reason, would silently change which tool fires. |

**Design 3 (chosen):** compare references with `==` (`enchantable == usedTool`).

| Pros | Cons |
|---|---|
| It asks the real question, "is this the exact tool used this turn?", with no cast and no type check. | It is Connascence of Identity, the strongest kind. Acceptable because both references are found and compared inside one method, and never stored. The rule cannot leak to other classes. |
| Two identical Hoes are told apart, because the comparison is by reference, so only the tool actually used matches. |  |

**Foresight:** an enchantable Scythe implements `Enchantable` through the base class in Section B, and the actions stay the same.

**Connascence:** the actions only depend on the type `Enchantable`, not on three tool classes (Connascence of Type). They also depend on the name `fire` (Connascence of Name). The one Connascence of Identity (`==`) stays inside one method. **Code smells:** avoided type checks, casts, Switch Statements on the tool's class and Duplicate Code. Accepted: a weakly typed `Object` parameter.

## B. Sharing enchanting: one base class and one slot

All three tools store, offer, fire and clear Enchantments the same way. Only their spread differs.

**One base class.** All three tools must do the same three things. They store the Enchantments they offer, offer one `EnchantAction` for each, and hold the one attached. None of this depends on the tool.

**Design 1:** each tool implements `Enchantable` on its own.

| Pros | Cons |
|---|---|
| There is no new abstract class, so each tool reads alone, top to bottom, without a parent to open (KISS). | The same storage and menu code would be written three times (Duplicate Code). A fourth tool would copy it again. |
|  | A change to how `EnchantAction`s are offered would need three edits (Divergent Change, Connascence of Algorithm). |

**Design 2 (chosen):** `AbstractEnchantableTool extends Item implements Enchantable`. It owns the offered Enchantments, the attached one (through `EnchantmentSlot`, below), `enchant()` and the menu. `Hoe`, `WateringCan` and `Pickaxe` extend it and write only `triggerEnchantment()`. An interface default method cannot hold a field, so sharing the stored Enchantment needs a class. This is a language limit, not a style choice.

| Pros | Cons |
|---|---|
| Storage and the menu are written once, so a fix reaches all three tools (DRY). | A tool that should offer only some Enchantments would have to override `allowableActions()`. Acceptable because no tool needs a partial menu today (YAGNI). |
| Each tool changes only when its own spread changes, because the bookkeeping now lives in the parent (SRP). | Subclasses reach the slot through the `protected` accessor `enchantmentSlot()`, a Connascence of Name that a rename would break. Acceptable because the accessor is the only member subclasses depend on, never a field. |
| Each of the three tools works in `EnchantAction`, because all of them inherit the same `enchant()` (LSP). |  |

**One slot that stores, fires and clears.** Firing is the same for every tool. If an Enchantment is attached, apply it to the affected tiles, then clear it. What differs is which tiles the tool's own spread touches.

**Design 1:** write the store, fire and clear steps inside each tool's `triggerEnchantment()`.

| Pros | Cons |
|---|---|
| There is one class fewer, and everything about one tool's turn sits in one method, easy to read once. | One method would mix the bookkeeping with the tool's spread, so it changes for two reasons (SRP). Each tool's tests would also cover the bookkeeping again. |
|  | The steps "check, apply, clear" would be written three times. Forgetting "clear" in one copy would let an Enchantment fire forever (Connascence of Execution). |

**Design 2 (chosen):** `EnchantmentSlot`, a small helper. It holds at most one Enchantment. It offers `enchant()`, `isEmpty()` and `fire(...)`, which applies the effect and then clears the slot.

| Pros | Cons |
|---|---|
| Each tool only builds its list of tiles and hands it to the slot. So the tool holds only its own spread (SRP). | Tracing a turn needs one more step: tool, then slot, then Enchantment. The slot is close to a Middle Man. Acceptable because the extra step is what keeps the one-use rule in one place. |
| "Apply, then clear" is written once, so a tool that fires through the slot cannot skip the clear step (DRY). | The slot holds `null` while empty. Acceptable because the `null` stays private and callers only see `isEmpty()`. |
| Other small helpers in the code work the same way, such as `StatisticAdjuster`. So the pattern is familiar. |  |

**Foresight:** a fourth enchantable tool writes only its `triggerEnchantment()`.

**Connascence:** subclasses depend on the parent only by Name (`enchantmentSlot()`), never on its fields. "apply, then clear" is Connascence of Execution, kept inside one method. **Code smells:** avoided Duplicate Code and Divergent Change. Accepted: one extra level of indirection (tool, slot, Enchantment).

## C. Enchantment effects: reuse, and one contract

Each Enchantment reuses an existing effect, and all of them report in the same way.

| Enchantment | Reuses | Instead of |
|---|---|---|
| `BlazeEnchantment` | The existing `Fire` item | A new burning mechanism |
| `ShatterEnchantment` | The existing `StunnedStatus` | A new stun status |
| `BountyEnchantment` | REQ1's `Plant.forceGrowth()`, which tries the plant's own abilities at once, without ageing it or printing | A growth loop of its own |

**Reusing existing classes.**

**Design 1:** each Enchantment writes its own fire, stun and growth logic.

| Pros | Cons |
|---|---|
| Each Enchantment class stands alone and can be read without opening other packages. | There would be a second burning rule and a second growth loop. Both must be kept in step with the originals (Duplicate Code, Connascence of Algorithm). |
|  | A frost pause or a new growth rule would have to be added in two places. Missing one would make magic growth differ from natural growth. |

**Design 2:** Bounty calls `Plant.attemptGrowth()`, the method the plant uses on its own turn (our first version).

| Pros | Cons |
|---|---|
| Bounty needs no new method in `Plant`. It uses exactly the rules of a normal turn. | `attemptGrowth` checks each ability's interval and chance first. On most turns a plant is "not due", so the magic silently did nothing. |
|  | The player spent an Enchantment and saw no growth, which breaks the proposal's promise that Bounty forces growth. |

**Design 3 (chosen):** reuse `Fire` and `StunnedStatus`, and call `Plant.forceGrowth()` for Bounty. `forceGrowth` tries the same abilities, but skips their interval and chance (REQ1, Section B).

| Pros | Cons |
|---|---|
| Each effect already exists and is already tested. There is no second burning rule and no second growth loop in `game.enchantments` (DRY, KISS). | `game.enchantments` depends on `game.items`, `game.status` and `game.plants` (Connascence of Name across packages). Acceptable because these are stable public methods, and there is no package cycle. |
| Bounty now grows a plant on any turn, as the proposal says. Three tests check this on turns a plant is not due: a seedling at ages 2 and 4 grows, and a strawberry at age 2 drops fruit. | `Plant` gained a second growth method, so its short loop is written twice. Acceptable because the copies differ in one call and sit side by side in one class (REQ1, Section G). |
| `forceGrowth` checks REQ5's frost pause too, so Bounty does not grow a paused plant. A test checks this. |  |

**The empty-string contract.** `Enchantable.triggerEnchantment`, `EnchantmentSlot.fire` and `Enchantment.applyTo` must say when nothing fired.

**Design 1:** return `null` for "nothing happened".

| Pros | Cons |
|---|---|
| It is common in Java and cheap to write. A caller can test for it with one `if`. | Every caller must remember what `null` means (Connascence of Meaning), and a forgotten check crashes the turn. Scattered `null` checks are a known smell. |
|  | REQ1's abilities already use `""` for the same meaning, so two features would say "nothing" in two different ways. |

**Design 2:** throw an exception when nothing fired.

| Pros | Cons |
|---|---|
| No caller can ignore it. The exception can also say why nothing fired. | An empty slot is normal, not an error. Yet every caller would need a `try/catch` for it, repeated in each caller (Connascence of Execution). |
|  | Exceptions would be used for normal events. Real errors would be harder to spot among them. |

**Design 3 (chosen):** return `""`, the same rule REQ1's abilities use.

| Pros | Cons |
|---|---|
| Every caller simply joins the strings, with no `null` check and no `try/catch` around the call (KISS). | A reader must know that `""` means "nothing happened" (Connascence of Meaning). Acceptable because the Javadoc of each method says so. It is also one rule for two features, not a different rule per method. |
| All implementations keep one contract, shared with REQ1's abilities, so any Enchantment can replace another (LSP). |  |

Every message still names a location. With no affected tiles, each Enchantment reports "near" the tool's tile. Both actions start their message with "At" and the location.

**Connascence:** Connascence of Name on three stable public parts of other packages: `Fire`, `StunnedStatus` and `Plant.forceGrowth`. There is also one Connascence of Meaning: `""` means "nothing happened". It is documented, and REQ1 uses the same rule. **Code smells:** avoided Duplicate Code, parallel mechanisms for the same effect and scattered `null` checks.

## D. Visibility, `final`, `protected` and `static`

| Choice | Where | Why | Cost |
|---|---|---|---|
| Every class is `public` | All REQ4 classes | Another package can reuse them, for example a new action that uses `EnchantmentTrigger`. Being `public` only makes reuse possible. Openness for extension comes from the interfaces and hooks, not from the modifier. | A wider public API, so more classes could depend on them (more connascence across packages). |
| Every field is `private` | All classes | Subclasses use `enchantmentSlot()`, not the field, so no Data Class is manipulated from outside. | A subclass that needs more of the parent's state must ask for a new accessor. |
| The offered list is copied | `AbstractEnchantableTool` keeps `List.copyOf(offeredEnchantments)` | A `private` field alone does not stop a caller changing the list it passed in. With the copy, adding to or removing from that list later does not change a tool's menu (no mutable aliasing). | One small copy per tool, made once at construction. |
| `protected` accessor | `AbstractEnchantableTool.enchantmentSlot()` | Only the tools need the slot to fire it. Callers use `Enchantable`. | Classes in `game.items.tools` could also call it. None does. |
| `protected` constructor | `AbstractEnchantableTool` | It is abstract, so only tools construct it, and `protected` says so. | Same-package classes could call it, but Java forbids creating an abstract class anyway. |
| `final` classes | `EnchantmentSlot`, `EnchantmentTrigger` | Small helpers used by composition, not by inheritance. `EnchantmentSlot` promises to hold at most one Enchantment and to clear it after firing. A subclass could override `enchant`, `isEmpty` or `fire` and break that promise. Making only `fire` final would not be enough. They are the only `final` classes in the game. Every other class is open for extension. | Neither class can be extended. A tool that needs two Enchantments would hold two slots, or a new class that holds several. This extends the behaviour by composition and keeps each slot's one-Enchantment promise. Limiting which Enchantments a tool offers needs no subclass either. Each tool gets its own list when it is built. |
| Constants are `private static final` | e.g. `CHAIN_BREAK_CHANCE_PERCENT`, `STUN_DURATION_TURNS` | A name explains each number, avoiding magic numbers (Connascence of Meaning). No public constants (A2 feedback). | Tests repeat the numbers. |
| No static methods | Every REQ4 class | A2 feedback penalised static methods as "function thinking". | `EnchantmentTrigger` is created with `new` in each action instead of being called statically. |

**Connascence:** `private` fields and the copied list keep connascence on state inside each class. **Code smells:** avoided public attributes, mutable aliasing, magic numbers and missing modifiers.

## E. Changes to the earlier design

| Change | Why | Cost |
|---|---|---|
| `Hoe`, `WateringCan` and `Pickaxe` extend `AbstractEnchantableTool` instead of `Item`. | They are the three tools changed to carry an Enchantment (Section B). | An unenchanted tool behaves as before. Its menu gains the enchant options. |
| `CapabilityToolAction` and `BreakAction` call `EnchantmentTrigger` after their own effect. | So every tool use can fire its Enchantment (Section A). | One more call in each action. |
| `TillingTool` and `WateringTool` declare `canTill(Tillable)` and `canWater(Waterable)`. | A2 feedback: empty marker interfaces are not good OO design. | Every tool returns `true` today. |
| `Enchantment` and `EnchantAction` live in `game.items.tools`. | Keeping them there removes a package cycle between `game.items.tools` and `game.enchantments`. | The interface sits beside the tools, not beside its implementations. |
| The tools receive their Enchantments through the constructor. | No hard-coded list inside the tools. `StackQueueValleyWorld` decides what is offered. | One more constructor parameter. |

## F. Known minor violations and why we accept them

| Violation or cost | Why it is acceptable |
|---|---|
| `EnchantmentTrigger.fire` takes `Object usedTool`, so a wrong argument fails silently (no match, `""`) instead of at compile time. | The two callers hold tools of different types: `T extends HasStaminaCost` and `BreakingTool`. `HasStaminaCost` does not say the tool is an `Item`. A narrower type would need a new shared interface or a new generic bound, just to tighten one parameter (YAGNI). |
| The three `triggerEnchantment()` bodies share a shape: check the slot, build a tile list, fire, join the messages. | Their spreads differ in kind: every adjacent tile, one random tile, or one random tile behind a chance roll. What they share, the slot logic, is already in `EnchantmentSlot`. With three different shapes, a common template would be a guess (rule of three). |
| `game.enchantments` depends on three other packages (Section C). | Reuse is the point. There is no cycle. |
| A Hoe or Pickaxe rarely carries Bounty to a plant, because tilled or broken tiles hold no plant. | The proposal says Bounty forces growth on any plant on an affected tile. With the Watering Can, it makes a watered crop produce or spread again. `WateringCanTest` checks this through the real tool. |

## Summary

The final design uses the chosen design of every part. The used tool is found through the `Enchantable` capability, by reference and not by class (A). One base class and one slot hold the shared bookkeeping (B). Enchantments reuse existing effects under one `""` contract (C). Each abstraction removes code that three tools or two actions already shared. The main cost is extra steps. A turn passes from the action through `EnchantmentTrigger`, the tool and the slot to the Enchantment. In return, the design passes a real OCP test. An enchantable Scythe needs one new tool class. A Frost Enchantment needs one new `Enchantment` class. Neither changes either action, `AbstractEnchantableTool`, `EnchantmentSlot` or any other Enchantment. Only `StackQueueValleyWorld` offers them.

| Principle | Where it shows | Change it absorbs | Where it is only partly met, and why |
|---|---|---|---|
| SRP | Each job has one class. `AbstractEnchantableTool` offers. `EnchantmentSlot` stores and fires. `EnchantmentTrigger` finds the used tool. Each tool has its spread, and each Enchantment its effect. | A new firing rule edits only `EnchantmentSlot`. | Each tool's `triggerEnchantment()` both spreads and joins the messages. Splitting them would add a class per tool. |
| OCP | Adding a tool or an Enchantment means writing one new class. | A Scythe or a Frost Enchantment: no existing tool, action or Enchantment changes. | New Enchantments are listed in `StackQueueValleyWorld`, the composition root. |
| LSP | Each of the three tools works in both actions, and each of the three Enchantments in any slot. Each tool and each Enchantment has its own test class. | A new tool fires with no new branch. | All Enchantments must keep the `""` contract. The Javadoc states it. |
| ISP | `Enchantable` has two methods and `Enchantment` one. The actions see only `Enchantable`. | A new Enchantment implements one method and sees nothing of the tools. | `Enchantable` joins enchanting and firing. Every enchantable tool needs both, so splitting would add a type nobody uses alone. |
| DIP | The actions depend on `Enchantable`, the tools on `Enchantment`. `RandomUtils` and `AreaFactory` are passed in through constructors, which lets tests replace them. Both are concrete classes, so this is dependency injection rather than full DIP. | A new Enchantment needs no change to the tools. | `EnchantmentTrigger` is created with `new` in each action. It has no state and one possible behaviour. |

**DRY and KISS.** The menu, the slot and the lookup are each written once. The Enchantments reuse `Fire`, `StunnedStatus` and `Plant.forceGrowth`. **Connascence.** The connascence we found between packages is of Name and Type. The one Connascence of Identity (`==`) stays inside one method.

**Testing.** Each tool, the slot and each Enchantment has its own test class, such as `HoeTest` and `EnchantmentSlotTest`. They cover each tool's spread, with and without an Enchantment. They also cover Enchantments with and without affected tiles. Bounty is tested on turns a plant is not due to grow, and on a paused plant it cannot grow. An Enchantment is spent after one use. `AbstractEnchantableToolTest` checks that changing the list given to a tool does not change its menu. `FixedRandom` makes them repeatable, and `mvn test` runs them all.
