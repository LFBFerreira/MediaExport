package luis.ferreira.libraries.media;

import com.hamoid.*;
import processing.core.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import processing.pdf.*;

public class MediaExport {

    // Configuration
    private final String DATE_TIME_FORMAT = "dd_MM_yyyy_HH_mm_ss";

    // variables
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private Boolean isVideoInitialized = false;
    private Boolean isRecordingVideo = false;
    private File outputFolder;
    private VideoExport videoExport;
    private PApplet parent;

    private int videoExportFramerate = 0;
    private int videoExportQuality = 0;
    private String videoExportFormat = "";
    private String screenshotExportFormat = "";
    private String pdfExportFormat = "";
    private String recordingName = "";
    private String outputFolderPath = "";

    private boolean screenshotOnDraw = false;
    private boolean capturingPDF = false;
    private int pdfFrameBuffer = 0;

    private PGraphics hdBuffer;
    private int hdBufferWidth = 0;
    private int hdBufferHeight = 0;
    private String hdBufferRenderer;

    /**
     * Main constructor
     *
     * @param parent
     */
    public MediaExport(int quality, int framerate, String videoFormat, String screenshotFormat, String pdfFormat, PApplet parent) {
        this.parent = parent;
        this.videoExportFramerate = framerate;
        this.videoExportQuality = quality;
        this.videoExportFormat = removeDot(videoFormat);
        this.screenshotExportFormat = removeDot(screenshotFormat);
        this.pdfExportFormat = removeDot(pdfFormat);
    }

    /**
     * Constructor with default parameters. Quality 100, framerate 30, video MP4 and image PNG
     *
     * @param parent
     */
    public MediaExport(PApplet parent) {
        this(100, 30, "mp4", "png", "pdf", parent);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Stops any ongoing recording and disposes of resources
     */
    public void dispose() {
        if (isVideoInitialized) {
            endCapture();
        }

        videoExport.dispose();
    }


    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Sets a new output folder
     *
     * @param path
     */
    public void setOutputFolder(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        outputFolderPath = directory.getAbsolutePath();
    }

    /**
     * Sets a new output folder
     *
     * @param path
     */
    public void setOutputFolder(Path path) {
        setOutputFolder(path.toAbsolutePath().toString());
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Tells if there is an active recording or not
     *
     * @return
     */
    public boolean isRecording() {
        return isRecordingVideo;
    }

    /**
     * Saves the current frame
     */
    public void saveFrame() {
        if (screenshotOnDraw) {
            takeScreenshot();
            screenshotOnDraw = false;
        }

        if (capturingPDF) {
            pdfFrameBuffer--;
            if (pdfFrameBuffer == 0) {
                endPdfCapture();
            }
        }

        if (isRecordingVideo) {
            videoExport.saveFrame();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Video

    /**
     * Start a video recording, or pauses an active recording
     */
    public void toggleVideoRecording() {
        LocalDateTime now = LocalDateTime.now();

        recordingName = String.format("recording %s.%s", now.format(dateTimeFormatter), videoExportFormat);

        String fullPath = getMediaFullPath(recordingName);

        if (!isVideoInitialized) {
            System.out.println(String.format("Starting video recording to '%s'", fullPath));
            videoExport = new VideoExport(parent, fullPath);
            configureVideo(videoExport);
            videoExport.startMovie();
            isVideoInitialized = true;
        }

        isRecordingVideo = !isRecordingVideo;

        System.out.println(String.format("Video recording is %s", isRecordingVideo ? "On" : "Off"));
    }

    /**
     * Stops an active recordings and saves the file
     */
    public void endCapture() {
        if (!isVideoInitialized) {
            System.out.println("There is no video being recorded");
            return;
        }

        isVideoInitialized = false;
        isRecordingVideo = false;

        System.out.println(String.format("Stoping recording and saving video."));
        videoExport.endMovie();
    }


    // -----------------------------------------------------------------------------------------------------------------

    // Screenshots

    public void takeScreenshotNextFrame() {
        screenshotOnDraw = true;
    }

    /**
     * Saves a screenshot
     */
    public void takeScreenshot() {
        LocalDateTime now = LocalDateTime.now();
        String filename = String.format("screenshot %s.%s", now.format(dateTimeFormatter), screenshotExportFormat);
        String fullPath = getMediaFullPath(filename);

        parent.save(fullPath);

        System.out.println(String.format("Screenshot saved the to '%s'", fullPath));
    }

    private void takeScreenshotOnDraw() {
        LocalDateTime now = LocalDateTime.now();
        String filename = String.format("screenshot %s.%s", now.format(dateTimeFormatter), screenshotExportFormat);
        String fullPath = getMediaFullPath(filename);

        parent.save(fullPath);

        System.out.println(String.format("Screenshot saved the to '%s'", fullPath));

        screenshotOnDraw = false;
    }

    // -----------------------------------------------------------------------------------------------------------------

    // HD export

    public PGraphics getHDBuffer(int smoothStrength, boolean optimizeStroke) {
        if (hdBufferWidth <= 0 || hdBufferHeight <= 0 || hdBufferRenderer.isEmpty()) {
            System.err.println("First set the image buffer size with 'setHDBufferSize(width, height, renderer)'");
            return null;
        }

        System.out.println(String.format("Creating HD buffer of %dx%d (%s)",
                hdBufferWidth,
                hdBufferHeight,
                hdBufferRenderer));

        hdBuffer = parent.createGraphics(hdBufferWidth, hdBufferHeight, hdBufferRenderer);

        if (smoothStrength > 0) {
            hdBuffer.smooth(smoothStrength);
        }

        if (optimizeStroke) {
            hdBuffer.hint(PConstants.DISABLE_OPTIMIZED_STROKE);
        }

        // needs to be cleared before use, otherwise output is empty
        // not sure why...
        hdBuffer.beginDraw();
        hdBuffer.clear();
        hdBuffer.endDraw();

        return hdBuffer;
    }

    public void exportHDBuffer() {
        String fullPath = getExportPath("hd");

        System.out.println(String.format("Saving HD buffer to '%s'", fullPath));

        hdBuffer.save(fullPath);
    }

    public void setHDBufferSize(int width, int height, String renderer) {
        hdBufferWidth = width;
        hdBufferHeight = height;
        hdBufferRenderer = renderer;
    }

    public void disposeHDBuffer() {
        System.out.println("Disposing of HD buffer.");
        hdBuffer.dispose();
        hdBuffer = null;
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Vector

    /**
     *
     */
    public void startPdfCatpure() {
        startPdfCapture(1);
    }

    /**
     * @param numFrames
     */
    private void startPdfCapture(int numFrames) {
        LocalDateTime now = LocalDateTime.now();
        String filename = String.format("vector %s.%s", now.format(dateTimeFormatter), pdfExportFormat);
        String fullPath = getMediaFullPath(filename);

        System.out.println(String.format("Starting PDF recording to '%s' (%d frame)", fullPath, numFrames));

        pdfFrameBuffer = numFrames;

//        parent.beginRecord(PConstants.PDF, fullPath);

        PGraphicsPDF pdf = (PGraphicsPDF) parent.beginRaw(PConstants.PDF, fullPath);

        // set default Illustrator stroke styles and paint background rect.
//        pdf.strokeJoin(PConstants.MITER);
//        pdf.strokeCap(PConstants.SQUARE);
//        pdf.fill(0);
//        pdf.noStroke();
        //pdf.rect(255, 255, parent.width, parent.height);

        capturingPDF = true;
    }

    /**
     *
     */
    public void endPdfCapture() {
        parent.endRaw();

        capturingPDF = false;

        System.out.println(String.format("PDF capture stoppped"));
    }

    /**
     * @return
     */
    public boolean isCapturingPDF() {
        return capturingPDF;
    }

    // -----------------------------------------------------------------------------------------------------------------

    private void configureVideo(VideoExport video) {
        video.setFrameRate(videoExportFramerate);
        video.setDebugging(false);
        video.setLoadPixels(true);
        video.setQuality(videoExportQuality, 0);
    }

    private String getExportPath(String prefix) {
        LocalDateTime now = LocalDateTime.now();
        String filename = String.format("%s %s.%s", prefix, now.format(dateTimeFormatter), screenshotExportFormat);
        return getMediaFullPath(filename);
    }

    /**
     * concats the output folder and the media file name
     *
     * @param filename
     * @return
     */
    private String getMediaFullPath(String filename) {
        Path fullPath;

        if (!outputFolderPath.isEmpty()) {
            fullPath = Paths.get(outputFolderPath, filename);
        } else {
            fullPath = Paths.get(parent.sketchPath(), filename);
        }

        return fullPath.toAbsolutePath().toString();
    }

    /**
     * Removes a eventual dot from the providede extension
     *
     * @param extension
     * @return
     */
    private String removeDot(String extension) {
        if (extension.charAt(0) == '.' && extension.length() > 2) {
            return extension.substring(1);
        } else {
            return extension;
        }
    }
}

