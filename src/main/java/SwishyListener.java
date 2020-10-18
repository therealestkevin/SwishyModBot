import com.gikk.twirk.Twirk;
import com.gikk.twirk.events.TwirkListener;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwishyListener implements TwirkListener {

    private Map<Long, List<UserMessageEvent>> allMessages = new HashMap<>();
    Twirk t;
    List<String> boolers;
    public SwishyListener(Twirk t, List<String> boolers){
        this.t = t;
        this.boolers = boolers;
    }

    public void onPrivMsg(TwitchUser sender, TwitchMessage message){
        List<UserMessageEvent> curUserMessages;

        curUserMessages  = allMessages.getOrDefault(sender.getUserID(), new ArrayList<>());

        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            Document doc = Document.newBuilder().setContent(message.getContent()).setType(Type.PLAIN_TEXT).build();

            Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

            float score = sentiment.getScore();
            float magnitude = sentiment.getMagnitude();

            float totalSent = score * magnitude;
            if(totalSent < 0) {


                curUserMessages.add(new UserMessageEvent(sender.getDisplayName(), totalSent, message.getContent()));
                if(curUserMessages.size() == 4){
                    StringBuilder sb = new StringBuilder("");
                    for(String s : boolers){
                        sb.append("@").append(s).append("  ");
                    }
                    sb.append("High Toxicity Alert.  ");
                    sb.append("User: ").append("@").append(sender.getDisplayName()).append("    .    ");
                    double allSents = 0.0;
                    for( UserMessageEvent i : curUserMessages) {
                        allSents += i.getSentiment();
                    }




                    sb.append("     Overall Sentiment:    ").append(new DecimalFormat("#.##").format(allSents/curUserMessages.size()));
                    sb.append(".    Negative Messages:    ");

                    for( UserMessageEvent i : curUserMessages) {
                        sb.append(i.getMessage()).append(",   ");
                    }

                    t.channelMessage(sb.toString());
                    curUserMessages.remove(0);
                    curUserMessages.remove(0);

                }
                allMessages.put(sender.getUserID(), curUserMessages);
                //t.channelMessage(sender.getDisplayName() + "   " + "Sentiment: " + totalSent + "     Message: " + message.getContent());
            }



        } catch (IOException e) {
            e.printStackTrace();
        }





    }

    public void onWhisper(TwitchUser sender, TwitchMessage message){
        String curMod = message.getContent();
    }


    public Map<Long, List<UserMessageEvent>> getAllMessages() {
        return allMessages;
    }

    public void setAllMessages(Map<Long, List<UserMessageEvent>> allMessages) {
        this.allMessages = allMessages;
    }
}
