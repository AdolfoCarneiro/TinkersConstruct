package slimeknights.tconstruct.library.utils;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * Helper for use with our extensions of resource location for some type safety in IDs.
 * Note we left {@link ResourceLocation#withPath(String)} and alike as returning {@link ResourceLocation} as there is not much use extending an ID.
 * @see IdParser
 */
public abstract class ResourceId extends ResourceLocation {
  public ResourceId(String namespace, String path) {
    super(namespace, path);
  }

  public ResourceId(ResourceLocation location) {
    this(location.getNamespace(), location.getPath());
  }

  public ResourceId(String location) {
    this(parseNamespace(location), parsePath(location));
  }

  private static String parseNamespace(String s) {
    int i = s.indexOf(':');
    return i > 0 ? s.substring(0, i) : DEFAULT_NAMESPACE;
  }

  private static String parsePath(String s) {
    int i = s.indexOf(':');
    return i >= 0 ? s.substring(i + 1) : s;
  }


  /* Helpers for static constructors */

  /**
   * Creates a new ID from the given string
   * @param string  String
   * @return  ID, or null if invalid
   */
  @Nullable
  protected static <T extends ResourceLocation> T tryParse(String string, BiFunction<String,String,T> constructor) {
    int i = string.indexOf(':');
    String namespace = i > 0 ? string.substring(0, i) : DEFAULT_NAMESPACE;
    String path = i >= 0 ? string.substring(i + 1) : string;
    return tryBuild(namespace, path, constructor);
  }

  /**
   * Creates a new ID from the given namespace and path
   * @param namespace  Namespace
   * @param path       Path
   * @return  ID, or null if invalid
   */
  @Nullable
  protected static <T extends ResourceLocation> T tryBuild(String namespace, String path, BiFunction<String,String,T> constructor) {
    if (ResourceLocation.tryBuild(namespace, path) != null) {
      return constructor.apply(namespace, path);
    }
    return null;
  }
}
