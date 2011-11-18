# pubsub-java

## Requirements

* TODO (none for now, includes the implementation by Douglas Crockford)

## Installation

* TODO bullet list for installing the android library in Eclipse

## Getting started with Pubsub.io for Java w. Swing

**Connect**

``` java
	// Create the library object
	mPubsub = new Pubsub();
	// Add the listener (this is where your application will get responses from the server)
	mPubsub.addPubsubListener(this);
	// Connect to the server, it's important that you add the listener first!
	mPubsub.connect("java");
```

**Subscribe**

*It's important that all subscription messages be sent in the onOpen method of the PubsubListener!**

``` java
	@Override
	public void onOpen() {
		JSONObject filter = new JSONObject();
		JSONObject version = new JSONObject();
		try {
			version.put("$gt", 0.1);
			filter.put("version", version);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// You need to give the filter a unique name (String) when subscribing, this is 
		// so the library can keep track of the different filters! You will get a unique
		// integer in return. (You only need this if you want to unsubscribe at some point!)
		subscription_all = mPubsub.subscribe(filter, "version");
	}
```

**Publish**

*Don't publish before the server has connected (the onOpen method has been called)*

``` java
	JSONObject doc = new JSONObject();
	try {
		doc.put("version", 0.5);
	} catch (JSONException e1) {
		e1.printStackTrace();
	}
	mPubsub.publish(doc);
```

**Receive messages**

*All messages will be recieved in the onMessage method of the PubsubListener!*

``` java
	@Override
	public void onMessage(int callback_id, JSONObject doc) {
		if (callback_id == subscription_all){
			try {
				double version = doc.getDouble("version");
				System.out.println("version:" + version);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		mJTextArea.append("\n");
	}
```