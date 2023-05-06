package net.zatrit.skins.lib.enumtypes;

import com.google.common.hash.HashFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.google.common.hash.Hashing.*;

@AllArgsConstructor
public enum HashFunc {
    CRC32(crc32()),

    @Deprecated
    SHA1(sha1()),

    MURMUR3(murmur3_128()),

    SHA384(sha384());

    private final @Getter HashFunction function;
}
