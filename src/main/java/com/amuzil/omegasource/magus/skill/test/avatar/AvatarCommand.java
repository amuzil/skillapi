package com.amuzil.omegasource.magus.skill.test.avatar;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.InputModule;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.amuzil.omegasource.magus.network.packets.client_executed.FormActivatedPacket;
import com.amuzil.omegasource.magus.registry.Registries;
import com.amuzil.omegasource.magus.skill.forms.Form;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import static com.amuzil.omegasource.magus.Magus.MOD_ID;


public class AvatarCommand {
    // Class for registering the '/gesture' command
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("avatar")
                .then(Commands.literal("key")
                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                .executes(c -> key(IntegerArgumentType.getInteger(c, "value")))
                        )
                        .executes(c -> record())
                )
                .then(Commands.literal("tree")
                        .then(Commands.literal("reset")
                                .executes(c -> reset())
                        )
                        .executes(c -> tree())
                )
                .then(createActivateFormCommand())
                .executes(c -> {
                    InputModule.sendDebugMsg("Options: activate_form, tree, reset");
                    return 1;
                })
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createActivateFormCommand() {
        return Commands.literal("activate")
            .then(Commands.argument("form", StringArgumentType.string())
                .then(Commands.argument("target", EntityArgument.player())
                    .executes(c -> activateForm(
                            StringArgumentType.getString(c, "form"),
                            EntityArgument.getPlayer(c, "target")))
                )
            );
    }

    private static int key(int keyValue) {
        return 1;
    }

    private static int record() {
        return 1;
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
        FormActivatedPacket.handleServerSide(form, player);
        return 1;
    }
}