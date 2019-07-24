import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class TestPersonCount {
    @Test
    public void testOpencv() {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File("d:\\litingwei.JPG"));
            DataBuffer dataBuffer = bufferedImage.getData().getDataBuffer();
            byte [] data = ((DataBufferByte)dataBuffer).getData();
            System.out.println(data.length);
            System.out.println(bufferedImage.getType());
            System.out.println(bufferedImage.getWidth() *bufferedImage.getHeight() *3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
