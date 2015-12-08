/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

import dagger.Component;

@Component(dependencies = DatabaseModule.class)
public interface DatabaseSystem
{
    // DatabaseConnectionFactory createConnectionFactory();
    PostgresDatabaseConnection createConnection();
}