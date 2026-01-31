package mygame;

import com.jme3.asset.AssetManager;

import com.jme3.material.Material;

import com.jme3.math.ColorRGBA;

import com.jme3.math.Vector3f;

import com.jme3.scene.Geometry;

import com.jme3.scene.Node;

import com.jme3.scene.shape.Box;

import mygame.Main;

public class AssemblyStation {

    Node node = new Node();

    Geometry geom;

    Box box;

    float maxHeight = 4;

    boolean moving = false;

    Trajectory trajectory;
    Trajectory trajectory2;

    boolean moving1 = false;
    boolean moving2 = false;

    RobotArm assemblyArm;

    RobotArm assemblyArm2;

    float x, z;

    float surfaceHeight;

    float legoSpacingX = 2; // legojen slottipaikkojen etäisyys x-suuntaan

    float legoSpacingZ = 2; // legojen slottipaikkojen etäisyys z-suuntaan

    float legoSpacingY = 0.4f;

    public AssemblyStation(AssetManager assetManager, Node rootNode, RobotArm arm, RobotArm arm2, float xOffset,
            float zOffset) {

        float yExtent = 6f;

        this.assemblyArm = arm;

        this.assemblyArm2 = arm2;

        box = new Box(20f, yExtent, 10f);

        geom = new Geometry("Box", box);

        node.attachChild(geom);

        rootNode.attachChild(node);

        Material mat = new Material(assetManager,

                "Common/MatDefs/Light/Lighting.j3md");

        mat.setBoolean("UseMaterialColors", true);

        mat.setColor("Diffuse", ColorRGBA.LightGray);

        geom.setMaterial(mat);

        // Position the station so its TOP is at working height

        float stationTopY = Main.floorHeight + (2 * yExtent); // -15 + 12 = -3

        geom.setLocalTranslation(xOffset, stationTopY - yExtent, zOffset); // Center at -3 - 6 = -9

        x = xOffset;

        z = zOffset;

        surfaceHeight = stationTopY; // surfaceHeight should be -3 (top of station)

    }

    public void initTestMove(Vector3f destination) {

        trajectory = new Trajectory();

        // eka välietappi suoraan ylös max korkeuteen

        Vector3f v1 = assemblyArm.getToolTipLocation();

        Vector3f v2 = destination.clone();

        v1.setY(maxHeight);

        v2.setY(maxHeight);

        trajectory.addPoint(v1);

        // toka välietappi max korkeuteen destination ylle

        trajectory.addPoint(v2);

        trajectory.addPoint(destination);

        trajectory.initTrajectory();

    }

    // käskyttää robottia ajamaan reitin, joka on määritelty

    // trajectory-attribuuttiin

    // palauttaa false jos saavutettiin trajectory viimeinen (väli)etappi, eli

    // initTestMove() saama destination. Muuten palauttaa true.

    // tätä tulee kutsua syklisesti kunnes se palauttaa false
    public boolean move2() {
        if (trajectory2 == null) {
            return false;
        }
        if (moving2) {
            moving2 = assemblyArm2.move();
            return true;
        } else {
            Vector3f nextPoint = trajectory2.nextPoint();
            if (nextPoint == null) {
                moving2 = false;
                return false;
            } else {
                assemblyArm2.initMove(nextPoint);
                moving2 = true;
                return true;
            }
        }
    }

    public boolean move() {

        if (trajectory == null) {

            return false;

        }

        if (moving) {

            moving = assemblyArm.move();

            return true;

        } else {

            Vector3f nextPoint = trajectory.nextPoint();

            if (nextPoint == null) {

                moving = false;

                return false;

            } else {

                assemblyArm.initMove(nextPoint);

                moving = true;

                return true;

            }

        }

    }

    // kokoonpanoasemalla on slotteja, joiden indeksi on kokonaisluku

    // tämä palauttaa slotin 3D koordinaatit

    public Vector3f slotPosition(int slot) {

        // vain osa asemasta on varattu tähän tarkoitukseen. Sen koko on 16x12

        int rowSize = (int) ((16) / legoSpacingX);

        int columnSize = (int) ((12) / legoSpacingZ);

        int rowIndex = slot % rowSize;

        float xOffset = (rowIndex - 1) * legoSpacingX;

        int columnIndex = slot / rowSize;

        float zOffset = (columnIndex + 2) * legoSpacingZ;

        float yOffset = 0.4f; // legonyExtent

        // ’x’ ja ’z’ on float muuttujia, joihin on tallennettu konstruktorin

        // xOffset/zOffset

        // laske ’surfaceHeight’ konstruktorissa

        return new Vector3f(x + xOffset, surfaceHeight + yOffset, z + zOffset - 12);

    }

    public Vector3f newSlots(int slot, int index) {

        int slotsPerColumn = (15);

        int row = slot / slotsPerColumn;

        int column = slot % slotsPerColumn;

        float xOffset = row * legoSpacingX;

        float yOffset = column * legoSpacingY;

        float surface = surfaceHeight + 0.4f;

        return new Vector3f(

                x + xOffset,

                surface + yOffset,

                z

        );

    }

    public Vector3f finalSlotPosition(int slot) {
        // Define the final grid size (how many legos per row/column)
        int rowSize = 10; // number of legos per row
        int columnSize = 6; // number of legos per column

        float spacingX = 2.0f; // distance between legos in X
        float spacingZ = 2.0f; // distance between legos in Z

        // Calculate column and row indices
        int rowIndex = slot % rowSize;
        int columnIndex = slot / rowSize;

        // Center the grid around the station's x/z
        float xOffset = (rowIndex - rowSize / 2f) * spacingX;
        float zOffset = (columnIndex - columnSize / 2f) * spacingZ;

        // Y position on the final plane
        float y = surfaceHeight + 0.4f;

        return new Vector3f(
                5 + xOffset,
                Main.floorHeight + 2 * 7 + 0.4f,
                0 + zOffset + 10 // push away from temp field
        );
    }

    // APP kohteeseen lego.location

    // sama idea kuin edellisen harjoituksen initTestMove()
    public void initMoveToLego2(Lego lego) {

        if (lego == null || lego.location == null) {

            return;
        }
        trajectory2 = new Trajectory();

        Vector3f v1 = assemblyArm2.getToolTipLocation().clone();
        Vector3f v2 = lego.location.clone();
        Vector3f v3 = lego.location.clone();

        v1.setY(maxHeight);

        v2.setY(maxHeight);

        trajectory2.addPoint(v1);

        trajectory2.addPoint(v2);

        trajectory2.addPoint(v3);

        trajectory2.initTrajectory();

    }

    public void initMoveToLego(Lego lego) {

        if (lego == null || lego.location == null) {

            return;

        }

        trajectory = new Trajectory();

        // Current tooltip position

        Vector3f v1 = assemblyArm.getToolTipLocation().clone();

        // Above the lego (at max height)

        Vector3f v2 = lego.location.clone();

        // The lego's top location (where we pick it up)

        Vector3f v3 = lego.location.clone();

        v1.setY(maxHeight);

        v2.setY(maxHeight);

        trajectory.addPoint(v1);

        trajectory.addPoint(v2);

        trajectory.addPoint(v3);

        trajectory.initTrajectory();

    }

    // APP kohteeseen destination

    public void initMoveToStation(Lego lego, Vector3f destination) {

        if (lego == null) {

            return;

        }

        // Attach lego to the tooltip for transport

        assemblyArm.tooltipNode.attachChild(lego.node);

        // Position the lego relative to the tooltip

        // Tooltip has yExtent = 0.4f, lego has yExtent = 0.2f

        // We want the lego to be below the tooltip, centered

        lego.node.setLocalTranslation(0, -0.2f, 0); // -0.4f (tooltip) - 0.2f (half lego)

        // Create trajectory to destination

        trajectory = new Trajectory();

        // Start from current position

        Vector3f v1 = assemblyArm.getToolTipLocation().clone();

        // Above destination (at max height)

        Vector3f v2 = destination.clone();

        v2.setY(maxHeight);

        // Final destination (place lego)

        Vector3f v3 = destination.clone();

        v1.setY(maxHeight);

        trajectory.addPoint(v1);

        trajectory.addPoint(v2);

        trajectory.addPoint(v3);

        trajectory.initTrajectory();

    }

    public void initMoveToStation2(Lego lego, Vector3f destination) {

        if (lego == null) {

            return;

        }

        // Attach lego to the tooltip for transport

        assemblyArm2.tooltipNode.attachChild(lego.node);

        // Position the lego relative to the tooltip

        // Tooltip has yExtent = 0.4f, lego has yExtent = 0.2f

        // We want the lego to be below the tooltip, centered

        lego.node.setLocalTranslation(0, -0.2f, 0); // -0.4f (tooltip) - 0.2f (half lego)

        // Create trajectory to destination

        trajectory2 = new Trajectory();

        // Start from current position

        Vector3f v1 = assemblyArm2.getToolTipLocation().clone();

        // Above destination (at max height)

        Vector3f v2 = destination.clone();

        v2.setY(maxHeight);

        // Final destination (place lego)

        Vector3f v3 = destination.clone();

        v1.setY(maxHeight);

        trajectory2.addPoint(v1);

        trajectory2.addPoint(v2);

        trajectory2.addPoint(v3);

        trajectory2.initTrajectory();

    }
}