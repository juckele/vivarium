/*
 * The MIT License (MIT)
 *
 * Copyright © 2015 John H. Uckele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright
 * notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.vivarium.util.concurrency;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * SimpleFuture provides a simple implementation of the Future interface, adding a put(T value) method and allowing
 * threads to wait for the values.
 *
 * @author juckele
 *
 * @param <T>
 */
public class SimpleFuture<T> implements Future<T>
{
    private volatile T _value;
    private volatile boolean _done = false;

    private synchronized T getValue()
    {
        return _value;
    }

    /**
     * GenericFuture objects cannot be cancelled. Calling this method returns false, because the future will not be
     * cancelled.
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        // SimpleFuture does not know how to cancel tasks and always fails to cancel.
        return false;
    }

    /**
     * GenericFuture objects cannot be cancelled. Calling this method returns false, because the future is not
     * cancelled.
     */
    @Override
    public boolean isCancelled()
    {
        // SimpleFuture cannot be cancelled, so is never cancelled.
        return false;
    }

    @Override
    public synchronized boolean isDone()
    {
        return _done;
    }

    /**
     * The SimpleFuture is an externally controlled future. The SimpleFuture is completed by calling put, thus providing
     * the value to any other waiting threads.
     *
     * @param value
     *            The value to set the future to.
     */
    public synchronized void put(T value)
    {
        _value = value;
        _done = true;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException
    {
        while (!isDone())
        {
            Thread.sleep(0);
        }
        return getValue();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        long timeoutInMS = unit.toMillis(timeout);
        long startWaitTime = System.currentTimeMillis();
        while (!isDone() && startWaitTime + timeoutInMS < System.currentTimeMillis())
        {
            Thread.sleep(0);
        }
        if (isDone())
        {
            return getValue();
        }
        else
        {
            throw new TimeoutException("Timed out waiting for future. Timeout of " + timeout + " " + unit);
        }
    }
}
