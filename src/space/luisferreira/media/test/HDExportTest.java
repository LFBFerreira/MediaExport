package space.luisferreira.media.test;

import space.luisferreira.media.MediaExport;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Sketch that exports an image from an independent image buffer, using PGraphics.
 * This new buffer can be of any size, also bigger then your sketch's size, allowing you to run a sketch in
 * low resolution and export in an image in a higher resolution
 * Press F1 to save a 2000 x 1000 screenshot of the sketch
 */
public class HDExportTest extends PApplet {

    MediaExport mediaExport;
    boolean capture = false;

    public void settings() {
        size(300, 200, PConstants.P2D);
        smooth(16);
    }


    public void setup() {
        frameRate(30);

        mediaExport = new MediaExport("png", this);
        mediaExport.setOutputFolder(sketchPath());
        mediaExport.autoOpen(true);

        fillSketch(g);
    }


    public void draw() {
        PGraphics imageBuffer = g;

        if (capture) {
            imageBuffer = mediaExport.getHDGraphics(2000, 1000, PConstants.P2D, 16, true);
            fillSketch(imageBuffer);
        }

        // draw more stuff

        if (capture) {
            mediaExport.exportHDGraphics(false);
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