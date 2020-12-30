package commands.user;

import java.awt.Color;
import java.io.File;
import java.util.Random;

import commands.CommandManager;
import configuration.constant.Command;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyUtils;

public class SendImage implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;
    private String path;

    public SendImage(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    public SendImage(GuildMessageReceivedEvent event, PGuild guild, String path) {
        this.event = event;
        this.guild = guild;
        this.path = path;
    }

    @Override
    public void execute() {
        File directory = new File(path);
        File[] files = directory.listFiles();
        File image = files[new Random().nextInt((files.length))];

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);
        /*
         * "attachment://" contains only the file name, not the path, that's why a split is done on "\".
         * Next, we need to split the entire path and search for a String who contains ".png", ".jpg", ".jpeg" or ".gif".
         * Finally, we have only the file name to concat with "attachment".
         * 
         * FOR WINDOWS
         * 
         * The double backward slash is treated as a single backward slash "\" in regular expression.
         * So four backward slash "\\\\" should be added to match a single backward slash in a String.
         * 
         * FOR UNIX
         * 
         * Just use "/".
         */
        for (String pathTmp : image.getPath().split("/")) {
            if (pathTmp.contains(".png") || pathTmp.contains(".jpg") || pathTmp.contains(".jpeg") || pathTmp.contains(".gif")) {
                embed.setImage("attachment://" + pathTmp);
                embed.setFooter(event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                event.getMessage().delete().queue();
                event.getChannel().sendFile(image).embed(embed.build()).queue();
            }
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.ISSOU.getName(),
                    "Generate a random issou image.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.ISSOU.getName() + "`.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Generate a random issou image. **Example:** `" + guild.getPrefix() + Command.ISSOU.getName() + "`.");
        }
    }

}