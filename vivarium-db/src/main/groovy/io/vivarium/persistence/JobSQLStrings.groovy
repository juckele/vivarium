/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.persistence

class JobSQLStrings {
    // This query updates the status of jobs based on a few conditions.
    // - If the job is BLOCKED and doesn't have any pre-requisites which are not DONE,
    // the job will be set to WAITING.
    // - If the job is PROCESSING and the worker that has it checked out is no longer active, the job will be set to WAITING.
    // @formatter:off
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
    // @formatter:on
}
