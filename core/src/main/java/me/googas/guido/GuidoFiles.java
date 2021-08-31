package me.googas.guido;

import com.google.gson.GsonBuilder;
import java.net.URL;
import java.util.Objects;
import lombok.NonNull;
import me.googas.io.StarboxFile;
import me.googas.io.context.Json;
import me.googas.io.context.PropertiesContext;

public class GuidoFiles {

  @NonNull public static final StarboxFile DIR = StarboxFile.DIR;

  @NonNull public static final StarboxFile CONFIG = new StarboxFile(GuidoFiles.DIR, "config.json");

  @NonNull
  public static final StarboxFile MAPS =
      new StarboxFile(StarboxFile.DIR, "src/main/resources/maps.json");

  public static class Contexts {

    @NonNull
    public static final Json JSON = new Json(new GsonBuilder().setPrettyPrinting().create());

    public static PropertiesContext PROPERTIES = new PropertiesContext();
  }

  public static class Resources {

    @NonNull private static final ClassLoader LOADER = GuidoFiles.class.getClassLoader();

    @NonNull
    public static URL getResource(@NonNull String name) {
      Resources.LOADER.getResource(name);
      return Objects.requireNonNull(
          Resources.LOADER.getResource(name), "Could not find resource " + name);
    }

    public static class Schemas {

      public static URL SQL = Resources.getResource("schemas/sql.properties");
    }
  }
}
