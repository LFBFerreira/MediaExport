package luis.ferreira.libraries.media.test;

import luis.ferreira.libraries.media.MediaExport;
//import peasy.PeasyCam;
import processing.core.*;

public class Model3DTest extends PApplet {
    MediaExport mediaExport;
    boolean capture = false;
    private final String MODEL_PATH = "";
    public PShape model;
    //private PeasyCam cam;
    private boolean lightOn = true;

    public void settings() {
        size(800, 600, PConstants.P3D);
        smooth(16);
    }


    public void setup() {
        mediaExport = new MediaExport("png", "pdf", this);

        model = loadShape(MODEL_PATH);

        //cam = new PeasyCam(this, 150);

        initializeModelRecursive(model);
    }


    public void draw() {
        PGraphics imageBuffer = g;
        background(0);

        if (capture) {
            imageBuffer = mediaExport.initializeVectorGraphics();
            color(0);
        }

        if (lightOn) {
            lights();
        }

        shape(model);

        if (capture) {
            mediaExport.exportVectorGraphics();
            capture = false;
        }
    }

    public void keyPressed() {
        switch (keyCode) {
            case 97: // F1
                capture = true;
                break;
        }

        switch (key) {
            case 'l':
                lightOn = !lightOn;
        }
    }

    private void initializeModelRecursive(PShape shape) {
        if (shape.getChildCount() > 0) {
            for (int i = 0; i < shape.getChildCount(); i++) {
                initializeModelRecursive(shape.getChild(i));
            }
        }

        if (shape.getVertexCount() == 0) {
            return;
        }

        int color = color(random(255), random(255), random(255), 255);
        shape.setFill(color);
        shape.setAmbient(color);
        shape.setSpecular(color);
        shape.noStroke();
    }
}
