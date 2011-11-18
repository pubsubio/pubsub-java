package pubsub.io.java;

import org.json.JSONObject;

public interface PubsubListener {

	/**
	 * Default id is -1, that means we failed to read a proper message!
	 * 
	 * @param callback_id
	 * @param msg
	 */
	public void onMessage(int callback_id, JSONObject msg);

	public void onOpen();

	public void onClose();

	public void onError(JSONObject msg);
}
