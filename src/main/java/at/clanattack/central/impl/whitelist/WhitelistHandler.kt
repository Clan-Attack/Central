package at.clanattack.central.impl.whitelist

import at.clanattack.bootstrap.call.Call
import at.clanattack.bootstrap.call.SystemState
import at.clanattack.central.impl.whitelist.modle.WhitelistState
import at.clanattack.discord.flux.deferReplyKeepEvent
import at.clanattack.top.discord.jda
import at.clanattack.top.message.getStringMessage
import at.clanattack.top.player.getPlayer
import at.clanattack.xjkl.scope.asExpr
import club.minnced.jda.reactor.on
import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import java.util.*

class WhitelistHandler {

    @Call(SystemState.LOADED)
    fun load() {
        jda.on<ButtonInteractionEvent>()
            .filter { it.message.author.id == jda.selfUser.id }
            .filter { it.componentId.startsWith("ca:central:whitelist") }
            .deferReplyKeepEvent()
            .subscribe { (event, hook) ->
                val allow = event.componentId.endsWith(":allow")

                val uuid = UUID.fromString(
                    event.message.embeds
                        .firstOrNull()
                        ?.fields
                        ?.firstOrNull { it.name == "UUID" }
                        ?.value
                )

                val player = getPlayer(uuid) ?: return@subscribe asExpr {
                    hook.editOriginal(getStringMessage("central.whitelist.interaction.player.unknown")).queue()
                }

                player.setPlayerData(
                    "central.whitelist.state",
                    if (allow) WhitelistState.ALLOWED else WhitelistState.BLOCKED
                )

                event.message.editMessageEmbeds(Embed {
                    color = if (allow) 0x008000 else 0x800000
                    timestamp = Date().toInstant()
                    title = getStringMessage(
                        "central.whitelist.embed.title.${if (allow) "allowed" else "blocked"}",
                        "uuid=>${player.uuid}",
                        "name=>${player.name}",
                        "editor=>${event.user.asMention}"
                    )
                    description = getStringMessage(
                        "central.whitelist.embed.description.${if (allow) "allowed" else "blocked"}",
                        "uuid=>${player.uuid}",
                        "name=>${player.name}",
                        "editor=>${event.user.asMention}"
                    )
                    field {
                        name = "UUID"
                        value = player.uuid.toString()
                    }
                })
                    .setComponents()
                    .queue()

                hook.editOriginal(getStringMessage("central.whitelist.interaction.success")).queue()
            }
    }

}