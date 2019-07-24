package i221.entity;

import i221.service.MessageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageTask implements Runnable {
    private  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private BufferedImage bufferedImage;
    private int caseId;
    private Rectangle rectangle;
    private MessageService messageService;
    static private Log log = LogFactory.getLog(ImageTask.class);

    public ImageTask(BufferedImage bufferedImage,
                     Rectangle rectangle,
                     int caseId,
                     MessageService messageService
                     ) {
        this.bufferedImage = bufferedImage;
        this.caseId = caseId;
        this.messageService = messageService;
        this.rectangle = rectangle;
    }

    @Override
    public void run() {
        TrackMessage trackMessage = null;
        try {
            ImageIO.write(rectangle != null? bufferedImage.getSubimage(rectangle.x, rectangle.y, rectangle.width, rectangle.height) : bufferedImage,
                    "jpg", byteArrayOutputStream);
            trackMessage =  new TrackMessage();
            trackMessage.setImage(Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
            trackMessage.setCaseId(caseId);

        } catch (IOException e) {
            log.error("process image exception:" + e.getMessage());
        }
        if (trackMessage != null) {
            messageService.sendMessage(trackMessage);
        }
    }
}
