package space.luisferreira.media.test;

import space.luisferreira.media.MediaExport;
import processing.core.PApplet;
import processing.core.PConstants;

/**
 * The basic functions of this library are shown in this example, where you can start and pause a video recording,
 * take a screenshot, and export both with automated names.
 * Two screenshot modes are available, one where the screen is saved immediately, and another during the next draw cycle.
 * The first mode is compatible with most uses, but some structures might require the later.
 * <p>
 * Press F1 to start/pause a video recording
 * Press F2 to stop the video recording and export it
 * Press F3 to take a screenshot immediately
 * Press F4 to take a screenshot at the end of the next Draw cycle
 */
public class VideoAndScreenshotTest extends PApplet {

    MediaExport mediaExport;
    boolean capture = false;
    int frameRate = 30;

    public void settings() {
        size(300, 200, PConstants.P2D);
    }

    public void setup() {
        frameRate(frameRate);

        mediaExport = new MediaExport(100, frameRate, "mp4", "png", this);
        mediaExport.setOutputFolder(sketchPath());
        mediaExport.autoOpen(true);

        background(random(255));
        fillSketch();
    }


    public void draw() {
    }


    public void keyPressed() {
        switch (keyCode) {
            case 97: // F1
                mediaExport.toggleVideoRecording();
                break;
            case 98: // F2
                mediaExport.exportVideo(true);
                break;
            case 99: // F3
                mediaExport.captureScreenshot();
                break;
        }
    }

    public void mouseReleased() {
        fillSketch();
    }

    private void fillSketch() {
        strokeWeight(0.5f);
        stroke(255, 0, 0);
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