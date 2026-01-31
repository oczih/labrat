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

public class Main extends SimpleApplication {

    public static float floorHeight = -15;

    AssemblyStation station;

    LegoBuffer legoBuffer;

    LegoBuffer legoBuffer2;

    boolean freeze = false;
    boolean freeze2 = false;
    boolean moving = false;
    boolean moving2 = false;

    boolean goingToLego = false;
    boolean goingToLego2 = false;
    boolean done = false;
    ArrayList<Lego> placedLegos = new ArrayList<>();

    Lego lego;

    int slotIndex = 0;

    final int numColors = 4;

    int colorIndex = 0;

    int index = 0;

    boolean mode = true;

    ArrayList<String> colors = new ArrayList<>(numColors);

    public static void main(String[] args) {

        Main app = new Main();

        app.start();

    }

    @Override

    public void simpleInitApp() {

        flyCam.setMoveSpeed(20);

        /*
         * 
         * Lego lego = new Lego(assetManager, "red");
         * 
         * Lego legoBlue = new Lego(assetManager, "blue");
         * 
         * Lego legoGreen = new Lego(assetManager, "green");
         * 
         * Lego legoYellow = new Lego(assetManager, "yellow");
         * 
         * Lego legoPink = new Lego(assetManager, "pink");
         * 
         * legoGreen.node.setLocalTranslation(0f, 0, 0);
         * 
         * lego.node.setLocalTranslation(2f, 0, 0);
         * 
         * legoYellow.node.setLocalTranslation(4f, 0, 0);
         * 
         * legoPink.node.setLocalTranslation(6f, 0, 0);
         * 
         * legoBlue.node.setLocalTranslation(8f, 0, 0);
         * 
         * rootNode.attachChild(legoGreen.node);
         * 
         * rootNode.attachChild(lego.node);
         * 
         * rootNode.attachChild(legoYellow.node);
         * 
         * rootNode.attachChild(legoPink.node);
         * 
         * rootNode.attachChild(legoBlue.node);
         * 
         */

        legoBuffer = new LegoBuffer(assetManager, rootNode, 5, -29, 10, 6, true);

        legoBuffer2 = new LegoBuffer(assetManager, rootNode, 5, 7, 10, 6, false);

        RobotArm robot = new RobotArm(assetManager, rootNode, "orange", false);
        robot.node.setLocalTranslation(-10f, 0f, 0f);

        // right side
        RobotArm robot2 = new RobotArm(assetManager, rootNode, "pink", true);
        robot2.node.setLocalTranslation(35f, 0f, 0f);

        station = new AssemblyStation(assetManager, rootNode, robot, robot2, 5f, -11f);

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

                // Just arrived at lego buffer, now move to station

                Vector3f v = mode == true ? station.newSlots(slotIndex, index) : station.slotPosition(slotIndex);

                slotIndex++;

                station.initMoveToStation(lego, v);

                goingToLego = false;

                moving = true;

            } else {

                // Just placed lego at station, detach it

                if (lego != null) {

                    Vector3f loc = lego.node.getWorldTranslation().clone();

                    // Detach from tooltip

                    if (lego.node.getParent() != null) {

                        lego.node.removeFromParent();

                    }

                    // Set position and attach to root

                    lego.node.setLocalTranslation(loc);

                    rootNode.attachChild(lego.node);
                    placedLegos.add(lego);
                }

                // Get next lego of current color

                lego = legoBuffer.giveLego(colors.get(colorIndex));

                if (lego == null) {

                    // No more legos of current color

                    colorIndex++;

                    index++;

                    if (colorIndex >= numColors) {

                        freeze = true;
                        done = true;
                        colorIndex = 0;
                        index = 0;
                        slotIndex = 0;

                    } else {

                        // Try next color

                        lego = legoBuffer.giveLego(colors.get(colorIndex));

                    }

                }

                if (!freeze && lego != null) {

                    station.initMoveToLego(lego);

                    goingToLego = true;

                    moving = true;

                }

            }

        }
        if (done) {

            // Continue moving arm 2 if it's currently moving
            if (!freeze2 && moving2) {
                moving2 = station.move2();
            }

            // If arm 2 is not moving, start the next move
            if (!moving2 && !freeze2) {

                if (goingToLego2) {
                    Vector3f v = station.finalSlotPosition(slotIndex);
                    station.initMoveToStation2(lego, v);

                    goingToLego2 = false;
                    moving2 = true;

                    // Now increment slotIndex AFTER placing
                    slotIndex++;
                } else {
                    // Just placed the previous lego, detach it from tooltip
                    if (lego != null) {
                        Vector3f loc = lego.node.getWorldTranslation().clone();

                        if (lego.node.getParent() != null) {
                            lego.node.removeFromParent();
                        }

                        lego.node.setLocalTranslation(loc);
                        rootNode.attachChild(lego.node);
                    }

                    // Get the next lego from the station field
                    if (slotIndex < placedLegos.size()) {
                        lego = placedLegos.get(placedLegos.size() - 1 - slotIndex);
                        lego.location = lego.node.getWorldTranslation().clone();

                        // Start moving arm 2 to pick this lego
                        station.initMoveToLego2(lego);
                        goingToLego2 = true;
                        moving2 = true;

                        // Increment slotIndex only after arm 2 finishes placing this lego
                    } else {
                        // No more legos to process
                        freeze2 = true;
                    }
                }
            }
        }

    }

    @Override

    public void simpleRender(RenderManager rm) {

        // TODO: add render code

    }

}