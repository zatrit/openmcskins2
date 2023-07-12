package net.zatrit.skins.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
public class ExceptionConsumerImpl implements ExceptionConsumer<Void> {
    private boolean verbose;

    @Override
    public void accept(@NotNull Throwable error) {
        if (this.verbose) {
            error.printStackTrace();
        }
    }

    @Override
    public Void apply(Throwable error) {
        this.accept(error);
        return null;
    }
}
