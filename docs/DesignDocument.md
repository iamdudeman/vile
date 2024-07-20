# Vile

------------------------------------------------------------------------------------------------------------------------

## Introduction

### "Elevator pitch" / Game summary

You have entered the underground gauntlet of Vials to learn as much forbidden knowledge as you can and ultimately learn
how to transmute lead to gold. A prize any true alchemist would be willing to risk their lives for.

### Inspiration

Knuckle Bones from Cult of the Lamb

### Platform

- [x] Browser
- [x] Windows
- [ ] Mac
- [ ] Linux
- [ ] Android

### Genre

Puzzle rogue-lite

### Player experience

#### Target audience

* Puzzle game enthusiasts
* Hobbyist game developers

#### First minutes

todo

------------------------------------------------------------------------------------------------------------------------

## Concept

### Gameplay overview

In Vials, each player will be forced to drink a full vial. You'll want to keep your vials as close to pH 7 (neutral) as
possible, or you won't last very long. The player who dies first loses!

Each turn a random number on a normal distribution between 0 and 14 and the player puts it in one of the not-full files.
Once a vile is full the player will take damage based on the difference between the sum of the contents of the vial and
7\. For example, a sum of 11 would equal 4 damage and a sum of 3 would also equal 4 damage.

Once a player's health reached zero the round is over!

Between battles the player will be given some options to choose from to gain various buffs to help them through
progressively more difficult challenges.

#### Mechanics / Systems overview

* Forbidden knowledge
    * Chemical tolerance - more "health"
    * Neutralizing Concoction - reverses last played pH
        * Can only be played if you haven't rolled yet
    * Reroll - gives a new pH to play
    * Extra life - gives another retry attempt
    * Vaccuum? - remove top pH
        * might be too strong so might not implement

* Event tiles
    * Regular fight
    * Boss fight
    * Library (could have a rare variant)
        * Chance to gain additional knowledge
    * Modification
        * Give you or your opponent a head start in the next round

### Story overview

The player character enters the underground Vials tournament to learn how to transmute lead into gold. Throughout the
gauntlet they will encounter other characters who for various reasons are challenging the gauntlet. They will also
learn other forbidden knowledge that will assist them in winning the tournament.

#### Character(s)

##### NPC opponents

* Ran Dumb
    * Play-style - completely random
    * Knowledge - none
* todo name
    * Play-style - only plays on own board unless it is full
    * Knowledge - todo
* todo name
    * Play-style - only plays on opponent's board unless it is full
    * Knowledge - todo

------------------------------------------------------------------------------------------------------------------------

## Art

### Style

todo

### Camera

There will not be a camera implemented in this game. The player will always be able to see the full Vials game being
played or the full world map post game of Vials.

------------------------------------------------------------------------------------------------------------------------

## Audio

### Music

todo

### Sound effects

todo

------------------------------------------------------------------------------------------------------------------------

## Interface

### Controls

* Arrow keys to move between UI elements
* Space or enter key to interact
* Mouse click to interact

### UI

todo

------------------------------------------------------------------------------------------------------------------------

## Proof of concept outline

### Legend

* Type
    * `Art`   : Art related assets
    * `Audio` : Audio related assets
    * `Code`  : All programming tasks
    * `Misc`  : Other tasks like documentation
* Status
    * `TODO`  : This task has not been started yet
    * `WIP`   : This task is currently in progress
    * `DONE`  : This task has been completed

### Outline

| Task              | Type    | Status | Details                                             |
|-------------------|---------|--------|-----------------------------------------------------|
| Design Document   | `Misc`  | `WIP`  | This document!                                      |
| Vials Gameplay    | `Code`  | `DONE` | Basic gameplay loop                                 |
| AI                | `Code`  | `DONE` | Create some AI to play against                      |
| Events            | `Code`  | `TODO` | Implement events where upgrades can be received     |
| Story progression | `Code`  | `TODO` | Implement story progress between rounds             |
| Battle music      | `Audio` | `TODO` | Music that plays during a battle                    |
| Event board music | `Audio` | `TODO` | Menu that plays on the world map after a battle     |
| Sound effects     | `Audio` | `TODO` | Add in sound effects where they make sense!         |
| Menu music        | `Audio` | `TODO` | Music that plays at the main menu                   |
| Event tiles       | `Art`   | `TODO` | The world map tiles                                 |
| Icons             | `Art`   | `TODO` | The icons for the various buffs used during battles |
| Numbers           | `Art`   | `TODO` | The sprites for the numbers (0-14)                  |
| Vials             | `Art`   | `TODO` | The sprites for the vials that contain numbers      |
