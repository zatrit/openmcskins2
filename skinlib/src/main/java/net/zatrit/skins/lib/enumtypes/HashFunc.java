package net.zatrit.skins.lib.enumtypes;

import static com.google.common.hash.Hashing.crc32;
import static com.google.common.hash.Hashing.murmur3_128;
import static com.google.common.hash.Hashing.sha1;
import static com.google.common.hash.Hashing.sha384;
import com.google.common.hash.HashFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum HashFunc {
    CRC32(crc32()),

    @Deprecated
    SHA1(sha1()),

    MURMUR3(murmur3_128()),

    SHA384(sha384());

    private final @Getter HashFunction function;
}
