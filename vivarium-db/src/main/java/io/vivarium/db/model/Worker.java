package io.vivarium.db.model;

public class Worker
{
    // Table name
    private static final String TABLE_NAME = "workers";
    // Column names
    private static final String ID = "id";
    private static final String THROUGHPUTS = "throughputs";
    private static final String IS_ACTIVE = "is_active";
    private static final String LAST_ACTIVITY = "last_activity";
    private static final String CODE_VERSION = "code_version";
    private static final String FILE_FORMAT_VERSION = "file_format_version";

    // relation data
    // public final UUID workerID;

    // insert into workers (id, throughputs, is_active, last_activity, file_format_version, code_version) select
    // 'ec1bb1e7-d471-363e-7724-bf995021f543', '{100, 150, 200}', true, now(), 1, '{0,3,2}';
}
