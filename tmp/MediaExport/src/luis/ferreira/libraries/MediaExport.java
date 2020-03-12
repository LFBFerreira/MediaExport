package luis.ferreira.libraries;

import com.hamoid.VideoExport;
import processing.core.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class MediaExport {

    private final String DATE_TIME_FORMAT = "dd_MM_yyyy_HH_mm_ss";

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private Boolean isVideoInitialized = false;
    private Boolean isRecording = false;
    private File outputFolder;
    private VideoExport videoExport;
    private PApplet parent;

    private int videoExportFramerate = 0;
    private int videoExportQuality = 0;
    private String videoExportFormat = "";
    private String screenshotExportFormat = "";
    private String recordingName = "";
    private String outputFolderPath = "";

    /**
     * Main constructor
     *
     * @param parent
     */
    public MediaExport(int quality, int framerate, String videoFormat, String screenshotFormat, PApplet parent) {
        this.parent = parent;
        this.videoExportFramerate = framerate;
        this.videoExportQuality = quality;
        this.videoExportFormat = removeDot(videoFormat);
        this.screenshotExportFormat = removeDot(screenshotFormat);
    }

    /**
     * Constructor with default parameters. Quality 100, framerate 30, video MP4 and image PNG
     *
     * @param parent
     */
    public MediaExport(PApplet parent) {
        this(100, 30, "mp4", "png", parent);
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

    public void setOutputFolder(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        outputFolderPath = directory.getAbsolutePath();
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void saveFrame() {
        if (!isRecording) {
            return;
        }
        videoExport.saveFrame();
    }

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

        isRecording = !isRecording;

        System.out.println(String.format("Video recording is %s", isRecording ? "On" : "Off"));
    }

    public void endCapture() {
        if (!isVideoInitialized) {
            System.out.println("There is no video being recorded");
            return;
        }

        isVideoInitialized = false;
        isRecording = false;

        System.out.println(String.format("Stoping recording and saving video."));
        videoExport.endMovie();
    }

    public void takeScreenshot() {
        LocalDateTime now = LocalDateTime.now();
        String filename = String.format("screenshot %s.%s", now.format(dateTimeFormatter), screenshotExportFormat);
        String fullPath = getMediaFullPath(filename);

        parent.save(fullPath);

        System.out.println(String.format("Screenshot saved the to '%s'", fullPath));
    }

    // -----------------------------------------------------------------------------------------------------------------

    private void configureVideo(VideoExport video) {
        video.setFrameRate(videoExportFramerate);
        video.setDebugging(false);
        video.setLoadPixels(true);
        video.setQuality(videoExportQuality, 0);
    }

    private String getMediaFullPath(String filename)
    {
        Path fullPath;

        if (!outputFolderPath.isEmpty())
        {
            fullPath = Paths.get(outputFolderPath, filename);
        }
        else
        {
            fullPath = Paths.get(parent.sketchPath(), filename);
        }

        return fullPath.toAbsolutePath().toString();
    }

    private String removeDot(String extension) {
        if (extension.charAt(0) == '.' && extension.length() > 2) {
            return extension.substring(1);
        } else {
            return extension;
        }
    }
}

