// http://stackoverflow.com/questions/5840455/capture-screen-with-mouse-pointer-using-xuggler
package screencapture.xuggler;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

public class ScreenCaptureXuggler {

    private static final double FRAME_RATE = 15;
    private static final int SECONDS_TO_RUN_FOR = 10;
    private static final String OUTPUT_FILENAME = "E:\\Netbeans Projects\\General\\General\\src\\main\\resources\\media\\XugglerRecording.mp4";
    private static Dimension screenBounds;
    public static Image m_MouseIcon = null;

    public static void main(String[] args) {
        // let's make a IMediaWriter to write the file.
        final IMediaWriter writer = ToolFactory.makeWriter(OUTPUT_FILENAME);

        screenBounds = Toolkit.getDefaultToolkit().getScreenSize();

        // We tell it we're going to add one video stream, with id 0,
        // at position 0, and that it will have a fixed frame rate of FRAME_RATE.
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, screenBounds.width, screenBounds.height);

        long startTime = System.nanoTime();

        for (int index = 0; index < SECONDS_TO_RUN_FOR * FRAME_RATE; index++) {

            // take the screen shot
            BufferedImage screen = getDesktopScreenshot();

            // convert to the right image type
            BufferedImage bgrScreen = convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);

            // encode the image to stream #0
            writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        }

        // tell the writer to close and write the trailer if  needed
        writer.close();

    }

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;

        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } // otherwise create a new image of the target type and draw the new image
        else {
            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;

    }

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
