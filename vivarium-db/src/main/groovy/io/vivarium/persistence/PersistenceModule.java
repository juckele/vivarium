package io.vivarium.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import io.vivarium.util.UUID;

public class PersistenceModule
{
    private final Connection _databaseConnection;

    public PersistenceModule(Connection databaseConnection)
    {
        _databaseConnection = databaseConnection;
    }

    public boolean persist(PersistenceModel model)
    {
        try
        {
            model.persistToDatabase(_databaseConnection);
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean persist(Collection<PersistenceModel> models)
    {
        try
        {
            for (PersistenceModel model : models)
            {
                model.persistToDatabase(_databaseConnection);
            }
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends PersistenceModel> Optional<T> fetch(UUID id, Class<T> clazz)
    {
        try
        {
            if (clazz == ResourceModel.class)
            {
                return (Optional<T>) ResourceModel.getFromDatabase(_databaseConnection, id);
            }
            if (clazz == WorkerModel.class)
            {
                return (Optional<T>) WorkerModel.getFromDatabase(_databaseConnection, id);
            }
            if (clazz == JobModel.class)
            {
                return (Optional<T>) JobModel.getFromDatabase(_databaseConnection, id);
            }
            else
            {
                throw new IllegalArgumentException("Unknown class" + clazz);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<JobModel> fetchJobsWithStatus(JobStatus status)
    {
        try
        {
            return JobModel.getFromDatabase(_databaseConnection, status);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<WorkerModel> fetchAllWorkers()
    {
        try
        {
            return WorkerModel.getFromDatabase(_databaseConnection);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void updateJobStatuses()
    {
        try
        {
            JobModel.updateJobStatuses(_databaseConnection);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
