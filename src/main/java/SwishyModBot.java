import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class SwishyModBot {

    public static void main(String[] args) throws IOException, InterruptedException{
        System.out.println("Welcome to SwishyModBot, a Twitch chat moderator bot. Enter channel to join (leave out the #):");
        Scanner scanner = new Scanner(new InputStreamReader(System.in, "UTF-8"));
        String channelName = scanner.nextLine();
        String channel = "#" + channelName;

        final Twirk twirk = new TwirkBuilder(channel, "SwishyModBot", "Read Oauth.txt")
                .build();				//Create the Twirk object

        twirk.connect();

        //String line;
        //while( !(line = scanner.nextLine()).matches(".quit") )
        twirk.channelMessage("hello I am an experimental Swishy detection bot");

        twirk.whisper("dwengw_50", "hello");

        Files.copy(new URL("https://tmi.twitch.tv/group/user/" + channelName + "/chatters").openStream(), Paths.get("./mods.json"));


        Gson gson = new Gson();

        JsonObject mods= gson.fromJson(new FileReader("mods.json"), JsonObject.class);
        //As long as we don't type .quit into the command prompt, send everything we type as a message to twitch
        JsonObject mod = (JsonObject) mods.get("chatters");
        JsonElement realMods = mod.get("moderators");

        Type listType = new TypeToken<List<String>>(){}.getType();

        List<String> bools = new Gson().fromJson(realMods, listType);

        SwishyListener swish = new SwishyListener(twirk, bools);

        twirk.addIrcListener(swish);




        System.out.println("\nTo exit this example, type .quit and press Enter\n");



        	//Connect to Twitch




        twirk.channelMessage("These are the mods: " + bools.toString());


        Thread.sleep(300000);

        Map<Long, List<UserMessageEvent>> curUsers = swish.getAllMessages();
        /*StringBuilder sb;
        for(List<UserMessageEvent> messages : curUsers.values()){
            double curFloat = 0;
            //if(messages.size() >= 3){
                for(UserMessageEvent i : messages){
                    curFloat += i.getSentiment();
                }
                sb = new StringBuilder("");
                sb.append("User: ").append("@").append(messages.get(0).getDisplayName()).append("    ");
                if(curFloat / messages.size() <= -0.25){
                    for(String s : bools) sb.append(s).append(" ");
                }

                sb.append("Overall Sentiment:    ").append(new DecimalFormat("#.##").format(curFloat/messages.size()));
                sb.append("Negative Messages:    ");

                //for(UserMessageEvent i : messages){
                 //   sb.append(i.getMessage()).append("     ");
                //}

                twirk.channelMessage(sb.toString());
            //}
            */


        /*Collection<List<UserMessageEvent>> bob = curUsers.values();
        for(List<UserMessageEvent> i : bob){
            for(UserMessageEvent babubhatt : i ){
                twirk.channelMessage("Message: " + babubhatt.getMessage() + "Sentiment: " + babubhatt.getSentiment() + " Display Name: "
                         + babubhatt.getDisplayName());
            }
        }*/


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