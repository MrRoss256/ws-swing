package org.ross;

import java.net.URI;
import java.net.URISyntaxException;

public class Chatty {

	public static void main(String[] args) {
		
		try {
            // open websocket
            final WSClient clientEndPoint = new WSClient(new URI("ws://localhost:8080"));

            // add listener
            clientEndPoint.addMessageHandler(new WSClient.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println("." + message);
                }
            });

            // send message to websocket
            clientEndPoint.sendMessage("{'event':'addChannel','channel':'ok_btccny_ticker'}");
            
            for (int i = 0; i < 100; i++){
                // wait 5 seconds for messages from websocket
                Thread.sleep(500);
                clientEndPoint.sendMessage("Oy" + Integer.toString(i));
            }


        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
		

	}
}
