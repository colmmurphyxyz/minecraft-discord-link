package xyz.colmmurphy.d2mc.listeners

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.kyori.adventure.text.Component
import xyz.colmmurphy.d2mc.D2MC

class GuildMessageListener : ListenerAdapter() {
    override fun onGuildMessageReceived(e: GuildMessageReceivedEvent) {
        D2MC.server.broadcast(Component.text(
            "[Discord] <${e.message.author.name}> ${e.message.contentStripped}")
        )
    }
}