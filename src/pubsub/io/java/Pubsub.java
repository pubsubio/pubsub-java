/*
 * pubsub.io Java Library
 * Copyright (C) 2011  Andreas Göransson

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pubsub.io.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Andreas Göransson
 * 
 */
public class Pubsub {

	private static final String DEBUGTAG = "::pubsub-java:::";
	private static final boolean DEBUG = false;

	private String mHost;
	private String mPort;
	private String mSub;

	private PubsubComm mPubsubComm;

	private Socket mSocket;

	private HashMap<Integer, String> callbacks;

	private PubsubComm t;

	// Listenerlist (really only one listener, the sketch!)
	private Vector PubsubListeners;

	public Pubsub() {
		PubsubListeners = new Vector();
	}

	/**
	 * Connect to the default sub at hub.pubsub.io.
	 */
	public void connect() {
		// connect("hub.pubsub.io", "10547", "/");
		connect("79.125.4.43", "10547", "");
	}

	/**
	 * Connect to a specified sub at hub.pubsub.io.
	 * 
	 * @param sub
	 */
	public void connect(String sub) {
		// connect("hub.pubsub.io", "10547", sub);
		connect("79.125.4.43", "10547", sub);
	}

	/**
	 * Connect to a specified sub on a specified pubsub hub & port.
	 * 
	 * @param url
	 * @param port
	 */
	public void connect(String host, String port, String sub) {
		if (DEBUG)
			System.out.println(DEBUGTAG + "connect( " + host + ":" + port + "/"
					+ sub + " )");

		mHost = host;
		mPort = port;
		mSub = sub;

		// Make sure we've got internet
		if (!hasInternet()) {
			// TODO Maybe log something to the console to let the user know we
			// failed internet check
			return;
		}

		if (!isConnected()) {
			try {
				InetAddress addr = InetAddress.getByName(mHost);
				mSocket = new Socket(addr, Integer.parseInt(mPort));
				t = new PubsubComm(mSocket);
				sub(mSub);
				t.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (DEBUG)
				System.out.println(DEBUGTAG
						+ "Pubsub.io already connected, ignoring");
		}
	}

	/**
	 * Hook up to a specific sub.
	 * 
	 * @param sub
	 */
	public void sub(String sub) {
		try {
			t.write(PubsubParser.sub(sub).getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Subscribe to a filter, with a specified handler_callback, on the
	 * connected sub. The handler_callback should be a declared constant, and it
	 * should be used in the Handler of your activity!
	 * 
	 * @param json_filter
	 * @param handler_callback
	 */
	public int subscribe(JSONObject json_filter, String method_callback) {
		int callback_id = callbacks.size() + 1;

		// Add the callback
		callbacks.put(callback_id, method_callback);

		try {
			t.write(PubsubParser.subscribe(json_filter, callback_id).getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return callback_id;
	}

	/**
	 * Unsubscribe the specified handler_callback.
	 * 
	 * @param handler_callback
	 */
	public void unsubscribe(Integer handler_callback) {
		// Remove the handler callback
		callbacks.remove(handler_callback);
		try {
			t.write(PubsubParser.unsubscribe(handler_callback).getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Publish a document to the connected sub.
	 * 
	 * @param doc
	 */
	public void publish(JSONObject json_doc) {
		try {
			t.write(PubsubParser.publish(json_doc).getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean hasInternet() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Add a listener.
	 * 
	 * @param wsl
	 */
	public void addPubsubListener(PubsubListener wsl) {
		if (PubsubListeners.contains(wsl))
			return;
		PubsubListeners.addElement(wsl);
	}

	/**
	 * Remove a listener
	 * 
	 * @param wsl
	 */
	public void removePubsubListener(PubsubListener wsl) {
		if (!PubsubListeners.contains(wsl))
			return;
		PubsubListeners.removeElement(wsl);
	}

	private class PubsubComm extends Thread {
		// Socket & streams
		private final Socket mSocket;
		private final InputStream mInputStream;
		private final OutputStream mOutputStream;

		public PubsubComm(Socket socket) {
			mSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mInputStream = tmpIn;
			mOutputStream = tmpOut;
		}

		public boolean isConnected() {
			return mSocket.isConnected();
		}

		@Override
		public synchronized void start() {
			if (mSocket.isConnected())
				createWebSocketEvent(PubsubEvent.ON_OPEN, null);
			super.start();
		}

		/**
		 * 
		 * @param event_type
		 * @param msg
		 */
		private void createWebSocketEvent(int event_type, JSONObject msg) {
			createWebSocketEvent(event_type, msg, -1);
		}

		/**
		 * 
		 * @param event_type
		 * @param msg
		 */
		private void createWebSocketEvent(int event_type, JSONObject msg,
				int callback_id) {
			// PubsubEvent wse = new PubsubEvent(this, event_type, msg);

			Vector vtemp = (Vector) PubsubListeners.clone();
			for (int x = 0; x < vtemp.size(); x++) {
				PubsubListener target = null;
				target = (PubsubListener) vtemp.elementAt(x);

				switch (event_type) {
				case PubsubEvent.ON_CLOSE:
					target.onClose();
					break;
				case PubsubEvent.ON_ERROR:
					target.onError(msg);
					break;
				case PubsubEvent.ON_MESSAGE:
					target.onMessage(callback_id, msg);
					break;
				case PubsubEvent.ON_OPEN:
					target.onOpen();
					break;
				}
			}
		}

		public void run() {
			byte[] buffer = new byte[1024];
			int bytes;

			StringBuffer mStringBuffer = new StringBuffer();

			// Keep listening to the InputStream while connected
			while (true) {
				try {
					// Read from the InputStream
					bytes = mInputStream.read(buffer);

					if (bytes > 0) {
						// Add the characters to the stringbuffer
						mStringBuffer.append(new String(buffer, 0, bytes));

						// Get all JSON messages!
						while (hasNext(mStringBuffer)) {
							// Get the next JSONObject
							int[] startAndStop = getNext(mStringBuffer);
							String next = mStringBuffer.substring(
									startAndStop[0], startAndStop[1]);

							// Process the JSON message
							process(next);

							// Delete the selected characters from the buffer.
							mStringBuffer.delete(startAndStop[0],
									startAndStop[1]);
						}
					}
				} catch (IOException e) {
					// TODO, restart the thing if it failed?
					JSONObject root = new JSONObject();
					try {
						root.put("error", e);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}

					createWebSocketEvent(PubsubEvent.ON_ERROR, root);
					break;
				}
			}

		}

		/**
		 * Create and send the JSONObject to the Processing sketch.
		 * 
		 * @param next
		 */
		private void process(String json_formatted) {
			JSONObject json_obj = null;
			try {
				json_obj = new JSONObject(json_formatted);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			int callback_id = -1;
			try {
				callback_id = json_obj.getInt("id");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (json_obj != null)
				createWebSocketEvent(PubsubEvent.ON_MESSAGE, json_obj,
						callback_id);
		}

		/**
		 * Detect if the StringBuffer has another JSON package inside it...
		 * 
		 * @param mStringBuffer
		 * @return
		 */
		private boolean hasNext(StringBuffer mStringBuffer) {
			int start = mStringBuffer.indexOf("{");
			int end = -1;

			if (start != -1) {
				int starts = 1;
				int ends = 0;

				for (int i = 0; i < mStringBuffer.length(); i++) {

					if (mStringBuffer.charAt(i) == '{') {
						starts++;
					} else if (mStringBuffer.charAt(i) == '}') {
						ends++;
						end = i + 1;
					}

					if (starts == ends)
						break;
				}
			}

			if (start != -1 && end != -1 && start < end)
				return true;

			return false;
		}

		private int[] getNext(StringBuffer mStringBuffer) {
			int start = mStringBuffer.indexOf("{");
			int end = -1;

			if (start != -1) {
				int starts = 1;
				int ends = 0;

				for (int i = 0; i < mStringBuffer.length(); i++) {

					if (mStringBuffer.charAt(i) == '{') {
						starts++;
					} else if (mStringBuffer.charAt(i) == '}') {
						ends++;
						end = i + 1;
					}

					if (starts == ends)
						break;
				}
			}

			if (start != -1 && end != -1)
				return new int[] { start, end };

			return null;
		}

		/**
		 * Write to the connected OutStream.
		 * 
		 * @param buffer
		 *            The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				mOutputStream.write(attachHeaderAndFooter(buffer));
			} catch (IOException e) {
				JSONObject root = new JSONObject();
				try {
					root.put("error", e);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				createWebSocketEvent(PubsubEvent.ON_ERROR, root);
			}
		}

		public void cancel() {
			try {
				mSocket.close();
			} catch (IOException e) {
				JSONObject root = new JSONObject();
				try {
					root.put("error", e);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				createWebSocketEvent(PubsubEvent.ON_ERROR, root);
			}

		}

		/**
		 * This adds the required header and footer for the package, without
		 * them the hub won't recognize the message.
		 * 
		 * @param buffer
		 * @return
		 */
		private byte[] attachHeaderAndFooter(byte[] buffer) {
			// In total, 2 bytes longer than the original message!
			byte[] sendbuffer = new byte[buffer.length + 2];

			// Set the first byte (0x000000)
			sendbuffer[0] = (byte) 0x000000;

			// Add the real package (buffer)
			for (int i = 1; i < sendbuffer.length - 1; i++)
				sendbuffer[i] = buffer[i - 1];

			// Add the footer (0xFFFFFD)
			sendbuffer[sendbuffer.length - 1] = (byte) 0xFFFFFD;

			return sendbuffer;
		}
	}

}
