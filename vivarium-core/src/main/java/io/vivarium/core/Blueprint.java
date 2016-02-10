/*
 * Copyright © 2015 John H Uckele. All rights reserved.
 */

package io.vivarium.core;

import java.util.ArrayList;

import io.vivarium.audit.AuditFunction;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;

@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class Blueprint extends VivariumObject
{
    // World Generation
    @SerializedParameter
    private int _width = 25;
    @SerializedParameter
    private int _height = 25;
    @SerializedParameter
    private double _foodGenerationProbability = 0.01;
    @SerializedParameter
    private double _initialFoodGenerationProbability = 0.0;
    @SerializedParameter
    private double _initialWallGenerationProbability = 0.1;

    // Simulation Details
    @SerializedParameter
    private boolean _soundEnabled = false;

    // Species
    @SerializedParameter
    private ArrayList<Species> _species;

    // Audit Functions
    @SerializedParameter
    private ArrayList<AuditFunction> _auditFunctions;

    // Private constructor for deserialization
    private Blueprint()
    {
    }

    public void setSize(int size)
    {
        this._width = size;
        this._height = size;
    }

    public void setHeight(int height)
    {
        this._height = height;
    }

    public void setWidth(int width)
    {
        this._width = width;
    }

    public int getHeight()
    {
        return this._height;
    }

    public int getWidth()
    {
        return this._width;
    }

    public double getFoodGenerationProbability()
    {
        return _foodGenerationProbability;
    }

    public double getInitialFoodGenerationProbability()
    {
        return _initialFoodGenerationProbability;
    }

    public double getInitialWallGenerationProbability()
    {
        return _initialWallGenerationProbability;
    }

    public boolean getSoundEnabled()
    {
        return this._soundEnabled;
    }

    public ArrayList<AuditFunction> getAuditFunctions()
    {
        return this._auditFunctions;
    }

    public ArrayList<Species> getSpecies()
    {
        return this._species;
    }

    public void setFoodGenerationProbability(double p)
    {
        this._foodGenerationProbability = p;
    }

    public void setInitialFoodGenerationProbability(double p)
    {
        this._initialFoodGenerationProbability = p;
    }

    public void setInitialFoodGenerationProbability(int p)
    {
        this._initialFoodGenerationProbability = p;

    }

    public void setInitialWallGenerationProbability(double p)
    {
        this._initialWallGenerationProbability = p;
    }

    public void setSoundEnabled(boolean soundEnabled)
    {
        this._soundEnabled = soundEnabled;
    }

    public void setSpecies(ArrayList<Species> species)
    {
        this._species = species;
    }

    public void setAuditFunctions(ArrayList<AuditFunction> auditFunctions)
    {
        this._auditFunctions = auditFunctions;
    }

    public static Blueprint makeDefault()
    {
        Blueprint wb = new Blueprint();
        wb.finalizeSerialization();
        wb._species = new ArrayList<Species>();
        wb._species.add(Species.makeDefault());
        wb._auditFunctions = new ArrayList<AuditFunction>();
        return wb;
    }

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_auditFunctions == null) ? 0 : _auditFunctions.hashCode());
        long temp;
        temp = Double.doubleToLongBits(_foodGenerationProbability);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + _height;
        temp = Double.doubleToLongBits(_initialFoodGenerationProbability);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(_initialWallGenerationProbability);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (_soundEnabled ? 1231 : 1237);
        result = prime * result + ((_species == null) ? 0 : _species.hashCode());
        result = prime * result + _width;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        Blueprint other = (Blueprint) obj;
        if (_auditFunctions == null)
        {
            if (other._auditFunctions != null)
            {
                return false;
            }
        }
        else if (!_auditFunctions.equals(other._auditFunctions))
        {
            return false;
        }
        if (Double.doubleToLongBits(_foodGenerationProbability) != Double
                .doubleToLongBits(other._foodGenerationProbability))
        {
            return false;
        }
        if (_height != other._height)
        {
            return false;
        }
        if (Double.doubleToLongBits(_initialFoodGenerationProbability) != Double
                .doubleToLongBits(other._initialFoodGenerationProbability))
        {
            return false;
        }
        if (Double.doubleToLongBits(_initialWallGenerationProbability) != Double
                .doubleToLongBits(other._initialWallGenerationProbability))
        {
            return false;
        }
        if (_soundEnabled != other._soundEnabled)
        {
            return false;
        }
        if (_species == null)
        {
            if (other._species != null)
            {
                return false;
            }
        }
        else if (!_species.equals(other._species))
        {
            return false;
        }
        if (_width != other._width)
        {
            return false;
        }
        return true;
    }
}
