package at.clanattack.central

import at.clanattack.bootstrap.plugin.Plugin
import at.clanattack.central.whitelist.WhitelistHandler

class Central : Plugin() {

    override fun enable() {
        WhitelistHandler()
    }

}