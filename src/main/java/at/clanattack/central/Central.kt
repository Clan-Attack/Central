package at.clanattack.central

import at.clanattack.bootstrap.plugin.Plugin
import at.clanattack.central.whitelist.WhitelistHandler
import at.clanattack.player.IPlayerServiceProvider
import at.clanattack.top.bootstrap.getServiceProvider

class Central : Plugin() {

    val playerServiceProvider: IPlayerServiceProvider
        get() = getServiceProvider()

    override fun enable() {

        WhitelistHandler()
    }

}