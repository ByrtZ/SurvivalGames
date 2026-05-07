package dev.byrt.survivalgames

import com.github.benmanes.caffeine.cache.Caffeine

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.description.CommandDescription
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.kotlin.coroutines.annotations.installCoroutineSupport
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.incendo.cloud.processors.cache.CaffeineCache
import org.incendo.cloud.processors.confirmation.ConfirmationConfiguration
import org.incendo.cloud.processors.confirmation.ConfirmationManager
import org.incendo.cloud.processors.confirmation.annotation.ConfirmationBuilderModifier

import com.noxcrew.interfaces.InterfacesListeners
import dev.byrt.survivalgames.game.Game
import dev.byrt.survivalgames.resource.ResourcePackApplier
import dev.byrt.survivalgames.resource.ResourcePackLoader
import dev.byrt.survivalgames.resource.registry.GitHubReleasesRegistry
import dev.byrt.survivalgames.text.Formatting
import dev.byrt.survivalgames.text.SGTranslator
import dev.byrt.survivalgames.text.TextAlignment
import net.kyori.adventure.translation.GlobalTranslator
import org.bukkit.event.Listener
import org.reflections.Reflections

import java.time.Duration
import java.util.function.Consumer
import kotlin.io.path.createDirectories

class Main : JavaPlugin() {
    private lateinit var commandManager: LegacyPaperCommandManager<CommandSender>
    private lateinit var annotationParser: AnnotationParser<CommandSender>
    lateinit var resourcePackLoader: ResourcePackLoader
        private set

    lateinit var resourcePackApplier: ResourcePackApplier
        private set

    override fun onEnable() {
        logger.info("Starting Survival Games plugin.")
        GlobalTranslator.translator().addSource(SGTranslator())
        server.pluginManager.registerEvents(TextAlignment, this)
        resourcePackLoader = ResourcePackLoader(
            GitHubReleasesRegistry("ByrtZ/SurvivalGamesResourcePack"),
            dataPath.resolve("packs").createDirectories(),
            "master"
        )

        resourcePackApplier = ResourcePackApplier(resourcePackLoader)
        server.pluginManager.registerEvents(resourcePackApplier, this)

        Game.setup()
        setupCommands()
        setupEventListeners()
        InterfacesListeners.install(this)
    }

    override fun onDisable() {
        logger.info("Stopping Survival Games plugin.")
        Game.cleanup()
    }

    private fun setupCommands() {
        logger.info("Registering commands.")
        commandManager = LegacyPaperCommandManager.createNative(
            this,
            ExecutionCoordinator.simpleCoordinator()
        )
        annotationParser = AnnotationParser(commandManager, CommandSender::class.java)
            .installCoroutineSupport(onlyForSuspending = true)
        setupCommandConfirmation()
        annotationParser.parseContainers()
    }

    private fun setupCommandConfirmation() {
        logger.info("Setting up command confirmation.")
        ConfirmationBuilderModifier.install(annotationParser)

        val confirmationConfig = ConfirmationConfiguration.builder<CommandSender>()
            .cache(CaffeineCache.of(
                Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(30)).build()
            ))
            .noPendingCommandNotifier { sender ->
                sender.sendMessage(
                    Formatting.allTags.deserialize("<red>You do not have any pending commands.")
                ) }
            .confirmationRequiredNotifier { sender, ctx ->
                sender.sendMessage(
                    Formatting.allTags.deserialize("<red><b><unicodeprefix:warning></b> This action is potentially disruptive!<newline>Confirm command <green>'/${ctx.commandContext().rawInput().input()}' <red>by running <yellow>'/confirm' <red>to execute.")
                ) }
            .expiration(Duration.ofSeconds(30))
            .build()

        val confirmationManager = ConfirmationManager.confirmationManager(confirmationConfig)
        commandManager.registerCommandPostProcessor(confirmationManager.createPostprocessor())

        commandManager.command(
            commandManager.commandBuilder("confirm")
                .handler(confirmationManager.createExecutionHandler())
                .commandDescription(CommandDescription.commandDescription("Confirm a pending command."))
                .permission("sg.confirm")
                .build()
        )
    }

    private fun setupEventListeners() {
        logger.info("Registering events.")
        logger.info("Registering events.")
        val reflections = Reflections("dev.byrt.survivalgames.event")
        val listeners = reflections.getSubTypesOf(Listener::class.java)

        listeners.forEach(Consumer { listener : Class<out Listener> ->
                try {
                    val instance = listener.getConstructor().newInstance()
                    server.pluginManager.registerEvents(instance, this)
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        )
    }
}

val plugin = Bukkit.getPluginManager().getPlugin("SurvivalGames")!! as Main
val logger = plugin.logger
val messenger = Bukkit.getMessenger()
