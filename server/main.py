import json
from flask import Flask, Response, request, send_file
import os
import argparse
import mmh3

parser = argparse.ArgumentParser()
parser.add_argument("--port", type=int, default=8000)
parser.add_argument("--assets-path", type=str, default=".")

args = parser.parse_args()

app = Flask(__name__)

cache = {}


def list_assets(type, name, ext):
    path = os.path.join(args.assets_path, type)
    if not os.path.exists(path):
        return

    types = os.listdir(path)

    for _type in types:
        data_path = os.path.join(path, _type, name) + "." + ext
        if os.path.exists(data_path):
            yield _type, data_path


def list_textures(name):
    result = {}
    for _type, data_path in list_assets("textures", name, "png"):
        _type = _type.upper()
        if _type not in result:
            result[_type] = {}
        base_url = "/".join(request.base_url.split("/")[:3])
        _hash = get_texture(data_path)
        result[_type]["url"] = f"{base_url}/assets/{_hash}"

    
    for _type, data_path in list_assets("metadata", name, "json"):
            _type = _type.upper()
            if _type not in result:
                result[_type] = {}
            with open(data_path) as file:
                result[_type]["metadata"] = json.load(file)

    return result


def get_texture(path) -> int:
    with open(path, "rb") as file:
        content = file.read()
    _hash = mmh3.hash_from_buffer(content, signed=False)
    cache[_hash] = path
    return _hash


@app.route("/textures/<name>")
def textures(name):
    response = {}

    response.update(list_textures(name))

    return response


@app.route("/assets/<int:_hash>")
def assets(_hash):
    if _hash not in cache:
        return Response(status=404)

    path = cache[_hash]

    if not os.path.exists(path):
        return Response(status=404)

    return send_file(path)


if __name__ == "__main__":
    app.run(port=args.port)
