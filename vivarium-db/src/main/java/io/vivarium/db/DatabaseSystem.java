/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

import dagger.Component;

@Component()
public interface DatabaseSystem
{
    PostgresDatabaseConnection connect();
}