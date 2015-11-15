/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.vivarium.util.Version;

public class ServerGreeting extends Message
{
    public final int networkProtocol = 1;

    @JsonCreator
    public ServerGreeting(@JsonProperty("networkProtocol") int networkProtocol)
    {
        networkProtocol = Version.NETWORK_PROTOCOL_VERSION;
    }
}
