package i221.entity;

import i221.config.DeviceInfo;
import i221.service.impl.ImageHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;

import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadPoolExecutor;

import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_BGR24;

public class GrabTask implements Runnable{
    private String rtsp;
    private int interval;;
    private FFmpegFrameGrabber grabber;
    private ImageHandler imageHandler;
    private ThreadPoolExecutor taskPoolExecutor;
    private int caseId;
    private int index = 0;
    private int id;
    private final int NULL_TIMEOUT = 3;
    private int timeout = NULL_TIMEOUT;
    private int reConnectTime = 10;
    private static Log logger = LogFactory.getLog(GrabTask.class);

    private  volatile GrabTaskState grabTaskState = GrabTaskState.WAIT;
    public GrabTask(DeviceInfo deviceInfo,
                    ThreadPoolExecutor taskPoolExecutor,
                    ImageHandler imageHandler) {
        this.rtsp = deviceInfo.getRtsp();
        grabber = new FFmpegFrameGrabber(rtsp);
        grabber.setPixelFormat(AV_PIX_FMT_BGR24);
        grabber.setOption("max_delay", String.valueOf(3000000));
        grabber.setOption("stimeout", String.valueOf(10000000));
        grabber.setOption("rtsp_transport", "tcp");
        this.interval = 25 * deviceInfo.getInterval();
        this.caseId = deviceInfo.getCaseId();
        this.taskPoolExecutor = taskPoolExecutor;
        this.imageHandler = imageHandler;
    }

    public GrabTaskState getGrabTaskState() {
        return grabTaskState;
    }

    public void setGrabTaskState(GrabTaskState grabTaskState) {
        this.grabTaskState = grabTaskState;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private void grab() {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        Frame frame = null;
        try {
            frame = grabber.grabImage();
        } catch (FrameGrabber.Exception e) {
            logger.error(e.getMessage());
        }

        if (frame == null) {
            timeout--;
            if (timeout <= 0) {
                timeout = 0;
                try {
                    grabber.stop();
                } catch (FrameGrabber.Exception e) {
                }
                grabTaskState = GrabTaskState.WAIT;
            }
            return;
        }
        timeout = NULL_TIMEOUT;

        int flag = index % interval;
        index = (index + 1) % interval;
        if (flag != 0) {
            return;
        }
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        if (bufferedImage != null) {
            imageHandler.processImage(caseId, bufferedImage);
            id++;
        }

    }

    @Override
    public void run() {
        if (grabTaskState == GrabTaskState.WAIT) {
            try {
                grabber.start();
                grabTaskState = GrabTaskState.ON;
            } catch (FrameGrabber.Exception e) {
                logger.error("start error " + e.getMessage());
            }
        }

        if (grabTaskState != GrabTaskState.END) {
            if (grabTaskState == GrabTaskState.ON) {
                grab();
            } else {
                logger.error("wait seconds");
            }
            taskPoolExecutor.execute(this);
        } else  {
            System.out.println("grabber task stop");
            try {
                grabber.stop();
            } catch (FrameGrabber.Exception e) {
                logger.error("stop error" + e.getMessage());
            }
        }
    }

}
