package i221.service.impl;
import i221.entity.ImageTask;
import i221.entity.TrackMessage;
import i221.service.MessageService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadPoolExecutor;

public class ImageHandler
{
    private ThreadPoolExecutor threadPoolExecutor;
    private  MessageService messageService;
    private Rectangle rectangle;
    public ImageHandler(MessageService messageService, ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.messageService = messageService;
    }

    public void processImage(int caseId, BufferedImage image) {
        ImageTask imageTask = new ImageTask(image, rectangle, caseId, messageService);
        threadPoolExecutor.execute(imageTask);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }
}
