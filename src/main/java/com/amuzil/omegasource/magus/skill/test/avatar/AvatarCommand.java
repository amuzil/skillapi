package com.amuzil.omegasource.magus.skill.test.avatar;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.InputModule;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.network.MagusNetwork;
import com.amuzil.omegasource.magus.network.packets.client_executed.FormActivatedPacket;
import com.amuzil.omegasource.magus.network.packets.server_executed.ElementActivatedPacket;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.elements.Element;
import com.amuzil.omegasource.magus.skill.elements.Elements;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.Arrays;

import static com.amuzil.omegasource.magus.Magus.MOD_ID;


public class AvatarCommand {
    private static LiteralArgumentBuilder<CommandSourceStack> builder =  Commands.literal("avatar");
    // Class for registering the '/avatar' command
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        builder.then(Commands.literal("tree")
            .then(Commands.literal("reset")
                    .executes(c -> reset()))
            .executes(c -> tree()))
        .executes(c -> {
            // Default message when no options are provided.
            InputModule.sendDebugMsg("Options: activate, form, tree");
            return 1;
        });

        createActivateArtCommand();
        createActivateFormCommand();
        dispatcher.register(builder);
    }

    private static void createActivateArtCommand() {
        Arrays.stream(Element.Art.values()).toList().forEach(elem ->
                builder.then(Commands.literal("activate")
                        .then(Commands.literal(elem.name().toLowerCase())
                                .executes(c -> activateElement(c, elem, null))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(c -> activateElement(c, elem, EntityArgument.getPlayer(c, "target")))
                                )
                        )
                )
        );
    }

    private static void createActivateFormCommand() {
        Registries.forms.forEach(form -> builder.then(Commands.literal("form")
            .then(Commands.literal(form.name().toLowerCase())
                .then(Commands.argument("target", EntityArgument.player())
                    .executes(c -> activateForm(
                            form.name().toLowerCase(),
                            EntityArgument.getPlayer(c, "target")))
                )
            )
        ));
    }

    private static int tree() {
        Magus.keyboardMouseInputModule.getFormsTree().printAllBranches();
        System.out.println("Current Active Forms: " + Magus.keyboardMouseInputModule.getActiveConditions());
        return 1;
    }

    private static int reset() {
        KeyboardMouseInputModule kim = (KeyboardMouseInputModule) Magus.keyboardMouseInputModule;
        kim.getFormsTree().resetTree();
        kim.getActiveConditions().clear();
        InputModule.resetFormsTree();
        AvatarFormRegistry.registerForms();
        kim.registerRunnables(Magus.keyboardMouseInputModule.getFormsTree());
        InputModule.sendDebugMsg("Reset Forms RadixTree");
        return 1;
    }

    private static int activateForm(String name, ServerPlayer player) {
        Form form =  Registries.FORMS.get().getValue(new ResourceLocation(MOD_ID, name));
        FormActivatedPacket.handleServerSide(form, InputModule.activeElement, 0, player);
        return 1;
    }

    private static int activateElement(CommandContext<CommandSourceStack> ctx, Element.Art art, ServerPlayer player) throws CommandSyntaxException {
//        InputModule.setDiscipline(Elements.fromArt(art))
        if (player == null)
            player = ctx.getSource().getPlayerOrException();
        ElementActivatedPacket packet = new ElementActivatedPacket(Elements.fromArt(art), player.getId());
        ServerPlayer finalPlayer = player;
        MagusNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> finalPlayer), packet);
        player.sendSystemMessage(Component.literal("Bending set to " + art));
        return 1;
    }
}