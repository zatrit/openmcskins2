# Commands

With the commands you can add a source of skins or change their order,
instead of changing the config file manually.

In OpenMCSkins, there`s a command ``/omcs`` (an alias for ``/openmcskins``) for this.

## Subcommands:

* ``/omcs list`` - lists all host list.
* ``/omcs add (preset) [id]`` - adds a new host to config by cloning an existing [preset](presets.md). If the ``id``
  argument is passed, adds the preset to the host list with given ID, otherwise to the top of the list.
* ``/omcs remove (id)`` - removes a host from the host list by ID.
* ``/omcs move (from) (to)`` - changes the host ID.
* ``/omcs refresh`` - forces OpenMCSkins to re-download all players' skins.
* ``/omcs clean`` - deletes all skin caches on disk.