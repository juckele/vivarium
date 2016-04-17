package io.vivarium.visualizer;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
import io.vivarium.core.EntityType;
import io.vivarium.core.World;
import io.vivarium.core.WorldBlueprint;
import io.vivarium.serialization.SerializationEngine;

public class Vivarium extends ApplicationAdapter
{
    private static final int SIZE = 30;
    private static final int BLOCK_SIZE = 32;

    // Simulation information
    private WorldBlueprint _blueprint;
    private World _world;

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
    private int _ticks = 1;
    private int _overFrames = 1;

    private enum CreatureRenderMode
    {
        GENDER, HEALTH, HUNGER, AGE, MEMORY, SIGN
    }

    // High Level Graphics information
    private Stage stage;
    private Skin skin;
    private Label fpsLabel;

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
        }
        _blueprint.setSignEnabled(true);
        _blueprint.setSize(SIZE);
        _world = new World(_blueprint);
        _worldSnapshot1 = new SerializationEngine().makeCopy(_world);
        _worldSnapshot2 = new SerializationEngine().makeCopy(_world);

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
        Gdx.input.setInputProcessor(stage);

        // Simulation Speed
        TextField framesPerTickTextInput = new TextField("", skin);
        framesPerTickTextInput.setMessageText("1");
        framesPerTickTextInput.setAlignment(Align.center);
        TextField perFramesTextInput = new TextField("", skin);
        perFramesTextInput.setMessageText("1");
        perFramesTextInput.setAlignment(Align.center);

        // Render Mode
        final Label renderModeLabel = new Label("Render Mode: ", skin);
        final SelectBox<String> renderModeSelectBox = new SelectBox<>(skin);
        renderModeSelectBox.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                _creatureRenderMode = CreatureRenderMode.valueOf(renderModeSelectBox.getSelected());
                System.out.println();
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

        // Layout
        Table table = new Table();
        table.setPosition(200, getHeight() - 100);
        table.add(renderModeLabel).colspan(2);
        table.add(renderModeSelectBox).maxWidth(100);
        table.row();
        table.add(framesPerTickTextInput).minWidth(100).expandX().fillX().colspan(3);
        table.row();
        table.add(perFramesTextInput).minWidth(100).expandX().fillX().colspan(3);
        table.row();
        table.add(fpsLabel).colspan(4);
        stage.addActor(table);

        /*
         * Window window = new Window("Dialog", skin); window.getTitleTable().add(new TextButton("X",
         * skin)).height(window.getPadTop()); window.setPosition(0, 0); window.defaults().spaceBottom(10);
         * window.row().fill().expandX(); window.add(renderModeLabel).colspan(2);
         * window.add(renderModeSelectBox).maxWidth(100); window.row();
         * window.add(framesPerTickTextInput).minWidth(100).expandX().fillX().colspan(3); window.row();
         * window.add(perFramesTextInput).minWidth(100).expandX().fillX().colspan(3); window.row();
         * window.add(fpsLabel).colspan(4); window.pack();
         * 
         * // stage.addActor(new Button("Behind Window", skin)); stage.addActor(window);
         */

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

        fpsLabel.setText("fps: " + Gdx.graphics.getFramesPerSecond());
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
                    _worldSnapshot2 = new SerializationEngine().makeCopy(_world);
                }
            }
            framesSinceTick = 0;
        }
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
                if (_world.getEntityType(r, c) == EntityType.WALL)
                {
                    drawSprite(VivariumSprite.WALL, c, r, 0);
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
                if (_world.getEntityType(r, c) == EntityType.FOOD)
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
                if (w2.getEntityType(r, c) == EntityType.CREATURE)
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
                            if (w1.getEntityType(r2 + dr, c2 + dc) == EntityType.CREATURE
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
}
