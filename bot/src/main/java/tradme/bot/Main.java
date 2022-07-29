package tradme.bot;

import javax.security.auth.login.LoginException;
public class Main{

    public static void main(String[] args) throws Exception {
    	DiscordBot bot = new DiscordBot();
		try {bot.run();}catch(LoginException e){}
    }
}
