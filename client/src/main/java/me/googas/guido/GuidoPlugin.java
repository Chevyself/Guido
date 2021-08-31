package me.googas.guido;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import lombok.NonNull;
import me.googas.commands.bukkit.CommandManager;
import me.googas.commands.bukkit.messages.BukkitMessagesProvider;
import me.googas.commands.bukkit.messages.MessagesProvider;
import me.googas.commands.bukkit.providers.registry.BukkitProvidersRegistry;
import me.googas.guido.commands.LinkCommands;
import me.googas.guido.compatibilities.InvitesCompatibility;
import me.googas.guido.config.GuidoConfig;
import me.googas.guido.receptors.LinkReceptors;
import me.googas.net.api.exception.MessengerListenFailException;
import me.googas.net.api.messages.Message;
import me.googas.net.api.messages.RequestBuilder;
import me.googas.net.sockets.json.adapters.MessageDeserializer;
import me.googas.net.sockets.json.client.JsonClient;
import me.googas.starbox.commands.providers.ChannelProvider;
import me.googas.starbox.compatibilities.Compatibility;
import me.googas.starbox.compatibilities.CompatibilityManager;
import me.googas.starbox.modules.ModuleRegistry;
import org.bukkit.plugin.java.JavaPlugin;

public class GuidoPlugin extends JavaPlugin implements Guido.Client {

  @NonNull private final GuidoConfig config = GuidoConfig.load(this);
  @NonNull private final MessagesProvider messages = new BukkitMessagesProvider();
  @NonNull private final ModuleRegistry registry = new ModuleRegistry(this);

  @NonNull
  private final CompatibilityManager compatibilities =
      new CompatibilityManager().add(new InvitesCompatibility());

  @NonNull
  private final CommandManager manager =
      new CommandManager(
          this,
          new BukkitProvidersRegistry(this.messages).addProviders(new ChannelProvider()),
          this.messages);

  private JsonClient client;

  @Override
  public void onEnable() {
    Guido.setClient(this);
    this.getSocket();
    this.manager.parseAndRegisterAll(new LinkCommands());
    this.compatibilities.check().getCompatibilities().stream()
        .filter(Compatibility::isEnabled)
        .forEach(
            compatibility -> {
              this.registry.engage(compatibility.getModules(this));
            });
    super.onEnable();
  }

  @Override
  public @NonNull Optional<JsonClient> getSocket() {
    if (this.client == null || this.client.isClosed()) {
      try {
        this.client =
            JsonClient.join(this.config.getHost(), this.config.getPort())
                .addReceptors(new LinkReceptors())
                .setGson(
                    new GsonBuilder()
                        .serializeNulls()
                        .registerTypeAdapter(Message.class, new MessageDeserializer()))
                .start();
        try {
          if (this.client
              .sendRequest(
                  new RequestBuilder<>(Boolean.class, "auth")
                      .put("token", this.config.getToken())
                      .build())
              .orElse(false)) {
            this.getLogger().info("Connected to Guido server");
          } else {
            this.getLogger().warning("Wrong auth token");
          }
        } catch (MessengerListenFailException e) {
          this.getLogger().log(Level.SEVERE, e, () -> "Could not authenticate");
        }
      } catch (IOException e) {
        this.getLogger().log(Level.SEVERE, e, () -> "Could not connect to server");
      }
    }
    return Optional.ofNullable(this.client);
  }
}
