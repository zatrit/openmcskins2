package zatrit.skins.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.util.logging.LoggerPrintStream;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
public class ExceptionConsumerImpl implements ExceptionConsumer<Void> {
  private static final LoggerPrintStream printStream = new LoggerPrintStream("OMCS", System.out);
  private boolean verbose;

  @Override
  public Void apply(@NotNull Throwable error) {
    if (this.verbose) {
      error.printStackTrace(printStream);
    }

    return null;
  }
}
