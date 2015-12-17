package io.vivarium.persistence

class JobSQLStrings {
    // This query updates the status of jobs based on a few conditions.
    //
    static public final String updateStatusString = """
UPDATE jobs
SET    status = 'WAITING'
WHERE  id IN (SELECT t1.id
              FROM   (SELECT jobs.id,
                             jobs.status,
                             Min(requires.status) AS requires_status
                      FROM   jobs
                             LEFT OUTER JOIN job_dependencies
                                          ON ( jobs.id = job_dependencies.job_id
                                             )
                             LEFT OUTER JOIN jobs AS requires
                                          ON ( job_dependencies.requires_job_id
                                               =
                                               requires.id
                                             )
                      GROUP  BY jobs.id) AS t1
              WHERE  status = 'BLOCKED'
                     AND ( requires_status IS NULL
                            OR requires_status = 'DONE' )
              UNION
              SELECT jobs.id
              FROM   jobs
                     LEFT JOIN workers
                            ON ( jobs.checked_out_by = workers.id )
              WHERE  status = 'PROCESSING'
                     AND NOT workers.is_active);
"""
}
