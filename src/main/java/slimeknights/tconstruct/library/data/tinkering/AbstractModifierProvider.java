package slimeknights.tconstruct.library.data.tinkering;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.RegistryOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.json.JsonRedirect;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.impl.ComposableModifier;
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** Datagen for dynamic modifiers */
@SuppressWarnings("SameParameterValue")
public abstract class AbstractModifierProvider extends GenericDataProvider {
  private final Map<ModifierId,Composable> composableModifiers = new HashMap<>();
  private final CompletableFuture<HolderLookup.Provider> registriesFuture;
  /** Resolved registry access, only valid while {@link #addModifiers()} is running as part of {@link #run(CachedOutput)}. */
  protected HolderLookup.Provider registries;

  public AbstractModifierProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
    super(packOutput, Target.DATA_PACK, ModifierManager.FOLDER, ModifierManager.GSON);
    this.registriesFuture = registries;
  }

  /**
   * Function to add all relevant modifiers
   */
  protected abstract void addModifiers();

  /** Adds the given builder, handling duplicate modifiers */
  private void addBuilder(ModifierId id, @Nullable ComposableModifier.Builder builder, @Nullable ICondition condition, JsonRedirect... redirects) {
    Composable previous = composableModifiers.putIfAbsent(id, new Composable(builder, condition, redirects));
    if (previous != null) {
      throw new IllegalArgumentException("Duplicate modifier " + id);
    }
  }

  /* Composable helpers */

  /** Sets up a builder for a composable modifier */
  protected ComposableModifier.Builder buildModifier(ModifierId id, @Nullable ICondition condition, JsonRedirect... redirects) {
    ComposableModifier.Builder builder = ComposableModifier.builder();
    addBuilder(id, builder, condition, redirects);
    return builder;
  }

  /** Sets up a builder for a composable modifier */
  protected ComposableModifier.Builder buildModifier(ModifierId id, JsonRedirect... redirects) {
    return buildModifier(id, null, redirects);
  }

  /** Sets up a builder for a composable modifier */
  protected ComposableModifier.Builder buildModifier(DynamicModifier modifier, @Nullable ICondition condition, JsonRedirect... redirects) {
    return buildModifier(modifier.getId(), condition, redirects);
  }

  /** Sets up a builder for a composable modifier */
  protected ComposableModifier.Builder buildModifier(DynamicModifier modifier, JsonRedirect... redirects) {
    return buildModifier(modifier, null, redirects);
  }


  /* Redirect helpers */

  /** Adds a redirect with no modifier modules */
  protected void addRedirect(ModifierId id, @Nullable ICondition condition, JsonRedirect... redirects) {
    addBuilder(id, null, condition, redirects);
  }

  /** Adds a modifier redirect */
  protected void addRedirect(ModifierId id, JsonRedirect... redirects) {
    addRedirect(id, null, redirects);
  }

  /** Makes a conditional redirect to the given ID */
  protected JsonRedirect conditionalRedirect(ModifierId id, @Nullable ICondition condition) {
    return new JsonRedirect(id, condition);
  }

  /** Makes an unconditional redirect to the given ID */
  protected JsonRedirect redirect(ModifierId id) {
    return conditionalRedirect(id, null);
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    return registriesFuture.thenCompose(provider -> {
      this.registries = provider;
      addModifiers();
      return allOf(composableModifiers.entrySet().stream().map(entry -> saveJson(cache, entry.getKey(), entry.getValue().serialize(provider))));
    });
  }

  /** Result for composable too */
  private record Composable(@Nullable ComposableModifier.Builder builder, @Nullable ICondition condition, JsonRedirect[] redirects) {
    /** Writes this result to JSON */
    public JsonObject serialize(HolderLookup.Provider registries) {
      JsonObject json;
      if (builder != null) {
        json = ComposableModifier.LOADER.serialize(builder.build()).getAsJsonObject();
      } else {
        json = new JsonObject();
      }
      if (redirects.length != 0) {
        JsonArray array = new JsonArray();
        for (JsonRedirect redirect : redirects) {
          array.add(redirect.toJson());
        }
        json.add("redirects", array);
      }
      if (condition != null) {
        // FIXME F3: ICondition.CODEC is a registry-dispatch codec (neoforge:condition_codecs); it needs RegistryOps context to resolve
        // the codec holder, plain JsonOps.INSTANCE throws "Unregistered holder". Mirrors NeoForge's own ICondition.writeConditions(...).
        json.add("condition", ICondition.CODEC.encodeStart(RegistryOps.create(JsonOps.INSTANCE, registries), condition).getOrThrow());
      }
      return json;
    }
  }
}
