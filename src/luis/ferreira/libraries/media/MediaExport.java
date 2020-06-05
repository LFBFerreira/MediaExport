package luis.ferreira.libraries.media;

import com.hamoid.*;
import processing.core.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import processing.pdf.*;


/**
 * MediaExport simplifies the process of media creation in Processing. It can export screenshots, videos, vector graphics
 * and a custom size image buffer.
 */
public class MediaExport {

    // Configuration

    private static String DATE_TIME_FORMAT = "dd_MM_yyyy-HH_mm_ss_SS";
    private static String DEFAULT_VIDEO_FORMAT = "mp4";
    private static String DEFAULT_SCREENSHOT_FORMAT = "png";
    private static String DEFAULT_VECTOR_FORMAT = "pdf";
    private static Boolean MEDIA_OPEN_AUTO = false;

    // variables

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private boolean isVideoInitialized = false;
    private boolean isRecordingVideo = false;
    private boolean hdBufferActive = false;
    private File outputFolder;
    private VideoExport videoExport;
    private PApplet parent;

    private int videoExportFramerate = 0;
    private int videoExportQuality = 0;
    private String videoExportFormat = "mp4";
    private String screenshotExportFormat = "png";
    private String hdExportFormat = "png";
    private String vectorExportFormat = "pdf";
    private String recordingName = "";
    private String outputFolderPath = "";
    private String videoSavePath = "";

    private boolean screenshotOnDraw = false;
    private boolean hdScreenshotOnDraw = false;

    private PGraphics hdBuffer;
    private PGraphicsPDF vectorBuffer;


    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Main constructor
     *
     * @param quality          video quality 1-100
     * @param framerate        video framerate
     * @param videoFormat      video format
     * @param screenshotFormat screenshot / HD format
     * @param vectorFormat     vector format
     * @param parent           parent sketch
     */
    public MediaExport(int quality, int framerate, String videoFormat, String screenshotFormat, String vectorFormat, PApplet parent) {
        this.parent = parent;
        this.videoExportFramerate = framerate;
        this.videoExportQuality = quality;
        this.videoExportFormat = removeDot(videoFormat);
        this.screenshotExportFormat = removeDot(screenshotFormat);
        this.vectorExportFormat = removeDot(vectorFormat);

        parent.registerMethod("pre", this);
        parent.registerMethod("post", this);

        setOutputFolder(parent.sketchPath());
    }

    /**
     * Video and screenshot constructor
     *
     * @param quality     video quality 1-100
     * @param framerate   video framerate
     * @param videoFormat video format
     * @param parent      parent sketch
     */
    public MediaExport(int quality, int framerate, String videoFormat, String screenshotFormat, PApplet parent) {
        this(quality, framerate, videoFormat, screenshotFormat, DEFAULT_VECTOR_FORMAT, parent);
    }

    /**
     * Video constructor
     *
     * @param quality     video quality 1-100
     * @param framerate   video framerate
     * @param videoFormat video format
     * @param parent      parent sketch
     */
    public MediaExport(int quality, int framerate, String videoFormat, PApplet parent) {
        this(quality, framerate, videoFormat, DEFAULT_SCREENSHOT_FORMAT, DEFAULT_VECTOR_FORMAT, parent);
    }

    /**
     * Screenshot / HD constructor
     *
     * @param screenshotFormat screenshot format
     * @param parent           parent sketch
     */
    public MediaExport(String screenshotFormat, PApplet parent) {
        this(100, 30, DEFAULT_VIDEO_FORMAT, screenshotFormat, DEFAULT_VECTOR_FORMAT, parent);
    }

    /**
     * Screenshot / HD and vector constructor
     *
     * @param screenshotFormat screenshot format
     * @param parent           parent sketch
     */
    public MediaExport(String screenshotFormat, String vectorFormat, PApplet parent) {
        this(100, 30, DEFAULT_VIDEO_FORMAT, screenshotFormat, vectorFormat, parent);
    }

    /**
     * Constructor with default parameters. Quality 100, framerate 30, video MP4, image PNG, vector PDF
     *
     * @param parent
     */
    public MediaExport(PApplet parent) {
        this(100, 30, DEFAULT_VIDEO_FORMAT, DEFAULT_SCREENSHOT_FORMAT, DEFAULT_VECTOR_FORMAT, parent);
    }


    // -----------------------------------------------------------------------------------------------------------------


    /**
     * Stops any ongoing recording and disposes of resources
     */
    public void dispose() {
        if (isVideoInitialized) {
            exportVideo(true);
        }

        if (hdBufferActive) {
            exportHDGraphics(true);
        }

//        disposeVideoBuffer();
//        disposeHDGraphics();
    }

    public void pre() {

    }

    public void post() {
        updateMedia();
    }

    // -----------------------------------------------------------------------------------------------------------------

    public void setOpenMediaAuto(boolean auto) {
        MEDIA_OPEN_AUTO = auto;
    }

    /**
     * Set the output folder
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
     * Saves the current frame to video or image if necessary
     */
    private void updateMedia() {
        if (screenshotOnDraw) {
            saveGraphics();
            screenshotOnDraw = false;
        }

        if (isRecordingVideo) {
            videoExport.saveFrame();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Video

    /**
     * Start a new video recording, or pauses an active recording
     */
    public void toggleVideoRecording() {

        if (!isVideoInitialized) {
            videoSavePath = getExportPath("video", videoExportFormat);
            System.out.println(String.format("Starting video recording to '%s'", videoSavePath));
            videoExport = new VideoExport(parent, videoSavePath);
            configureVideo(videoExport);
            videoExport.startMovie();
            isVideoInitialized = true;
        }

        isRecordingVideo = !isRecordingVideo;

        System.out.println(String.format("Video recording is %s", isRecordingVideo ? "On" : "Paused"));
    }

    /**
     * Stops an active recordings and saves the file
     */
    public void exportVideo(boolean dispose) {
        if (!isVideoInitialized) {
            System.out.println("There is no video being recorded.");
            return;
        }

        System.out.println(String.format("Stoping recording and saving video."));

        // reset control variables
        isVideoInitialized = false;
        isRecordingVideo = false;

        // export video
        videoExport.endMovie();

        if (MEDIA_OPEN_AUTO) {
            openMedia(new File(videoSavePath));
        }

        videoSavePath = "";

        if (dispose) {
            videoExport.dispose();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Screenshots

    /**
     * Saves a screenshot in the next saveFrame(...) call
     */
    public void captureScreenshot() {
        screenshotOnDraw = true;
    }

    /**
     * Saves a screenshot immediately
     */
    private String saveGraphics() {
        String fullPath = getExportPath("screenshot", screenshotExportFormat);

        System.out.println(String.format("Saving screenshot to '%s'", fullPath));

        parent.save(fullPath);

        if (MEDIA_OPEN_AUTO) {
            openMedia(new File(fullPath));
        }

        return fullPath;
    }

    // -----------------------------------------------------------------------------------------------------------------

    // HD export

    /**
     * Creates a new graphics buffer
     *
     * @param width          buffer width
     * @param height         buffer height
     * @param renderer       buffer renderer
     * @param smoothStrength antialiasing strenght (2 - 16)
     * @param optimizeStroke enables or disables OpenGL hint ENABLE_OPTIMIZED_STROKE
     * @return
     */
    public PGraphics getHDGraphics(int width, int height, String renderer, int smoothStrength, boolean optimizeStroke) {
        if (width <= 0 || height <= 0) {
            System.err.println("First set the image buffer size with 'setHDBufferSize(width, height, renderer)'");
            return null;
        }

        if (!renderer.equals(PConstants.P2D) && !renderer.equals(PConstants.P3D)) {
            System.err.println("Unsuported renderer. Please use P2D or P3D");
            return null;

        }

        System.out.println(String.format("Creating HD graphics buffer of %dx%d (%s)",
                width,
                height,
                renderer));

        hdBuffer = parent.createGraphics(width, height, String.valueOf(renderer));

        if (smoothStrength > 0) {
            hdBuffer.smooth(smoothStrength);
        }

        if (renderer.equals(PConstants.P3D)) {
            if (optimizeStroke) {
                hdBuffer.hint(PConstants.ENABLE_OPTIMIZED_STROKE);
            } else {
                hdBuffer.hint(PConstants.DISABLE_OPTIMIZED_STROKE);
            }
        }

        // needs to be cleared before use, otherwise output is empty
        // not sure why...
        hdBuffer.beginDraw();
        hdBuffer.clear();
        hdBuffer.endDraw();

        hdBufferActive = true;

        return hdBuffer;
    }

    /**
     * Creates a new graphics buffer
     *
     * @param width    buffer width
     * @param height   buffer height
     * @param renderer buffer renderer
     * @return
     */
    public PGraphics getHDGraphics(int width, int height, String renderer) {
        return getHDGraphics(width, height, renderer, 0, false);
    }

    /**
     * Export the contents of the HD graphics buffer
     */
    public void exportHDGraphics(boolean dispose) {
        String fullPath = getExportPath("hd", screenshotExportFormat);

        System.out.println(String.format("Saving HD graphics"));

        hdBuffer.endDraw();
        hdBuffer.save(fullPath);

        if (dispose) {
            System.out.println("Disposing of HD buffer.");
            hdBuffer.dispose();
            hdBuffer = null;
        }

        System.out.println("Saved to: " + fullPath);

        if (MEDIA_OPEN_AUTO) {
            openMedia(new File(fullPath));
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Vector

    /**
     * Creates a vector graphics buffer with beginRaw(...)
     *
     * @return
     */
    public PGraphicsPDF initializeVectorGraphics() {
        String fullPath = getExportPath("vector", vectorExportFormat);

        System.out.println(String.format("Saving Vector graphics to '%s'", fullPath));

        vectorBuffer = (PGraphicsPDF) parent.beginRaw(PConstants.PDF, fullPath);

//        vectorBuffer.hint(DISABLE_DEPTH_TEST);
//        vectorBuffer.hint(DISABLE_DEPTH_MASK);
//        vectorBuffer.hint(DISABLE_DEPTH_SORT);

        return vectorBuffer;
    }

    /**
     * Export the contents of the vector graphics buffer
     */
    public void exportVectorGraphics() {
        parent.endRaw();
        System.out.println(String.format("Vector graphics exported."));
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Configure video properties
     *
     * @param video
     */
    private void configureVideo(VideoExport video) {
        video.setFrameRate(videoExportFramerate);
        video.setDebugging(false);
        video.setLoadPixels(true);
        video.setQuality(videoExportQuality, 0);
    }

    /**
     * Generates the file export path
     *
     * @param rootname
     * @param extension
     * @return
     */
    private String getExportPath(String rootname, String extension) {
        LocalDateTime now = LocalDateTime.now();
        String filename = String.format("%s %s.%s", rootname, now.format(dateTimeFormatter), extension);
        return getMediaFullPath(filename);
    }

    /**
     * Joins the output folder and the media file name
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
     * Removes the existing dot from the provided extension
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

    /**
     * @param file
     */
    private void openMedia(File file) {
        if (!file.exists()) {
            System.err.println("The file '" + "' doesn't exist");
            return;
        }

        Desktop dt = Desktop.getDesktop();
        try {
            dt.open(file);
        } catch (IOException e) {
            System.err.println("Could not open the media automatically");
            e.printStackTrace();
        }catch (IllegalArgumentException e) {
            System.err.println("Could not open the media automatically");
            e.printStackTrace();
        }
    }
}

