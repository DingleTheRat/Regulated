![Title](https://cdn.modrinth.com/data/cached_images/b7d1328eef111ba622d5062c7fc18868dd5a8c99.png)

**Notice: This mod is designed for the Rat SMP (a friend group server), and the current set of commands is the one we currently use.**

## Summary
This mod allows server members to have access to a few commands (Not all have been added yet). With the /regulate command, you can enable/disable a command of your choice, enable/disable/edit one's special condition, and make all of the settings you put effect operators or not. If a special condition is not met or a command is disabled, the player will see an error message in the chat and the command won't be executed. By default, all commands are disabled.

## Command format
- /regulate - The command
- Command Name - The command whose permission you wanna modify (See below which commands are available to see which ones you can modify)
- Action - What do you wanna do with the command? You can change its enabled status (enabled), change whether it affects OPs or not (effects_ops), or modify its special condition (special_condition) (see more below).
### If action is "enabled", "effects_ops", or /teleport's "special_condition"
- Boolean - Enter true or false if you want it to be on or off.
### "special_condition" for /give
- Action - What do you wanna do to the whitelist? Do you wanna get its contents ("get"), add an item "add", or remove an item ("remove")?
- Item - What item do you wanna add/remove

## Special Conditions
A few commands have "special conditions" which restrict the use of a command if the condition is not met.
- /teleport or /tp: If enabled, players will only be able to teleport if they took damage **from a player** or died in the last **10 seconds**
- /give: Allows you to add, get or remove items to a whitelist. If the whitelist is empty (which it is by default), a player will be able to /give themselves any item they want

## Available Commands
These are the commands that you can modify with /regulate and that players can use:
- /give
- /teleport or /tp
- /tick
- /gamerule
