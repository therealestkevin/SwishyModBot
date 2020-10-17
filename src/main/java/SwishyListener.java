import com.gikk.twirk.events.TwirkListener;

public class SwishyListener implements TwirkListener {

    public void onAnything(String unformatedMessage){


        System.out.println(unformatedMessage);
    }
}
