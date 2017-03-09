package io.vivarium.core;

import java.util.ArrayList;

import io.vivarium.audit.AuditBlueprint;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class WorldBlueprint extends VivariumObject
{
    // Simulation Details
    @SerializedParameter
    private boolean _soundEnabled = false;
    // Simulation Details
    @SerializedParameter
    private boolean _signEnabled = false;

    // Blueprints for creatures
    @SerializedParameter
    protected ArrayList<CreatureBlueprint> _creatureBlueprints;

    // Audit Functions
    @SerializedParameter
    protected ArrayList<AuditBlueprint> _auditBlueprints;

    protected WorldBlueprint()
    {
    }

    public boolean getSoundEnabled()
    {
        return this._soundEnabled;
    }

    public boolean getSignEnabled()
    {
        return this._signEnabled;
    }

    public ArrayList<AuditBlueprint> getAuditBlueprints()
    {
        return this._auditBlueprints;
    }

    public ArrayList<CreatureBlueprint> getCreatureBlueprints()
    {
        return this._creatureBlueprints;
    }

    public void setSoundEnabled(boolean soundEnabled)
    {
        this._soundEnabled = soundEnabled;
    }

    public void setSignEnabled(boolean signEnabled)
    {
        this._signEnabled = signEnabled;
    }

    public void setCreatureBlueprints(ArrayList<CreatureBlueprint> creatureBlueprints)
    {
        this._creatureBlueprints = creatureBlueprints;
    }

    public void setAuditBlueprints(ArrayList<AuditBlueprint> auditBlueprints)
    {
        this._auditBlueprints = auditBlueprints;
    }

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }
}
