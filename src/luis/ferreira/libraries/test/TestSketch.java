package luis.ferreira.libraries.test;

import com.hamoid.VideoExport;
import luis.ferreira.libraries.MediaExport;
import processing.core.PApplet;
import processing.core.PConstants;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class TestSketch extends PApplet {

    MediaExport mediaExport;


    public void settings() {
        size(300, 200, PConstants.P2D);
    }


    public void setup() {
        mediaExport = new MediaExport(100, 30, ".mp4", "png", this);

        //mediaExport.setOutputFolder("C:\\Users\\luisf\\Downloads");
    }


    public void draw() {
        background(frameCount % 255);
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
        }
    }
}