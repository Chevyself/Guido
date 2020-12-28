package me.googas.bot.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.util.Map;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.bot.adapters.schemes.Scheme;
import me.googas.commons.gson.adapters.JsonAdapter;

public interface SchemeAdapter<T> extends JsonAdapter<T> {

  default @NonNull Class<? extends Scheme<T>> getScheme(@Nullable String version) {
    if (version == null || version.isEmpty()) {
      version = this.isEmptyAsLatest() ? this.getLatest() : "legacy";
    }
    for (String schemeVersion : this.getSchemes().keySet()) {
      if (schemeVersion.equalsIgnoreCase(version)) {
        return this.getSchemes().get(schemeVersion);
      }
    }
    return this.getScheme("legacy");
  }

  @Nullable
  default String getVersion(JsonObject json) {
    if (json == null || json.isJsonNull()) return null;
    JsonElement versionElem = json.get("version");
    if (versionElem != null && !versionElem.isJsonNull()) return versionElem.getAsString();
    return null;
  }

  @NonNull
  Map<String, Class<? extends Scheme<T>>> getSchemes();

  /**
   * Get the latest version as a string
   *
   * @return the latest version
   */
  @NonNull
  String getLatest();

  /**
   * Whether an object with no version is the latest
   *
   * @return whether to use the empty version as latest
   */
  boolean isEmptyAsLatest();

  @Override
  default T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (json.isJsonObject()) {
      Scheme<T> scheme =
          context.deserialize(json, this.getScheme(this.getVersion(json.getAsJsonObject())));
      return scheme.build();
    }
    return null;
  }

  @Override
  default JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src);
  }
}
