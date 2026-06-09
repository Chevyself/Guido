package dev.xevy.guido.bot;

import java.util.concurrent.ExecutionException;
import lombok.NonNull;

public interface JsonReceptorHandleFunction<O> {
  O apply(@NonNull JClientContext context) throws ExecutionException, InterruptedException;
}
