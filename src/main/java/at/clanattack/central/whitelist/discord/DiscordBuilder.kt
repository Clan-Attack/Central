package at.clanattack.central.whitelist.discord

import at.clanattack.bootstrap.ICore
import at.clanattack.message.IMessageServiceProvider
import at.clanattack.player.IPlayer
import at.clanattack.utility.IUtilityServiceProvider
import at.clanattack.xjkl.scope.fromT
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.awt.Color
import java.time.temporal.TemporalAccessor
import java.util.Date

enum class EmbedType(val message: String) {
    WAITING("waiting"),
    ALLOWED("allowed"),
    BLOCKED("blocked")
}

class WhitelistDiscordEmbed(core: ICore, player: OfflinePlayer, type: EmbedType, changedTime: Long, editedBy: User?) {

    constructor(core: ICore, player: OfflinePlayer, type: EmbedType, editedBy: User) : this(
        core,
        player,
        type,
        System.currentTimeMillis(),
        editedBy
    )

    constructor(core: ICore, player: OfflinePlayer, type: EmbedType) : this(
        core,
        player,
        type,
        System.currentTimeMillis(),
        null
    )

    private val messageProvider = core.getServiceProvider(IMessageServiceProvider::class)
    private val utilityProvider = core.getServiceProvider(IUtilityServiceProvider::class)

    val embed = EmbedBuilder()
        .setColor(when(type) {
            EmbedType.WAITING -> Color.decode("#fff905")
            EmbedType.ALLOWED -> Color.GREEN
            EmbedType.BLOCKED -> Color.RED
        })
        .setTitle(
            messageProvider.getStringMessage(
                "central.whitelist.embed.title.${type.message}",
                "name=>${player.name}",
                "uuid=>${player.uniqueId}",
                "time=>${utilityProvider.formatDateUtil.formatTime(changedTime)}",
                "editor=>${editedBy?.asMention ?: "Unknown"}"
            )
        )
        .setDescription(
            messageProvider.getStringMessage(
                "central.whitelist.embed.description.${type.message}",
                "name=>${player.name}",
                "uuid=>${player.uniqueId}",
                "time=>${utilityProvider.formatDateUtil.formatTime(changedTime)}",
                "editor=>${editedBy?.asMention ?: "Unknown"}"
            )
        )
        .addField("UUID", player.uniqueId.toString(), false)
        .setTimestamp(Date().toInstant())
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
        ButtonStyle.DANGER,
        "central.whitelist.block",
        messageProvider.getStringMessage("central.whitelist.action.block")
    )

    val actionRow = arrayOf(allow, block)

}