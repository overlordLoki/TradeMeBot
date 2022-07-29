package tradme.bot;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

public class DiscordBot {
	public DiscordBot() {}
	//the token is in the file discordToken.java to get this ask admin. this file is hided in the gitignore file.
	private final String token = discordToken.getToken();
	public void run() throws LoginException {
		//create a new jda builder
		JDABuilder jda = JDABuilder.createDefault(token);
		//set the status to online
		jda.setStatus(OnlineStatus.ONLINE);
		//start it with the commands
		jda.addEventListeners(new Commands());
		jda.build();
	}
}
