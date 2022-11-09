package at.clanattack.central.whitelist.listener

import at.clanattack.bootstrap.ICore
import at.clanattack.central.whitelist.discord.WhitelistActionRow
import at.clanattack.central.whitelist.discord.WhitelistDiscordEmbed
import at.clanattack.central.whitelist.modle.WhitelistState
import at.clanattack.discord.IDiscordServiceProvider
import at.clanattack.message.IMessageServiceProvider
import at.clanattack.settings.ISettingServiceProvider
import at.clanattack.top.player.getPlayerData
import at.clanattack.top.player.setPlayerData
import at.clanattack.utility.listener.ListenerTrigger
import at.clanattack.xjkl.scope.empty
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerLoginEvent.Result

class PlayerLoginListener(private val core: ICore) {

    @ListenerTrigger(PlayerLoginEvent::class)
    fun playerJoin(event: PlayerLoginEvent) {
        val settingProvider = this.core.getServiceProvider(ISettingServiceProvider::class)

        if (!settingProvider.getSetting("central.whitelist.enabled", true, Boolean::class)) return
        val messageProvider = this.core.getServiceProvider(IMessageServiceProvider::class)

        when (val whitelistState = event.player.getPlayerData("central.whitelist.state", WhitelistState.NOTHING)) {
            WhitelistState.BLOCKED, WhitelistState.REQUESTED -> event.disallow(
                Result.KICK_WHITELIST,
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
                event.disallow(Result.KICK_WHITELIST, messageProvider.getMessage("central.whitelist.kick.requested"))
                event.player.setPlayerData("central.whitelist.state", WhitelistState.REQUESTED)
                (this.core.getServiceProvider(IDiscordServiceProvider::class).guild.getTextChannelById(
                    settingProvider.getSetting(
                        "central.whitelist.channel",
                        Long::class
                    ) ?: throw IllegalStateException("Discord Channel must be set")
                ) ?: throw IllegalStateException("Discord Channel must exists"))
                    .sendMessageEmbeds(WhitelistDiscordEmbed(core, event.player).embed)
                    .addActionRow(*WhitelistActionRow(this.core).actionRow)
                    .queue()
            }

            WhitelistState.ALLOWED -> empty()
        }
    }

}