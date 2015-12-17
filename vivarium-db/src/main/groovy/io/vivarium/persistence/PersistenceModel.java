/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.persistence;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class PersistenceModel
{
    protected abstract void persistToDatabase(Connection connection) throws SQLException;
}
