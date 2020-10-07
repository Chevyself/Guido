package com.starfishst.guido.api.implementations.messaging.json;

import com.starfishst.guido.api.implementations.messaging.AwaitingRequest;
import com.starfishst.guido.api.implementations.messaging.ResponseGiver;
import com.starfishst.guido.api.implementations.messaging.json.response.DisconnectedResponse;
import com.starfishst.guido.api.implementations.messaging.json.response.MultiResponse;
import com.starfishst.guido.api.implementations.messaging.json.response.PingResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import me.googas.commons.fallback.Fallback;
import org.jetbrains.annotations.NotNull;

/** This object represents a client that can be used to connect to the {@link JsonSocketServer} */
public class JsonClient extends Thread implements JsonMessenger {

  /** The map to give responses to requests */
  @NotNull private final HashMap<String, ResponseGiver<?>> responseMap = new HashMap<>();

  /** The socket that the client is using */
  @NotNull private final Socket socket;

  /** The output channel */
  @NotNull private final PrintWriter out;
  /** The input channel */
  @NotNull private final BufferedReader in;
  /** The time to timeout requests */
  private final long timeout;

  /** The request that are waiting for a response */
  @NotNull private final HashMap<AwaitingRequest<?>, Long> requests = new HashMap<>();

  /** Whether the messenger is closed */
  private boolean closed;
  /**
   * Create the guido client with a given socket
   *
   * @param socket the socket that the client must use
   * @param timeout the time to timeout requests
   * @throws IOException if the streams of the socket are closed
   */
  public JsonClient(@NotNull Socket socket, long timeout) throws IOException {
    this.socket = socket;
    this.timeout = timeout;
    this.out = new PrintWriter(this.socket.getOutputStream(), true);
    this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    this.responseMap.put("ping", new PingResponse());
    this.responseMap.put("multi", new MultiResponse(this));
    this.responseMap.put("disconnected", new DisconnectedResponse(this));
  }

  /** Closes the messenger */
  @Override
  public void close() {
    this.setClosed(true);
    this.responseMap.clear();
    this.out.close();
    try {
      this.socket.close();
    } catch (IOException e) {
      Fallback.addError("IOException: Socket could not be closed successfully");
      e.printStackTrace();
    }
    try {
      this.in.close();
    } catch (IOException e) {
      Fallback.addError("IOException: Socket input stream could not be closed successfully");
      e.printStackTrace();
    }
  }

  /**
   * Get the output line to send messages
   *
   * @return the output line to send messages
   */
  @Override
  public @NotNull PrintWriter getOutput() {
    return this.out;
  }

  /**
   * Get the input line to receive messages
   *
   * @return the input line
   */
  @Override
  public @NotNull BufferedReader getInput() {
    return this.in;
  }

  /**
   * Get the response givers for this messenger
   *
   * @return the response givers
   */
  @Override
  public @NotNull HashMap<String, ResponseGiver<?>> getResponseGivers() {
    return this.responseMap;
  }

  @NotNull
  @Override
  public HashMap<AwaitingRequest<?>, Long> getRequests() {
    return this.requests;
  }

  /**
   * Get when request may timeout
   *
   * @return the time to timeout in millis
   */
  @Override
  public long getTimeout() {
    return this.timeout;
  }

  @Override
  public void run() {
    JsonMessenger.super.run();
  }

  @Override
  public boolean isClosed() {
    return this.closed;
  }

  @Override
  public void setClosed(boolean bol) {
    this.closed = bol;
  }
}
