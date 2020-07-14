package listeners.commands;

import commands.user.Adminlist;
import commands.user.Banlist;
import commands.user.ControlGate;
import commands.user.GuildInfo;
import commands.user.Info;
import commands.user.MemberInfo;
import commands.user.Modolist;
import commands.user.Ping;
import commands.user.SendImage;
import commands.user.TextChanInfo;
import commands.user.Uptime;
import configuration.constants.Command;
import configuration.constants.Folder;
import dao.pojo.GuildPojo;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyUtils;

public class UserListener {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;
    private Command command;

    public UserListener(GuildMessageReceivedEvent event, GuildPojo guild) {
        this.event = event;
        this.guild = guild;
    }

    public UserListener(GuildMessageReceivedEvent event, GuildPojo guild, Command command) {
        this.event = event;
        this.guild = guild;
        this.command = command;
    }

    public void route() {

        String[] args = ProxyUtils.getArgs(event);

        if (command == Command.INFO) {

            Info infoCmd = new Info(event, guild);
            infoCmd.execute();

        } else if (command == Command.PING) {
            Ping pingCmd = new Ping(event, guild);
            pingCmd.execute();

        } else if (command == Command.UPTIME) {
            Uptime uptimeCmd = new Uptime(event, guild);
            uptimeCmd.execute();

        } else if (command == Command.GUILD_INFO) {
            GuildInfo guildInfoCmd = new GuildInfo(event, guild);
            guildInfoCmd.execute();

        } else if (command == Command.MEMBER_INFO) {

            if (args.length == 2) {
                MemberInfo memberInfoCmd = new MemberInfo(event, guild);
                memberInfoCmd.execute();
            } else {
                MemberInfo memberInfoCmd = new MemberInfo(event, guild);
                memberInfoCmd.help(false);
            }

        } else if (command == Command.TEXTCHAN_INFO) {

            if (args.length == 2) {
                TextChanInfo textChanCmd = new TextChanInfo(event, guild);
                textChanCmd.execute();
            } else {
                TextChanInfo textChanCmd = new TextChanInfo(event, guild);
                textChanCmd.help(false);
            }

        } else if (command == Command.CONTROL_GATE) {

            ControlGate controlGateCmd = new ControlGate(event, guild);
            controlGateCmd.execute();

        } else if (command == Command.BANLIST) {

            if (args.length == 1) {
                Banlist banlistCmd = new Banlist(event, guild);
                banlistCmd.execute();
            } else if (args.length == 2) {
                Banlist banlistCmd = new Banlist(event, guild);
                banlistCmd.consultBannedMember();
            } else {
                Banlist banlistCmd = new Banlist(event, guild);
                banlistCmd.help(false);
            }

        } else if (command == Command.MODOLIST) {

            Modolist modolistCmd = new Modolist(event, guild);
            modolistCmd.execute();

        } else if (command == Command.ADMINLIST) {

            Adminlist adminlistCmd = new Adminlist(event, guild);
            adminlistCmd.execute();

        } else if (command == Command.ISSOU) {

            SendImage memeCmd = new SendImage(event, guild, Folder.RESOURCES.getName() + Folder.ISSOU.getName());
            memeCmd.execute();
        }
    }

}