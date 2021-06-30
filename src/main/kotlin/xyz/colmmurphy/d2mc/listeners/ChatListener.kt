package xyz.colmmurphy.d2mc.listeners

import club.minnced.discord.webhook.send.WebhookMessageBuilder
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import xyz.colmmurphy.d2mc.D2MC
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatListener : Listener {
    @EventHandler
    fun onMessage(e: AsyncChatEvent) {
        val timeOfMessage = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        val builder = WebhookMessageBuilder()
            .setUsername(e.player.name)
            .setAvatarUrl(D2MC.playerAvatars[e.player.name])
            .setContent("**[$timeOfMessage]** ${e.originalMessage()}")
        D2MC.webhookClient.send(builder.build())
    }
}
