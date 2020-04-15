package luis.ferreira.libraries.media.test;

import luis.ferreira.libraries.media.MediaExport;
import processing.core.PApplet;
import processing.core.PConstants;

public class TestSketch extends PApplet {

    MediaExport mediaExport;
    boolean capture = false;

    public void settings() {
//        size(300, 200, PConstants.P2D);
        size(300, 200, PConstants.P3D);
    }


    public void setup() {
        mediaExport = new MediaExport(100, 30, ".mp4", "png", "pdf", this);
        mediaExport.setOutputFolder(sketchPath());

        background(random(255));
        fillSketch();

        hint(PConstants.DISABLE_OPTIMIZED_STROKE);
    }


    public void draw() {
        if (capture) {
            //mediaExport.startPdfCatpure();

            background(frameCount % 255);
            fillSketch();

            capture = false;
        }

        pushMatrix();
        popMatrix();

        mediaExport.saveFrame();
    }


    public void keyPressed() {
        switch (keyCode) {
            case 97: // F1
                mediaExport.toggleVideoRecording();
                break;
            case 98: // F2
                mediaExport.endCapture();
                break;
            case 99: // F3
                mediaExport.takeScreenshot();
                break;
            case 100: // F4
                mediaExport.takeScreenshotNextFrame();
                break;
            case 101: // F5
                capture = true;
                break;
        }

        fillSketch();
    }

    private void fillSketch()
    {
        strokeWeight(0.5f);
        stroke(255, 0 ,0);
        fill(random(255));
        circle(random(0, width), random(0, height), 60);

        strokeWeight(2f);
        stroke(0, 255, 0);
        fill(random(255));
        rect(random(0, width), random(0, height), 20, 80);

        strokeWeight(3.5f);
        stroke(0, 0, 255);
        fill(random(255));
        triangle(random(0, width), random(0, height),
                random(0, width), random(0, height),
                random(0, width), random(0, height));
    }
}