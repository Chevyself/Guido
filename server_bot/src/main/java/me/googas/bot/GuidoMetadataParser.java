package me.googas.bot;

import com.github.chevyself.starbox.metadata.CommandMetadata;
import com.github.chevyself.starbox.parsers.CommandMetadataParser;
import java.lang.reflect.AnnotatedElement;
import lombok.NonNull;
import me.googas.bot.core.commands.middleware.GuidoJdaPermission;

public class GuidoMetadataParser implements CommandMetadataParser {

  @NonNull public static final String PERMISSION_KEY = "permission";

  @Override
  public @NonNull CommandMetadata parse(@NonNull AnnotatedElement annotatedElement) {
    CommandMetadata metadata = new CommandMetadata();
    if (annotatedElement.isAnnotationPresent(GuidoJdaPermission.class)) {
      GuidoJdaPermission permission = annotatedElement.getAnnotation(GuidoJdaPermission.class);
      metadata.put(PERMISSION_KEY, permission.value());
    }
    return metadata;
  }

  @Override
  public void close() {}
}
