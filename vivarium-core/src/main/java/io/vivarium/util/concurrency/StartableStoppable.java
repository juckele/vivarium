/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.util.concurrency;

public interface StartableStoppable
{
    void start() throws Exception;

    void stop() throws Exception;
}
