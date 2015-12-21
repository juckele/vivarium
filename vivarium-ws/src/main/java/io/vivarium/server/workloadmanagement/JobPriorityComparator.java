/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.server.workloadmanagement;

import java.util.Comparator;

import io.vivarium.persistence.JobModel;

public class JobPriorityComparator implements Comparator<JobModel>
{
    @Override
    public int compare(JobModel o1, JobModel o2)
    {
        if (o1.getPriority() < o2.getPriority())
        {
            return 1;
        }
        else if (o1.getPriority() > o2.getPriority())
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}
