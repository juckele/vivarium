/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

import java.sql.SQLException;

public interface DatabaseConnectionFactory
{
    DatabaseConnection createDatabaseConnection() throws SQLException;
}
