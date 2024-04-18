# Commands

With the commands you can add a source of skins or change their order,
instead of changing the config file manually.

In OpenMCSkins, there`s a command ``/omcs`` (an alias for ``/openmcskins``) for this.

## Subcommands:

* ``/omcs list`` - lists all host list.
* ``/omcs add (preset) [pos]`` - adds a new host to config by cloning an existing [preset](presets.md). If the ``pos``
  argument is passed, inserts host at the given position, otherwise to the top of the list.
* ``/omcs remove (pos)`` - removes a host from the list of hosts at the specified position.
* ``/omcs move (from) (to)`` - changes the host position.
* ``/omcs refresh`` - forces OpenMCSkins to re-download all players' skins.
* ``/omcs clean`` - deletes all skin caches on disk.