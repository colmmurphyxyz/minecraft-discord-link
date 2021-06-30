package xyz.colmmurphy.d2mc.listeners

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import xyz.colmmurphy.d2mc.D2MC
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PlayerJoinLeaveListener : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        D2MC.addAvatar(e.player.name)
        if (e.player.name.equals("bstan", ignoreCase = true)) {
            D2MC.server.broadcast(Component.text("Bstan"))
        }
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        D2MC.webhookClient.send("**[$time]** ${e.player.name} joined the game")
    }

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent) {
        D2MC.removeAvatar(e.player.name)
        if (e.player.name.equals("bstan", ignoreCase = true)) {
            D2MC.server.broadcast(Component.text("Bstan"))
        }
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        D2MC.webhookClient.send("**[$time]** ${e.player.name} left the game")
    }
}