package xyz.colmmurphy.d2mc.listeners

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.ChatColor
import xyz.colmmurphy.d2mc.D2MC

class GuildMessageListener : ListenerAdapter() {
    override fun onGuildMessageReceived(e: GuildMessageReceivedEvent) {
        if (e.message.channel.id != D2MC.channelId || e.message.author.isBot || e.isWebhookMessage) return
        val nameColor = ChatColor.of(e.member!!.color) ?: ChatColor.AQUA
        val name = e.member!!.nickname ?: e.author.name
        D2MC.server.broadcast(Component.text(
            "${ChatColor.AQUA}[Discord]" +
                    "${ChatColor.RESET}${nameColor}<${name}> " +
                    "${ChatColor.RESET}${e.message.contentStripped}")
        )
    }
}