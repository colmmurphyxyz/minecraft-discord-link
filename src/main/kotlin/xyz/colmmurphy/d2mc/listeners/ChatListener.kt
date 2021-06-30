package xyz.colmmurphy.d2mc.listeners

import club.minnced.discord.webhook.send.WebhookMessageBuilder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import xyz.colmmurphy.d2mc.D2MC
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatListener : Listener {
    @EventHandler
    fun onMessage(e: AsyncPlayerChatEvent) {
        val timeOfMessage = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        val builder = WebhookMessageBuilder()
            .setUsername(e.player.name)
            .setAvatarUrl(D2MC.playerAvatars[e.player.name])
            .setContent("**[$timeOfMessage]** ${e.message}")
        D2MC.webhookClient.send(builder.build())
    }
}
