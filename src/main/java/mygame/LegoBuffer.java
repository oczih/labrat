package mygame;

import java.util.ArrayList;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

import mygame.Main;

public class LegoBuffer {
    private Box box;
    private Geometry geom;
    private float surfaceHeight;
    ArrayList<Lego> legos = new ArrayList<Lego>(500);
    ArrayList<String> legoColors = new ArrayList<String>(500);
    float x;
    float z;
    private float legoSpacingX = 2;
    private float legoSpacingZ = 2;
    int rowSize;
    int columnSize;

    public LegoBuffer(AssetManager assetManager, Node rootNode, float xOffset,
            float zOffset, int rowSize, int columnSize) {
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        x = xOffset;
        z = zOffset;

        // Create buffer box
        float yExtent = 7;
        box = new Box(16f, yExtent, 8f);
        geom = new Geometry("LegoBuffer", box);

        Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.LightGray);

        geom.setMaterial(mat);
        rootNode.attachChild(geom);

        // Calculate surface height
        surfaceHeight = Main.floorHeight + 2 * yExtent;

        geom.setLocalTranslation(x, surfaceHeight - yExtent, z);

        // Create legos in a grid pattern with alternating colors
        for (int i = 0; i < (rowSize * columnSize); i++) {
            String colorLego;
            switch (i % 4) {
                case 0:
                    colorLego = "yellow";
                    break;
                case 1:
                    colorLego = "blue";
                    break;
                case 2:
                    colorLego = "pink";
                    break;
                case 3:
                    colorLego = "green";
                    break;
                default:
                    colorLego = "red"; // Should never happen
            }

            Lego lego = new Lego(assetManager, colorLego);
            legos.add(lego);
            legoColors.add(colorLego);
            rootNode.attachChild(lego.node);
        }

        // Position all legos
        for (int i = 0; i < (rowSize * columnSize); i++) {
            legos.get(i).node.setLocalTranslation(getLegoCenterLocation(i));
        }
    }

    private float xCoord(int index) {
        int rowIndex = index % rowSize;
        return (rowIndex - rowSize / 2) * legoSpacingX;
    }

    private float zCoord(int index) {
        int columnIndex = index / rowSize;
        return (columnIndex - columnSize / 2) * legoSpacingZ;
    }

    private Vector3f getLegoCenterLocation(int index) {
        return new Vector3f(x + xCoord(index), surfaceHeight + 0.2f, z + zCoord(index));
    }

    public float getSurfaceHeight() {
        return surfaceHeight;
    }

    // APP tarvitsee legonyläpinnan koordinaatin, johon robotti tuo työkalunsa
    // alapinnassa
    // olevan ’tooltip’ pisteensä
    private Vector3f getLegoTopLocation(int index) {
        return new Vector3f(x + xCoord(index), surfaceHeight + 0.4f, z + zCoord(index));
    }

    // palauttaa Lego olion joka on halutun värinen tai null jos tällaista legoa ei
    // ole
    // päivitä legoColor Lego luokkaan ja konstruktoriin
    // päivitä location Lego luokkaan
    public Lego giveLego(String color) {
        for (int i = 0; i < legos.size(); i++) {
            Lego lego = legos.get(i);
            if (lego != null && legoColors.get(i).equals(color)) {
                lego.location = getLegoTopLocation(i);
                legos.set(i, null);
                legoColors.set(i, null);
                return lego;
            }
        }
        return null;
    }
}