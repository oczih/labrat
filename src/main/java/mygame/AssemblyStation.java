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
        // eka vÃ¤lietappi suoraan ylÃ¶s max korkeuteen
        Vector3f v1 = assemblyArm.getToolTipLocation();
        Vector3f v2 = destination.clone();
        v1.setY(maxHeight);
        v2.setY(maxHeight);
        trajectory.addPoint(v1);
        // toka vÃ¤lietappi max korkeuteen destination ylle
        trajectory.addPoint(v2);
        trajectory.addPoint(destination);
        trajectory.initTrajectory();
    }

    // kÃ¤skyttÃ¤Ã¤ robottia ajamaan reitin, joka on mÃ¤Ã¤ritelty
    // trajectory-attribuuttiin
    // palauttaa false jos saavutettiin trajectory viimeinen (vÃ¤li)etappi, eli
    // initTestMove() saama destination. Muuten palauttaa true.
    // tÃ¤tÃ¤ tulee kutsua syklisesti kunnes se palauttaa false
    public boolean move() {
        if (moving) {
            moving = assemblyArm.move();
            return true;
        } else {
            Vector3f nextPoint = trajectory.nextPoint();
            if (nextPoint == null) {
                moving = false;
                return false;
            } else {
                System.out.println(nextPoint.toString());
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

    // APP kohteeseen lego.location
    // sama idea kuin edellisen harjoituksen initTestMove()
    public void initMoveToLego(Lego lego) {
        if (moving) {
            moving = assemblyArm.move();
        } else {
            Vector3f nextPoint = lego.node.getWorldTranslation();
            if (nextPoint == null) {
                moving = false;
            } else {
                System.out.println(nextPoint.toString());
                assemblyArm.initMove(nextPoint);
                moving = true;
            }
        }
    }

    // APP kohteeseen destination
    public void initMoveToStation(Lego lego, Vector3f destination) {
        assemblyArm.nodeToolTip.attachChild(lego.node); // muuten lego ei lähde mukaan
        // nyt legon noden sijainti pitää määritellä nodeToolTip paikallisissa
        // koordinaateissa. lego.node.setLocalTranslation(0,0,0) laittaisi legon
        // keskipisteen tooltipin keskipisteeseen
        // vinkki: tooltipin yExtent = 0.4f ja legon yExtent = 0.2f
        lego.node.setLocalTranslation(0, 0, 0);
        // sitten tehdään APP kohteeseen ”destination”
        initMoveToLego(destination);
    }
}
