package pepse.util.pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject {

    private static final String ICY_TOWER_IMG_PATH = "assets/IcyTowerGuy.png";
//    private static final String WOOPDEEDOO1 = "assets/woopdeedoo.png";
//    private static final String WOOPDEEDOO2 = "assets/woopdeedoo2.png";
//    private static final String WOOPDEEDOO3 = "assets/woopdeedoo3.png";
//    private static final String WOOPDEEDOO4 = "assets/woopdeedoo4.png";
    private static final String FLYING_ANIMATION1_PATH = "assets/woopdeedoo1_no_bg.png";
    private static final String FLYING_ANIMATION2_PATH = "assets/woopdeedoo2_no_bg.png";
    private static final String FLYING_ANIMATION3_PATH = "assets/woopdeedoo3_no_bg.png";
    private static final String FLYING_ANIMATION4_PATH = "assets/woopdeedoo4_no_bg.png";
    private static final String JUMPING_SOUND_PATH = "assets/icy-tower-woopdeedoo_sound.wav";
    private static final String BULLET_PATH = "assets/bullet.png";
    private static final String BULLET_SOUND_PATH = "assets/shoot.wav";
    private static final double FLYING_ANIMATION_TIME = 0.1;
    private static final Vector2 AVATAR_DIMENSIONS = new Vector2(25, 50);
    private static final float VELOCITY_X = 150;
    private static final float VELOCITY_Y = -300;
    private static final float Y_ACCELERATION = 500;
    private static final float FLY_ENERGY_LOSS = 0.5f;
    private static final float MAX_ENERGY_LEVEL = 100f;
    private static final int SHOT_LAYER = Layer.DEFAULT + 30;
    private static final float BULLET_VELOCITY = 300;
    private static final Vector2 BULLET_SIZE = new Vector2(5, 15);
    private static final int BULLET_LIFESPAN = 3;
    private static final int SHOT_TIME_DELAY = 1;
    private final UserInputListener inputListener;
    private static Renderable defaultRenderable;
    private static AnimationRenderable JumpingAnimationRenderable;
    private Sound flyingAndJumpingSound;
    private Sound shootingSound;
    private float energyLevel;
    private float prevYVel;
    private ImageReader imageReader;
    private  SoundReader soundReader;
    private GameObjectCollection gameObjects;
    private boolean canShoot = true;
    private ImageRenderable shotImage;


    /**
     * Avatar object constructor
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, ImageReader imageReader,
                  GameObjectCollection gameObjects) {
        super(topLeftCorner, dimensions, renderable);
        this.gameObjects = gameObjects;
        this.imageReader = imageReader;
        this.inputListener = inputListener;
        energyLevel = MAX_ENERGY_LEVEL;
        prevYVel = 0;
        this.shotImage = imageReader.readImage(BULLET_PATH, true);
    }

    /**
     * @param gameObjects
     * @param layer
     * @param topLeftCorner
     * @param inputListener
     * @param imageReader
     * @return
     */
    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader) {
        loadRenderables(imageReader);
        Avatar avatar = new Avatar(topLeftCorner, AVATAR_DIMENSIONS,
                defaultRenderable, inputListener, imageReader, gameObjects);
        gameObjects.addGameObject(avatar, layer);

        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        avatar.transform().setAccelerationY(Y_ACCELERATION);

        return avatar;
    }

    public void setSoundReaderAndSounds(SoundReader soundReader) {
        this.soundReader = soundReader;
        flyingAndJumpingSound = soundReader.readSound(JUMPING_SOUND_PATH);
        shootingSound = soundReader.readSound(BULLET_SOUND_PATH);
    }

    public static void loadRenderables(ImageReader imageReader) {
        defaultRenderable = imageReader.readImage(ICY_TOWER_IMG_PATH, true);
        JumpingAnimationRenderable = new AnimationRenderable(new Renderable[]{
                imageReader.readImage(FLYING_ANIMATION1_PATH, true),
                imageReader.readImage(FLYING_ANIMATION2_PATH, true),
                imageReader.readImage(FLYING_ANIMATION3_PATH, true),
                imageReader.readImage(FLYING_ANIMATION4_PATH, true)},
                FLYING_ANIMATION_TIME);
    }

    private void updateVelocityX() {
        float xVel = 0;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT))
            xVel -= VELOCITY_X;
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT))
            xVel += VELOCITY_X;
        transform().setVelocityX(xVel);
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
            transform().setVelocityY(0);
    }

    private void updateSoundAndRenderable() {
        if (prevYVel != 0 && transform().getVelocity().y() == 0) {
            renderer().setRenderable(defaultRenderable);
        }
        if (prevYVel == 0 && transform().getVelocity().y() != 0) {
            if (flyingAndJumpingSound != null) {
                flyingAndJumpingSound.play();
            }
            renderer().setRenderable(JumpingAnimationRenderable);
        }
    }


    private void updateVelocityYAndEnergy() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0) {
            transform().setVelocityY(VELOCITY_Y);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_SHIFT) &&
                inputListener.isKeyPressed(KeyEvent.VK_SPACE) && energyLevel > 0) {
            transform().setVelocityY(VELOCITY_Y);
            energyLevel -= FLY_ENERGY_LOSS;
        }
        if (getVelocity().y() == 0 && energyLevel < MAX_ENERGY_LEVEL) {
            energyLevel += FLY_ENERGY_LOSS;
        }
    }
    private void updateBullets() {
        if (inputListener.isKeyPressed(KeyEvent.VK_UP) && canShoot) {
            Shot shot = new Shot(getTopLeftCorner(), BULLET_SIZE, shotImage, gameObjects, SHOT_LAYER);
            shot.setVelocity(Vector2.UP.mult(BULLET_VELOCITY));
            gameObjects.addGameObject(shot, SHOT_LAYER);
            shootingSound.play();
            new ScheduledTask(shot, BULLET_LIFESPAN, false, () ->
                    gameObjects.removeGameObject(shot, SHOT_LAYER));
            canShoot = false;
            new ScheduledTask(this, SHOT_TIME_DELAY, false, () -> canShoot = true);
        }
    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (transform().getVelocity().y() == 0 && inputListener.isKeyPressed(KeyEvent.VK_SPACE)
                && flyingAndJumpingSound != null) {
                flyingAndJumpingSound.play();
        }
        updateVelocityX();
        updateVelocityYAndEnergy();
        updateSoundAndRenderable();
        updateBullets();
        prevYVel = transform().getVelocity().y();
    }

}
