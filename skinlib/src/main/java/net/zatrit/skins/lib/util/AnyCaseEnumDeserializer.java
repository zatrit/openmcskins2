package net.zatrit.skins.lib.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.val;

import java.lang.reflect.Type;
import java.util.Arrays;

// https://stackoverflow.com/a/66760847

/**
 * Json adapter for deserializing enums ignoring case.
 */
public class AnyCaseEnumDeserializer<T extends Enum<?>> implements JsonDeserializer<T> {
    @Override
    public T deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }

        if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
            throw new JsonParseException(
                    "Expecting a String JsonPrimitive, getting " + json);
        }

        @SuppressWarnings("unchecked") val tClass = (Class<T>) typeOfT;
        val asString = json.getAsString().toLowerCase();
        val variant = Arrays.stream(tClass.getEnumConstants())
                              .filter(v -> v.toString().toLowerCase()
                                                   .equals(asString))
                              .findFirst();

        if (!variant.isPresent()) {
            throw new JsonParseException(
                    "No matching enum variant found for " + asString);
        }

        return variant.get();
    }
}
