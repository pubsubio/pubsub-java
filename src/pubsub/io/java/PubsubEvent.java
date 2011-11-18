package pubsub.io.java;

import java.util.EventObject;

import org.json.JSONObject;

public class PubsubEvent extends EventObject {

	public static final int ON_MESSAGE = 0;
	public static final int ON_OPEN = 1;
	public static final int ON_CLOSE = 2;
	public static final int ON_ERROR = 3;

	private int event_type;

	private int callback_id;

	private JSONObject message;

	public PubsubEvent(Object source, int event_type, JSONObject message) {
		this(source, event_type, message, -1);
	}

	public PubsubEvent(Object source, int event_type, JSONObject message,
			int callback_id) {
		super(source);
		this.event_type = event_type;
		this.message = message;
		this.callback_id = callback_id;
	}

	public JSONObject getMessage() {
		return message;
	}

	public int getCallbackId() {
		return callback_id;
	}

}
