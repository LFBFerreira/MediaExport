
import luis.ferreira.libraries.media.MediaExport;
import processing.pdf.*;


MediaExport mediaExport;
boolean capture = false;


void setup() {
  size(300, 200, PConstants.P2D);

  mediaExport = new MediaExport( "pdf", this);
  mediaExport.setOutputFolder(sketchPath());

  fillSketch(g);
}


void draw() {
  PGraphics imageBuffer = g;

  if (capture) {
    imageBuffer = mediaExport.initializeVectorGraphics();
    fillSketch(imageBuffer);
  }

  // draw more stuff

  if (capture) {
    mediaExport.exportVectorGraphics();
    capture = false;
  }
}

void keyPressed() {
  switch (keyCode) {
  case 97: // F1
    capture = true;
    break;
  }
}

void mousePressed() {
  fillSketch(g);
}

void fillSketch(PGraphics buffer) {
  int width = buffer.width;
  int height = buffer.height;

  buffer.beginDraw();

  buffer.background(random(255));

  buffer.strokeWeight(random(10));
  buffer.stroke(255, 0, 0);
  buffer.fill(random(255));
  buffer.circle(random(0, width), random(0, height), width / 2);

  buffer.strokeWeight(random(10));
  buffer.stroke(0, 255, 0);
  buffer.fill(random(255));
  buffer.rect(random(0, width), random(0, height), 20, width / 2);

  buffer.strokeWeight(random(10));
  buffer.stroke(0, 0, 255);
  buffer.fill(random(255));
  buffer.triangle(random(0, width), random(0, height), 
    random(0, width), random(0, height), 
    random(0, width), random(0, height));

  buffer.endDraw();
}
