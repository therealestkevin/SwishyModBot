import com.gikk.twirk.events.TwirkListener;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwishyListener implements TwirkListener {

    private Map<Long, List<UserMessageEvent>> allMessages = new HashMap<>();


    public void onPrivMsg(TwitchUser sender, TwitchMessage message){
        List<UserMessageEvent> curUserMessages;

        curUserMessages  = allMessages.getOrDefault(sender.getUserID(), new ArrayList<>());

        curUserMessages.add(new UserMessageEvent(sender.getDisplayName(), 0.00, message.getContent()));


        allMessages.put(sender.getUserID(), curUserMessages);


    }

    public Map<Long, List<UserMessageEvent>> getAllMessages() {
        return allMessages;
    }

    public void setAllMessages(Map<Long, List<UserMessageEvent>> allMessages) {
        this.allMessages = allMessages;
    }
}
