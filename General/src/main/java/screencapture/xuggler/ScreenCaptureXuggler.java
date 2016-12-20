// http://stackoverflow.com/questions/5840455/capture-screen-with-mouse-pointer-using-xuggler
package screencapture.xuggler;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

public class ScreenCaptureXuggler {

    private static final String OUTPUT = "E:\\Netbeans Projects\\General\\General\\src\\main\\resources\\media\\xuggle.mp4";
    private static final String CURSOR = "/media/cursor.png";
    private static Dimension screenBounds;
    public static Image m_MouseIcon = null;

    /**
     * This will record a screen for the given time.
     *
     * @param args
     */
    public static void main(String[] args) {

        // Make a IMediaWriter to write the file.
        final IMediaWriter writer = ToolFactory.makeWriter(OUTPUT);
        start(writer);
    }

    /**
     * Start recording the screen.
     *
     * @param writer
     */
    private static void start(IMediaWriter writer) {

        screenBounds = Toolkit.getDefaultToolkit().getScreenSize();

        // Add one video stream, with id 0,
        // at position 0, with a fixed frame rate of FRAME_RATE.
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, screenBounds.width, screenBounds.height);

        // Save the start time because it will be used to encode the video properly.
        long startTime = System.nanoTime();

        // Attempt to load the mouse pointer.
        // TODO - add a second pointer for when the mouse was pressed.
        try {
            URL resource = ScreenCaptureXuggler.class.getResource(CURSOR);
            m_MouseIcon = ImageIO.read(resource);
        } catch (IOException e1) {
            System.out.println("Error: " + e1);
        }

        // Set the amount of time for the recorder to record.
        // TODO - replace with a start and stop method, no timer needed.
        long endTime = System.currentTimeMillis() + 5000;

        while (System.currentTimeMillis() < endTime) {
            // take the screenshot
            BufferedImage screenshot = getDesktopScreenshot();

            // Convert the screenshot to the right image type
            BufferedImage bgrScreen = convertToType(screenshot, BufferedImage.TYPE_3BYTE_BGR);

            // Draw the mouse pointer in the screen.
            Graphics2D bGr = bgrScreen.createGraphics();
            Point location = MouseInfo.getPointerInfo().getLocation();
            bGr.drawImage(m_MouseIcon, location.x, location.y, null);
            bGr.dispose();

            // Encode the image to stream #0.
            writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        }
        stop(writer);
    }

    /**
     * Stop recording.
     *
     * @param writer the mediaWriter that needs to be stopped.
     */
    private static void stop(IMediaWriter writer) {
        // Close the writer.
        writer.close();
    }

    /**
     * Convert the screenshot to the right image type
     *
     * @param sourceImage the source image.
     * @param targetType the type of image it needs to become
     *
     * @return the converted image.
     */
    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;

        // Ff the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } // Otherwise create a new image of the target type and draw the new image
        else {
            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;
    }

    /**
     * Take a screenshot of the desktop.
     *
     * @return the screenshot
     */
    private static BufferedImage getDesktopScreenshot() {
        try {
            Robot robot = new Robot();
            Rectangle captureSize = new Rectangle(screenBounds);
            return robot.createScreenCapture(captureSize);
        } catch (AWTException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }
}
