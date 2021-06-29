package xyz.colmmurphy.d2mc.listeners

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import xyz.colmmurphy.d2mc.D2MC

class PlayerJoinLeaveListener : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if (e.player.name.equals("bstan", ignoreCase = true)) {
            broadcast("Bstan")
        }
    }

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent) {
        if (e.player.name.equals("bstan", ignoreCase = true)) {
            broadcast("Bstan")
        }
    }

    private fun broadcast(s: String) = D2MC.server.broadcast(Component.text(s))
}