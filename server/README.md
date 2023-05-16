# HTTP server

This is a Flask read-only HTTP server for OpenMCSkins. It has an API similar
to [ely.by API](https://docs.ely.by/en/skins-system.html). You can use the server directory as local storage instead of
running it because it has the same filesystem structure as the local skins store.

### The directory structure of the server (and local storage) looks like this:

* textures
    * skin
        * [PLAYER NAME].png
    * cape
        * [PLAYER NAME].png
    * ears
        * [PLAYER NAME].png
* metadata (OPTIONAL)
    * skin
        * [PLAYER NAME].json
    * cape
        * [PLAYER NAME].json

Skin metadata is a JSON file, that contains skin model name

### Example skin metadata for a skin with a slim model:

```json5
{
  "model": "slim" // Use slim model for skin
}
```

### Example animated cape metadata

```json5
{
  "animated": true // Mark player cape as animated
}
```
