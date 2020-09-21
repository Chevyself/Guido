package com.starfishst.guido.api.implementations.messaging.json;

import com.starfishst.core.fallback.Fallback;
import com.starfishst.guido.api.implementations.messaging.AwaitingRequest;
import com.starfishst.guido.api.implementations.messaging.ResponseGiver;
import com.starfishst.guido.api.implementations.messaging.json.response.DisconnectResponse;
import com.starfishst.guido.api.implementations.messaging.json.response.MultiResponse;
import com.starfishst.guido.api.implementations.messaging.json.response.PingResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/**
 * A guido client thread is the {@link Thread} where a client connected to the {@link
 * JsonSocketServer}.
 */
public class JsonClientThread extends Thread implements JsonMessenger {

  /** The map to give responses to requests */
  @NotNull private final HashMap<String, ResponseGiver<?>> responseMap = new HashMap<>();

  /** The socket that is connected to the client */
  @NotNull private final Socket socket;

  /** The line that is being an input into the server */
  @NotNull private final BufferedReader input;

  /** The output used to send requests to the client */
  @NotNull private final PrintWriter output;

  /** The server to which this client is connected to */
  @NotNull private final JsonSocketServer server;
  /** The time to timeout requests */
  private final long timeout;
  /** The request that are waiting for a response */
  @NotNull private final HashMap<AwaitingRequest<?>, Long> requests = new HashMap<>();

  /**
   * Create the client thread
   *
   * @param socket the socket that connected to the server
   * @param server the server to which this client is connected to
   * @param timeout the time to timeout requests
   * @throws IOException in case the streams are already closed
   */
  public JsonClientThread(Socket socket, @NotNull JsonSocketServer server, long timeout)
      throws IOException {
    this.socket = socket;
    this.output = new PrintWriter(socket.getOutputStream(), true);
    this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.server = server;
    this.timeout = timeout;
    this.responseMap.put("ping", new PingResponse());
    this.responseMap.put("disconnect", new DisconnectResponse(this.server));
    this.responseMap.put("multi", new MultiResponse(this));
  }

  /**
   * Get the output line to send messages
   *
   * @return the output line to send messages
   */
  @Override
  public @NotNull PrintWriter getOutput() {
    return this.output;
  }

  /**
   * Get the input line to receive messages
   *
   * @return the input line
   */
  @Override
  public @NotNull BufferedReader getInput() {
    return this.input;
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

  /** Closes the messenger */
  @Override
  public void close() {
    this.responseMap.clear();
    this.requests.clear();
    this.output.close();
    try {
      this.socket.close();
    } catch (IOException e) {
      Fallback.addError("IOException: Socket could not be closed successfully");
      e.printStackTrace();
    }
    try {
      this.input.close();
    } catch (IOException e) {
      Fallback.addError("IOException: Socket input stream could not be closed successfully");
      e.printStackTrace();
    }
    this.server.remove(this);
  }

  @Override
  public void run() {
    JsonMessenger.super.run();
  }
}
