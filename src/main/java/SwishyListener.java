import com.gikk.twirk.events.TwirkListener;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwishyListener implements TwirkListener {

    private Map<Long, List<UserMessageEvent>> allMessages = new HashMap<>();


    public void onPrivMsg(TwitchUser sender, TwitchMessage message){
        List<UserMessageEvent> curUserMessages;

        curUserMessages  = allMessages.getOrDefault(sender.getUserID(), new ArrayList<>());

        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            Document doc = Document.newBuilder().setContent(message.getContent()).setType(Type.PLAIN_TEXT).build();

            Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

            float score = sentiment.getScore();
            float magnitude = sentiment.getMagnitude();

            double totalSent = score * magnitude;

            curUserMessages.add(new UserMessageEvent(sender.getDisplayName(), totalSent, message.getContent()));

            allMessages.put(sender.getUserID(), curUserMessages);

        } catch (IOException e) {
            e.printStackTrace();
        }





    }

    public Map<Long, List<UserMessageEvent>> getAllMessages() {
        return allMessages;
    }

    public void setAllMessages(Map<Long, List<UserMessageEvent>> allMessages) {
        this.allMessages = allMessages;
    }
}
