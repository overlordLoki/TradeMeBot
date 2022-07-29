package tradme.bot;

import javax.security.auth.login.LoginException;
public class Main{

    public static void main(String[] args) throws Exception {
      //make a discord bot
    	DiscordBot bot = new DiscordBot();
      //run the bot
		  try {bot.run();}catch(LoginException e){}
    }
}
