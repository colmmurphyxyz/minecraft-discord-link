package xyz.colmmurphy.d2mc

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.bukkit.plugin.java.JavaPlugin
import java.lang.NullPointerException

class D2MC : JavaPlugin() {

    companion object {
        lateinit var guildId: String
        lateinit var channelId: String

        lateinit var gld: Guild
        lateinit var chnl: TextChannel

        lateinit var jda: JDA
    }

    override fun onEnable() {
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

        gld = jda.getGuildById(guildId)!!
        chnl = gld.getTextChannelById(channelId)!!

        chnl.sendMessage("Server is online")
            .queue()
    }

    override fun onDisable() {
        println("[D2MC] Shutting down D2MC")
    }
}