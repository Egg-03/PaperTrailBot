package starter;

import listeners.MemberList;
import listeners.MessageListener;
import listeners.ReactionListener;
import listeners.RecoverDeletedMessages;
import listeners.SimpleImageProvider;
import listeners.UserUpdateListener;

public class FireRun {

	public static void main(String[] args) {
		ConnectionInitializer ci = new ConnectionInitializer();
		ci.getManager().addEventListener(new ReactionListener());
		ci.getManager().addEventListener(new MessageListener());
		ci.getManager().addEventListener(new SimpleImageProvider());
		ci.getManager().addEventListener(new RecoverDeletedMessages());
		ci.getManager().addEventListener(new UserUpdateListener());
		ci.getManager().addEventListener(new MemberList());
	}

}
