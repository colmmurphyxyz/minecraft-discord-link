package xyz.colmmurphy.d2mc

import club.minnced.discord.webhook.WebhookClient
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.kyori.adventure.text.Component
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin
import java.lang.NullPointerException
import xyz.colmmurphy.d2mc.listeners.ChatListener
import xyz.colmmurphy.d2mc.listeners.PlayerJoinLeaveListener

class D2MC : JavaPlugin() {

    companion object {
        lateinit var server: org.bukkit.Server

        lateinit var guildId: String
        lateinit var channelId: String

        lateinit var gld: Guild
        lateinit var chnl: TextChannel

        lateinit var jda: JDA
        lateinit var webhook: WebhookClient
    }

    override fun onEnable() {
        D2MC.server = this.server
        server.pluginManager.registerEvents(ChatListener(), this)
        server.pluginManager.registerEvents(PlayerJoinLeaveListener(), this)
        // config stuff
        this.saveDefaultConfig()
        val config = this.config

        lateinit var token: String
        try {
            guildId = config.getString("guild-id")!!
            channelId = config.getString("channel-id")!!
            token = config.getString("bot-token")!!
        } catch (e: NullPointerException) {
            println("[D2MC] Your config.yml file isn't configured correctly")
            kotlin.system.exitProcess(1)
        }

        //login into Discord and send notice
        jda = JDABuilder.create(
            token,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_WEBHOOKS,
            GatewayIntent.GUILD_MEMBERS,
        )
            .setActivity(Activity.playing("mc.colmmurphy.xyz"))
            .disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS,
                CacheFlag.ONLINE_STATUS)
            .build()
            .awaitReady()
        println("[D2MC] successfully logged into discord as ${jda.selfUser.name}#${jda.selfUser.discriminator}")

        try {
            gld = jda.getGuildById(guildId)!!
            chnl = gld.getTextChannelById(channelId)!!
        } catch (e: NullPointerException) {
            println("[D2MC] Couldn't find a Discord server or channel with the ID's provided in config.yml\n" +
                "server ID: ${guildId}\nchannel ID: $channelId")
            kotlin.system.exitProcess(1)
        }

        // Create a webhook and send a message with it
        chnl.createWebhook("mc.colmmurphy.xyz").queue { createdWebhook ->
            webhook = WebhookClient.withUrl(createdWebhook.url)
            webhook.send("Server is online")
        }

        chnl.sendMessage("Server is online")
            .queue()
    }

    override fun onDisable() {
        println("[D2MC] Shutting down D2MC")
    }
}
