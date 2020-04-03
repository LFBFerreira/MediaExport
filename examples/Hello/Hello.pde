import luis.ferreira.libraries.media.*;
import com.hamoid.VideoExport;

MediaExport mediaExport;

void setup() {
    size(300, 200, PConstants.P2D);
    mediaExport = new MediaExport(100, 30, ".mp4", "png", this);
}


void draw() {
    background(frameCount % 255);
    mediaExport.saveFrame();
}


void keyPressed() {
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