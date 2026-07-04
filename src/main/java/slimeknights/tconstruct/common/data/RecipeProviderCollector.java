package slimeknights.tconstruct.common.data;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Collects each module's individual recipe {@link DataProvider} instances and merges them into a single provider
 * before registration with {@link net.minecraft.data.DataGenerator}.
 * <p>
 * In 1.21.1, {@link net.minecraft.data.recipes.RecipeProvider#getName()} became {@code final} (it always returns
 * "Recipes"), whereas in 1.20.1 it was abstract and every module gave its recipe provider a unique name. Since
 * {@link net.minecraft.data.DataGenerator#addProvider(boolean, DataProvider)} dedupes providers by name, registering
 * more than one {@code RecipeProvider}-derived instance directly now throws {@code IllegalStateException: Duplicate
 * provider: Recipes}. Each TinkerXxx module still builds its own recipe provider instance for its own recipes
 * exactly as before, but hands it to this collector instead of registering it directly; a single combined provider
 * is then registered once (see {@link slimeknights.tconstruct.TConstruct}, at
 * {@link net.neoforged.bus.api.EventPriority#LOWEST} so every module has had a chance to contribute first).
 */
public final class RecipeProviderCollector {
  private RecipeProviderCollector() {}

  private static final List<DataProvider> PROVIDERS = new ArrayList<>();

  /** Adds a module's recipe provider to be merged into the combined provider */
  public static void add(DataProvider provider) {
    PROVIDERS.add(provider);
  }

  /** Builds a single combined provider running every collected provider so far, then clears the collector */
  public static DataProvider combineAndClear() {
    List<DataProvider> providers = List.copyOf(PROVIDERS);
    PROVIDERS.clear();
    return new DataProvider() {
      @Override
      public CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.allOf(providers.stream().map(provider -> provider.run(output)).toArray(CompletableFuture[]::new));
      }

      @Override
      public String getName() {
        return "Recipes";
      }
    };
  }
}
