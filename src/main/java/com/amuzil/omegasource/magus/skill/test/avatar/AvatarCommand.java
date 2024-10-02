package com.amuzil.omegasource.magus.skill.test.avatar;

import com.amuzil.omegasource.magus.Magus;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;


public class AvatarCommand {
    // Class for registering the '/gesture' command
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("avatar")
                .then(Commands.literal("record")
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(c -> record(BoolArgumentType.getBool(c, "value")))
                        )
                        .executes(c -> record())
                )
                .then(Commands.literal("tree")
                        .executes(c -> tree())
                )
                .then(Commands.literal("save")
                        .executes(c -> save())
                )
                .executes(c -> {
                    Magus.sendDebugMsg("Possible modes: record, tree, save");
                    return 1;
                })
        );
    }

    private static int record(boolean mode) {
        return 1;
    }

    private static int record() {
        return 1;
    }

    private static int tree() {
        Magus.keyboardInputModule.getFormsTree().printAllBranches();
        return 1;
    }

    private static int save() {
        return 1;
    }
}