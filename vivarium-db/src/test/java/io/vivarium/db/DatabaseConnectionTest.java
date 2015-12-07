/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.db;

import org.junit.Test;

public class DatabaseConnectionTest {
	@Test
	public void testConnectToDatabase() {
		DatabaseConnection x = io.vivarium.db.DaggerDatabaseSystem.create().connect();
	}
}
