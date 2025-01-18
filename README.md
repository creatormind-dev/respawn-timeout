# Respawn Timeout

Respawn Timeout is a Fabric mod that times out players when they die, preventing them from immediately respawning. Go outside and touch some grass in the meantime.

## Configuration

The mod is configurable via commands, since it operates on the server-side of the game.

### Get Timeout

> Returns the currently operating timeout setting.

**Usage:** `/respawntimeout get`

### Set Fixed Timeout

> Sets the specified amount of time as the timeout setting. Up to a maximum amount of 1 week for all time units.
> Smaller time units can be used for a more specific control of time.
> Using a fixed timeout overrides any previously configured random timeout.

**Usage:** `/respawntimeout set fixed <amount> <seconds|minutes|hours|days>`

### Set Random Timeout

> Sets a random timeout (in seconds) between a `minimum` and `maximum` value. Up to an overall maximum amount of 1 week.
> Each time a player dies a new, random timeout will be assigned.
> Using a random timeout overrides any previously configured fixed timeout.

**Usage:** `/respawntimeout set random <min> <max>`

### Disable/Clear Timeout

> Changes the timeout setting to 0, effectively disabling it.
> Disables both fixed and random configurations.

**Usage:** `/respawntimeout clear`

### Respawning

> Once a player's timeout finishes, they can respawn by executing this command.
> A moderator level or higher user can force respawn a player with this command, bypassing the player's timeout.

**Usage:** `/respawntimeout respawn [player]`

*Note: The mod will automatically respawn returning players if their timeout has finished.*

### Version Information

> Displays the currently running mod's version.

**Usage:** `/respawntimeout version`

## Disclaimer

Yes, this mod sounds like a terrible idea, but it's part of a mechanic employed in a server that I own.
Additional requested functionality may not be added if it strays away from the core idea of the mod.

If you have any issues with the mod, or a feature request, please use the [issue tracker](https://github.com/creatormind-dev/respawn-timeout/issues).
