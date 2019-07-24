package i221.service.impl;

import i221.entity.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import i221.service.TrackService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class TrackServiceImpl implements TrackService {
    private Log logger = LogFactory.getLog(TrackServiceImpl.class);
    private ThreadPoolExecutor imagePoolExecutor = new ThreadPoolExecutor(20, 30, 60, TimeUnit.SECONDS, new SynchronousQueue<>());
    private ThreadPoolExecutor taskPoolExecutor = new ThreadPoolExecutor(20, 30, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5));
    private Map<String, GrabTask> grabTaskMap = new ConcurrentHashMap<>();
    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    public void stopTrack(VideoControlTask videoControlTask) throws TrackException {
        GrabTask grabTask = videoControlTask.getGrabTask();
        if (null == grabTask) {
            throw new TrackException(-1, "no such task");
        }
        grabTask.setGrabTaskState(GrabTaskState.END);
    }

    public void trackFromRtsp(VideoControlTask videoControlTask) throws TrackException {
        //  Rectangle rectangle = new Rectangle(500, 0, 2000, 2000);
        Rectangle rectangle = null;
        ImageHandler imageHandler = new ImageHandler(new KafkaMessageHandler("face_" + videoControlTask.getDeviceInfo().getCaseId(), kafkaTemplate), imagePoolExecutor);
        GrabTask grabTask = new GrabTask(videoControlTask.getDeviceInfo(), taskPoolExecutor, imageHandler);

        videoControlTask.setGrabTask(grabTask);
        if (videoControlTask.getDeviceInfo().getX() != -1) {
            rectangle = new Rectangle(videoControlTask.getDeviceInfo().getX(), videoControlTask.getDeviceInfo().getY(),
                    videoControlTask.getDeviceInfo().getWidth(), videoControlTask.getDeviceInfo().getHeight());
            imageHandler.setRectangle(rectangle);
        }
        taskPoolExecutor.execute(grabTask);
    }

    public void syncTrack(VideoControlTask videoControlTask) {
        TrackSync trackSync = new TrackSync();
        trackSync.setCaseId(videoControlTask.getDeviceInfo().getCaseId());
        trackSync.setGrabTaskState(videoControlTask.getGrabTask().getGrabTaskState());
        trackSync.setId(videoControlTask.getGrabTask().getId());
        kafkaTemplate.send("face_status", trackSync);
    }
}
