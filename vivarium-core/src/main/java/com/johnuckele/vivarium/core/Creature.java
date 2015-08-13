package com.johnuckele.vivarium.core;

import java.util.LinkedList;
import java.util.List;

import com.johnuckele.vivarium.core.brain.Brain;
import com.johnuckele.vivarium.serialization.MapSerializer;
import com.johnuckele.vivarium.serialization.SerializationCategory;
import com.johnuckele.vivarium.serialization.SerializationEngine;
import com.johnuckele.vivarium.serialization.SerializedParameter;
import com.johnuckele.vivarium.util.Functions;
import com.johnuckele.vivarium.util.Rand;
import com.johnuckele.vivarium.visualization.RenderCode;

public class Creature implements Cloneable, Comparable<Creature>, MapSerializer
{
    // Meta information
    private int     _id;
    private Species _species;
    private double  _generation;

    // Brain
    private Brain    _brain;
    private double[] _inputs;
    private double[] _memoryUnits;
    private double[] _soundInputs;
    private double[] _soundOutputs;

    // State
    private boolean   _hasActed      = true;        // Defaults to true to prevent a newborn from acting before it
                                                    // plans
    private boolean   _wasSuccessful = true;
    private Gender    _gender;
    private double    _randomSeed;
    private int       _age;
    private int       _gestation;
    private int       _food;
    private Direction _facing;
    private Action    _action        = Action.SPAWN;

    // Fetus
    private Creature _fetus;

    private static final List<SerializedParameter> SERIALIZED_PARAMETERS = new LinkedList<SerializedParameter>();

    static
    {
        SERIALIZED_PARAMETERS.add(new SerializedParameter("id", Integer.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("species", Species.class, SerializationCategory.SPECIES));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("generation", Double.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("brain", Brain.class, SerializationCategory.BRAIN));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("inputs", double[].class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("memoryUnits", double[].class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("soundInputs", double[].class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("soundOutputs", double[].class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("hasActed", Boolean.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("wasSuccessful", Boolean.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("gender", Gender.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("randomSeed", Double.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("age", Integer.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("gestation", Integer.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("food", Integer.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("facing", Direction.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("action", Action.class));
        SERIALIZED_PARAMETERS.add(new SerializedParameter("fetus", Creature.class, SerializationCategory.CREATURE));
    }

    private Creature()
    {

    }

    public Creature(Species species)
    {
        this(species, null, null);
    }

    public Creature(Species species, Creature prototype)
    {
        this(species, prototype, null);
    }

    public Creature(Creature prototype)
    {
        this(prototype._species, prototype, null);
    }

    public Creature(Creature parent1, Creature parent2)
    {
        this(parent1._species, parent1, parent2);
    }

    public Creature(Species species, Creature parent1, Creature parent2)
    {
        this._species = species;

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

        // Create brain to control the Creature
        if (parent1 != null)
        {
            if (parent2 != null)
            {
                // Brain combined from genetic legacy
                this._brain = _species.getBrainType().makeWithParents(_species, parent1._brain, parent2._brain);
            }
            else
            {
                // Brain from single parent (might still mutate)
                this._brain = _species.getBrainType().makeWithParents(_species, parent1._brain, parent1._brain);
            }
        }
        else if (parent1 == null && parent1 == null)
        {
            // Create a new default brain
            this._brain = _species.getBrainType().makeWithSpecies(_species);
        }
        else
        {
            // Only an error if parent2 is non-null, otherwise assumed to be a cloneS
            throw new Error("Creature with single parent");
        }
        _inputs = new double[_species.getTotalBrainInputCount()];
        _memoryUnits = new double[_species.getMemoryUnitCount()];
        _soundInputs = new double[_species.getSoundChannelCount()];
        _soundOutputs = new double[_species.getSoundChannelCount()];

        // Set gender
        double randomNumber = Rand.getRandomPositiveDouble();
        if (randomNumber < _species.getFemaleThreshold())
        {
            this._gender = Gender.FEMALE;
        }
        else
        {
            this._gender = Gender.MALE;
        }

        // Set the per Creature random seed (this is
        // currently only used to set animation offsets in
        // GWT viewer)
        this._randomSeed = Rand.getRandomPositiveDouble();

        // Set defaults
        this._age = 0;
        this._gestation = 0;
        this._food = _species.getMaximumFood();
        this._facing = Direction.NORTH;
        this._action = Action.REST;

        // TODO MOVE ACTION PROFILE GENERATION
        /*
         * if (this._world.getWorldVariables().getKeepGenerationActionProfile()) { this._actionProfile = new
         * ActionProfile(); this._generationGenderActionProfile = this._world.getActionProfileForGeneration((int)
         * this._generation, this._gender); }
         */
    }

    public void tick()
    {
        // Reset action
        this._hasActed = false;
        // Age
        this._age++;

        // Gestate if preggars
        if (this._gestation > 0)
        {
            this._gestation++;
            this._food += _species.getPregnantFoodRate();
        }
        this._food += _species.getBaseFoodRate();
    }

    public Action getAction()
    {
        return (_action);
    }

    public void listenToCreature(Creature u, double distanceSquared)
    {
        int soundChannels = Math.min(this._species.getSoundChannelCount(), u._species.getSoundChannelCount());
        for (int i = 0; i < soundChannels; i++)
        {
            this._soundInputs[i] += u._soundOutputs[i] / distanceSquared;
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
            // Hard coded inputs
            int facingR = r + Direction.getVerticalComponent(this._facing);
            int facingC = c + Direction.getHorizontalComponent(this._facing);
            EntityType facingObject = w.getEntityType(facingR, facingC);
            _inputs[0] = this._gender == Gender.FEMALE ? 1 : 0;
            _inputs[1] = facingObject == EntityType.FOOD ? 1 : 0;
            _inputs[2] = facingObject == EntityType.CREATURE ? 1 : 0;
            _inputs[3] = facingObject == EntityType.WALL ? 1 : 0;
            _inputs[4] = ((double) this._food) / _species.getMaximumFood();
            // Read memory units
            for (int i = 0; i < this._memoryUnits.length; i++)
            {
                _inputs[_species.getHardBrainInputs() - 1 + i] = _memoryUnits[i];
            }
            // Read sound inputs
            for (int i = 0; i < this.getSpecies().getSoundChannelCount(); i++)
            {
                _inputs[_species.getHardBrainInputs() - 1 + this._memoryUnits.length + i] = _soundInputs[i];
            }
            // Main brain computation
            double[] outputs = this._brain.outputs(_inputs);
            // Save memory units
            for (int i = 0; i < this._memoryUnits.length; i++)
            {
                _memoryUnits[i] = outputs[_species.getHardBrainOutputs() + i - 1];
            }
            // Clear the sound inputs and set the sound outputs
            for (int i = 0; i < this.getSpecies().getSoundChannelCount(); i++)
            {
                this._soundInputs[i] = 0;
                this._soundOutputs[i] = outputs[_species.getHardBrainOutputs() - 1 + this._memoryUnits.length + i];
            }
            // Hard coded outputs (actionable outputs)
            int maxActionOutput = 0;
            for (int i = 1; i < _species.getHardBrainOutputs(); i++)
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
        if (this._age > _species.getMaximumAge() || this._food < 1)
        {
            return (Action.DIE);
        }
        // Pregnant Creatures can give birth
        else if (this._gestation > _species.getMaximumGestation())
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

    public Brain getBrain()
    {
        return (_brain);
    }

    public void executeAction(Action action)
    {
        this._hasActed = true;
        executeAction(action, null);
    }

    public void executeAction(Action action, Creature breedingTarget)
    {
        switch (action)
        {
            case BIRTH:
                this._gestation = 0;
                break;
            case BREED:
                assert breedingTarget != null;
                if (this._gender == Gender.FEMALE && this._gestation < 1)
                {
                    this._gestation = 1;
                    this._fetus = createOffspringWith(breedingTarget);
                }
                this._food += _species.getBreedingFoodRate();
                break;
            case MOVE:
                this._food += _species.getMovingFoodRate();
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
                this._food += _species.getEatingFoodRate();
                if (this._food > _species.getMaximumFood())
                {
                    this._food = _species.getMaximumFood();
                }
                break;
            default:
                System.err.println("Non-Fatal Error, unhandled action");
                new Error().printStackTrace();
                break;
        }
    }

    public void failAction(Action action)
    {
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
                this._food -= 10;
                break;
            case EAT:
                this._food -= 1;
                break;
            case MOVE:
                this._food -= 1;
                break;
            case DIE:
                break;
            case REST:
            case TURN_LEFT:
            case TURN_RIGHT:
                System.err.println("Action class " + action + " should not fail");
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

    public Species getSpecies()
    {
        return this._species;
    }

    public double getGeneration()
    {
        return (this._generation);
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

    public void setBrain(Brain brain)
    {
        this._brain = brain;
    }

    /*
     * Returns true if the creature has already acted during this simulation tick.
     */
    public boolean hasActed()
    {
        return this._hasActed;
    }

    public void setID(int id)
    {
        this._id = id;
    }

    @Override
    public int compareTo(Creature c)
    {
        return this.toString().compareTo(c.toString());
    }

    @Override
    public List<MapSerializer> getReferences()
    {
        LinkedList<MapSerializer> list = new LinkedList<MapSerializer>();
        list.add(_species);
        list.add(_brain);
        if (_fetus != null)
        {
            list.add(_fetus);
        }
        return list;
    }

    @Override
    public List<SerializedParameter> getMappedParameters()
    {
        return Creature.SERIALIZED_PARAMETERS;
    }

    @Override
    public Object getValue(String key)
    {
        switch (key)
        {
            case "id":
                return _id;
            case "species":
                return _species;
            case "generation":
                return _generation;
            case "brain":
                return _brain;
            case "inputs":
                return _inputs;
            case "memoryUnits":
                return _memoryUnits;
            case "soundInputs":
                return _soundInputs;
            case "soundOutputs":
                return _soundOutputs;
            case "hasActed":
                return _hasActed;
            case "wasSuccessful":
                return _wasSuccessful;
            case "gender":
                return _gender;
            case "randomSeed":
                return _randomSeed;
            case "age":
                return _age;
            case "gestation":
                return _gestation;
            case "food":
                return _food;
            case "facing":
                return _facing;
            case "action":
                return _action;
            case "fetus":
                return _fetus;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    @Override
    public void setValue(String key, Object value)
    {
        switch (key)
        {
            case "id":
                _id = (Integer) value;
                break;
            case "species":
                _species = (Species) value;
                break;
            case "generation":
                _generation = (Double) value;
                break;
            case "brain":
                _brain = (Brain) value;
                break;
            case "inputs":
                _inputs = (double[]) value;
                break;
            case "memoryUnits":
                _memoryUnits = (double[]) value;
                break;
            case "soundInputs":
                _soundInputs = (double[]) value;
                break;
            case "soundOutputs":
                _soundOutputs = (double[]) value;
                break;
            case "hasActed":
                _hasActed = (Boolean) value;
                break;
            case "wasSuccessful":
                _wasSuccessful = (Boolean) value;
                break;
            case "gender":
                _gender = (Gender) value;
                break;
            case "randomSeed":
                _randomSeed = (Double) value;
                break;
            case "age":
                _age = (Integer) value;
                break;
            case "gestation":
                _gestation = (Integer) value;
                break;
            case "food":
                _food = (Integer) value;
                break;
            case "facing":
                _facing = (Direction) value;
                break;
            case "action":
                _action = (Action) value;
                break;
            case "fetus":
                _fetus = (Creature) value;
                break;
            default:
                throw new UnsupportedOperationException("Key " + key + " not in mapped parameters");
        }
    }

    @Override
    public SerializationCategory getSerializationCategory()
    {
        return SerializationCategory.CREATURE;
    }

    public static Creature makeUninitialized()
    {
        return new Creature();
    }

    public static Creature makeCopy(Creature original)
    {
        return (Creature) new SerializationEngine().makeCopy(original);
    }
}
