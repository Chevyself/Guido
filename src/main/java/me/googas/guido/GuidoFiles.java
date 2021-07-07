package me.googas.guido;

import com.google.gson.GsonBuilder;
import java.net.URL;
import java.util.Objects;
import lombok.NonNull;
import me.googas.io.StarboxFile;
import me.googas.io.context.Json;

public class GuidoFiles {

  @NonNull public static final StarboxFile DIR = StarboxFile.DIR;

  @NonNull public static final StarboxFile CONFIG = new StarboxFile(GuidoFiles.DIR, "config.json");

  public static class Contexts {

    @NonNull
    public static final Json JSON = new Json(new GsonBuilder().setPrettyPrinting().create());
  }

  public static class Resources {

    @NonNull private static final ClassLoader LOADER = GuidoFiles.class.getClassLoader();

    @NonNull public static final URL CONFIG = Resources.getResource("config.json");

    @NonNull
    public static URL getResource(@NonNull String name) {
      Resources.LOADER.getResource(name);
      return Objects.requireNonNull(
          Resources.LOADER.getResource(name), "Could not find resource " + name);
    }
  }
}
