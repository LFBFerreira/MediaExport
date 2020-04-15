package luis.ferreira.libraries.media.test;

import luis.ferreira.libraries.media.MediaExport;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

public class HDExportTest extends PApplet {

    MediaExport mediaExport;
    boolean capture = false;

    public void settings() {
        size(300, 200, PConstants.P2D);
        smooth(16);
    }


    public void setup() {
        mediaExport = new MediaExport("png", this);
        mediaExport.setOutputFolder(sketchPath());

        fillSketch(g);
    }


    public void draw() {
        PGraphics imageBuffer = g;

        if (capture) {
            imageBuffer = mediaExport.getHDGraphics(2000, 1000, P2D, 8, false);
            fillSketch(imageBuffer);
        }

        // draw more stuff

        if (capture) {
            mediaExport.exportHDGraphics();
            mediaExport.disposeHDGraphics();
            capture = false;
        }
    }

    public void keyPressed() {
        switch (keyCode) {
            case 97: // F1
                capture = true;
                break;
        }
    }

    public void mousePressed() {
        fillSketch(g);
    }

    private void fillSketch(PGraphics buffer) {
        int width = buffer.width;
        int height = buffer.height;

        buffer.beginDraw();

        buffer.background(random(255));

        buffer.strokeWeight(random(10));
        buffer.stroke(255, 0, 0);
        buffer.fill(random(255));
        buffer.circle(random(0, width), random(0, height), width/2);

        buffer.strokeWeight(random(10));
        buffer.stroke(0, 255, 0);
        buffer.fill(random(255));
        buffer.rect(random(0, width), random(0, height), 20, width/2);

        buffer.strokeWeight(random(10));
        buffer.stroke(0, 0, 255);
        buffer.fill(random(255));
        buffer.triangle(random(0, width), random(0, height),
                random(0, width), random(0, height),
                random(0, width), random(0, height));

        buffer.endDraw();
    }
}