package com.starfishst.bot.server.providers;

import com.starfishst.bot.Guido;
import com.starfishst.bot.handlers.data.GuidoPermission;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.data.links.LinkedDataType;
import me.googas.messaging.Request;
import me.googas.messaging.json.Provider;
import me.googas.messaging.json.exception.JsonIllegalArgumentException;
import org.jetbrains.annotations.NotNull;

/** Providers for data types */
public class DataParameterProviders {

  /**
   * Provide linked data type in a request
   *
   * @param request the request needing the argument
   * @param param the parameter to get the needed argument
   * @return the wanted data type
   * @throws JsonIllegalArgumentException if the parameter cannot provide the data type
   */
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

  /**
   * Provide a bot user in a request
   *
   * @param request the request needing the argument
   * @param param the parameter to get the needed argument
   * @return the wanted bot user
   * @throws JsonIllegalArgumentException if the parameter cannot provide the bot user
   */
  @Provider(clazz = UserData.class)
  public UserData provideUser(@NotNull Request<?> request, @NotNull Object param)
      throws JsonIllegalArgumentException {
    if (param instanceof String) {
      UserData user = Guido.getDataLoader().getUserData((String) param);
      if (user != null) {
        return user;
      } else {
        throw new JsonIllegalArgumentException(param + " is not a valid user!");
      }
    } else {
      throw new JsonIllegalArgumentException(param.getClass() + " cannot be an user!");
    }
  }

  /**
   * Provide a permission in a request
   *
   * @param request the request needing the argument
   * @param param the parameter to get the needed argument
   * @return the wanted permission
   * @throws JsonIllegalArgumentException if the parameter cannot provide the permission
   */
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
