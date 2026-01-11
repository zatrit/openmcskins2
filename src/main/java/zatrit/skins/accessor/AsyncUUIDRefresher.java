package zatrit.skins.accessor;

import java.util.concurrent.CompletableFuture;
import zatrit.skins.lib.api.Profile;

public interface AsyncUUIDRefresher {
  /** Asynchronously refreshes UUID from Mojang API or other API. */
  CompletableFuture<Profile> skins$refreshUuid();
}
