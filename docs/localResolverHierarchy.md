# The directory structure of the local skin storage looks like this:

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

*you shouldn't use metadata for the default skin model, it works without it anyway*

```json
{
  "model": "slim"
}
```

### Example animated cape metadata:

```json
{
  "animated": true
}
```