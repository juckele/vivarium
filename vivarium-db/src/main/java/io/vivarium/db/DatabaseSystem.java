package io.vivarium.db;

import dagger.Component;

@Component()
public interface DatabaseSystem {
	PostgresDatabaseConnection connect();
}