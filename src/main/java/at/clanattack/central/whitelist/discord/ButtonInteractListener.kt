package at.clanattack.central.whitelist.discord

import at.clanattack.bootstrap.ICore
import at.clanattack.central.whitelist.modle.WhitelistState
import at.clanattack.discord.DiscordListenerTrigger
import at.clanattack.message.IMessageServiceProvider
import at.clanattack.player.IPlayerServiceProvider
import at.clanattack.xjkl.scope.asExpr
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.bukkit.Bukkit
import java.util.UUID

class ButtonInteractListener(private val core: ICore) {

    @DiscordListenerTrigger(ButtonInteractionEvent::class)
    fun buttonClick(event: ButtonInteractionEvent) {
        if (event.interaction.componentId != "central.whitelist.allow" &&
            event.componentId != "central.whitelist.block"
        ) return

        val allowed = event.interaction.componentId == "central.whitelist.allow"

        event.deferReply(true).queue {
            val uuid =
                UUID.fromString(event.message.embeds.first().fields.first { field -> field.name == "UUID" }.value)

            val player =
                this.core.getServiceProvider(IPlayerServiceProvider::class).getPlayer(uuid) ?: return@queue asExpr {
                    event.reply(
                        this.core.getServiceProvider(IMessageServiceProvider::class)
                            .getStringMessage("central.whitelist.interaction.player.unknown")
                    ).queue()
                }

            player.setPlayerData(
                "central.whitelist.state",
                if (allowed) WhitelistState.ALLOWED else WhitelistState.BLOCKED
            )

            event.message.editMessageEmbeds(
                WhitelistDiscordEmbed(
                    this.core,
                    Bukkit.getOfflinePlayer(uuid),
                    if (allowed) EmbedType.ALLOWED else EmbedType.BLOCKED,
                    event.user
                ).embed
            )
                .setActionRow()
                .queue()

            it.editOriginal(
                this.core.getServiceProvider(IMessageServiceProvider::class)
                    .getStringMessage("central.whitelist.interaction.success")
            ).queue()
        }
    }

}