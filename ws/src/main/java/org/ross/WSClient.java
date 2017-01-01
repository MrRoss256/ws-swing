package org.ross;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */
@ClientEndpoint
public class WSClient {

    Session userSession = null;
    private MessageHandler messageHandler;
    private final URI endpointURI; 

    public WSClient(URI endpointURI) {
    	this.endpointURI = endpointURI;
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, this.endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket");
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason){
        System.out.println("closing websocket");
        this.userSession = null;
        
        while (this.userSession == null){
        	WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        	try {
        		container.connectToServer(this, this.endpointURI);
        	} catch (IOException ex){
        		System.out.println(ex.getLocalizedMessage());
        	} catch (DeploymentException ex){
        		System.out.println(ex.getLocalizedMessage());
        	}	
        	
        	try {
        		Thread.sleep(5000);
        	} catch (InterruptedException ex){
        		System.out.println(ex.getLocalizedMessage());
        	}
        }
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
    	if (this.userSession != null){
    		this.userSession.getAsyncRemote().sendText(message);
    	} else {
    		System.out.println("Connection not open.");
    	}
    }

    /**
     * Message handler.
     *
     * @author Jiji_Sasidharan
     */
    public static interface MessageHandler {

        public void handleMessage(String message);
    }
}