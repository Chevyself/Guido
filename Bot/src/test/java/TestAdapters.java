import com.starfishst.bot.adapters.ValuesMapAdapter;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.handlers.data.types.maps.GuidoValuesMap;
import com.starfishst.guido.api.data.ValuesMap;
import java.io.IOException;
import java.io.InputStreamReader;
import me.googas.commons.CoreFiles;
import me.googas.commons.gson.GsonProvider;

public class TestAdapters {

  public static void main(String[] args) throws IOException {
    GsonProvider.addAdapter(ValuesMap.class, new ValuesMapAdapter());
    GsonProvider.addAdapter(GuidoValuesMap.class, new ValuesMapAdapter());
    GsonProvider.refresh();

    InputStreamReader reader = new InputStreamReader(CoreFiles.getResource("test.json"));
    BotLinkedData data = GsonProvider.GSON.fromJson(reader, BotLinkedData.class);
    reader.close();
    System.out.println(data);
  }
}
