package slimeknights.tconstruct.shared.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;

/** Minimal compile-clean surface for the melting recipe generation command. */
public final class GenerateMeltingRecipesCommand {
  public static final ResourceLocation MELTING_CONFIGURATION = TConstruct.getResource("command/generate_melting_recipes.json");

  private GenerateMeltingRecipesCommand() {}

  public static void register(LiteralArgumentBuilder<CommandSourceStack> subCommand, CommandBuildContext context) {
    subCommand.executes(command -> {
      command.getSource().sendFailure(Component.literal("GenerateMeltingRecipesCommand is not yet ported to 1.21.1"));
      return 0;
    });
  }
}
