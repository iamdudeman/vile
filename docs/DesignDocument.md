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

The first minutes of the game will expose the player to the basics of the gameplay of Vials. They will play a simple
AI opponent that will not be very difficult to defeat. After the first victory they will experience their first event
board where the game will start to be modified between each battle.

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

* Event tiles
    * Battle
        * Normal battle
        * Difficult battle
    * Library
        * Chance to gain additional knowledge usages
        * Chance to gain an additional life
        * Chance to gain additional max health
    * Modification
        * Give you or your opponent a head start in the next round
        * Possible upgrades to starting player health
        * Additional vials or vial depth
        * Chance to see more events each event round

### Story overview

The player character enters the underground Vials tournament to learn how to transmute lead into gold. Throughout the
gauntlet they will encounter other characters who for various reasons are challenging the gauntlet. They will also
learn other forbidden knowledge that will assist them in winning the tournament.

#### Character(s)

##### NPC opponents

* Ran Dum
    * Play-style - random
* Quack Dealer
    * Play-style - aggressive
    * Reason - To create the perfect donut
* Isaac Mewton
    * Play-style - random
* Osmeowdias
    * Play-style - aggressive

------------------------------------------------------------------------------------------------------------------------

## Art

### Style

The Vials game board itself will be pretty simplistic only showing the player and opponent portraits for images.
Everything else will be "GUI" elements. The event board will contain cards that are also very simplistic.

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
* Space or enter key to interact with a button
* Mouse click to interact as well

### UI

The UI will be fairly basic allowing the user to interact with buttons to control the flow of the game. During a battle
the UI will display the current health of each player as well as information about Knowledge they can use and what the
AI is currently doing.

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

Things towards the bottom will likely be implemented post game jam. Story progression especially will be a lot more work
than time allows.

| Task              | Type    | Status | Details                                         |
|-------------------|---------|--------|-------------------------------------------------|
| Design Document   | `Misc`  | `WIP`  | This document!                                  |
| Vials Gameplay    | `Code`  | `DONE` | Basic gameplay loop                             |
| AI                | `Code`  | `DONE` | Create some AI to play against                  |
| Events            | `Code`  | `DONE` | Implement events where upgrades can be received |
| Player portraits  | `Code`  | `DONE` | Implement portraits in UI                       |
| Main menu         | `Code`  | `DONE` | Implement the games main menu                   |
| Battle music      | `Audio` | `WIP`  | Music that plays during a battle                |
| Event board music | `Audio` | `WIP`  | Menu that plays on the world map after a battle |
| Player portrait   | `Art`   | `DONE` | The player portrait                             |
| AI portraits      | `Art`   | `DONE` | The opponent portraits                          |
| Vile main title   | `Art`   | `DONE` | The word "Vile" in main title                   |
| Balance           | `Code`  | `WIP`  | Balance the systems implemented above           |
| Story progression | `Code`  | `TODO` | Implement story progress between rounds         |
| Sound effects     | `Audio` | `TODO` | Add in sound effects where they make sense!     |
| Voice acting      | `Audio` | `TODO` | Voice act opponent lines                        |
