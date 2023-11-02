# Presets
In OpenMCSkins, a preset is a small [TOML file](https://toml.io/) containing a description of the host so that the mod can use it to add a host.

Example preset file:
```toml
type = 'GEYSER'

[properties]
floodgate_prefix = '.'
```

As you can see, there are two main fields here: ``type`` and ``properties``. ``properties`` contains additional information, such as the host API URL or the GeyserMC prefix as in the example.
Each host type has its own set of properties required for its operation.

## Here is a list of supported host types:
*(if the host does not have ``properties`` explicitly specified, then it does not have them)*

### ``GEYSER``
GeyserMC implementation [Global API](https://wiki.geysermc.org/geyser/global-api/).

Properties:
* ``floodgate_prefix`` (optional) - a prefix before the player's name, indicating that the player plays through GeyserMC.

### ``FALLBACK``
Uses vanilla skin implementation and allows NPC skins to work correctly, but only works on online-mode servers.

### ``FIVEZIG``
Implementation of the 5zig capes API.

### ``MOJANG``
Implementation of [Mojang skin system API](https://wiki.vg/Mojang_API).

### ``MINECRAFT_CAPES``
Implementation of the MinecraftCapes API.

### ``NAMED_HTTP``
Implementation of the API described [here](httpServer.md).

Properties:
* ``base_url`` - the base URL to which the player's name will be appended (must end with ``/``).

### ``OPTIFINE``
Implementation of [Optifine capes API](https://optifine.readthedocs.io/capes.html).

Properties:
* ``base_url`` - the base URL to which the player's name will be appended (must end with ``/``).

### ``VALHALLA``
Implementation of [Valhalla Skins API](https://skins.minelittlepony-mod.com/docs)

Properties:
* ``base_url`` - the base URL to which the player's name will be appended (must end with ``/``).

### ``DIRECT``
Loads the skin directly from the URL, where fields such as ``{id}``, ``{shortId}``, ``{name}`` and ``{type}`` are substituted.

Properties:
* ``types`` - types of skins for which the substitution will be performed. Example: ``['CAPE', 'SKIN', 'EARS']``.
* ``base_url`` - base URL where the substitution will take place. Example: ``https://dl.labymod.net/capes/{id}``

### ``LOCAL``
Searches for a skin in the computer's file system, starting from the given path.

Properties:
* ``directory`` - path to the directory with skins, must have the structure described [here](localResolverHierarchy.md)
