package at.clanattack.central.whitelist

import at.clanattack.central.whitelist.modle.WhitelistState
import at.clanattack.player.IPlayerServiceProvider
import at.clanattack.top.bootstrap.getServiceProvider
import at.clanattack.top.discord.jda
import at.clanattack.top.message.getStringMessage
import at.clanattack.xjkl.scope.asExpr
import club.minnced.jda.reactor.on
import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import java.util.*

class WhitelistHandler {

    init {
        jda.on<ButtonInteractionEvent>()
            .filter { it.message.author.id == jda.selfUser.id }
            .filter { it.componentId.startsWith("ca:central:whitelist") }
            .map { it to it.componentId.endsWith(":allow") }
            .subscribe { (event, allow) ->
                event.deferReply(true).queue { reply ->
                    val uuid = UUID.fromString(
                        event.message.embeds
                            .firstOrNull()
                            ?.fields
                            ?.firstOrNull { it.name == "UUID" }
                            ?.value
                    )

                    val player = getServiceProvider<IPlayerServiceProvider>().getPlayer(uuid) ?: return@queue asExpr {
                        reply.editOriginal(getStringMessage("central.whitelist.interaction.player.unknown")).queue()
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
                        )
                        description = getStringMessage(
                            "central.whitelist.embed.description.${if (allow) "allowed" else "blocked"}",
                            "uuid=>${player.uuid}",
                            "name=>${player.name}",
                        )
                        field {
                            name = "UUID"
                            value = player.uuid.toString()
                        }
                    })
                        .setComponents()
                        .queue()

                    reply.editOriginal(getStringMessage("central.whitelist.interaction.success")).queue()
                }
            }
    }

}