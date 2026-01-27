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
    ArrayList<Lego> legos = new ArrayList(500);
    float x;
    float z;
    private float legoSpacingX = 2;
    private float legoSpacingZ = 2;
    int rowSize;
    int columnSize;

    // luo varastotason koordinaatteihin x=xOffset, z=zOffset (y-korkeus siten että
    // se
    // on solun lattialla. Legoja on riveissa ja sarakkeissa, joihin mahtuu
    // ”rowSize”
    // * ”columnSize” legoja
    public LegoBuffer(AssetManager assetManager, Node rootNode, float xOffset,
            float zOffset, int rowSize, int columnSize) {
        // tallennetaan parametrien arvot olion yllämääriteltyihin muuttujiin, jotta
        // ne olisivat käytössä sen jälkeen kun tämä konstruktori metodi on suoritettu
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        x = xOffset;
        z = zOffset;
        // buffer on box jonka yExtent on:
        float yExtent = 7;
        // luo ColorRGBA.LightGray värinen box jonka koko on (16f, yExtent, 8f)
        // luo sille geometria ja liitä se rootNodeen
        box = new Box(16f, yExtent, 8f);
        geom = new Geometry("LegoBuffer", box);

        Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.LightGray);

        geom.setMaterial(mat);
        rootNode.attachChild(geom);
        // tason pinnan y koordinaatti lasketaan Main.floorHeight + yExtent avulla
        surfaceHeight = Main.floorHeight + yExtent;
        // laitetaan varastotaso siten että pohja on tuotantosolun lattian korkeudella
        geom.setLocalTranslation(x, surfaceHeight, z);

        String colorLego = "red"; // punainen lego tulee vain jos koodissasi on bugi
        // laitetaan tasolle rowSize*columnSize legoa. Vuorotellen eri värisiä. Muista
        // Lego konstruktori hyväksyy nämä: ”yellow”, ”blue”, ”pink”, ”green”
        for (int i = 0; i < (rowSize * columnSize / 4); i++) {
            for (int j = 0; j < 4; j++) {
                // asetetaan colorLego arvo, vuorotellen eri väri
                String[] colors = { "yellow", "blue", "pink", "green" };
                colorLego = colors[j];
                Lego lego;
                // luodaan lego, lisätään se legos ArrayListiin, liitetään rootNodeen
                lego = new Lego(assetManager, colorLego);
                legos.add(lego);
                System.out.println("lego " + i + " " + colorLego);
                rootNode.attachChild(lego.node);
            }
        }
        for (int i = 0; i < (rowSize * columnSize); i++) {
            legos.get(i).node.setLocalTranslation(getLegoCenterLocation(i));
        }
    }

    // legon x koordinaatti suhteessa LegoBuffer keskipisteeseen. ”legos” listan eka
    // lego on paikassa rowIndex=0 columnIndex=0. Listan toka lego on paikassa
    // rowIndex=0 columnIndex=1 jne
    private float xCoord(int index) {
        int rowIndex = index % rowSize;
        return (rowIndex - rowSize / 2) * legoSpacingX;
    }

    // legon z koordinaatti suhteessa LegoBuffer keskipisteeseen. ”legos” listan eka
    // lego on paikassa rowIndex=0 columnIndex=0. Listan toka lego on paikassa
    // rowIndex=0 columnIndex=1 jne
    private float zCoord(int index) {
        int columnIndex = index / rowSize;
        return (columnIndex - columnSize / 2) * legoSpacingZ;
    }

    // palauttaa legos listan “index” kohdan legon (keskipisteen) koordinaatit
    // palauta maailma-koordinaatit eli huomioi x, z ja surfaceHeight
    // 0.2f on y-etäisyys pöydän pinnasta legon keskipisteeseen
    // käytä xCoord() ja yCoord() metodeja
    private Vector3f getLegoCenterLocation(int index) {
        float legoY = surfaceHeight + 7 + 0.2f; // box yExtent = 7
        return new Vector3f(x + xCoord(index), legoY, z + zCoord(index));
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
                legos.set(i, null);
                return lego;
            }
        }
        return null;
    }

}