package io.vivarium.visualizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.common.collect.Lists;

import io.vivarium.core.Action;
import io.vivarium.core.Creature;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.DynamicBalancer;
import io.vivarium.core.GridWorld;
import io.vivarium.core.GridWorldBlueprint;
import io.vivarium.core.ItemType;
import io.vivarium.core.TerrainType;
import io.vivarium.core.processor.NeuralNetworkBlueprint;
import io.vivarium.core.processor.Processor;

public class Vivarium extends ApplicationAdapter implements InputProcessor
{
    private static final int SIZE = 30;
    private static final int RENDER_BLOCK_SIZE = 32;
    private static final int SOURCE_BLOCK_SIZE = 32;

    // Simulation information
    private GridWorldBlueprint _gridWorldBlueprint;
    private GridWorld _gridWorld;

    // Simulation + Animation
    private int framesSinceTick = 0;
    private boolean _enableInterpolation = false;
    private Map<Integer, CreatureDelegate> _animationCreatureDelegates = new HashMap<>();
    private Creature _selectedCreature;

    // Low Level Graphics information
    private SpriteBatch _batch;
    private Texture _img;

    // Graphical settings
    private CreatureRenderMode _creatureRenderMode = CreatureRenderMode.GENDER;

    private enum CreatureRenderMode
    {
        GENDER, HEALTH, HUNGER, AGE, MEMORY, SIGN
    }

    private int _ticks = 1;
    private int _overFrames = 1;
    private MouseClickMode _mouseClickMode = MouseClickMode.SELECT_CREATURE;

    private enum MouseClickMode
    {
        SELECT_CREATURE(false),
        ADD_FLAMETHROWER(true),
        ADD_FOODGENERATOR(true),
        ADD_WALL(true),
        ADD_WALL_BRUTALLY(true),
        REMOVE_TERRAIN(true),
        REMOVE_ANYTHING(true);

        private final boolean _isPaintbrushMode;

        MouseClickMode(boolean isPaintbrushMode)
        {
            _isPaintbrushMode = isPaintbrushMode;
        }

        public boolean isPaintbrushMode()
        {
            return _isPaintbrushMode;
        }
    }

    // High Level Graphics information
    private Stage stage;
    private Skin skin;
    private Label fpsLabel;
    private Label populationLabel;
    private Label generationLabel;
    private Label foodSupplyLabel;
    private Label foodSpawnRateLabel;
    private Label breedingCostLabel;
    private Label creatureIdLabel;
    private Label creatureAgeLabel;
    private Label creatureFoodLabel;
    private Label creatureGestationLabel;
    private Label creatureHealthLabel;

    // Input tracking
    private int _xDownWorld = -1;
    private int _yDownWorld = -1;

    public Vivarium()
    {
    }

    @Override
    public void create()
    {
        // Create simulation
        _gridWorldBlueprint = GridWorldBlueprint.makeDefault();
        CreatureBlueprint creatureBlueprint = CreatureBlueprint.makeDefault(0, 0, 0);
        NeuralNetworkBlueprint nnBlueprint = (NeuralNetworkBlueprint) creatureBlueprint.getProcessorBlueprints()[0];
        // nnBlueprint.setRandomInitializationProportion(1);
        nnBlueprint.setMutationRateExponent(-6);
        nnBlueprint.setNormalizeAfterMutation(0);
        _gridWorldBlueprint.setCreatureBlueprints(Lists.newArrayList(creatureBlueprint));
        // _blueprint.setSignEnabled(true);
        _gridWorldBlueprint.setSize(SIZE);
        _gridWorldBlueprint.setInitialWallGenerationProbability(0);
        _gridWorld = new GridWorld(_gridWorldBlueprint);
        _gridWorld.setDynamicBalancer(DynamicBalancer.makeDefault());

        // Start with selected creature
        LinkedList<Creature> creatures = _gridWorld.getCreatures();
        if (creatures.size() > 41)
        {
            _selectedCreature = creatures.get(41);
        }

        // Setup Input Listeners
        Gdx.input.setInputProcessor(this);

        // Low level grahpics
        _batch = new SpriteBatch();
        _img = new Texture("sprites.png");

        buildSidebarUI();
    }

    private void buildSidebarUI()
    {
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        // stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, new PolygonSpriteBatch());
        stage = new Stage(new ScreenViewport());
        // Gdx.input.setInputProcessor(stage);

        // Simulation Speed
        final Label ticksLabel = new Label("Ticks", skin);
        TextField framesPerTickTextInput = new TextField("", skin);
        framesPerTickTextInput.setMessageText("1");
        framesPerTickTextInput.setAlignment(Align.center);
        final Label perLabel = new Label("per", skin);
        final Label framesLabel = new Label("Frames", skin);
        TextField perFramesTextInput = new TextField("", skin);
        perFramesTextInput.setMessageText("1");
        perFramesTextInput.setAlignment(Align.center);

        // Food Spawn Rate
        final Label foodSpawnLabel = new Label("Food Spawn", skin);
        TextField foodSpawnTextInput = new TextField("", skin);
        foodSpawnTextInput.setMessageText("" + _gridWorldBlueprint.getFoodGenerationProbability());
        foodSpawnTextInput.setAlignment(Align.center);

        // Click Mode
        final Label clickModeLabel = new Label("Click Mode: ", skin);
        final SelectBox<String> clickModeSelectBox = new SelectBox<>(skin);
        clickModeSelectBox.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                _mouseClickMode = MouseClickMode.valueOf(clickModeSelectBox.getSelected());
                _xDownWorld = -1;
                _yDownWorld = -1;
            }
        });
        String[] clickModeStrings = new String[MouseClickMode.values().length];
        for (int i = 0; i < MouseClickMode.values().length; i++)
        {
            clickModeStrings[i] = MouseClickMode.values()[i].toString();
        }
        clickModeSelectBox.setItems(clickModeStrings);
        clickModeSelectBox.setSelected(_mouseClickMode.toString());

        // Render Mode
        final Label renderModeLabel = new Label("Render Mode: ", skin);
        final SelectBox<String> renderModeSelectBox = new SelectBox<>(skin);
        renderModeSelectBox.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                _creatureRenderMode = CreatureRenderMode.valueOf(renderModeSelectBox.getSelected());
            }
        });
        String[] creatureRenderModeStrings = new String[CreatureRenderMode.values().length];
        for (int i = 0; i < CreatureRenderMode.values().length; i++)
        {
            creatureRenderModeStrings[i] = CreatureRenderMode.values()[i].toString();
        }
        renderModeSelectBox.setItems(creatureRenderModeStrings);
        renderModeSelectBox.setSelected(_creatureRenderMode.toString());

        // FPS Display
        fpsLabel = new Label("fps:", skin);

        // World Stats
        populationLabel = new Label("population:", skin);
        generationLabel = new Label("generation:", skin);
        foodSupplyLabel = new Label("food:", skin);
        foodSpawnRateLabel = new Label("food spawn rate:", skin);
        breedingCostLabel = new Label("breeding cost:", skin);

        // Layout
        Table worldTable = new Table();
        worldTable.setPosition(200, getHeight() - 150);
        worldTable.add(renderModeLabel).colspan(2);
        worldTable.add(renderModeSelectBox).maxWidth(100);
        worldTable.row();
        worldTable.add(clickModeLabel).colspan(2);
        worldTable.add(clickModeSelectBox).maxWidth(100);
        worldTable.row();
        worldTable.add();
        worldTable.add(framesPerTickTextInput);
        worldTable.add(ticksLabel);
        worldTable.row();
        worldTable.add(perLabel);
        worldTable.add(perFramesTextInput);
        worldTable.add(framesLabel);
        worldTable.row();
        worldTable.add(foodSpawnLabel);
        worldTable.add(foodSpawnTextInput);
        worldTable.row();
        worldTable.add(fpsLabel).colspan(4);
        worldTable.row();
        worldTable.add(populationLabel).colspan(4);
        worldTable.row();
        worldTable.add(generationLabel).colspan(4);
        worldTable.row();
        worldTable.add(foodSupplyLabel).colspan(4);
        worldTable.row();
        worldTable.add(foodSpawnRateLabel).colspan(4);
        worldTable.row();
        worldTable.add(breedingCostLabel).colspan(4);
        stage.addActor(worldTable);

        // Creature Stats
        creatureIdLabel = new Label("creature id:", skin);
        creatureAgeLabel = new Label("age:", skin);
        creatureFoodLabel = new Label("food:", skin);
        creatureGestationLabel = new Label("gestation:", skin);
        creatureHealthLabel = new Label("health:", skin);

        Table creatureTable = new Table();
        creatureTable.setPosition(200, getHeight() - 500);
        creatureTable.add(creatureIdLabel).colspan(4);
        creatureTable.row();
        creatureTable.add(creatureAgeLabel).colspan(4);
        creatureTable.row();
        creatureTable.add(creatureFoodLabel).colspan(4);
        creatureTable.row();
        creatureTable.add(creatureGestationLabel).colspan(4);
        creatureTable.row();
        creatureTable.add(creatureHealthLabel).colspan(4);
        stage.addActor(creatureTable);

        framesPerTickTextInput.setTextFieldListener(new TextFieldListener()
        {
            @Override
            public void keyTyped(TextField textField, char key)
            {
                if (key == '\n')
                {
                    textField.getOnscreenKeyboard().show(false);
                }
                try
                {
                    _ticks = Integer.parseInt(textField.getText().trim());
                }
                catch (Exception e)
                {
                    _ticks = 1;
                }
                _ticks = Math.max(_ticks, 1);
                _ticks = Math.min(_ticks, 1_000);
                _enableInterpolation = _ticks == 1 && _overFrames > 1;
            }
        });
        perFramesTextInput.setTextFieldListener(new TextFieldListener()
        {
            @Override
            public void keyTyped(TextField textField, char key)
            {
                if (key == '\n')
                {
                    textField.getOnscreenKeyboard().show(false);
                }
                try
                {
                    _overFrames = Integer.parseInt(textField.getText().trim());
                }
                catch (Exception e)
                {
                    _overFrames = 1;
                }
                _overFrames = Math.max(_overFrames, 1);
                _overFrames = Math.min(_overFrames, 600);
                _enableInterpolation = _ticks == 1 && _overFrames > 1;
            }
        });
        foodSpawnTextInput.setTextFieldListener(new TextFieldListener()
        {
            @Override
            public void keyTyped(TextField textField, char key)
            {
                if (key == '\n')
                {
                    textField.getOnscreenKeyboard().show(false);
                }
                try
                {
                    _gridWorld.getGridWorldBlueprint().setFoodGenerationProbability(
                            Double.parseDouble(textField.getText().trim()));
                }
                catch (Exception e)
                {
                }
            }
        });
    }

    @Override
    public void render()
    {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _batch.begin();

        _batch.setColor(Color.WHITE);
        float interpolationFraction = _enableInterpolation ? (float) framesSinceTick / _overFrames : 1;
        drawTerrain(interpolationFraction);
        drawFood();
        drawCreatures(interpolationFraction);

        _batch.end();

        setLabels();
        drawCreatureMultiplexer();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        framesSinceTick++;
        if (framesSinceTick >= _overFrames)
        {
            for (int i = 0; i < _ticks; i++)
            {
                _gridWorld.tick();
                updateCreatureDelegates();
            }
            framesSinceTick = 0;
        }

        mouseBrush();
    }

    private void drawCreatureMultiplexer()
    {
        if (_selectedCreature != null)
        {
            ShapeRenderer sr = new ShapeRenderer();
            sr.begin(ShapeType.Filled);
            for (int j = 0; j < _selectedCreature.getInputs().length; j++)
            {
                drawMultiplexerCell(sr, 0, j, (float) _selectedCreature.getInputs()[j]);
            }
            for (int i = 0; i < _selectedCreature.getProcessors().length; i++)
            {
                Processor p = _selectedCreature.getProcessors()[i];
                for (int j = 0; j < p.outputs().length; j++)
                {
                    drawMultiplexerCell(sr, i + 1, j, (float) p.outputs()[j]);
                }
            }
            sr.end();
        }
    }

    private void drawMultiplexerCell(ShapeRenderer sr, int x, int y, float color)
    {
        sr.setColor(Color.WHITE);
        sr.rect(50 + x * 10, 500 - y * 10, 9, 9);
        sr.setColor(color, color, color, 1);
        sr.rect(51 + x * 10, 501 - y * 10, 7, 7);
    }

    private void mouseBrush()
    {
        if (this._xDownWorld > -1 && this._yDownWorld > -1)
        {
            if (this._mouseClickMode.isPaintbrushMode())
            {
                applyMouseBrush(_xDownWorld, _yDownWorld);
            }
        }
    }

    private void removeTerrain(int r, int c)
    {
        _gridWorld.setTerrain(null, r, c);
    }

    private void applyMouseBrush(int x, int y)
    {
        Creature creature;

        // Check bounds, don't let the user change terrain near the edge of the world
        if (x < 1 || x >= _gridWorld.getWidth() - 1 || y < 1 || y >= _gridWorld.getHeight() - 1)
        {
            return;
        }
        // Apply brush modes
        switch (this._mouseClickMode)
        {
            case ADD_WALL:
                if (_gridWorld.squareIsEmpty(y, x))
                {
                    _gridWorld.setTerrain(TerrainType.WALL, y, x);
                }
                break;
            case ADD_WALL_BRUTALLY:
                creature = _gridWorld.getCreature(y, x);
                if (creature != null)
                {
                    this._animationCreatureDelegates.remove(creature.getID());
                    _gridWorld.removeCreature(y, x);
                }
                _gridWorld.removeFood(y, x);
                _gridWorld.setTerrain(TerrainType.WALL, y, x);
                break;
            case REMOVE_TERRAIN:
                removeTerrain(y, x);
                break;
            case REMOVE_ANYTHING:
                creature = _gridWorld.getCreature(y, x);
                if (creature != null)
                {
                    this._animationCreatureDelegates.remove(creature.getID());
                    _gridWorld.removeCreature(y, x);
                }
                _gridWorld.removeFood(y, x);
                _gridWorld.setTerrain(null, y, x);
                break;
            case ADD_FLAMETHROWER:
                if (_gridWorld.squareIsEmpty(y, x))
                {
                    _gridWorld.setTerrain(TerrainType.FLAMETHROWER, y, x);
                }
                break;
            case ADD_FOODGENERATOR:
                if (_gridWorld.squareIsEmpty(y, x))
                {
                    _gridWorld.setTerrain(TerrainType.FOOD_GENERATOR, y, x);
                }
                break;
            case SELECT_CREATURE:
                throw new IllegalStateException("" + MouseClickMode.SELECT_CREATURE + " is not a brush mode");
        }
    }

    private void setLabels()
    {
        fpsLabel.setText("fps: " + Gdx.graphics.getFramesPerSecond());
        populationLabel.setText("population: " + _gridWorld.getCreatureCount());
        LinkedList<Creature> creatures = _gridWorld.getCreatures();
        double generation = 0;
        for (Creature creature : creatures)
        {
            generation += creature.getGeneration();
        }
        generation /= creatures.size();
        generationLabel.setText("generation: " + ((int) (generation * 100) / 100.0));
        foodSupplyLabel.setText("food: " + _gridWorld.getItemCount());
        foodSpawnRateLabel.setText("food spawn rate: " + _gridWorld.getGridWorldBlueprint().getFoodGenerationProbability());
        breedingCostLabel.setText("breeding cost: "
                + (-1 * _gridWorld.getGridWorldBlueprint().getCreatureBlueprints().get(0).getBreedingFoodRate()));

        if (_selectedCreature != null)
        {
            creatureIdLabel.setText("creature id: " + _selectedCreature.getID());
            creatureAgeLabel.setText("age: " + _selectedCreature.getAge());
            creatureFoodLabel.setText("food: " + _selectedCreature.getFood());
            creatureGestationLabel.setText("gestation: " + _selectedCreature.getGestation());
            creatureHealthLabel.setText("health: " + _selectedCreature.getHealth());
        }
    }

    private void drawSprite(VivariumSprite sprite, float xPos, float yPos, float angle)
    {
        drawSprite(sprite, xPos, yPos, angle, 1);
    }

    private void drawSprite(VivariumSprite sprite, float xPos, float yPos, float angle, float scale)
    {
        float x = SIZE / 2 * RENDER_BLOCK_SIZE + xPos * RENDER_BLOCK_SIZE;
        float y = getHeight() - yPos * RENDER_BLOCK_SIZE - RENDER_BLOCK_SIZE;
        float originX = RENDER_BLOCK_SIZE / 2;
        float originY = RENDER_BLOCK_SIZE / 2;
        float width = RENDER_BLOCK_SIZE;
        float height = RENDER_BLOCK_SIZE;
        float rotation = angle; // In degrees
        int srcX = sprite.x * SOURCE_BLOCK_SIZE;
        int srcY = sprite.y * SOURCE_BLOCK_SIZE;
        int srcW = SOURCE_BLOCK_SIZE;
        int srcH = SOURCE_BLOCK_SIZE;
        boolean flipX = false;
        boolean flipY = false;
        _batch.draw(_img, x, y, originX, originY, width, height, scale, scale, rotation, srcX, srcY, srcW, srcH, flipX,
                flipY);
    }

    private void drawTerrain(float interpolationFraction)
    {
        for (int c = 0; c < _gridWorld.getWorldWidth(); c++)
        {
            for (int r = 0; r < _gridWorld.getWorldHeight(); r++)
            {
                if (_gridWorld.getTerrain(r, c) == TerrainType.WALL)
                {
                    drawSprite(VivariumSprite.WALL, c, r, 0);
                }
                if (_gridWorld.getTerrain(r, c) == TerrainType.FOOD_GENERATOR)
                {
                    drawSprite(VivariumSprite.FOOD_GENERATOR_ACTIVE, c, r, 0);
                }
                if (_gridWorld.getTerrain(r, c) == TerrainType.FLAMETHROWER)
                {
                    drawSprite(VivariumSprite.FLAMETHROWER_ACTIVE, c, r, 0);
                }
                if (_gridWorld.getTerrain(r, c) == TerrainType.FLAME)
                {
                    if (interpolationFraction < 1f / 3f)
                    {
                        drawSprite(VivariumSprite.FLAME_1, c, r, 0);
                    }
                    else if (interpolationFraction < 2f / 3f)
                    {
                        drawSprite(VivariumSprite.FLAME_2, c, r, 0);
                    }
                    else
                    {
                        drawSprite(VivariumSprite.FLAME_3, c, r, 0);
                    }
                }
            }
        }
    }

    private void drawFood()
    {
        for (int c = 0; c < _gridWorld.getWorldWidth(); c++)
        {
            for (int r = 0; r < _gridWorld.getWorldHeight(); r++)
            {
                if (_gridWorld.getItem(r, c) == ItemType.FOOD)
                {
                    drawSprite(VivariumSprite.FOOD, c, r, 0);
                }
            }
        }
    }

    private void drawCreatures(float interpolationFraction)
    {
        for (CreatureDelegate delegate : _animationCreatureDelegates.values())
        {
            drawCreature(delegate, interpolationFraction);
        }
    }

    private void drawCreature(CreatureDelegate delegate, float interpolationFraction)
    {
        Creature creature = delegate.getCreature();
        switch (_creatureRenderMode)
        {
            case GENDER:
                setColorOnGenderAndPregnancy(delegate, interpolationFraction);
                break;
            case HEALTH:
                setColorOnHealth(creature);
                break;
            case AGE:
                setColorOnAge(creature);
                break;
            case HUNGER:
                setColorOnFood(creature);
                break;
            case MEMORY:
                setColorOnMemory(creature);
                break;
            case SIGN:
                setColorOnSignLanguage(creature);
                break;
        }
        float scale = delegate.getScale(interpolationFraction);
        VivariumSprite creatureSprite = getCreatureSpriteFrame(interpolationFraction, creature);
        drawSprite(creatureSprite, delegate.getC(interpolationFraction), delegate.getR(interpolationFraction),
                delegate.getRotation(interpolationFraction), scale);
        if (creature == _selectedCreature)
        {
            _batch.setColor(Color.WHITE);
            VivariumSprite creatureHaloSprite = getCreatureHaloSpriteFrame(interpolationFraction, creature);
            drawSprite(creatureHaloSprite, delegate.getC(interpolationFraction), delegate.getR(interpolationFraction),
                    delegate.getRotation(interpolationFraction), scale);
        }
    }

    private void updateCreatureDelegates()
    {
        // Find creatures that are dying and mark them as such. These creatures will no longer
        // be present in the world, but it's expensive to check this. Fortunately, a creature
        // will use the die action as its last act in the world. Any existing delegate
        // with a creature that has used dye either needs to animate its death, or has
        // already done so.
        Set<Integer> creatureIDsToRemove = new HashSet<>();
        for (Entry<Integer, CreatureDelegate> delegatePair : _animationCreatureDelegates.entrySet())
        {
            if (delegatePair.getValue().isDying())
            {
                creatureIDsToRemove.add(delegatePair.getKey());
            }
            else if (delegatePair.getValue().getCreature().getAction() == Action.DIE)
            {
                delegatePair.getValue().die();
            }
        }
        // Remove creatures that have finished dying from the animation delegates.
        for (Integer creatureID : creatureIDsToRemove)
        {
            _animationCreatureDelegates.remove(creatureID);
        }
        // For all other creatures, update their animation delegate, or add a new one if they
        // don't have an animation delegate yet.
        for (int c = 0; c < _gridWorld.getWorldWidth(); c++)
        {
            for (int r = 0; r < _gridWorld.getWorldHeight(); r++)
            {
                Creature creature = _gridWorld.getCreature(r, c);
                if (creature != null)
                {
                    if (_animationCreatureDelegates.containsKey(creature.getID()))
                    {
                        _animationCreatureDelegates.get(creature.getID()).updateSnapshot(r, c);
                    }
                    else
                    {
                        _animationCreatureDelegates.put(creature.getID(), new CreatureDelegate(creature, r, c));
                    }
                }
            }
        }
    }

    public void setColorOnGenderAndPregnancy(CreatureDelegate delegate, float interpolationFraction)
    {
        if (delegate.getCreature().getIsFemale())
        {
            float pregnancyFraction = delegate.getPregnancy(interpolationFraction);
            float red = (0.4f - 0) * pregnancyFraction + 0.0f;
            float green = (0 - 0.8f) * pregnancyFraction + 0.8f;
            float blue = (0.4f - 0.8f) * pregnancyFraction + 0.8f;
            _batch.setColor(new Color(red, green, blue, 1));
        }
        else
        {
            _batch.setColor(new Color(0.8f, 0, 0, 1));
        }
    }

    public void setColorOnFood(Creature creature)
    {
        float food = ((float) creature.getFood()) / creature.getBlueprint().getMaximumFood();
        _batch.setColor(new Color(1, food, food, 1));
    }

    public void setColorOnHealth(Creature creature)
    {
        float health = ((float) creature.getHealth()) / creature.getBlueprint().getMaximumHealth();
        _batch.setColor(new Color(1, health, health, 1));
    }

    public void setColorOnAge(Creature creature)
    {
        float age = ((float) creature.getAge()) / creature.getBlueprint().getMaximumAge();
        _batch.setColor(new Color(age, 1, age, 1));
    }

    public void setColorOnMemory(Creature creature)
    {
        double[] memories = creature.getMemoryUnits();
        float[] displayMemories = { 1, 1, 1 };
        for (int i = 0; i < memories.length && i < displayMemories.length; i++)
        {
            displayMemories[i] = (float) memories[i];
        }
        _batch.setColor(new Color(displayMemories[0], displayMemories[1], displayMemories[2], 1));
    }

    public void setColorOnSignLanguage(Creature creature)
    {
        double[] signs = creature.getSignOutputs();
        float[] displaySigns = { 1, 1, 1 };
        for (int i = 0; i < signs.length && i < displaySigns.length; i++)
        {
            displaySigns[i] = (float) signs[i];
        }
        _batch.setColor(new Color(displaySigns[0], displaySigns[1], displaySigns[2], 1));
    }

    private VivariumSprite getCreatureSpriteFrame(float cycle, Creature creature)
    {
        int offset = (int) (cycle * 100 + creature.getRandomSeed() * 100) % 100;
        if (offset < 25)
        {
            return VivariumSprite.CREATURE_1;
        }
        else if (offset < 50)
        {
            return VivariumSprite.CREATURE_2;
        }
        else if (offset < 75)
        {
            return VivariumSprite.CREATURE_3;
        }
        else
        {
            return VivariumSprite.CREATURE_2;
        }
    }

    private VivariumSprite getCreatureHaloSpriteFrame(float cycle, Creature creature)
    {
        int offset = (int) (cycle * 100 + creature.getRandomSeed() * 100) % 100;
        if (offset < 25)
        {
            return VivariumSprite.HALO_CREATURE_1;
        }
        else if (offset < 50)
        {
            return VivariumSprite.HALO_CREATURE_2;
        }
        else if (offset < 75)
        {
            return VivariumSprite.HALO_CREATURE_3;
        }
        else
        {
            return VivariumSprite.HALO_CREATURE_2;
        }
    }

    public static int getHeight()
    {
        return SIZE * RENDER_BLOCK_SIZE;
    }

    public static int getWidth()
    {
        return SIZE * RENDER_BLOCK_SIZE;
    }

    @Override
    public boolean keyDown(int keycode)
    {
        stage.keyDown(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        stage.keyUp(keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        stage.keyTyped(character);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        stage.touchDown(screenX, screenY, pointer, button);

        if (screenX > SIZE / 2 * RENDER_BLOCK_SIZE)
        {
            this._xDownWorld = (screenX - SIZE / 2 * RENDER_BLOCK_SIZE) / RENDER_BLOCK_SIZE;
            this._yDownWorld = screenY / RENDER_BLOCK_SIZE;
        }
        else
        {
            this._xDownWorld = -1;
            this._yDownWorld = -1;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        stage.touchUp(screenX, screenY, pointer, button);

        if (screenX > SIZE / 2 * RENDER_BLOCK_SIZE)
        {
            int xUpWorld = (screenX - SIZE / 2 * RENDER_BLOCK_SIZE) / RENDER_BLOCK_SIZE;
            int yUpWorld = screenY / RENDER_BLOCK_SIZE;
            if (_xDownWorld == xUpWorld && _yDownWorld == yUpWorld)
            {
                if (_mouseClickMode == MouseClickMode.SELECT_CREATURE)
                {
                    if (_gridWorld.getCreature(yUpWorld, xUpWorld) != null)
                    {
                        this._selectedCreature = _gridWorld.getCreature(yUpWorld, xUpWorld);
                    }
                    this._xDownWorld = -1;
                    this._yDownWorld = -1;
                }
            }
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        stage.touchDragged(screenX, screenY, pointer);
        if (screenX > SIZE / 2 * RENDER_BLOCK_SIZE)
        {
            int xDragWorld = (screenX - SIZE / 2 * RENDER_BLOCK_SIZE) / RENDER_BLOCK_SIZE;
            int yDragWorld = screenY / RENDER_BLOCK_SIZE;
            if (this._mouseClickMode.isPaintbrushMode())
            {
                applyMouseBrush(xDragWorld, yDragWorld);
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        stage.mouseMoved(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        stage.scrolled(amount);
        return false;
    }
}
