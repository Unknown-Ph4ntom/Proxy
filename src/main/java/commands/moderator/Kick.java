package commands.moderator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constant.Command;
import configuration.constant.Permissions;
import dao.database.Dao;
import dao.pojo.PGuildMember;
import dao.pojo.PGuild;
import factory.DaoFactory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyString;
import proxy.utility.ProxyUtils;

public class Kick implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;
    private PGuildMember author;

    public Kick(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    public Kick(GuildMessageReceivedEvent event, PGuild guild, PGuildMember author) {
        this.event = event;
        this.guild = guild;
        this.author = author;
    }

    @Override
    public void execute() {
        try {
            event.getGuild().retrieveMemberById(ProxyString.getMentionnedEntity(MentionType.USER, event.getMessage(), ProxyUtils.getArgs(event.getMessage())[1]), false).queue(gMember -> {
                try {
                    if (gMember.getUser().isBot()) {
                        kick(gMember);
                    } else {
                        Dao<PGuildMember> gMemberDao = DaoFactory.getGuildMemberDAO();
                        PGuildMember gMemberPojo = gMemberDao.find(event.getGuild().getId() + "#" + gMember.getId());

                        if (gMemberPojo.getId().equals(event.getAuthor().getId())) {
                            ProxyUtils.sendMessage(event.getChannel(), "Impossible to kick yourself.");

                        } else if (((author.getPermId() == Permissions.MODERATOR.getLevel()) && (gMemberPojo.getPermId() == Permissions.MODERATOR.getLevel()))
                                || ((author.getPermId() == Permissions.ADMINISTRATOR.getLevel()) && gMemberPojo.getPermId() == Permissions.ADMINISTRATOR.getLevel())) {
                            ProxyUtils.sendMessage(event.getChannel(), "Impossible to kick a member with the same or a higher permission than yours.");

                        } else if (author.getPermId() == Permissions.MODERATOR.getLevel() && (gMemberPojo.getPermId() == Permissions.ADMINISTRATOR.getLevel())) {
                            ProxyUtils.sendMessage(event.getChannel(), "Impossible to kick an administrator.");
                        } else {
                            kick(gMember);
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    ProxyUtils.sendMessage(event.getChannel(), "Invalid ID or mention.");
                }
            }, ContextException.here(acceptor -> ProxyUtils.sendMessage(event.getChannel(), "Invalid ID or mention.")));

        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Invalid ID or mention.");
        }
    }

    private void kick(Member mentionnedMember) {
        try {
            event.getGuild().kick(mentionnedMember).queue();
            ProxyUtils.sendMessage(event.getChannel(), "**" + mentionnedMember.getUser().getAsTag() + "** is successfully kicked !");

        } catch (InsufficientPermissionException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Missing permission: **" + Permission.KICK_MEMBERS.getName() + "**.");

        } catch (HierarchyException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Impossible to kick a member with the same or a higher permission than yours.");

        } catch (IllegalArgumentException e) {
        } catch (ErrorResponseException e) {
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.KICK.getName(),
                    "Kick a specified member from your server.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.KICK.getName() + " @aMember`\n"
                    + "*You can also mention a member by his ID*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Kick a specified member from your server. **Example:** `" + guild.getPrefix() + Command.KICK.getName() + " @aMember`.");
        }
    }

}