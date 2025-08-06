# Minecart Announcer 2.0

**Minecart Announcer** is a lightweight plugin designed for private Minecraft servers. It enables vanilla-friendly announcements for players traveling via **minecarts** or **boats**.

---

## Features

- Full color formatting support
- Player-specific **chat messages**
- Custom **bossbars**
- On-screen **titles & subtitles**
- **Directional triggering** (based on travel direction)
- Works with **minecarts** and **boats**

---

## Setup Instructions

### Minecarts

To set up a message trigger:

1. **Place a Rail on top of an Iron Block**  
   ![Minecart Setup](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/Setup.png)

2. Use a **Book and Quill** to write your message.

3. Once finished, **sign the book**, then **left-click** the Rail block (with Iron Block underneath) using the signed book.

If the setup is successful, you’ll see **green particles** and receive a private confirmation message in chat.

---

### Boats

1. **Place a Polished Blackstone Pressure Plate** on any type of Ice block.  
   ![Boat Setup](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/boatSetup.png)

2. Use the same method as with minecarts: write your message in a Book and Quill, sign it, then left-click the pressure plate with the signed book.

> _Note: Visible tripwires in the screenshot are from the **Vanilla Tweaks** resource pack and used for directional control._

---

## Chat Messages

By default, the content of the book will be sent to the **chat** of the player who triggered it.  
If you use special tags (see below), it will display as a bossbar or title instead.

---

## Color Formatting

You can use **Minecraft’s default color codes**, prefixed with a **`$`** (dollar sign).

Example: `$cWarning!` → displays as red text  
![Text Formatting](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/Minecraft_Formatting.webp)

---

## Bossbars

To display your message as a **bossbar**, begin the page with:

```
#DEF:BOSSBAR#
```

Optional bossbar parameters (must be placed **anywhere** on the same page):

- `#DEF:BB_COLOR-[RED|BLUE|PINK|GREEN|YELLOW|PURPLE|WHITE]#`  
  → Sets the bossbar color

- `#DEF:BB_DUR-[seconds]#`  
  → Sets the bossbar duration (in seconds, default: 10)

- `#GET:BB_TIME#`  
  → Replaced in the message with remaining bossbar time

---

## Titles & Subtitles

To show your message as an **on-screen title**, start with:

```
#DEF:TITLE#
```

Then, for a subtitle (optional), include:

```
#DEF:SUBTITLE#
```

Everything after that line will be shown as a subtitle below the title.

---

## Directional Triggering

### Minecarts

To make the message trigger **only when approaching from a specific direction**:

- Place a **Bone Block** beneath the previous Rail (from which the player will be coming).  
  Only players entering from that direction will see the message.

### Boats

- Place **String (Tripwire)** in the direction the player will approach from (including diagonals).  
  This ensures that players coming from the correct path trigger the pressure plate message.

---

## Example

**Setup:**  
![Example prepared message](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/example_message.png)

**In-game Result:**  
![Example player screen when running](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/example_playerscreen.png)

---

## Changelog

### 2.0
- Switched storage from JSON to **SQLite**  
  → You can edit it with [DB Browser for SQLite](https://sqlitebrowser.org/)
- Bugfixes: `#0003`

### 1.3
- **Boats support** added

### 1.2.3
- Bugfixes: `#0001`, `#0002`
