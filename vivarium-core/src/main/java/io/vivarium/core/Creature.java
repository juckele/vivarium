package io.vivarium.core;

import io.vivarium.core.processor.Processor;
import io.vivarium.core.processor.ProcessorBlueprint;
import io.vivarium.core.sensor.Sensor;
import io.vivarium.serialization.ClassRegistry;
import io.vivarium.serialization.SerializedParameter;
import io.vivarium.serialization.VivariumObject;
import io.vivarium.util.Functions;
import io.vivarium.util.Rand;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@SuppressWarnings("serial") // Default serialization is never used for a durable store
public class Creature extends VivariumObject
{
    static
    {
        ClassRegistry.getInstance().register(Creature.class);
    }

    // Meta information
    @SerializedParameter
    private int _id;
    @SerializedParameter
    private CreatureBlueprint _creatureBlueprint;
    @SerializedParameter
    private double _generation;

    // Processor
    @SerializedParameter
    private Processor[] _processors;
    @SerializedParameter
    private double[] _inputs;
    @SerializedParameter
    private double[] _memoryUnits;
    @SerializedParameter
    private double[] _soundInputs;
    @SerializedParameter
    private double[] _soundOutputs;
    @SerializedParameter
    private double[] _signInputs;
    @SerializedParameter
    private double[] _signOutputs;

    // State
    @SerializedParameter
    private boolean _hasActed = true; // Defaults to true to prevent a newborn from acting before it plans
    @SerializedParameter
    private boolean _wasSuccessful = true;
    @SerializedParameter
    private Gender _gender;
    @SerializedParameter
    private double _randomSeed;
    @SerializedParameter
    private int _age;
    @SerializedParameter
    private int _gestation;
    @SerializedParameter
    private int _food;
    @SerializedParameter
    private Direction _facing;
    @SerializedParameter
    private Action _action = Action.SPAWN;
    @SerializedParameter
    private int _health;

    // Fetus
    @SerializedParameter
    private Creature _fetus;

    protected Creature()
    {

    }

    public Creature(CreatureBlueprint creatureBlueprint)
    {
        this(creatureBlueprint, null, null);
    }

    public Creature(CreatureBlueprint creatureBlueprint, Creature prototype)
    {
        this(creatureBlueprint, prototype, null);
    }

    public Creature(Creature prototype)
    {
        this(prototype._creatureBlueprint, prototype, null);
    }

    public Creature(Creature parent1, Creature parent2)
    {
        this(parent1._creatureBlueprint, parent1, parent2);
    }

    public Creature(CreatureBlueprint creatureBlueprint, Creature parent1, Creature parent2)
    {
        this._creatureBlueprint = creatureBlueprint;

        // Compute creature generation
        if (parent1 != null)
        {
            if (parent2 != null)
            {
                this._generation = Functions.logarithmicAverage(parent1._generation, parent2._generation) + 1;
            }
            else
            {
                this._generation = parent1._generation;
            }
        }
        else
        {
            this._generation = 1;
        }

        // Create processors to control the Creature
        ProcessorBlueprint[] processorBlueprints = _creatureBlueprint.getProcessorBlueprints();
        _processors = new Processor[processorBlueprints.length];
        for (int i = 0; i < processorBlueprints.length; i++)
        {
            Processor processor1 = parent1 != null ? parent1._processors[i] : null;
            Processor processor2 = parent2 != null ? parent2._processors[i] : null;
            _processors[i] = createProcessor(processorBlueprints[i], processor1, processor2);
        }

        // Sets the size of all the processor i/o arrays
        _inputs = new double[_creatureBlueprint.getMultiplexerInputCount()];
        _memoryUnits = new double[_creatureBlueprint.getMemoryUnitCount()];
        _soundInputs = new double[_creatureBlueprint.getSoundChannelCount()];
        _soundOutputs = new double[_creatureBlueprint.getSoundChannelCount()];
        _signInputs = new double[_creatureBlueprint.getSignChannelCount()];
        _signOutputs = new double[_creatureBlueprint.getSignChannelCount()];

        // Set gender
        double randomNumber = Rand.getInstance().getRandomPositiveDouble();
        if (randomNumber < _creatureBlueprint.getFemaleThreshold())
        {
            this._gender = Gender.FEMALE;
        }
        else
        {
            this._gender = Gender.MALE;
        }

        // Set the per Creature random seed (this is
        // currently only used to set animation offsets)
        this._randomSeed = Rand.getInstance().getRandomPositiveDouble();

        // Set defaults
        this._age = 0;
        this._gestation = 0;
        this._food = _creatureBlueprint.getMaximumFood();
        this._facing = Direction.NORTH;
        this._action = Action.REST;
        this._health = 2000;
    }

    private Processor createProcessor(ProcessorBlueprint blueprint, Processor processor1, Processor processor2)
    {
        if (processor1 != null)
        {
            if (processor2 != null)
            {
                // Processor combined from genetic legacy
                return blueprint.makeProcessorWithParents(processor1, processor2);
            }
            else
            {
                // Processor from single parent (might still mutate)
                return blueprint.makeProcessorWithParents(processor1, processor1);
            }
        }
        else
        {
            if (processor2 != null)
            {
                // Single parent is only an error if parent2 is non-null, otherwise assumed to be a clone
                throw new Error("Creature with single parent");
            }
            else
            {
                // Create a new processor
                return blueprint.makeProcessor();
            }
        }
    }

    public void tick(int flameHit)
    {
        // Reset action
        this._hasActed = false;
        // Age
        this._age++;

        // Gestate if preggars
        if (this._gestation > 0)
        {
            this._gestation++;
            this._food += _creatureBlueprint.getPregnantFoodRate();
        }
        this._food += _creatureBlueprint.getBaseFoodRate();
        this._health -= 10 * flameHit;

        // healing
        if (this._health < _creatureBlueprint.getMaximumHealth())
        {
            this._health++;
            this._food -= 2;
        }
    }

    public Action getAction()
    {
        return (_action);
    }

    public void listenToCreature(Creature u, double distanceSquared)
    {
        int soundChannels = Math.min(this._creatureBlueprint.getSoundChannelCount(),
                u._creatureBlueprint.getSoundChannelCount());
        for (int i = 0; i < soundChannels; i++)
        {
            this._soundInputs[i] += u._soundOutputs[i] / distanceSquared;
        }
    }

    public void lookAtCreature(Creature u)
    {
        int signChannels = Math.min(this._creatureBlueprint.getSignChannelCount(),
                u._creatureBlueprint.getSignChannelCount());
        for (int i = 0; i < signChannels; i++)
        {
            this._signInputs[i] = u._signOutputs[i];
        }
    }

    public void planAction(World w, int r, int c)
    {
        _action = determineAction(w, r, c);
    }

    private Action determineAction(World w, int r, int c)
    {
        Action involuntaryAction = getInvoluntaryAction();
        if (involuntaryAction != null)
        {
            return (involuntaryAction);
        }
        else
        {
            // Run sensors
            int inputIndex = 0;
            Sensor[] sensors = _creatureBlueprint.getSensors();
            for (int i = 0; i < sensors.length; i++)
            {
                Sensor sensor = sensors[i];
                inputIndex += sensor.sense(w, _inputs, inputIndex, r, c, this);
            }
            // Read memory units
            System.arraycopy(_memoryUnits, 0, _inputs, _creatureBlueprint.getHardProcessorInputs(),
                    this._memoryUnits.length);
            // Read sound inputs
            for (int i = 0; i < this.getBlueprint().getSoundChannelCount(); i++)
            {
                _inputs[_creatureBlueprint.getHardProcessorInputs() + this._memoryUnits.length + i] = _soundInputs[i];
            }
            // Read sign inputs
            for (int i = 0; i < this.getBlueprint().getSignChannelCount(); i++)
            {
                _inputs[_creatureBlueprint.getHardProcessorInputs() + this._memoryUnits.length
                        + this._soundInputs.length + i] = _signInputs[i];
            }
            // Main processor computation
            double[] outputs = this._creatureBlueprint.getMultiplexer().outputs(_inputs, _processors);
            // Save memory units
            System.arraycopy(outputs, _creatureBlueprint.getHardProcessorOutputs(), _memoryUnits, 0,
                    this._memoryUnits.length);
            // Clear the sound inputs and set the sound outputs
            for (int i = 0; i < _creatureBlueprint.getSoundChannelCount(); i++)
            {
                this._soundInputs[i] = 0;
                this._soundOutputs[i] = outputs[_creatureBlueprint.getHardProcessorOutputs() + this._memoryUnits.length
                        + i];
            }
            // Clear the sign inputs and set the sign outputs
            for (int i = 0; i < this.getBlueprint().getSignChannelCount(); i++)
            {
                this._signInputs[i] = 0;
                this._signOutputs[i] = outputs[_creatureBlueprint.getHardProcessorOutputs() + this._memoryUnits.length
                        + this._soundInputs.length + i];
            }
            // Hard coded outputs (actionable outputs)
            int maxActionOutput = 0;
            for (int i = 1; i < _creatureBlueprint.getHardProcessorOutputs(); i++)
            {
                if (outputs[i] > outputs[maxActionOutput])
                {
                    maxActionOutput = i;
                }
            }
            // Return the output conversion to action
            return (Action.convertIntegerToAction(maxActionOutput));
        }
    }

    public Action getInvoluntaryAction()
    {
        // Forced actions
        // Old Creatures Die
        if (this._age > _creatureBlueprint.getMaximumAge() || this._food < 1 || this._health < 1)
        {

            return (Action.DIE);
        }
        // Pregnant Creatures can give birth
        else if (this._gestation > _creatureBlueprint.getMaximumGestation())
        {
            return (Action.BIRTH);
        }

        // If there are no involuntary actions, return null
        return (null);
    }

    public Direction getFacing()
    {
        return (this._facing);
    }

    public double getRandomSeed()
    {
        return (this._randomSeed);
    }

    public void setIsFemale(boolean isFemale)
    {
        this._gender = isFemale ? Gender.FEMALE : Gender.MALE;
    }

    public boolean getIsFemale()
    {
        return (this._gender == Gender.FEMALE);
    }

    public Creature getFetus()
    {
        return (_fetus);
    }

    public Processor[] getProcessors()
    {
        return (_processors);
    }

    public double[] getMemoryUnits()
    {
        return (_memoryUnits);
    }

    public double[] getSoundOutputs()
    {
        return (_soundOutputs);
    }

    public double[] getSignOutputs()
    {
        return (_signOutputs);
    }

    public void executeAction(Action action)
    {
        this._hasActed = true;
        executeAction(action, null);
    }

    public void executeAction(Action action, Creature target)
    {
        _wasSuccessful = true;
        switch (action)
        {
            case BIRTH:
                this._gestation = 0;
                break;
            case BREED:
                assert target != null;
                if (this._gender == Gender.FEMALE && this._gestation < 1)
                {
                    this._gestation = 1;
                    this._fetus = createOffspringWith(target);
                }
                this._food += _creatureBlueprint.getBreedingFoodRate();
                break;
            case MOVE:
                this._food += _creatureBlueprint.getMovingFoodRate();
                break;
            case REST:
            case DIE:
                // change nothing
                break;
            case TURN_LEFT:
                this._facing = Direction.stepCounterclockwise(this._facing);
                break;
            case TURN_RIGHT:
                this._facing = Direction.stepClockwise(this._facing);
                break;
            case EAT:
                this._food += _creatureBlueprint.getEatingFoodRate();
                if (this._food > _creatureBlueprint.getMaximumFood())
                {
                    this._food = _creatureBlueprint.getMaximumFood();
                }
                break;
            case FIGHT:
                assert target != null;
                target._health += _creatureBlueprint.getFightingDamageAmount();
                this._food += _creatureBlueprint.getFightingFoodRate();
                break;
            default:
                System.err.println("Non-Fatal Error, unhandled action");
                new Error().printStackTrace();
                break;
        }
    }

    public void failAction(Action action)
    {
        _wasSuccessful = false;
        switch (action)
        {
            case BIRTH:
                // If gestation has been reached but the birth failed,
                // let the Creature have a round of freedom before trying to
                // give birth again
                this._gestation -= 2;
                break;
            case BREED:
                // Trying to breed for no good reason is still tiring
                this._food += _creatureBlueprint.getBreedingFoodRate();
                break;
            case EAT:
                // Trying to eat nothing and failing costs nothing (this has been changed because the file format
                // doesn't have a variable for this cost currently. This cost could be reintroduced later)
                break;
            case MOVE:
                this._food += _creatureBlueprint.getMovingFoodRate();
                break;
            case DIE:
                break;
            case REST:
            case TURN_LEFT:
            case TURN_RIGHT:
                System.err.println("Action class " + action + " should not fail");
                break;
            case FIGHT:
                this._food += _creatureBlueprint.getFightingFoodRate();
                break;
            default:
                System.err.println("Non-Fatal Error, unhandled action");
                new Error().printStackTrace();
                break;
        }
    }

    private Creature createOffspringWith(Creature breedingTarget)
    {
        return new Creature(this, breedingTarget);
    }

    public String render(RenderCode code, int r, int c)
    {
        if (code == RenderCode.COMPLEX_CREATURE || code == RenderCode.SIMPLE_CREATURE)
        {
            return this.renderSelf(code, r, c);
        }
        else
        {
            throw new IllegalArgumentException("RenderCode " + code + " not supported for type " + this.getClass());
        }
    }

    private String renderSelf(RenderCode code, int r, int c)
    {
        StringBuilder output = new StringBuilder();
        output.append(code == RenderCode.COMPLEX_CREATURE ? "Creature ID: " : "U_");
        output.append(this._id);
        output.append(code == RenderCode.COMPLEX_CREATURE ? "\nCoordinates: (" : " @(");
        output.append(r);
        output.append(',');
        output.append(c);
        output.append(")");
        output.append(code == RenderCode.COMPLEX_CREATURE ? "\nGeneration: " : " GEN-");
        output.append(this._generation);
        output.append(code == RenderCode.COMPLEX_CREATURE ? "\nGender: " : "");
        if (this._gender == Gender.FEMALE)
        {
            output.append(code == RenderCode.COMPLEX_CREATURE ? "Female" : '女');
        }
        else
        {
            output.append(code == RenderCode.COMPLEX_CREATURE ? "Male" : '男');
        }
        output.append(code == RenderCode.COMPLEX_CREATURE ? "\nAge: " : "/a:");
        output.append(this._age);
        output.append(code == RenderCode.COMPLEX_CREATURE ? "\nGestation: " : "/g:");
        output.append(this._gestation);
        output.append(code == RenderCode.COMPLEX_CREATURE ? "\nFood: " : "/f:");
        output.append(this._food);
        output.append(code == RenderCode.COMPLEX_CREATURE ? "\nFacing: " : "/f:");
        switch (this._facing)
        {
            case EAST:
                output.append(code == RenderCode.COMPLEX_CREATURE ? "East" : '东');
                break;
            case NORTH:
                output.append(code == RenderCode.COMPLEX_CREATURE ? "North" : '北');
                break;
            case SOUTH:
                output.append(code == RenderCode.COMPLEX_CREATURE ? "South" : '南');
                break;
            case WEST:
                output.append(code == RenderCode.COMPLEX_CREATURE ? "West" : '西');
                break;
        }

        output.append(' ');

        for (int i = 0; i < this._memoryUnits.length; i++)
        {
            output.append(code == RenderCode.COMPLEX_CREATURE ? "\nMemory: " + i : "");
            output.append(this._memoryUnits[i]);
        }

        output.append(code == RenderCode.COMPLEX_CREATURE ? "\nAction: " : " ");

        output.append(this._action);

        return (output.toString());
    }

    public int getID()
    {
        return (this._id);
    }

    public CreatureBlueprint getBlueprint()
    {
        return this._creatureBlueprint;
    }

    public double getGeneration()
    {
        return this._generation;
    }

    public int getAge()
    {
        return (this._age);
    }

    public int getGestation()
    {
        return (this._gestation);
    }

    public int getFood()
    {
        return (this._food);
    }

    public int getHealth()
    {
        return (this._health);
    }

    public CreatureBlueprint getCreatureBlueprint()
    {
        return _creatureBlueprint;
    }

    public void setGeneration(double generation)
    {
        this._generation = generation;
    }

    public void setRandomSeed(double randomSeed)
    {
        this._randomSeed = randomSeed;
    }

    public void setAge(int age)
    {
        this._age = age;
    }

    public void setGestation(int gestation)
    {
        this._gestation = gestation;
    }

    public void setFood(int food)
    {
        this._food = food;
    }

    public void setFacing(Direction facing)
    {
        this._facing = facing;
    }

    public void setFetus(Creature fetus)
    {
        this._fetus = fetus;
    }

    /*
     * Returns true if the creature has already acted during this simulation tick.
     */
    public boolean hasActed()
    {
        return this._hasActed;
    }

    public boolean wasSuccessful()
    {
        return this._wasSuccessful;
    }

    public void setID(int id)
    {
        this._id = id;
    }

    @Override
    public void finalizeSerialization()
    {
        // Do nothing
    }
}
