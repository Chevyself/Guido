package me.googas.bot.core.server.request;

import java.util.Map;
import java.util.UUID;
import lombok.NonNull;

/** An extension to request booleans */
public class BooleanGuidoRequest extends GuidoRequest<Boolean> {
  /**
   * Create the request
   *
   * @param id the unique id of the request
   * @param method the method which will be used to get the receptor to process the request
   * @param parameters the parameters that will use the receptor to process the request
   */
  public BooleanGuidoRequest(
      @NonNull UUID id, @NonNull String method, @NonNull Map<String, ?> parameters) {
    super(Boolean.class, id, method, parameters);
  }

  /**
   * Create the request. It generates a random id
   *
   * @param method the method which will be used to get the receptor to process the request
   * @param parameters the parameters that will use the receptor to process the request
   */
  public BooleanGuidoRequest(@NonNull String method, @NonNull Map<String, ?> parameters) {
    super(Boolean.class, method, parameters);
  }

  /**
   * Create the request. It generates a random id and has no parameters
   *
   * @param method the method which will be used to get the receptor to process the request
   */
  public BooleanGuidoRequest(@NonNull String method) {
    super(Boolean.class, method);
  }
}
