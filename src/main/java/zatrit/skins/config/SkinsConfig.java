package zatrit.skins.config;

import com.google.common.collect.Lists;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SkinsConfig {
  @SerialEntry boolean cacheTextures = true;
  @SerialEntry boolean verboseLogs = false;
  @SerialEntry boolean refreshOnConfigSave = true;
  @SerialEntry double loaderTimeout = 2;
  @SerialEntry UuidMode uuidMode = UuidMode.OFFLINE;

  @SerialEntry
  List<HostEntry> hosts =
      Lists.newArrayList(
          new HostEntry(HostEntry.HostType.MOJANG), new HostEntry(HostEntry.HostType.FALLBACK));

  @SerialEntry FilterMode filterMode = FilterMode.NONE;
  @SerialEntry Set<String> whitelist = new HashSet<>();
  @SerialEntry Set<String> blacklist = new HashSet<>();
}
