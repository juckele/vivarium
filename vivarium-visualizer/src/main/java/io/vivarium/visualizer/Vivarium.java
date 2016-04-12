package io.vivarium.visualizer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

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

    // High Level Graphics information
    private Stage stage;
    private BitmapFont font;
    private TextureAtlas buttonAtlas;
    private Skin skin;
    private TextButtonStyle textButtonStyle;
    private TextButton button;
    private TextField textfield;

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
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but
        // strongly
        // recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
        // skin = new Skin();
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        // Generate a 1x1 white texture and store it in the skin named "white".
        Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        // Store the default libgdx font under the name "default".
        skin.add("default", new BitmapFont());

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't
        // overwrite the font.
        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        // Create a table that fills the screen. Everything else will go inside this table.
        Table table = new Table();
        table.setX(-600);
        table.setFillParent(true);
        stage.addActor(table);

        // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than
        // "default".
        final TextButton button = new TextButton("Click me!", skin);
        button.setX(0);
        table.add(button);

        final Slider slider = new Slider(0, 10, 1, false, skin);
        slider.setAnimateDuration(0.3f);
        table.add(slider);

        // Add a listener to the button. ChangeListener is fired when the button's checked state changes, eg when
        // clicked,
        // Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will
        // be reverted.
        // ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event
        // won't
        // revert the checked state.
        button.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                System.out.println("Clicked! Is checked: " + button.isChecked());
                button.setText("Good job!");
            }
        });

        // Add an image actor. Have to set the size, else it would be the size of the drawable (which is the 1x1
        // texture).
        table.add(new Image(skin.newDrawable("white", Color.RED))).size(64);
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
                    setColorOnGenderAndPregnancy(creature);
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
