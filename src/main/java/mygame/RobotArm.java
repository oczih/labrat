package mygame;

import com.jme3.asset.AssetManager;

import com.jme3.material.Material;

import com.jme3.math.ColorRGBA;

import com.jme3.math.Vector3f;

import com.jme3.scene.Geometry;

import com.jme3.scene.Node;

import com.jme3.scene.shape.Box;

public class RobotArm {

    public Node node;

    public Node tooltipNode;

    private Vector3f targetLocation;

    float step = 1f;

    Geometry xArm;

    Geometry zArm;

    Geometry yArm;

    Geometry tooltip;

    ColorRGBA c;

    public RobotArm(AssetManager assetManager, Node rootNode, String color, boolean mirrored) {

        node = new Node("RobotArm");

        Material mat = new Material(

                assetManager,

                "Common/MatDefs/Misc/Unshaded.j3md");

        if (color == "orange") {

            c = ColorRGBA.Orange;

        } else if (color == "pink") {

            c = ColorRGBA.Pink;

        }
        float side = mirrored ? 30f : 0f;
        mat.setColor("Color", c);

        Box mastBox = new Box(0.2f, 6f, 0.2f);

        Geometry mast = new Geometry("mast", mastBox);

        mast.setMaterial(mat);

        mast.setLocalTranslation(-8f, 0f, -10f);

        Box zBox = new Box(0.2f, 0.2f, 20f);

        zArm = new Geometry("zArm", zBox);

        zArm.setMaterial(mat);

        zArm.setLocalTranslation(-8f, 6f, -8f);

        Box xBox = new Box(18f, 0.2f, 0.2f);

        xArm = new Geometry("xArm", xBox);

        xArm.setMaterial(mat);

        xArm.setLocalTranslation(6f - side, 6f, 0f);

        Box yBox = new Box(0.2f, 6f, 0.2f);

        yArm = new Geometry("yArm", yBox);

        yArm.setMaterial(mat);

        yArm.setLocalTranslation(-7f, 6f, 0f);

        Box toolBox = new Box(0.14f, 0.4f, 0.14f);

        tooltip = new Geometry("tooltip", toolBox);

        tooltip.setMaterial(mat);

        tooltipNode = new Node("tooltipNode");

        tooltipNode.attachChild(tooltip);

        // siirto: 6 alas + 0.4 alas

        tooltipNode.setLocalTranslation(-7f, -0.4f, 0f);

        // kiinnitykset

        node.attachChild(mast);

        node.attachChild(zArm);

        node.attachChild(xArm);

        node.attachChild(yArm);

        node.attachChild(tooltipNode);

        rootNode.attachChild(node);

        // target on vÃ¤lietappi johon kuuluu ajaa

    }

    public void initMove(Vector3f target) {

        targetLocation = target;

    }

    // palauttaa tooltipin alapinnan keskipisteen koordinaatit

    // maailma-koordinaateissa

    // kÃ¤ytÃ¤ Geometry luokan getWorldTranslation()

    public Vector3f getToolTipLocation() {

        return tooltipNode.getWorldTranslation().clone();

    }

    // moves towards target location and returns false when it reached the location

    public boolean move() {

        Vector3f location = getToolTipLocation();

        // lasketaan etÃ¤isyys mÃ¤Ã¤rÃ¤npÃ¤Ã¤hÃ¤n maailma-koordinaateissa

        float xDistance = targetLocation.getX() - location.getX();

        float zDistance = targetLocation.getZ() - location.getZ();

        float yDistance = targetLocation.getY() - location.getY();

        // booleanit ilmaisee ettÃ¤ onko kyseisen akselin suuntainen liike valmis

        boolean xReady = false;

        boolean yReady = false;

        boolean zReady = false;

        float x; // x-akselin suuntainen liike tÃ¤mÃ¤n syklin aikana

        float y; // y-akselin suuntainen liike tÃ¤mÃ¤n syklin aikana

        float z; // z-akselin suuntainen liike tÃ¤mÃ¤n syklin aikana

        // siirrytÃ¤Ã¤n stepin verran oikeaan suuntaan jos matkaa on yli stepin verran

        // muuten siirrytÃ¤Ã¤n targetLocationin x koordinaattiit

        if (xDistance > step) {

            x = step;

        } else if ((-1 * xDistance) > step) {

            x = -1 * step;

        } else {

            xReady = true;

            x = xDistance;

        }

        if (zDistance > step) {

            z = step;

        } else if ((-1 * zDistance) > step) {

            z = -1 * step;

        } else {

            zReady = true;

            z = zDistance;

        }

        if (yDistance > step) {

            y = step;

        } else if ((-1 * yDistance) > step) {

            y = -1 * step;

        } else {

            yReady = true;

            y = yDistance;

        }

        // siirretÃ¤Ã¤n mastossa kiinni oleva zArm, joka liikkuu siis z-suuntaan

        // 0.5f siitÃ¤ syystÃ¤ ettÃ¤ robotti ulottuu paremmin (xArm liikuu zArmia

        // pitkin)

        Vector3f v = new Vector3f(0, 0, 0.5f * z);

        zArm.setLocalTranslation(zArm.getLocalTranslation().add(v));

        // xArm on zArmin varassa minkÃ¤ lisÃ¤ksi se liikkuu sitÃ¤ pitkin, joten nyt

        // kÃ¤ytetÃ¤ 0.5f kerrointa kuten Ã¤sken

        Vector3f v1 = new Vector3f(0, 0, z);

        xArm.setLocalTranslation(xArm.getLocalTranslation().add(v1));

        // yArm liikkuu xArm Pitkin x suuntaan ja tekee myÃ¶s y-suuntaisen liikkeen,

        // minkÃ¤ lisÃ¤ksi zArmin liike siirtÃ¤Ã¤ myÃ¶s yArmia

        Vector3f v2 = new Vector3f(x, y, z);

        yArm.setLocalTranslation(yArm.getLocalTranslation().add(v2));

        // nodetoolTip paikaksi on mÃ¤Ã¤ritelty yArm alapinta, mutta nodetoolTipin

        // parent

        // noodi ei liiku, joten nodetoolTip pitÃ¤Ã¤ siirtÃ¤Ã¤ kuten yArm

        // samalla liikkuu nodetoolTippiin liitetty tooltipin geometria

        tooltipNode.setLocalTranslation(tooltipNode.getLocalTranslation().add(

                new Vector3f(x, y, z)));

        if ((yReady && xReady) && zReady) {

            return false; // i.e. not moving anymore

        } else {

            return true;

        }

    }

}