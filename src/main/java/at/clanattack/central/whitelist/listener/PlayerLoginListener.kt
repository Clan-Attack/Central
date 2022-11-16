package at.clanattack.central.whitelist.listener

import at.clanattack.central.whitelist.modle.WhitelistState
import at.clanattack.top.discord.guild
import at.clanattack.top.message.getMessage
import at.clanattack.top.message.getStringMessage
import at.clanattack.top.player.getPlayerData
import at.clanattack.top.player.setPlayerData
import at.clanattack.top.settings.getSetting
import at.clanattack.utility.listener.ListenerTrigger
import at.clanattack.xjkl.scope.empty
import dev.minn.jda.ktx.interactions.components.danger
import dev.minn.jda.ktx.interactions.components.row
import dev.minn.jda.ktx.interactions.components.success
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.MessageCreate
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerLoginEvent.Result
import java.util.*

class PlayerLoginListener() {

    @ListenerTrigger(PlayerLoginEvent::class)
    fun playerJoin(event: PlayerLoginEvent) {
        if (!getSetting("central.whitelist.enabled", true)) return

        when (val whitelistState = event.player.getPlayerData("central.whitelist.state", WhitelistState.NOTHING)) {
            WhitelistState.BLOCKED, WhitelistState.REQUESTED -> event.disallow(
                Result.KICK_WHITELIST,
                getMessage(
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
                event.disallow(Result.KICK_WHITELIST, getMessage("central.whitelist.kick.requested"))
                event.player.setPlayerData("central.whitelist.state", WhitelistState.REQUESTED)
                (guild.getTextChannelById(
                    getSetting<Long>("central.whitelist.channel")
                        ?: throw IllegalStateException("Discord Channel must be set")
                ) ?: throw IllegalStateException("Discord Channel must exists"))
                    .sendMessage(MessageCreate {
                        embeds += Embed {
                            color = 0xffff950
                            timestamp = Date().toInstant()
                            title = getStringMessage(
                                "central.whitelist.embed.title.waiting",
                                "name=>${event.player.name}",
                                "uuid=>${event.player.uniqueId}"
                            )
                            description = getStringMessage(
                                "central.whitelist.embed.description.waiting",
                                "name=>${event.player.name}",
                                "uuid=>${event.player.uniqueId}"
                            )
                            field {
                                name = "UUID"
                                value = event.player.uniqueId.toString()
                            }
                        }
                        components += row(
                            success("ca:central:whitelist:allow", getStringMessage("central.whitelist.action.allow")),
                            danger("ca:central:whitelist:block", getStringMessage("central.whitelist.action.block"))
                        )
                    })
                    .queue()
            }

            WhitelistState.ALLOWED -> empty()
        }
    }

}