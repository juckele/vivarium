package io.vivarium.visualizer;

import java.util.ArrayList;
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

import io.vivarium.core.Action;
import io.vivarium.core.Creature;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.ItemType;
import io.vivarium.core.TerrainType;
import io.vivarium.core.World;
import io.vivarium.core.WorldBlueprint;
import io.vivarium.core.processor.NeuralNetworkBlueprint;
import io.vivarium.serialization.VivariumObjectCopier;

public class Vivarium extends ApplicationAdapter implements InputProcessor
{
    private static final int SIZE = 30;
    private static final int BLOCK_SIZE = 32;

    // Simulation information
    private WorldBlueprint _blueprint;
    private World _world;
    private VivariumObjectCopier _copier;

    // Simulation + Animation
    private int framesSinceTick = 0;
    private boolean _enableInterpolation = false;
    private Map<Integer, CreatureDelegate> _animationCreatureDelegates = new HashMap<>();
    private int _selectedCreature = 42;

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
        SELECT_CREATURE,
        ADD_FLAMETHROWER,
        ADD_FOODGENERATOR,
        ADD_WALL,
        ADD_WALL_BRUTALLY,
        REMOVE_FLAMETHROWER,
        REMOVE_FOODGENERATOR,
        REMOVE_WALL,
        VOID_GUN;
    }

    // High Level Graphics information
    private Stage stage;
    private Skin skin;
    private Label fpsLabel;
    private Label populationLabel;
    private Label generationLabel;
    private Label foodSupplyLabel;
    private Label mouseLabel;

    // Input tracking
    private int _xDownWorld = -1;
    private int _yDownWorld = -1;

    public Vivarium(VivariumObjectCopier copier)
    {
        _copier = copier;
    }

    @Override
    public void create()
    {
        // Create simulation
        _blueprint = WorldBlueprint.makeDefault();
        ArrayList<CreatureBlueprint> creatureBlueprints = _blueprint.getCreatureBlueprints();
        for (CreatureBlueprint creatureBlueprint : creatureBlueprints)
        {
            creatureBlueprint.setCreatureMemoryUnitCount(3);
            creatureBlueprint.setCreatureSignChannelCount(3);
            ((NeuralNetworkBlueprint) (creatureBlueprint.getProcessorBlueprint())).setHiddenLayerCount(1);
        }
        _blueprint.setSignEnabled(true);
        _blueprint.setSize(SIZE);
        _world = new World(_blueprint);

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
        foodSpawnTextInput.setMessageText("" + _blueprint.getFoodGenerationProbability());
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
        populationLabel = new Label("population:", skin);
        generationLabel = new Label("generation:", skin);
        foodSupplyLabel = new Label("food:", skin);
        mouseLabel = new Label("mouse:", skin);

        // Layout
        Table table = new Table();
        table.setPosition(200, getHeight() - 150);
        table.add(renderModeLabel).colspan(2);
        table.add(renderModeSelectBox).maxWidth(100);
        table.row();
        table.add(clickModeLabel).colspan(2);
        table.add(clickModeSelectBox).maxWidth(100);
        table.row();
        table.add();
        table.add(framesPerTickTextInput);
        table.add(ticksLabel);
        table.row();
        table.add(perLabel);
        table.add(perFramesTextInput);
        table.add(framesLabel);
        table.row();
        table.add(foodSpawnLabel);
        table.add(foodSpawnTextInput);
        table.row();
        table.add(fpsLabel).colspan(4);
        table.row();
        table.add(populationLabel).colspan(4);
        table.row();
        table.add(generationLabel).colspan(4);
        table.row();
        table.add(foodSupplyLabel).colspan(4);
        table.row();
        table.add(mouseLabel).colspan(4);
        stage.addActor(table);

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
                    _world.getBlueprint().setFoodGenerationProbability(Double.parseDouble(textField.getText().trim()));
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
        drawTerrain();
        drawFood();
        drawCreatures();

        _batch.end();

        setLabels();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        framesSinceTick++;
        if (framesSinceTick >= _overFrames)
        {
            for (int i = 0; i < _ticks; i++)
            {
                _world.tick();
                updateCreatureDelegates();
            }
            framesSinceTick = 0;
        }

        mouseBrush();
    }

    private void mouseBrush()
    {
        if (this._xDownWorld > -1 && this._yDownWorld > -1)
        {
            drawSwitch(_xDownWorld, _yDownWorld);
        }
    }

    private void removeCrossTerrain(int r, int c, TerrainType type, TerrainType surrounding)
    {
        removeTerrain(r, c, type);
        removeTerrain(r + 1, c, surrounding);
        removeTerrain(r - 1, c, surrounding);
        removeTerrain(r, c + 1, surrounding);
        removeTerrain(r, c - 1, surrounding);

    }

    private void removeTerrain(int r, int c, TerrainType type)
    {
        if (_world.getTerrain(r, c) == type)
        {
            _world.setTerrain(null, r, c);
        }
    }

    private void drawSwitch(int x, int y)
    {
        int height = _world.getHeight();
        int width = _world.getWidth();
        switch (this._mouseClickMode)
        {
            case ADD_WALL:
                if (_world.squareIsEmpty(y, x))
                {
                    _world.setTerrain(TerrainType.WALL, y, x);
                }
                break;
            case ADD_WALL_BRUTALLY:
                _world.removeCreature(y, x);
                _world.removeFood(y, x);
                _world.setTerrain(TerrainType.WALL, y, x);
                break;
            case REMOVE_WALL:
                if (y > 1 && x > 1 && y < height - 2 && x < width - 2)
                {
                    removeTerrain(y, x, TerrainType.WALL);
                }
                break;
            case VOID_GUN:
                _world.removeCreature(y, x);
            case SELECT_CREATURE:
                break;
            case ADD_FLAMETHROWER:
                if (_world.squareIsFlamable(y, x))
                {
                    _world.setTerrain(TerrainType.FLAMETHROWER, y, x);
                }
                break;
            case REMOVE_FLAMETHROWER:
                removeCrossTerrain(y, x, TerrainType.FLAMETHROWER, TerrainType.FLAME);
                break;
            case ADD_FOODGENERATOR:
                if (_world.squareIsFoodable(y, x))
                {
                    _world.setTerrain(TerrainType.FOOD_GENERATOR, y, x);
                }
                break;
            case REMOVE_FOODGENERATOR:
                removeTerrain(y, x, TerrainType.FOOD_GENERATOR);
                break;
        }
    }

    private void setLabels()
    {
        fpsLabel.setText("fps: " + Gdx.graphics.getFramesPerSecond());
        populationLabel.setText("population: " + _world.getCreatureCount());
        LinkedList<Creature> creatures = _world.getCreatures();
        double generation = 0;
        for (Creature creature : creatures)
        {
            generation += creature.getGeneration();
        }
        generation /= creatures.size();
        generationLabel.setText("generation: " + ((int) (generation * 100) / 100.0));
        foodSupplyLabel.setText("food: " + _world.getItemCount());
    }

    private void drawSprite(VivariumSprite sprite, float xPos, float yPos, float angle)
    {
        drawSprite(sprite, xPos, yPos, angle, 1);
    }

    private void drawSprite(VivariumSprite sprite, float xPos, float yPos, float angle, float scale)
    {
        float x = SIZE / 2 * BLOCK_SIZE + xPos * BLOCK_SIZE;
        float y = getHeight() - yPos * BLOCK_SIZE - BLOCK_SIZE;
        float originX = BLOCK_SIZE / 2;
        float originY = BLOCK_SIZE / 2;
        float width = BLOCK_SIZE;
        float height = BLOCK_SIZE;
        float rotation = angle; // In degrees
        int srcX = sprite.x * BLOCK_SIZE;
        int srcY = sprite.y * BLOCK_SIZE;
        int srcW = BLOCK_SIZE;
        int srcH = BLOCK_SIZE;
        boolean flipX = false;
        boolean flipY = false;
        _batch.draw(_img, x, y, originX, originY, width, height, scale, scale, rotation, srcX, srcY, srcW, srcH, flipX,
                flipY);
    }

    private void drawTerrain()
    {
        for (int c = 0; c < _world.getWorldWidth(); c++)
        {
            for (int r = 0; r < _world.getWorldHeight(); r++)
            {
                if (_world.getTerrain(r, c) == TerrainType.WALL)
                {
                    drawSprite(VivariumSprite.WALL, c, r, 0);
                }
                if (_world.getTerrain(r, c) == TerrainType.FOOD_GENERATOR)
                {
                    drawSprite(VivariumSprite.FOOD_GENERATOR_ACTIVE, c, r, 0);
                }
                if (_world.getTerrain(r, c) == TerrainType.FLAMETHROWER)
                {
                    drawSprite(VivariumSprite.FLAMETHROWER_ACTIVE, c, r, 0);
                }
                if (_world.getTerrain(r, c) == TerrainType.FLAME)
                {
                    drawSprite(VivariumSprite.FLAME_1, c, r, 0);
                }
            }
        }
    }

    private void drawFood()
    {
        for (int c = 0; c < _world.getWorldWidth(); c++)
        {
            for (int r = 0; r < _world.getWorldHeight(); r++)
            {
                if (_world.getItem(r, c) == ItemType.FOOD)
                {
                    drawSprite(VivariumSprite.FOOD, c, r, 0);
                }
            }
        }
    }

    private void drawCreatures()
    {
        float interpolationFraction = _enableInterpolation ? (float) framesSinceTick / _overFrames : 1;
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
        if (creature.getID() == this._selectedCreature)
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
        for (int c = 0; c < _world.getWorldWidth(); c++)
        {
            for (int r = 0; r < _world.getWorldHeight(); r++)
            {
                Creature creature = _world.getCreature(r, c);
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
            float red = (0 - 0.4f) * pregnancyFraction + 0.4f;
            float green = (0.8f - 0) * pregnancyFraction + 0;
            float blue = (0.8f - 0.4f) * pregnancyFraction + 0.4f;
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
        return SIZE * BLOCK_SIZE;
    }

    public static int getWidth()
    {
        return SIZE * BLOCK_SIZE;
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

        if (screenX > SIZE / 2 * BLOCK_SIZE)
        {
            this._xDownWorld = (screenX - SIZE / 2 * BLOCK_SIZE) / BLOCK_SIZE;
            this._yDownWorld = screenY / BLOCK_SIZE;
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

        if (screenX > SIZE / 2 * BLOCK_SIZE)
        {
            int xUpWorld = (screenX - SIZE / 2 * BLOCK_SIZE) / BLOCK_SIZE;
            int yUpWorld = screenY / BLOCK_SIZE;
            if (_xDownWorld == xUpWorld && _yDownWorld == yUpWorld)
            {
                switch (this._mouseClickMode)
                {
                    case ADD_WALL:
                        if (_world.squareIsEmpty(_yDownWorld, _xDownWorld))
                        {
                            _world.setTerrain(TerrainType.WALL, _yDownWorld, _xDownWorld);
                        }
                        break;
                    case ADD_WALL_BRUTALLY:
                        _world.removeCreature(_yDownWorld, _xDownWorld);
                        _world.setTerrain(TerrainType.WALL, _yDownWorld, _xDownWorld);
                        break;
                    case REMOVE_WALL:
                        if (_world.getTerrain(_yDownWorld, _xDownWorld) == TerrainType.WALL)
                        {
                            _world.setTerrain(null, _yDownWorld, _xDownWorld);
                        }
                        break;
                    case VOID_GUN:
                        _world.removeCreature(_yDownWorld, _xDownWorld);
                    case SELECT_CREATURE:
                        if (_world.getCreature(yUpWorld, xUpWorld) != null)
                        {
                            this._selectedCreature = _world.getCreature(yUpWorld, xUpWorld).getID();
                        }
                        this._xDownWorld = -1;
                        this._yDownWorld = -1;
                        break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        stage.touchDragged(screenX, screenY, pointer);
        if (screenX > SIZE / 2 * BLOCK_SIZE)
        {
            int xDragWorld = (screenX - SIZE / 2 * BLOCK_SIZE) / BLOCK_SIZE;
            int yDragWorld = screenY / BLOCK_SIZE;
            drawSwitch(xDragWorld, yDragWorld);
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
