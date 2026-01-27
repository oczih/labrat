package mygame;

import java.util.ArrayList;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import main.java.mygame.AssemblyStation;
import main.java.mygame.Lego;
import main.java.mygame.LegoBuffer;
import main.java.mygame.RobotArm;

public class Main extends SimpleApplication {
    public static float floorHeight = -15;
    AssemblyStation station;
    LegoBuffer legoBuffer;
    boolean freeze = false;
    boolean moving = false;
    boolean goingToLego = false;
    Lego lego;
    int slotIndex = 0;
    final int numColors = 4;
    int colorIndex = 0;
    ArrayList<String> colors = new ArrayList<>(numColors);

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);
        /*
         * Lego lego = new Lego(assetManager, "red");
         * Lego legoBlue = new Lego(assetManager, "blue");
         * Lego legoGreen = new Lego(assetManager, "green");
         * Lego legoYellow = new Lego(assetManager, "yellow");
         * Lego legoPink = new Lego(assetManager, "pink");
         * 
         * legoGreen.node.setLocalTranslation(0f, 0, 0);
         * lego.node.setLocalTranslation(2f, 0, 0);
         * legoYellow.node.setLocalTranslation(4f, 0, 0);
         * legoPink.node.setLocalTranslation(6f, 0, 0);
         * legoBlue.node.setLocalTranslation(8f, 0, 0);
         * rootNode.attachChild(legoGreen.node);
         * rootNode.attachChild(lego.node);
         * rootNode.attachChild(legoYellow.node);
         * rootNode.attachChild(legoPink.node);
         * 
         * rootNode.attachChild(legoBlue.node);
         */
        legoBuffer = new LegoBuffer(assetManager, rootNode, 5, -29, 10, 6);
        RobotArm robot = new RobotArm(assetManager, rootNode);
        station = new AssemblyStation(assetManager, rootNode, robot, 5f, -11f);

        cam.setLocation(new Vector3f(5, 0, -15));
        cam.lookAt(new Vector3f(5, -8, -29), Vector3f.UNIT_Y);

        PointLight lamp_light = new PointLight();
        lamp_light.setColor(ColorRGBA.White);
        lamp_light.setRadius(400f);
        lamp_light.setPosition(new Vector3f(2f, 8.0f, 10.0f));
        rootNode.addLight(lamp_light);

        // colors list
        colors.add("yellow");
        colors.add("blue");
        colors.add("pink");
        colors.add("green");

        // initial lego fetch
        lego = legoBuffer.giveLego(colors.get(colorIndex));
        goingToLego = true;
        moving = true;

    }

    @Override
    public void simpleUpdate(float tpf) {
        if (!freeze && moving) {
            moving = station.move();
        }

        if (!moving && !freeze) {
            if (goingToLego) {
                Vector3f v = station.slotPosition(slotIndex);
                slotIndex++;
                station.initMoveToLego(v);
                goingToLego = false;
                moving = true;
            } else {
                if (lego != null) {
                    Vector3f loc = lego.node.getWorldTranslation();
                    lego.node.removeFromParent();
                    lego.node.setLocalTranslation(loc);
                    lego.node.attachChild(station.node);
                }
                lego = legoBuffer.giveLego(colors.get(colorIndex));
                moving = true;
                if (lego == null) {
                    colorIndex++;
                    if (colorIndex >= numColors) {
                        freeze = true;
                    } else {
                        colors.get(colorIndex);
                    }
                }
                if (!freeze) {
                    station.initMoveToLego(lego);
                }
                goingToLego = true;
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // TODO: add render code
    }
}
