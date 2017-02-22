package io.vivarium.core;

import java.util.Collection;

import io.vivarium.audit.AuditBlueprint;
import io.vivarium.audit.AuditRecord;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public abstract class World extends VivariumObject
{
    @SerializedParameter
    private int _maximumCreatureID;
    @SerializedParameter
    private int _tick;

    @SerializedParameter
    protected AuditRecord[] _auditRecords;

    @SerializedParameter
    private WorldBlueprint _worldBlueprint;

    protected World()
    {
    }

    public World(WorldBlueprint worldBlueprint)
    {
        this._worldBlueprint = worldBlueprint;
    }

    private void constructAuditRecords()
    {
        int auditRecordCount = _worldBlueprint.getCreatureBlueprints().size()
                * _worldBlueprint.getAuditBlueprints().size();
        _auditRecords = new AuditRecord[auditRecordCount];
        int i = 0;
        for (CreatureBlueprint creatureBlueprint : _worldBlueprint.getCreatureBlueprints())
        {
            for (AuditBlueprint auditBlueprint : _worldBlueprint.getAuditBlueprints())
            {
                _auditRecords[i] = auditBlueprint.makeRecordWithCreatureBlueprint(creatureBlueprint);
                i++;
            }
        }
    }

    abstract protected void executeCreaturePlans();

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }

    abstract public int getCount(CreatureBlueprint s);

    abstract public int getCreatureCount();

    abstract public Collection<Creature> getCreatures();

    public int getMaximimCreatureID()
    {
        return this._maximumCreatureID;
    }

    public int getNewCreatureID()
    {
        return (++_maximumCreatureID);
    }

    public int getTickCounter()
    {
        return _tick;
    }

    public WorldBlueprint getWorldBlueprint()
    {
        return _worldBlueprint;
    }

    protected void initialize()
    {
        // Set up base variables
        this._maximumCreatureID = 0;

        // Fill the world with creatures and food
        this.populatateWorld();

        // Build audit records
        this.constructAuditRecords();
        this.performAudits();
    }

    abstract protected void letCreaturesPlan();

    private void performAudits()
    {
        for (int i = 0; i < _auditRecords.length; i++)
        {
            _auditRecords[i].record(this, _tick);
        }
    }

    abstract protected void populatateWorld();

    public void setMaximumCreatureID(int maximumCreatureID)
    {
        this._maximumCreatureID = maximumCreatureID;
    }

    abstract protected void spawnFood();

    /**
     * Top level simulation step of the entire world and all denizens within it. Simulations are divided into four
     * phases: 1, each creature will age and compute other time based values. 2, each creature will decide on an action
     * to attempt. 3, each creature will attempt to execute the planned action. 4, finally, food spawning and other
     * environmental effects are applied.
     */
    public void tick()
    {
        // Increment tick counter
        _tick++;

        // Each creature calculates time based
        // changes in condition such as age,
        // gestation, and energy levels.
        tickCreatures();

        // Creatures transmit sound & signs
        transmitSounds();
        transmitSigns();

        // Each creature plans which actions to
        // attempt to do during the next phase
        letCreaturesPlan();
        // Each creature will physically try to carry
        // out the planned action
        executeCreaturePlans();

        // Each terrain element is activated
        tickTerrain();

        // New food resources will be spawned in the world
        spawnFood();

        // Record with audit records
        performAudits();
    }

    abstract protected void tickCreatures();

    abstract protected void tickTerrain();

    abstract protected void transmitSigns();

    abstract protected void transmitSounds();
}
