# 2D Top-Down Shooter (GTA1-Style)

A learning project to build a simple top-down shooter game, while learning OpenGL, LWJGL, and game architecture patterns.

## Goal

Create a functional 2D shooter where:
- Player character moves around the world
- Enemies spawn and chase/attack the player
- Player can shoot enemies
- Camera follows the player (GTA1 perspective)

This is primarily a **learning project** focused on understanding low-level graphics programming and game architecture, not creating a polished game.

## Tech Stack

- **Java** - Programming language
- **LWJGL 3** - Lightweight Java Game Library (OpenGL bindings)
- **JOML** - Java OpenGL Math Library
- **OpenGL 3.3 Core** - Graphics API

## Development Approach

### Learning with AI as Code Reviewer

I use an LLM (Claude) as a **strict mentor/code reviewer**, not as a code generator:

**What the AI does:**
- Reviews my code critically, pointing out bugs, design flaws, and bad practices
- Explains concepts and patterns (game loops, matrix transformations, architecture)
- Gives hints and asks questions to guide my learning
- Challenges my understanding by making me explain concepts back

**What the AI doesn't do:**
- Provide ready-to-use solutions (unless explicitly requested)
- Sugarcoat feedback or provide encouragement
- Write code for me to copy-paste

**Why this approach:**
I want to understand fundamentals deeply, not just get working code. By having the AI explain concepts and critique my implementations, I'm forced to think through problems myself and learn from mistakes.

## Current Progress

- [x] Basic OpenGL window and context setup
- [x] Shader loading and compilation
- [x] Mesh rendering with VBO/VAO/EBO
- [x] Basic game loop structure (input → update → render)
- [x] 2D camera with view matrix
- [x] Player movement with deltaTime
- [ ] Camera following player
- [ ] Texture loading and rendering
- [ ] Entity system (Player, Enemy, Bullet classes)
- [ ] Collision detection
- [ ] Enemy AI and spawning
- [ ] Shooting mechanics
- [ ] Basic UI (health, score)

## Architecture Goals

Learning to separate concerns:
- **Window** - GLFW context and window management
- **Renderer** - OpenGL calls, drawing primitives
- **Input** - Keyboard/mouse state management
- **Game** - Game logic, entity management, collision
- **Entity classes** - Player, Enemy, Bullet with their behavior

Avoiding god classes and keeping Single Responsibility Principle in mind.

## Building & Running

*(Add build instructions once project is more stable)*

## Learning Resources

- [LearnOpenGL](https://learnopengl.com/) - OpenGL concepts
- [LWJGL Documentation](https://www.lwjgl.org/)
- Direct experimentation and AI-guided code review

## Notes

This is a **work in progress** by someone learning graphics programming. Code quality varies as I refactor and learn better patterns. Expect rough edges, bugs, and architectural changes as understanding improves.
