package xyz.colmmurphy.d2mc.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import xyz.colmmurphy.d2mc.D2MC

class PlayerDeathListener : Listener {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        D2MC.webhookClient.send("${e.deathMessage}")
    }
}