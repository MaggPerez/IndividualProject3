# PuzzleBot

- An android puzzle game where the user controls a robot to achieve its goals, collecting necessary keys and avoiding traps.
<img width="3840" height="3072" alt="PuzzleBot Github Image" src="https://github.com/user-attachments/assets/0e959b90-aba4-4421-96fa-db1b49e38c05" />


## Overview

PuzzleBot is an educational game where players guide a robot through grid based puzzles by creating command sequences. The app features a parent child account system allowing parents to track their children's progress.

## Features

- **Multiple Difficulty Levels**: Progressive puzzle complexity
- **Command-Based Gameplay**: Drag and drop directional commands to guide the robot
- **Objectives**: Collect keys and reach goals while avoiding traps
- **User Accounts**: Separate parent and kid accounts with invite code system
- **Progress Tracking**: Parents can monitor children's performance and game sessions
- **Local Database**: Room database for storing user data and game sessions

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM with ViewModels and Repositories
- **Database**: Room
- **Navigation**: Jetpack Navigation Compose


## How The Game is Played

1. Register as a parent or kid user
2. Select a difficulty level
3. Drag directional commands to build a sequence
4. Execute the sequence to guide the robot
5. Collect all keys and reach the goal to complete the level
