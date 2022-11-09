package at.clanattack.central.whitelist.discord

import at.clanattack.bootstrap.ICore
import at.clanattack.message.IMessageServiceProvider
import at.clanattack.utility.IUtilityServiceProvider
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import org.bukkit.entity.Player
import java.awt.Color

class WhitelistDiscordEmbed(core: ICore, player: Player) {

    private val messageProvider = core.getServiceProvider(IMessageServiceProvider::class)
    private val utilityProvider = core.getServiceProvider(IUtilityServiceProvider::class)

    val embed = EmbedBuilder()
        .setColor(Color.decode("#fff905"))
        .setTitle(
            messageProvider.getStringMessage(
                "central.whitelist.modal.title",
                "name=>${player.name}",
                "uuid=>${player.uniqueId}",
                "time=>${utilityProvider.formatDateUtil.formatTime(System.currentTimeMillis())}"
            )
        )
        .setDescription(
            messageProvider.getStringMessage(
                "central.whitelist.modal.description",
                "name=>${player.name}",
                "uuid=>${player.uniqueId}",
                "time=>${utilityProvider.formatDateUtil.formatTime(System.currentTimeMillis())}"
            )
        )
        .build()

}

class WhitelistActionRow(core: ICore) {

    private val messageProvider = core.getServiceProvider(IMessageServiceProvider::class)

    private val allow = Button.of(
        ButtonStyle.SUCCESS,
        "central.whitelist.allow",
        messageProvider.getStringMessage("central.whitelist.action.allow")
    )

    private val block = Button.of(
        ButtonStyle.SUCCESS,
        "central.whitelist.block",
        messageProvider.getStringMessage("central.whitelist.action.block")
    )

    val actionRow = arrayOf(allow, block)

}