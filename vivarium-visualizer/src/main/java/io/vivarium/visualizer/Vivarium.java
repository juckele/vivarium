package io.vivarium.visualizer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.vivarium.core.Creature;
import io.vivarium.core.Direction;
import io.vivarium.core.EntityType;
import io.vivarium.core.World;
import io.vivarium.core.WorldBlueprint;

public class Vivarium extends ApplicationAdapter
{
    private static final int SIZE = 30;
    private static final int BLOCK_SIZE = 32;

    // Simulation information
    private WorldBlueprint _blueprint;
    private World _world;
    private int tick = 0;

    // Low Level Graphics information
    private SpriteBatch _batch;
    private Texture _img;

    // Graphical settings
    private CreatureRenderMode _creatureRenderMode = CreatureRenderMode.GENDER;

    private enum CreatureRenderMode
    {
        GENDER, HEALTH
    }

    // High Level Graphics information
    private Stage stage;
    private Skin skin;
    private Texture texture1;
    private Texture texture2;
    Object[] listEntries = { "This is a list entry1", "And another one1", "The meaning of life1", "Is hard to come by1",
            "This is a list entry2", "And another one2", "The meaning of life2", "Is hard to come by2",
            "This is a list entry3", "And another one3", "The meaning of life3", "Is hard to come by3",
            "This is a list entry4", "And another one4", "The meaning of life4", "Is hard to come by4",
            "This is a list entry5", "And another one5", "The meaning of life5", "Is hard to come by5" };
    private Label fpsLabel;

    @SuppressWarnings("unchecked")
    @Override
    public void create()
    {
        // Create simulation
        _blueprint = WorldBlueprint.makeDefault();
        _blueprint.setSize(SIZE);
        _world = new World(_blueprint);

        // Low level grahpics
        _batch = new SpriteBatch();
        _img = new Texture("sprites.png");

        // High level graphics
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        texture1 = new Texture(Gdx.files.internal("sprites.png"));
        texture2 = new Texture(Gdx.files.internal("sprites.png"));
        TextureRegion image = new TextureRegion(texture1);
        TextureRegion imageFlipped = new TextureRegion(image);
        imageFlipped.flip(true, true);
        TextureRegion image2 = new TextureRegion(texture2);
        // stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, new PolygonSpriteBatch());
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Group.debug = true;

        ImageButtonStyle style = new ImageButtonStyle(skin.get(ButtonStyle.class));
        style.imageUp = new TextureRegionDrawable(image);
        style.imageDown = new TextureRegionDrawable(imageFlipped);
        ImageButton iconButton = new ImageButton(style);

        Button buttonMulti = new TextButton("Multi\nLine\nToggle", skin, "toggle");
        Button imgButton = new Button(new Image(image), skin);
        Button imgToggleButton = new Button(new Image(image), skin, "toggle");

        Label myLabel = new Label("this is some text.", skin);
        myLabel.setWrap(true);

        Table t = new Table();
        t.row();
        t.add(myLabel);

        t.layout();

        final CheckBox checkBox = new CheckBox(" Continuous rendering", skin);
        checkBox.setChecked(true);
        final Slider slider = new Slider(0, 10, 1, false, skin);
        slider.setAnimateDuration(0.3f);
        TextField textfield = new TextField("", skin);
        textfield.setMessageText("Click here!");
        textfield.setAlignment(Align.center);
        final SelectBox<String> selectBox = new SelectBox<>(skin);
        selectBox.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                _creatureRenderMode = CreatureRenderMode.valueOf(selectBox.getSelected());
                System.out.println();
            }
        });
        String[] creatureRenderModeStrings = new String[CreatureRenderMode.values().length];
        for (int i = 0; i < CreatureRenderMode.values().length; i++)
        {
            creatureRenderModeStrings[i] = CreatureRenderMode.values()[i].toString();
        }
        selectBox.setItems(creatureRenderModeStrings);
        selectBox.setSelected(_creatureRenderMode.toString());
        Image imageActor = new Image(image2);
        ScrollPane scrollPane = new ScrollPane(imageActor);
        List<Object> list = new List<>(skin);
        list.setItems(listEntries);
        list.getSelection().setMultiple(true);
        list.getSelection().setRequired(false);
        // list.getSelection().setToggle(true);
        ScrollPane scrollPane2 = new ScrollPane(list, skin);
        scrollPane2.setFlickScroll(false);
        SplitPane splitPane = new SplitPane(scrollPane, scrollPane2, false, skin, "default-horizontal");
        fpsLabel = new Label("fps:", skin);

        // configures an example of a TextField in password mode.
        final Label passwordLabel = new Label("Textfield in password mode: ", skin);
        final TextField passwordTextField = new TextField("", skin);
        passwordTextField.setMessageText("password");
        passwordTextField.setPasswordCharacter('*');
        passwordTextField.setPasswordMode(true);

        buttonMulti.addListener(new TextTooltip(
                "This is a tooltip! This is a tooltip! This is a tooltip! This is a tooltip! This is a tooltip! This is a tooltip!",
                skin));
        Table tooltipTable = new Table(skin);
        tooltipTable.pad(10).background("default-round");
        tooltipTable.add(new TextButton("Fancy tooltip!", skin));
        imgButton.addListener(new Tooltip<Table>(tooltipTable));

        // window.debug();
        Window window = new Window("Dialog", skin);
        window.getTitleTable().add(new TextButton("X", skin)).height(window.getPadTop());
        window.setPosition(0, 0);
        window.defaults().spaceBottom(10);
        window.row().fill().expandX();
        window.add(iconButton);
        window.add(buttonMulti);
        window.add(imgButton);
        window.add(imgToggleButton);
        window.row();
        window.add(checkBox);
        window.add(slider).minWidth(100).fillX().colspan(3);
        window.row();
        window.add(selectBox).maxWidth(100);
        window.add(textfield).minWidth(100).expandX().fillX().colspan(3);
        window.row();
        window.add(splitPane).fill().expand().colspan(4).maxHeight(200);
        window.row();
        window.add(passwordLabel).colspan(2);
        window.add(passwordTextField).minWidth(100).expandX().fillX().colspan(2);
        window.row();
        window.add(fpsLabel).colspan(4);
        window.pack();

        // stage.addActor(new Button("Behind Window", skin));
        stage.addActor(window);

        textfield.setTextFieldListener(new TextFieldListener()
        {
            @Override
            public void keyTyped(TextField textField, char key)
            {
                if (key == '\n')
                {
                    textField.getOnscreenKeyboard().show(false);
                }
            }
        });

        slider.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                Gdx.app.log("UITest", "slider: " + slider.getValue());
            }
        });

        iconButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                new Dialog("Some Dialog", skin, "dialog")
                {
                    @Override
                    protected void result(Object object)
                    {
                        System.out.println("Chosen: " + object);
                    }
                }
                        .text("Are you enjoying this demo?")
                        .button("Yes", true)
                        .button("No", false)
                        .key(Keys.ENTER, true)
                        .key(Keys.ESCAPE, false)
                        .show(stage);
            }
        });

        checkBox.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                Gdx.graphics.setContinuousRendering(checkBox.isChecked());
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

        fpsLabel.setText("fps: " + Gdx.graphics.getFramesPerSecond());
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        tick++;
        if (tick > 0)
        {
            for (int i = 0; i < 1; i++)
            {
                _world.tick();
            }
            tick = 0;
        }
    }

    private void drawSprite(VivariumSprite sprite, int xPos, int yPos, float angle)
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

    private void drawCreatures()
    {
        for (int c = 0; c < _world.getWorldWidth(); c++)
        {
            for (int r = 0; r < _world.getWorldHeight(); r++)
            {
                if (_world.getEntityType(r, c) == EntityType.CREATURE)
                {
                    Creature creature = _world.getCreature(r, c);
                    switch (_creatureRenderMode)
                    {
                        case GENDER:
                            setColorOnGenderAndPregnancy(creature);
                            break;
                        case HEALTH:
                            setColorOnAgeAndFood(creature);
                            break;
                    }
                    float rotation = (float) (Direction.getRadiansFromNorth(creature.getFacing()) * 180 / (Math.PI));
                    drawSprite(VivariumSprite.CREATURE_2, c, r, rotation);
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

    public static int getHeight()
    {
        return SIZE * BLOCK_SIZE;
    }

    public static int getWidth()
    {
        return SIZE * BLOCK_SIZE;
    }
}
