package tradme.bot;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

public class DiscordBot {
	public DiscordBot() {}
	private final String token = discordToken.getToken();
	public void run() throws LoginException {
		JDABuilder jda = JDABuilder.createDefault(token);
		jda.setStatus(OnlineStatus.ONLINE);
		jda.addEventListeners(new Commands());
		jda.build();
	}
}
