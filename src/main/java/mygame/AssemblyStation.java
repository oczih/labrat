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
    RobotArm assemblyArm;
    float x, z;
    float surfaceHeight;
    float legoSpacingX = 2; // legojen slottipaikkojen etäisyys x-suuntaan
    float legoSpacingZ = 2; // legojen slottipaikkojen etäisyys z-suuntaan

    public AssemblyStation(AssetManager assetManager, Node rootNode, RobotArm arm, float xOffset, float zOffset) {
        float yExtent = 6f;
        this.assemblyArm = arm;
        box = new Box(20f, yExtent, 10f);
        geom = new Geometry("Box", box);
        node.attachChild(geom);
        rootNode.attachChild(node);
        Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.LightGray);
        geom.setMaterial(mat);
        geom.setLocalTranslation(xOffset, Main.floorHeight + yExtent, zOffset);
        x = xOffset;
        z = zOffset;
        surfaceHeight = Main.floorHeight + yExtent;
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
        int rowSize = (int) (16 / legoSpacingX);
        int columnSize = (int) (12 / legoSpacingZ);
        int rowIndex = slot % rowSize;
        float xOffset = (rowIndex - 1) * legoSpacingX;
        int columnIndex = slot / rowSize;
        float zOffset = (columnIndex + 2) * legoSpacingZ;
        float yOffset = 0.4f; // legon yläpinnan korkeus
        float legoTopY = surfaceHeight + 0.4f;
        return new Vector3f(x + xOffset, legoTopY, z + zOffset - 12);
    }

    // APP kohteeseen lego.location
    // sama idea kuin edellisen harjoituksen initTestMove()
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
}