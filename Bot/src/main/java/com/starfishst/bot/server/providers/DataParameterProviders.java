package com.starfishst.bot.server.providers;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.handlers.data.GuidoPermission;
import com.starfishst.guido.api.data.links.LinkedDataType;
import me.googas.messaging.Request;
import me.googas.messaging.json.Provider;
import me.googas.messaging.json.exception.JsonIllegalArgumentException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/** Providers for data types */
public class DataParameterProviders {

  @Provider(clazz = LinkedDataType.class)
  public LinkedDataType provideLinkedDataType(@NotNull Request<?> request, @NotNull Object param)
      throws JsonIllegalArgumentException {
    if (param instanceof String) {
      try {
        return LinkedDataType.valueOf((String) param);
      } catch (IllegalArgumentException e) {
        throw new JsonIllegalArgumentException(e.getMessage());
      }
    } else {
      throw new JsonIllegalArgumentException(param.getClass() + " cannot be linked data type!");
    }
  }

  @Provider(clazz = BotUser.class)
  public BotUser provideUser(@NotNull Request<?> request, @NotNull Object param)
      throws JsonIllegalArgumentException {
    if (param instanceof String) {
      BotUser user = Guido.getDataLoader().getUserData((String) param);
      if (user != null) {
        return user;
      } else {
        throw new JsonIllegalArgumentException(param + " is not a valid user!");
      }
    } else {
      throw new JsonIllegalArgumentException(param.getClass() + " cannot be an user!");
    }
  }

  @Provider(clazz = GuidoPermission.class)
  public GuidoPermission providePermission(@NotNull Request<?> request, @NotNull Object param)
          throws JsonIllegalArgumentException {
    if (param instanceof String) {
      String that = (String) param;
      if (that.startsWith("-")) {
        return new GuidoPermission(that.substring(1), false);
      } else {
        return new GuidoPermission(that, true);
      }
    } else {
      throw new JsonIllegalArgumentException(param + " cannot be a permission");
    }
  }
}
