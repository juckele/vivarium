/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

import java.sql.SQLException;

import dagger.Component;

@Component()
public interface DatabaseSystem
{
    PostgresDatabaseConnectionFactory createConnectionFactory() throws SQLException;
}