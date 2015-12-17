/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server;

@FunctionalInterface
public interface VoidFunction
{
    /**
     * Execute the VoidFunction, side effects should be assumed.
     */
    void execute();
}
