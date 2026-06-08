package dev.xevy.guido.mongo.util;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;

public final class Options {
  public static final FindOneAndUpdateOptions RETURN_AFTER =
      new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
}
