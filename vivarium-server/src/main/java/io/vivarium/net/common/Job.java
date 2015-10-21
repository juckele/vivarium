/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.net.common;

import java.util.UUID;

public class Job
{
    public final UUID jobID;

    public Job()
    {
        jobID = UUID.randomUUID();
    }
}
