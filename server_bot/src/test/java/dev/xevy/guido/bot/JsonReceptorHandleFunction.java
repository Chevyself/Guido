package dev.xevy.guido.bot;

import lombok.NonNull;
import me.googas.net.api.exception.MessengerListenFailException;

public interface JsonReceptorHandleFunction<O> {
  O apply(@NonNull JClientContext context) throws MessengerListenFailException;
}
