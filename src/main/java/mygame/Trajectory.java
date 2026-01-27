package mygame;

import java.util.ArrayList;

import com.jme3.math.Vector3f;

public class Trajectory {
    ArrayList<Vector3f> points;
    int index; // â€˜pointsâ€™ listan indeksi
    int size; // kuinka monta waypointtia â€˜pointsâ€™ listassa on
    // alustaa yllÃ¤mainitut points ja index muuttujat

    public Trajectory() {
        points = new ArrayList<>();
        index = 0;
    }

    // lisÃ¤Ã¤ pisteen listan hÃ¤nnille
    public void addPoint(Vector3f v) {
        points.add(v.clone());
    }

    // nollaa indeksin ja asettaa size muuttujalle oikean arvon
    public void initTrajectory() {
        index = 0;
        size = points.size();
    }

    // palauttaa indexin kohdalla olevan pisteen tai null jos ei enÃ¤Ã¤ pisteitÃ¤
    public Vector3f nextPoint() {
        if (index < size) {
            return points.get(index++);
        } else {
            return null;
        }
    }
}
