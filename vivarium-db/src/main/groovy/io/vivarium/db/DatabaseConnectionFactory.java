/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

public interface DatabaseConnectionFactory
{
    DatabaseConnection createConnection(String databaseName, String username, String password);
}
