package com.starfishst.guido.api.implementations.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A request given to get a {@link Response} but it can also be a {@link Void} */
public class Request implements Message {

  /** The id of the request */
  @NotNull private final UUID id = UUID.randomUUID();
  /**
   * The method is used to identify how the type can be processed therefor give the resulting
   * response
   */
  @NotNull private final String method;

  /** The parameters of the request */
  @NotNull private final HashMap<String, Object> parameters;

  /**
   * Create the request
   *
   * @param method the method to identify the type that must return the request
   * @param parameters the parameters of the request
   */
  public Request(@NotNull String method, @NotNull HashMap<String, Object> parameters) {
    this.method = method;
    this.parameters = parameters;
  }

  /**
   * Create the request
   *
   * @param method the method to identify the type that must return the request
   */
  public Request(@NotNull String method) {
    this(method, new HashMap<>());
  }

  /**
   * Get a parameter from this request
   *
   * @param string the string that differentiates the parameter
   * @param clazz the class to cast the parameter
   * @param <T> the type of the class to cast the parameter
   * @return the parameter if it is found else null
   */
  @Nullable
  public <T> T getParameter(@NotNull String string, @NotNull Class<T> clazz) {
    Object parameter = this.parameters.get(string);
    return parameter == null ? null : clazz.cast(parameter);
  }

  /**
   * Get a parameter from this request or a default value
   *
   * @param string the string that differentiates the parameter
   * @param clazz the class to cast the parameter
   * @param def the default value oin case the parameter is null
   * @param <T> the type of the class to cast the parameter
   * @return the parameter or the default value if it is null
   */
  @NotNull
  public <T> T getParameterOr(@NotNull String string, @NotNull Class<T> clazz, @NotNull T def) {
    return clazz.cast(this.parameters.getOrDefault(string, def));
  }

  /**
   * The method is used to identify how the type can be processed therefor give the resulting
   * response
   *
   * @return the method that the request requires to be processed
   */
  @NotNull
  public String getMethod() {
    return this.method;
  }

  /**
   * Get the parameters of the request
   *
   * @return the parameters of the request
   */
  @NotNull
  public HashMap<String, Object> getParameters() {
    return parameters;
  }

  /**
   * Get a parameter that happens to be a list
   *
   * @param name the name of the parameter
   * @param <T> the type of the class
   * @return the list or empty if the setting does not exist
   */
  @SuppressWarnings("unchecked")
  @NotNull
  public <T> List<T> getListParameter(@NotNull String name) {
    List<T> list = new ArrayList<>();
    Class<List<T>> aClass = (Class<List<T>>) list.getClass();
    list.addAll(this.getParameterOr(name, aClass, new ArrayList<>()));
    return list;
  }

  @Override
  public @NotNull UUID getId() {
    return this.id;
  }
}
