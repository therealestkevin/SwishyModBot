import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.commands.PatternCommandExample;
import com.gikk.twirk.commands.PrefixCommandExample;
import com.gikk.twirk.events.TwirkListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**Simple example of how Twirk can be used. <br><br>
 *
 * Make sure that you replace the <code>SETTINGS.MY_NICK</code> and <code>SETTINGS.MY_PASS</code> with the
 * proper values. To generate a Twitch Irc password, visit
 * <a href="https://twitchapps.com/tmi/"> https://twitchapps.com/tmi/ </a>
 *
 * @author Gikkman
 *
 */
public class SwishyModBot {

    public static void main(String[] args) throws IOException, InterruptedException{
        System.out.println("Welcome to SwishyModBot, a Twitch chat moderator bot. Enter channel to join (leave out the #):");
        Scanner scanner = new Scanner(new InputStreamReader(System.in, "UTF-8"));
        String channel = "#" + scanner.nextLine();

        final Twirk twirk = new TwirkBuilder(channel, "SwishyModBot", "oauth:8hb84t3jjvkzfh2yxks4mfkup9kngp")
                .setVerboseMode(true)	//We want to print everything we receive from Twitch
                .build();				//Create the Twirk object

        twirk.addIrcListener( getOnDisconnectListener(twirk) );
        twirk.addIrcListener( new PatternCommandExample(twirk) );
        twirk.addIrcListener( new PrefixCommandExample(twirk) );

        System.out.println("\nTo exit this example, type .quit and press Enter\n");

        twirk.connect();	//Connect to Twitch

        //As long as we don't type .quit into the command prompt, send everything we type as a message to twitch
        String line;
        while( !(line = scanner.nextLine()).matches(".quit") )
            twirk.channelMessage(line);

        scanner.close();	//Close the scanner
        twirk.close();		//Close the connection to Twitch, and release all resources
    }

    private static TwirkListener getOnDisconnectListener(final Twirk twirk) {

        return new TwirkListener() {
            @Override
            public void onDisconnect() {
                //Twitch might sometimes disconnects us from chat. If so, try to reconnect.
                try {
                    if( !twirk.connect() )
                        //Reconnecting might fail, for some reason. If so, close the connection and release resources.
                        twirk.close();
                }
                catch (IOException e) {
                    //If reconnection threw an IO exception, close the connection and release resources.
                    twirk.close();
                }
                catch (InterruptedException e) {  }
            }
        };
    }
}