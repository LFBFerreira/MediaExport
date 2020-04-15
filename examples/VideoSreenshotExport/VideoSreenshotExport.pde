import com.hamoid.*;
import luis.ferreira.libraries.media.MediaExport;


MediaExport mediaExport;
boolean capture = false;


public void setup() {
  size(300, 200, PConstants.P2D);
  mediaExport = new MediaExport(100, 30, "mp4", "png", this);

  // optional
  mediaExport.setOutputFolder(sketchPath());

  background(random(255));
  fillSketch();
}


void draw() {
  // draw more stuff

  mediaExport.updateMedia();
}


void keyPressed() {
  switch (keyCode) {
  case 97: // F1
    mediaExport.toggleVideoRecording();
    break;
  case 98: // F2
    mediaExport.exportVideo();
    break;
  case 99: // F3
    mediaExport.captureScreenshotImmediate();
    break;
  case 100: // F4
    mediaExport.captureScreenshotNextFrame();
    break;
  }
}

void mouseReleased()
{
  fillSketch();
}

void fillSketch() {
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