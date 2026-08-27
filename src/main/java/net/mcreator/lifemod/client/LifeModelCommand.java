package net.mcreator.lifemod.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = "life_mod",
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class LifeModModelCommand {

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {

        CommandDispatcher<CommandSourceStack> dispatcher =
                event.getDispatcher();

        dispatcher.register(
                Commands.literal("lifemodmodel")

                        /*
                         * ==================================================
                         * X
                         * ==================================================
                         */

                        .then(
                                Commands.literal("x")

                                        .then(
                                                Commands.literal("male")

                                                        .then(
                                                                Commands.argument(
                                                                        "value",
                                                                        DoubleArgumentType.doubleArg()
                                                                )

                                                                        .executes(context -> {

                                                                            double value =
                                                                                    DoubleArgumentType.getDouble(
                                                                                            context,
                                                                                            "value"
                                                                                    );

                                                                            PlayerModelHider.setMaleX(
                                                                                    value
                                                                            );

                                                                            context.getSource()
                                                                                    .sendSuccess(
                                                                                            () -> Component.literal(
                                                                                                    "Erkek model X = "
                                                                                                            + value
                                                                                            ),
                                                                                            false
                                                                                    );

                                                                            return 1;
                                                                        })
                                                        )
                                        )

                                        .then(
                                                Commands.literal("female")

                                                        .then(
                                                                Commands.argument(
                                                                        "value",
                                                                        DoubleArgumentType.doubleArg()
                                                                )

                                                                        .executes(context -> {

                                                                            double value =
                                                                                    DoubleArgumentType.getDouble(
                                                                                            context,
                                                                                            "value"
                                                                                    );

                                                                            PlayerModelHider.setFemaleX(
                                                                                    value
                                                                            );

                                                                            context.getSource()
                                                                                    .sendSuccess(
                                                                                            () -> Component.literal(
                                                                                                    "Kadın model X = "
                                                                                                            + value
                                                                                            ),
                                                                                            false
                                                                                    );

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )


                        /*
                         * ==================================================
                         * Y
                         * ==================================================
                         */

                        .then(
                                Commands.literal("y")

                                        .then(
                                                Commands.literal("male")

                                                        .then(
                                                                Commands.argument(
                                                                        "value",
                                                                        DoubleArgumentType.doubleArg()
                                                                )

                                                                        .executes(context -> {

                                                                            double value =
                                                                                    DoubleArgumentType.getDouble(
                                                                                            context,
                                                                                            "value"
                                                                                    );

                                                                            PlayerModelHider.setMaleY(
                                                                                    value
                                                                            );

                                                                            context.getSource()
                                                                                    .sendSuccess(
                                                                                            () -> Component.literal(
                                                                                                    "Erkek model Y = "
                                                                                                            + value
                                                                                            ),
                                                                                            false
                                                                                    );

                                                                            return 1;
                                                                        })
                                                        )
                                        )

                                        .then(
                                                Commands.literal("female")

                                                        .then(
                                                                Commands.argument(
                                                                        "value",
                                                                        DoubleArgumentType.doubleArg()
                                                                )

                                                                        .executes(context -> {

                                                                            double value =
                                                                                    DoubleArgumentType.getDouble(
                                                                                            context,
                                                                                            "value"
                                                                                    );

                                                                            PlayerModelHider.setFemaleY(
                                                                                    value
                                                                            );

                                                                            context.getSource()
                                                                                    .sendSuccess(
                                                                                            () -> Component.literal(
                                                                                                    "Kadın model Y = "
                                                                                                            + value
                                                                                            ),
                                                                                            false
                                                                                    );

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )


                        /*
                         * ==================================================
                         * Z
                         * ==================================================
                         */

                        .then(
                                Commands.literal("z")

                                        .then(
                                                Commands.literal("male")

                                                        .then(
                                                                Commands.argument(
                                                                        "value",
                                                                        DoubleArgumentType.doubleArg()
                                                                )

                                                                        .executes(context -> {

                                                                            double value =
                                                                                    DoubleArgumentType.getDouble(
                                                                                            context,
                                                                                            "value"
                                                                                    );

                                                                            PlayerModelHider.setMaleZ(
                                                                                    value
                                                                            );

                                                                            context.getSource()
                                                                                    .sendSuccess(
                                                                                            () -> Component.literal(
                                                                                                    "Erkek model Z = "
                                                                                                            + value
                                                                                            ),
                                                                                            false
                                                                                    );

                                                                            return 1;
                                                                        })
                                                        )
                                        )

                                        .then(
                                                Commands.literal("female")

                                                        .then(
                                                                Commands.argument(
                                                                        "value",
                                                                        DoubleArgumentType.doubleArg()
                                                                )

                                                                        .executes(context -> {

                                                                            double value =
                                                                                    DoubleArgumentType.getDouble(
                                                                                            context,
                                                                                            "value"
                                                                                    );

                                                                            PlayerModelHider.setFemaleZ(
                                                                                    value
                                                                            );

                                                                            context.getSource()
                                                                                    .sendSuccess(
                                                                                            () -> Component.literal(
                                                                                                    "Kadın model Z = "
                                                                                                            + value
                                                                                            ),
                                                                                            false
                                                                                    );

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )


                        /*
                         * ==================================================
                         * RESET
                         * ==================================================
                         */

                        .then(
                                Commands.literal("reset")

                                        .then(
                                                Commands.literal("male")

                                                        .executes(context -> {

                                                            PlayerModelHider
                                                                    .resetMalePosition();

                                                            context.getSource()
                                                                    .sendSuccess(
                                                                            () -> Component.literal(
                                                                                    "Erkek model pozisyonu sıfırlandı."
                                                                            ),
                                                                            false
                                                                    );

                                                            return 1;
                                                        })
                                        )

                                        .then(
                                                Commands.literal("female")

                                                        .executes(context -> {

                                                            PlayerModelHider
                                                                    .resetFemalePosition();

                                                            context.getSource()
                                                                    .sendSuccess(
                                                                            () -> Component.literal(
                                                                                    "Kadın model pozisyonu sıfırlandı."
                                                                            ),
                                                                            false
                                                                    );

                                                            return 1;
                                                        })
                                        )
                        )
        );
    }
}
