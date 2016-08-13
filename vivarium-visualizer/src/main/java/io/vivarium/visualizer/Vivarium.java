package io.vivarium.visualizer;

import java.util.ArrayList;
import java.util.LinkedList;

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

import io.vivarium.core.Creature;
import io.vivarium.core.CreatureBlueprint;
import io.vivarium.core.Direction;
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
    private World _worldSnapshot1;
    private World _worldSnapshot2;
    private boolean _enableInterpolation = false;
    private int _selectedCreature = 42;

    // Low Level Graphics information
    private SpriteBatch _batch;
    private Texture _img;

    // Graphical settings
    private CreatureRenderMode _creatureRenderMode = CreatureRenderMode.GENDER;

    private enum CreatureRenderMode
    {
        GENDER, HEALTH, HUNGER, AGE, MEMORY, SIGN, FLAMEHEALTH
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
            creatureBlueprint.getProcessorBlueprint().setCreatureMemoryUnitCount(3);
            creatureBlueprint.getProcessorBlueprint().setCreatureSignChannelCount(3);
            ((NeuralNetworkBlueprint) (creatureBlueprint.getProcessorBlueprint())).setHiddenLayerCount(1);
        }
        _blueprint.setSignEnabled(true);
        _blueprint.setSize(SIZE);
        _world = new World(_blueprint);
        _worldSnapshot1 = _copier.copyObject(_world);
        _worldSnapshot2 = _copier.copyObject(_world);

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
        if (this._enableInterpolation)
        {
            drawCreatures(_worldSnapshot1, _worldSnapshot2);
        }
        else
        {
            drawCreatures(_world, _world);
        }

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
                if (_enableInterpolation)
                {
                    // TODO: Cache two ticks when transitioning to-from interpolation mode, this is going to create a
                    // weird flashback bug
                    _worldSnapshot1 = _worldSnapshot2;
                    _worldSnapshot2 = _copier.copyObject(_world);
                }
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
        float x = SIZE / 2 * BLOCK_SIZE + xPos * BLOCK_SIZE;
        float y = getHeight() - yPos * BLOCK_SIZE - BLOCK_SIZE;
        float originX = BLOCK_SIZE / 2;
        float originY = BLOCK_SIZE / 2;
        float width = BLOCK_SIZE;
        float height = BLOCK_SIZE;
        float scale = 1;
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

    private void drawCreatures(World w1, World w2)
    {
        for (int c = 0; c < w2.getWorldWidth(); c++)
        {
            for (int r = 0; r < w2.getWorldHeight(); r++)
            {
                if (w2.getCreature(r, c) != null)
                {
                    Creature creature = w2.getCreature(r, c);
                    switch (_creatureRenderMode)
                    {
                        case GENDER:
                            setColorOnGenderAndPregnancy(creature);
                            break;
                        case HEALTH:
                            setColorOnAgeAndFood(creature);
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
                        case FLAMEHEALTH:
                            setColorOnFlameHealth(creature);
                            break;
                    }
                    float interpolationFraction = (float) framesSinceTick / _overFrames;
                    int c1 = -1;
                    int r1 = -1;
                    Creature creature1 = null;
                    int c2 = c;
                    int r2 = r;
                    Creature creature2 = creature;
                    for (int dc = -1; dc <= 1; dc++)
                    {
                        for (int dr = -1; dr <= 1; dr++)
                        {
                            if (w1.getCreature(r2 + dr, c2 + dc) != null
                                    && w1.getCreature(r2 + dr, c2 + dc).getID() == creature.getID())
                            {
                                c1 = c2 + dc;
                                r1 = r2 + dr;
                                creature1 = w1.getCreature(r1, c1);
                            }
                        }
                    }
                    if (creature1 == null)
                    {
                        // Spawn animation
                        // TODO: WRITE THIS
                        float rotation = (float) (Direction.getRadiansFromNorth(creature2.getFacing()) * 180
                                / (Math.PI));
                        drawSprite(VivariumSprite.CREATURE_2, c, r, rotation);
                    }
                    else
                    {
                        float rInterpolated = (1 - interpolationFraction) * r1 + interpolationFraction * r2;
                        float cInterpolated = (1 - interpolationFraction) * c1 + interpolationFraction * c2;
                        float rotation1 = (float) (Direction.getRadiansFromNorth(creature1.getFacing()) * 180
                                / (Math.PI));
                        float rotation2 = (float) (Direction.getRadiansFromNorth(creature2.getFacing()) * 180
                                / (Math.PI));
                        if ((rotation1 == 0 && rotation2 > 180) || (rotation1 > 180 && rotation2 == 0))
                        {
                            rotation1 = rotation1 == 0 ? 360 : rotation1;
                            rotation2 = rotation2 == 0 ? 360 : rotation2;
                        }
                        float rotationInterpolated = (1 - interpolationFraction) * rotation1
                                + interpolationFraction * rotation2;
                        VivariumSprite creatureSprite = getCreatureSpriteFrame(interpolationFraction, creature2);
                        drawSprite(creatureSprite, cInterpolated, rInterpolated, rotationInterpolated);
                        if (creature2.getID() == this._selectedCreature)
                        {
                            _batch.setColor(Color.WHITE);
                            VivariumSprite creatureHaloSprite = getCreatureHaloSpriteFrame(interpolationFraction,
                                    creature2);
                            drawSprite(creatureHaloSprite, cInterpolated, rInterpolated, rotationInterpolated);
                        }
                    }
                }

            }
        }
    }

    public void setColorOnGenderAndPregnancy(Creature creature)
    {
        if (creature.getIsFemale())
        {
            if (creature.getGestation() > 0)
            {
                _batch.setColor(new Color(0.4f, 0, 0.4f, 1));
            }
            else
            {
                _batch.setColor(new Color(0, 0.8f, 0.8f, 1));
            }
        }
        else
        {
            _batch.setColor(new Color(0.8f, 0, 0, 1));
        }
    }

    public void setColorOnAgeAndFood(Creature creature)
    {
        float food = ((float) creature.getFood()) / creature.getBlueprint().getMaximumFood();
        float age = ((float) creature.getAge()) / creature.getBlueprint().getMaximumAge();
        _batch.setColor(new Color(1, food, age, 1));
    }

    public void setColorOnFood(Creature creature)
    {
        float food = ((float) creature.getFood()) / creature.getBlueprint().getMaximumFood();
        _batch.setColor(new Color(1, food, food, 1));
    }

    public void setColorOnFlameHealth(Creature creature)
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
