<h1 align="center">
    <br />
    Respawn Timeout
    <br />
</h1>

<h4 align="center">
    A Fabric mod for Minecraft that times out players when they die,
    preventing them from immediately respawning.
</h4>

<p align="center">
    <br />
    <a href="https://modrinth.com/mod/respawn-timeout/versions"><img alt="Modrinth Version" src="https://img.shields.io/modrinth/v/respawn-timeout?style=for-the-badge&color=green"></a>
    <img alt="Modrinth Downloads" src="https://img.shields.io/modrinth/dt/respawn-timeout?style=for-the-badge&color=blue">
    <img alt="GitHub License" src="https://img.shields.io/github/license/creatormind-dev/respawn-timeout?style=for-the-badge">
    <a href="https://buymeacoffee.com/creatormind"><img alt="Donate" src="https://img.shields.io/badge/%24-donate-bb5794?style=for-the-badge"></a>
</p>

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
