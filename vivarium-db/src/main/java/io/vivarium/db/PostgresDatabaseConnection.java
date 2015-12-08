/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

import javax.inject.Inject;

//@AutoFactory(implementing = DatabaseConnectionFactory.class)
public class PostgresDatabaseConnection implements DatabaseConnection {
	@Inject
	public PostgresDatabaseConnection()
	// public PostgresDatabaseConnection(@Provided String databaseName,
	// @Provided String username,
	// @Provided String password)
	{
		// DO A THANG
	}
}
