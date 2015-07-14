package com.johnuckele.vivarium.core;

import java.io.Serializable;

import com.johnuckele.vivarium.core.brain.NeuralNetworkBrain;
import com.johnuckele.vivarium.util.Functions;
import com.johnuckele.vivarium.util.Rand;
import com.johnuckele.vivarium.visualization.RenderCode;

public class Creature implements Serializable
{
    /**
     * serialVersion
     */
    private static final long  serialVersionUID   = 4L;

    private static final int   BREEDING_FOOD_RATE = -10;
    private static final int   EATING_FOOD_RATE   = 500;
    private static final int   MOVING_FOOD_RATE   = -1;
    private static final int   BASE_FOOD_RATE     = -1;
    private static final int   PREGNANT_FOOD_RATE = -2;

    // Meta information
    private int                _id;
    private Species            _species;
    private World              _world;
    private int                _r;
    private int                _c;
    private double             _generation;

    // Brain
    private NeuralNetworkBrain _brain;
    private double[]           _inputs;
    private double[]           _memoryUnits;
    private double[]           _soundInputs;
    private double[]           _soundOutputs;

    // State
    private Gender             _gender;
    private double             _randomSeed;
    private int                _age;
    private int                _gestation;
    private int                _food;
    private Direction          _facing;
    private Action             _action;

    // Fetus
    private Creature           _fetus;

    // Action history
    private ActionProfile      _actionProfile;
    private ActionProfile      _generationGenderActionProfile;

    public Creature(World world, Species species, int r, int c)
    {
        this(null, null, world, species, world.requestNewCreatureID(), r, c);
    }

    public Creature(World world, Species species, int id, int r, int c)
    {
        this(null, null, world, species, id, r, c);
    }

    public Creature(Creature parent1, Creature parent2, World world)
    {
        this(parent1, parent2, world, parent1._species, world.requestNewCreatureID(), 0, 0);
    }

    public Creature(Creature parent1, Creature parent2, World world, Species species, int id, int r, int c)
    {
        this._world = world;
        this._species = species;
        this._id = id;
        this._r = r;
        this._c = c;

        if (parent1 != null)
        {
            this._generation = Functions.logarithmicAverage(parent1._generation, parent2._generation) + 1;
        }
        else
        {
            this._generation = 1;
        }

        // Create brain to control the Creature
        int memoryUnitCount = _species.getMemoryUnitCount();
        int soundChannelCount = _species.getSoundChannelCount();
        int totalBrainInputs = _species.getBrainInputs() + memoryUnitCount + soundChannelCount;
        int totalBrainOutputs = _species.getBrainOutputs() + memoryUnitCount + soundChannelCount;

        if (parent1 != null && parent2 != null)
        {
            // Brain combined from genetic legacy
            this._brain = new NeuralNetworkBrain(_species, parent1._brain, parent2._brain);
        }
        else if (parent1 == null && parent1 == null)
        {
            // Create a new default brain
            this._brain = new NeuralNetworkBrain(totalBrainInputs, totalBrainOutputs, 0);
        }
        else
        {
            throw new Error("Creature with single parent");
        }
        _inputs = new double[totalBrainInputs];
        _memoryUnits = new double[memoryUnitCount];
        _soundInputs = new double[soundChannelCount];
        _soundOutputs = new double[soundChannelCount];

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
        this._randomSeed = Rand.getRandomDouble();

        // Set defaults
        this._age = 0;
        this._gestation = 0;
        this._food = _species.getMaximumFood();
        this._facing = Direction.NORTH;
        this._action = Action.REST;

        if (this._world.getWorldVariables().getKeepGenerationActionProfile())
        {
            this._actionProfile = new ActionProfile();
            this._generationGenderActionProfile = this._world.getActionProfileForGeneration((int) this._generation,
                    this._gender);
        }
    }

    public void tick()
    {
        // Age
        this._age++;

        // Gestate if preggars
        if (this._gestation > 0)
        {
            this._gestation++;
            this._food += Creature.PREGNANT_FOOD_RATE;
        }
        else
        {
            this._food += Creature.BASE_FOOD_RATE;
        }

    }

    public Action getAction()
    {
        return (_action);
    }

    public void listenToCreature(Creature u)
    {
        int distanceSquared = (this._r - u._r) * (this._r - u._r) + (this._c - u._c) * (this._c - u._c);
        for (int i = 0; i < u._soundOutputs.length; i++)
        {
            this._soundInputs[i] += u._soundOutputs[i] / distanceSquared;
        }
    }

    public void planAction()
    {
        _action = determineAction();
    }

    private Action determineAction()
    {
        Action involuntaryAction = getInvoluntaryAction();
        if (involuntaryAction != null)
        {
            return (involuntaryAction);
        }
        else
        {
            // Hard coded inputs
            int facingR = this._r + Direction.getVerticalComponent(this._facing);
            int facingC = this._c + Direction.getHorizontalComponent(this._facing);
            WorldObject facingObject = this._world.getWorldObject(facingR, facingC);
            _inputs[0] = this._gender == Gender.FEMALE ? 1 : 0;
            _inputs[1] = facingObject == WorldObject.FOOD ? 1 : 0;
            _inputs[2] = facingObject == WorldObject.CREATURE ? 1 : 0;
            _inputs[3] = facingObject == WorldObject.WALL ? 1 : 0;
            _inputs[4] = ((double) this._food) / _species.getMaximumFood();
            // Read memory units
            for (int i = 0; i < this._memoryUnits.length; i++)
            {
                _inputs[_species.getBrainInputs() - 1 + i] = _memoryUnits[i];
            }
            // Read sound inputs
            for (int i = 0; i < this._soundInputs.length; i++)
            {
                _inputs[_species.getBrainInputs() - 1 + this._memoryUnits.length + i] = _soundInputs[i];
            }
            // Main brain computation
            double[] outputs = this._brain.outputs(_inputs);
            // Save memory units
            for (int i = 0; i < this._memoryUnits.length; i++)
            {
                _memoryUnits[i] = outputs[_species.getBrainOutputs() + i - 1];
            }
            // Clear the sound inputs and set the sound outputs
            for (int i = 0; i < this._soundInputs.length; i++)
            {
                this._soundInputs[i] = 0;
                this._soundOutputs[i] = outputs[_species.getBrainOutputs() - 1 + this._memoryUnits.length + i];
            }
            // Hard coded outputs (actionable outputs)
            int maxActionOutput = 0;
            for (int i = 1; i < _species.getBrainOutputs(); i++)
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

    public void setPosition(int r, int c)
    {
        this._r = r;
        this._c = c;
    }

    public int getR()
    {
        return (this._r);
    }

    public int getC()
    {
        return (this._c);
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

    public NeuralNetworkBrain getBrain()
    {
        return (_brain);
    }

    public void executeAction(Action action)
    {
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
                this._food += Creature.BREEDING_FOOD_RATE;
                break;
            case MOVE:
                this._food += Creature.MOVING_FOOD_RATE;
                this._r += Direction.getVerticalComponent(this._facing);
                this._c += Direction.getHorizontalComponent(this._facing);
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
                this._food += Creature.EATING_FOOD_RATE;
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
        if (this._world.getWorldVariables().getKeepGenerationActionProfile())
        {
            this._actionProfile.recordAction(action, true);
            this._generationGenderActionProfile.recordAction(action, true);
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
        if (this._world.getWorldVariables().getKeepGenerationActionProfile())
        {
            this._actionProfile.recordAction(action, false);
            this._generationGenderActionProfile.recordAction(action, false);
        }
    }

    private Creature createOffspringWith(Creature breedingTarget)
    {
        return new Creature(this, breedingTarget, _world);
    }

    public String toString(RenderCode code)
    {
        if (code == RenderCode.COMPLEX_CREATURE || code == RenderCode.SIMPLE_CREATURE)
        {
            return this.renderSelf(code);
        }
        else if (code == RenderCode.BRAIN_WEIGHTS)
        {
            return (this._brain.toString());
        }
        else
        {
            throw new Error("Invalid Code " + code);
        }
    }

    private String renderSelf(RenderCode code)
    {
        StringBuilder output = new StringBuilder();
        output.append(code == RenderCode.COMPLEX_CREATURE ? "Creature ID: " : "U_");
        output.append(this._id);
        output.append(code == RenderCode.COMPLEX_CREATURE ? "\nCoordinates: (" : " @(");
        output.append(this._r);
        output.append(',');
        output.append(this._c);
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

    public void setBrain(NeuralNetworkBrain brain)
    {
        this._brain = brain;
    }

    public ActionProfile getActionHistory()
    {
        if (this._world.getWorldVariables().getKeepGenerationActionProfile())
        {
            return _actionProfile;
        }
        else
        {
            throw new Error("Action Profiles are not supported in this world");
        }
    }

    public void setActionHistory(ActionProfile _actionHistory)
    {
        this._actionProfile = _actionHistory;
    }

    public void disconnectFromWorld()
    {
        this._world = null;
        this._generationGenderActionProfile = null;
    }

    public void connectToWorld(World w)
    {
        this._world = w;
        if (this._world.getWorldVariables().getKeepGenerationActionProfile())
        {
            if (this._actionProfile == null)
            {
                this._actionProfile = new ActionProfile();
            }
            this._generationGenderActionProfile = this._world.getActionProfileForGeneration((int) this._generation,
                    this._gender);
        }
    }
}
