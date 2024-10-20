package com.amuzil.omegasource.magus.skill.test.avatar;

import com.amuzil.omegasource.magus.Magus;
import com.amuzil.omegasource.magus.input.InputModule;
import com.amuzil.omegasource.magus.input.KeyboardMouseInputModule;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;


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
                .executes(c -> {
                    Magus.sendDebugMsg("Possible modes: record, tree, reset");
                    return 1;
                })
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
        Magus.sendDebugMsg("Reset Forms RadixTree");
        return 1;
    }
}