package starter;

import listeners.RecoverDeletedMessages;

public class FireRun {

	public static void main(String[] args) {
		ConnectionInitializer ci = new ConnectionInitializer();
		ci.getManager().addEventListener(new RecoverDeletedMessages());
	}

}
