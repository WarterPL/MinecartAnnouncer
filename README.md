# Minecart Announcer (1.3)
This plugin made for private minecraft server with friends allows you to make Vanilla announcments for traveling players in minecarft

# Features
- Color support
- Chat messages
- Bossbars
- Titles
- Directional Travel Sending
- Support For Boats and Minecarts

# Setup
- **Minecarts** \
To create messages for travelers you need to place Iron Block under normal Rail block. \
![Minecart Setup](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/Setup.png) \
Next you will need Writable Book (Book and Quill) to write message in it. Once you are done sign book, and leftclick with it on rail with Iron Block underneath. If done correctly you should have seen green particles and private message on chat confirming assignment. 
- **Boats** (since 1.3) \
Place Polished Blackstone Pressure Plate on any Ice and proceed like with rails but click with Writen Book on Pressure plate \
![Boat Setup](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/boatSetup.png) \
*Tripwires are for directional setup, on screenshot are used Visible Tripwires resource pack from Vanilla Tweaks*

## Chat messages
Whatever you will write in book will be send on chat for player that run over defined rail, unless written otherwise

## Colors
You can use deafult Minecraft supported color by using color code forwarded with **$** sign. \
![Text Formating](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/Minecraft_Formatting.webp)

## Bossbars
You can create bossbar by defining it at the beggining of page with
**#DEF:BOSSBAR#** - this will make whatever you write appear as Bossbar text.
Bossbars have parameters that if used anywhere outside page declared at the beggining as a bossbar will be just send like normal text.

***BE AWARE THAT - square brackets are used as possible parameters***
- **#DEF:BB_COLOR-\['RED', 'BLUE', 'PINK', 'GREEN', 'YELLOW', 'PURPLE', 'WHITE'\]#** - will set color of bossbar
- **#DEF:BB_DUR-\[integer time in seconds, default 10s\]#** - will set visibility duration of bossbar
- **#GET:BB_TIME#** - will replace with remaining time of bossbar that is currently being shown

## Titles
You can create title by defining it at the beggining of page with
**#DEF:TITLE#** - this will make whatever you write next appear as title
On this page you can declare second section **#DEF:SUBTITLE#**, everything after that will be shown as a subtitle underneath title

## Directional Travel Sending
- **Minecarts** \
If you place bone block underneath rail from which incoming person will be traveling. Then only playes comming from that bone block onto your message rail will see message but not when going in opposite way
- **Boats** (since 1.3)  
Place string before pressure plate from direction player will be comming, if you are doing iceway diagonally - you will need to put two strings

# Example
![Example prepared message](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/example_message.png)

![Example player screen when running](https://github.com/WarterPL/MinecartAnnouncer/blob/main/ReadmeContent/example_playerscreen.png)

# Changelog
## 1.2.3
- Fixed Issues: 0001, 0002
## 1.3
- Added Boats support
