package tradme.bot;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

public class DiscordBot {
	public DiscordBot() {}
	public void run() throws LoginException {
		JDABuilder jda = JDABuilder.createDefault(
				"OTE5ODEzMDAxNjM5NTI2NDMw.YbbQnA.5Xi7iXBZwt8eclGFeIpSTLKY5QY");
		jda.setStatus(OnlineStatus.ONLINE);
		jda.addEventListeners(new Commands());
		jda.build();
	}
}
