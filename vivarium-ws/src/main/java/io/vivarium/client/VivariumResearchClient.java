package io.vivarium.client;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;

import io.vivarium.util.UUID;

public abstract class VivariumResearchClient extends WebSocketClient
{
    public static final String CLIENT_ID_HEADER = "clientID";

    public VivariumResearchClient(URI uri, UUID clientID)
    {
        super(uri, new Draft_10(), buildHeaderMap(clientID), 0);
    }

    protected static Map<String, String> buildHeaderMap(UUID clientID)
    {
        Map<String, String> map = new HashMap<>();
        map.put(CLIENT_ID_HEADER, clientID.toString());
        return map;
    }

}
