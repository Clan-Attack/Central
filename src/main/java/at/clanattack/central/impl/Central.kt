package at.clanattack.central.impl

import at.clanattack.bootstrap.plugin.Plugin
import at.clanattack.central.impl.whitelist.WhitelistHandler
import at.clanattack.player.IPlayerServiceProvider
import at.clanattack.top.bootstrap.getServiceProvider

class Central : Plugin() {

    val playerServiceProvider: IPlayerServiceProvider
        get() = getServiceProvider()

    override fun enable() {

        WhitelistHandler()
    }

}