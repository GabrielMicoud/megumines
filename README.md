# megumines
- A little minesweeper, just for fun.
- Just execute the Megumines.exe file to play.

*Well, this is my first video game, and it might have some bugs, also it will ask you to install Java SE 16.0.2*

## Offline game
There are 6 buttons:
- Reset button (looks like a smiley, also tells you if you won or lost the game), you can reset at any time.
- Online / offline button : if you click on it, there will be a window asking for an ip address, a port and a nickname for an online multiplayer game. More details in the *Online game* section.
- Quit button, if you want to ragequit. This button is prettier than the default exit button imo.
- 3 level buttons : EZ (easy), MID (medium) and HARD. The Easy level is a 10x10 grid with 15 mines. The Medium level is a 17x17 grid with 45 mines, and the Hard level is a 30x17 grid with 90 mines.

## Online game
When you're online, the game works differently.
First, when you click on *Play online*, you need to connect to a [Megumines server](https://github.com/GabrielMicoud/megumines-server). When a server is available, enter the ip address and a port (by default, the port is 2000) in the textboxes. Then enter your nickname. If it works, you will play a multiplayer game in Hard mode. 

- You can join only if the game hasn't started, when all the cases are hidden.
- You can play with a maximum of 5 players. (I was lazy enough to create only 5 different colors). Each player has a color and a score.
- Unlike the offline game, clicking on an empty case won't spread the click. You only play one case at a time, because having a huge amount of points just by clicking once in an empty area is unfair :)
- When you put a flag on a case, the other players won't see the flag, so you don't give hints to everyone.
- When you die, the mine won't be shown to other players, and you will see the entire grid
- You can reset only when everyone lost the game, or when the grid is cleared.
