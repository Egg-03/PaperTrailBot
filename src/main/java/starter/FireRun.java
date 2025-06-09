package starter;

import listeners.AuditLogListener;
import listeners.ReactionListener;

public class FireRun {

	public static void main(String[] args) {
		ConnectionInitializer ci = new ConnectionInitializer();
		ci.getManager().addEventListener(new AuditLogListener());
		ci.getManager().addEventListener(new ReactionListener());
	}

}
