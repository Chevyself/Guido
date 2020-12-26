import java.io.IOException;
import java.io.InputStreamReader;
import me.googas.api.ValuesMap;
import me.googas.bot.adapters.ValuesMapAdapter;
import me.googas.bot.api.types.links.BotLinkable;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.commons.CoreFiles;
import me.googas.commons.gson.GsonProvider;

public class TestAdapters {

  public static void main(String[] args) throws IOException {
    GsonProvider.addAdapter(ValuesMap.class, new ValuesMapAdapter());
    GsonProvider.addAdapter(GuidoValuesMap.class, new ValuesMapAdapter());
    GsonProvider.refresh();

    InputStreamReader reader = new InputStreamReader(CoreFiles.getResource("test.json"));
    BotLinkable data = GsonProvider.GSON.fromJson(reader, BotLinkable.class);
    reader.close();
    System.out.println(data);
  }
}
