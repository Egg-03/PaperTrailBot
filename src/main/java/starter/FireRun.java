package starter;

import listeners.MessageListener;
import listeners.ReactionListener;
import listeners.SimpleImageProvider;

public class FireRun {

	public static void main(String[] args) {
		ConnectionInitializer ci = new ConnectionInitializer();
		ci.getManager().addEventListener(new ReactionListener());
		ci.getManager().addEventListener(new MessageListener());
		ci.getManager().addEventListener(new SimpleImageProvider());
	}

}
