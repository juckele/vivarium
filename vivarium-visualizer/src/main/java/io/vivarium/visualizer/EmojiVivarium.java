package io.vivarium.visualizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.common.collect.Lists;

import io.vivarium.core.Action;
import io.vivarium.core.BubbleWorld;
import io.vivarium.core.BubbleWorldBlueprint;
import io.vivarium.core.Creature;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.bubble.BubblePosition;
import io.vivarium.core.processor.NeuralNetworkBlueprint;
import io.vivarium.core.processor.Processor;
import io.vivarium.visualizer.enums.CreatureRenderMode;
import io.vivarium.visualizer.enums.MouseClickMode;
import io.vivarium.visualizer.enums.SimulationSpeed;

public class EmojiVivarium extends ApplicationAdapter implements InputProcessor
{
    private static final double SIZE = 30;
    private static final int RENDER_BLOCK_SIZE = 32;
    private static final int SOURCE_BLOCK_SIZE = 512;

    // Simulation information
    private BubbleWorldBlueprint _bubbleWorldBlueprint;
    private BubbleWorld _bubbleWorld;

    // Simulation + Animation
    private int framesSinceTick = 0;
    private boolean _enableInterpolation = false;
    private Map<Integer, BubbleCreatureDelegate> _animationCreatureDelegates = new HashMap<>();
    private Creature _selectedCreature;

    // Low Level Graphics information
    private SpriteBatch _batch;
    private Texture _img;

    // Graphical settings
    private CreatureRenderMode _creatureRenderMode = CreatureRenderMode.GENDER;

    private int _ticks = 1;
    private int _overFrames = 1;
    private MouseClickMode _mouseClickMode = MouseClickMode.SELECT_CREATURE;

    // High Level Graphics information
    private Stage stage;
    private Skin skin;
    private Label fpsLabel;
    private Label populationLabel;
    private Label generationLabel;
    private Label foodSupplyLabel;
    private Label breedingCostLabel;
    private Label creatureIdLabel;
    private Label creatureAgeLabel;
    private Label creatureFoodLabel;
    private Label creatureGestationLabel;
    private Label creatureHealthLabel;

    // Input tracking
    private int _xDownWorld = -1;
    private int _yDownWorld = -1;

    public EmojiVivarium()
    {
    }

    @Override
    public void create()
    {
        // Create simulation
        _bubbleWorldBlueprint = BubbleWorldBlueprint.makeDefault();
        CreatureBlueprint creatureBlueprint = CreatureBlueprint.makeDefault(0, 0, 0);
        NeuralNetworkBlueprint nnBlueprint = (NeuralNetworkBlueprint) creatureBlueprint.getProcessorBlueprints()[0];
        // nnBlueprint.setRandomInitializationProportion(1);
        nnBlueprint.setMutationRateExponent(-6);
        nnBlueprint.setNormalizeAfterMutation(0);
        _bubbleWorldBlueprint.setCreatureBlueprints(Lists.newArrayList(creatureBlueprint));
        // _blueprint.setSignEnabled(true);
        _bubbleWorldBlueprint.setSize(SIZE);
        _bubbleWorld = new BubbleWorld(_bubbleWorldBlueprint);

        // Start with selected creature
        List<Creature> creatures = _bubbleWorld.getCreatures();
        if (creatures.size() > 41)
        {
            _selectedCreature = creatures.get(41);
        }

        // Setup Input Listeners
        Gdx.input.setInputProcessor(this);

        // Low level grahpics
        _batch = new SpriteBatch();
        _img = new Texture("emoji_sprites.png");

        buildSidebarUI();
    }

    private void buildSidebarUI()
    {
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        // stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, new PolygonSpriteBatch());
        stage = new Stage(new ScreenViewport());
        // Gdx.input.setInputProcessor(stage);

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

        // Simulation speed
        final Label simulationSpeedLabel = new Label("Simulation Speed: ", skin);
        final SelectBox<String> simulationSpeedSelectBox = new SelectBox<>(skin);
        simulationSpeedSelectBox.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                SimulationSpeed simulationSpeed = SimulationSpeed.valueOf(simulationSpeedSelectBox.getSelected());
                _ticks = simulationSpeed.getTicks();
                _overFrames = simulationSpeed.getPerFrame();
                _enableInterpolation = simulationSpeed.getEnableInterpolation();
            }
        });
        String[] simulationSpeedStrings = new String[SimulationSpeed.values().length];
        for (int i = 0; i < SimulationSpeed.values().length; i++)
        {
            simulationSpeedStrings[i] = SimulationSpeed.values()[i].toString();
        }
        simulationSpeedSelectBox.setItems(simulationSpeedStrings);
        simulationSpeedSelectBox.setSelected(SimulationSpeed.getDefault().toString());

        // FPS Display
        fpsLabel = new Label("fps:", skin);

        // World Stats
        populationLabel = new Label("population:", skin);
        generationLabel = new Label("generation:", skin);
        foodSupplyLabel = new Label("food:", skin);
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
        worldTable.add(simulationSpeedLabel).colspan(2);
        worldTable.add(simulationSpeedSelectBox).maxWidth(100);
        worldTable.row();
        worldTable.add(fpsLabel).colspan(4);
        worldTable.row();
        worldTable.add(populationLabel).colspan(4);
        worldTable.row();
        worldTable.add(generationLabel).colspan(4);
        worldTable.row();
        worldTable.add(foodSupplyLabel).colspan(4);
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
                _bubbleWorld.tick();
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

    private void applyMouseBrush(int x, int y)
    {
        // TODO: Re-implement
    }

    private void setLabels()
    {
        fpsLabel.setText("fps: " + Gdx.graphics.getFramesPerSecond());
        populationLabel.setText("population: " + _bubbleWorld.getCreatureCount());
        List<Creature> creatures = _bubbleWorld.getCreatures();
        double generation = 0;
        for (Creature creature : creatures)
        {
            generation += creature.getGeneration();
        }
        generation /= creatures.size();
        generationLabel.setText("generation: " + ((int) (generation * 100) / 100.0));
        foodSupplyLabel.setText("food: " + _bubbleWorld.getItemCount());
        breedingCostLabel.setText("breeding cost: "
                + (-1 * _bubbleWorld.getWorldBlueprint().getCreatureBlueprints().get(0).getBreedingFoodRate()));

        if (_selectedCreature != null)
        {
            creatureIdLabel.setText("creature id: " + _selectedCreature.getID());
            creatureAgeLabel.setText("age: " + _selectedCreature.getAge());
            creatureFoodLabel.setText("food: " + _selectedCreature.getFood());
            creatureGestationLabel.setText("gestation: " + _selectedCreature.getGestation());
            creatureHealthLabel.setText("health: " + _selectedCreature.getHealth());
        }
    }

    private void drawSprite(EmojiVivariumSprite sprite, float xPos, float yPos, float angle, float scale)
    {
        float x = (float) (SIZE / 2 * RENDER_BLOCK_SIZE + xPos * RENDER_BLOCK_SIZE);
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
        // TODO: THIS
    }

    private void drawFood()
    {
        // TODO: THIS
    }

    private void drawCreatures(float interpolationFraction)
    {
        for (BubbleCreatureDelegate delegate : _animationCreatureDelegates.values())
        {
            drawCreature(delegate, interpolationFraction);
        }
    }

    private void drawCreature(BubbleCreatureDelegate delegate, float interpolationFraction)
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
        drawSprite(EmojiVivariumSprite.CREATURE_1, delegate.getX(interpolationFraction),
                delegate.getY(interpolationFraction), delegate.getHeading(interpolationFraction), scale);
        if (creature == _selectedCreature)
        {
            _batch.setColor(Color.WHITE);
            drawSprite(EmojiVivariumSprite.HALO_CREATURE_1, delegate.getX(interpolationFraction),
                    delegate.getY(interpolationFraction), delegate.getHeading(interpolationFraction), scale);
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
        for (Entry<Integer, BubbleCreatureDelegate> delegatePair : _animationCreatureDelegates.entrySet())
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
        List<Creature> creatures = _bubbleWorld.getCreatures();
        List<BubblePosition> creaturePositions = _bubbleWorld.getCreaturePositions();
        for (int i = 0; i < creatures.size(); i++)
        {
            Creature creature = creatures.get(i);
            BubblePosition creaturePosition = creaturePositions.get(i);
            if (_animationCreatureDelegates.containsKey(creature.getID()))
            {
                _animationCreatureDelegates.get(creature.getID()).updateSnapshot();
            }
            else
            {
                _animationCreatureDelegates.put(creature.getID(),
                        new BubbleCreatureDelegate(creature, creaturePosition));
            }
        }
    }

    public void setColorOnGenderAndPregnancy(BubbleCreatureDelegate delegate, float interpolationFraction)
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

    public static int getHeight()
    {
        return (int) (SIZE * RENDER_BLOCK_SIZE);
    }

    public static int getWidth()
    {
        return (int) (SIZE * RENDER_BLOCK_SIZE);
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
        // TODO: THIS
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        stage.touchUp(screenX, screenY, pointer, button);
        // TODO: THIS
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        stage.touchDragged(screenX, screenY, pointer);
        // TODO: THIS
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
