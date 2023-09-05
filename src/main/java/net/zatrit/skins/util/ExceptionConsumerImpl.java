package net.zatrit.skins.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ExceptionConsumerImpl implements ExceptionConsumer<Void> {
    private boolean verbose;

    @Override
    public Void apply(Throwable error) {
        if (this.verbose) {
            error.printStackTrace();
        }

        return null;
    }
}
