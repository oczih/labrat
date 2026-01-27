package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

public class Lego {
    Node node = new Node();
    Geometry geom;
    Box box;

    public Lego(AssetManager assetManager, String color) {
        box = new Box(0.8f, 0.2f, 0.4f);
        geom = new Geometry("Box", box);
        node.attachChild(geom);
        Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        if (color.equals("green")) {
            mat.setColor("Diffuse", ColorRGBA.Green);
        } else if (color.equals("red")) {
            mat.setColor("Diffuse", ColorRGBA.Red);
        } else if (color.equals("yellow")) {
            mat.setColor("Diffuse", ColorRGBA.Yellow);
        } else if (color.equals("pink")) {
            mat.setColor("Diffuse", ColorRGBA.Pink);
        } else if (color.equals("blue")) {
            mat.setColor("Diffuse", ColorRGBA.Blue);
        } else {
            mat.setColor("Diffuse", ColorRGBA.DarkGray);
        }
        geom.setMaterial(mat);

        float boxHalfX = 0.8f;
        float boxHalfY = 0.2f;
        float boxHalfZ = 0.4f;
        int rows = 2;
        int cols = 4;

        for (int i = 0; i < 8; i++) {
            Cylinder cyl = new Cylinder(20, 20, 0.1f, 0.1f, true);
            Geometry g = new Geometry("Cyl" + i, cyl);
            g.rotate(FastMath.HALF_PI, 0, 0);

            int col = i % cols;
            int row = i / cols;

            float x = -boxHalfX + 0.2f + col * 0.4f;
            float z = -boxHalfZ + 0.2f + row * 0.4f;
            float y = boxHalfY + 0.05f;

            g.setLocalTranslation(x, y, z);
            g.setMaterial(mat);
            node.attachChild(g);
        }

    }
}