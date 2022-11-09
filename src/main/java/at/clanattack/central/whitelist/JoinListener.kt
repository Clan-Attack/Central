package at.clanattack.central.whitelist

import at.clanattack.bootstrap.ICore
import at.clanattack.discord.IDiscordServiceProvider
import at.clanattack.message.IMessageServiceProvider
import at.clanattack.settings.ISettingServiceProvider
import at.clanattack.top.player.getPlayerData
import at.clanattack.top.player.setPlayerData
import at.clanattack.utility.listener.ListenerTrigger
import at.clanattack.xjkl.scope.empty
import net.dv8tion.jda.api.EmbedBuilder
import org.bukkit.event.player.PlayerJoinEvent
import java.awt.Color
import java.lang.IllegalStateException

class JoinListener(private val core: ICore) {

    @ListenerTrigger(PlayerJoinEvent::class)
    fun playerJoin(event: PlayerJoinEvent) {
        val settingProvider = this.core.getServiceProvider(ISettingServiceProvider::class)

        if (!settingProvider.getSetting("central.whitelist.enabled", true, Boolean::class)) return
        val messageProvider = this.core.getServiceProvider(IMessageServiceProvider::class)

        val whitelistState = event.player.getPlayerData("central.whitelist.state", WhitelistState.NOTHING)
        when (whitelistState) {
            WhitelistState.BLOCKED, WhitelistState.REQUESTED -> event.player.kick(
                messageProvider.getMessage(
                    "central.whitelist.kick.${
                        when (whitelistState) {
                            WhitelistState.BLOCKED -> "blocked"
                            WhitelistState.REQUESTED -> "requested"
                            else -> "blocked"
                        }
                    }"
                )
            )

            WhitelistState.NOTHING -> {
                event.player.kick(messageProvider.getMessage("central.whitelist.kick.requested"))
                event.player.setPlayerData("central.whitelist.state", WhitelistState.REQUESTED)
                (this.core.getServiceProvider(IDiscordServiceProvider::class).guild.getTextChannelById(
                    settingProvider.getSetting(
                        "central.whitelist.channel",
                        Long::class
                    ) ?: throw IllegalStateException("Discord Channel must be set")
                ) ?: throw IllegalStateException("Discord Channel must exists"))
                    .sendMessageEmbeds(
                        EmbedBuilder()
                            .setColor(Color.decode("#fff905"))
                            .setTitle(
                                messageProvider.getStringMessage(
                                    "central.whitelist.modal.title",
                                    "name=>${event.player.name}",
                                    "uuid=>${event.player.uniqueId}"
                                )
                            )
                            .build()
                    )
                TODO("Send discord messsage")
            }

            WhitelistState.ALLOWED -> empty()
        }
    }

}