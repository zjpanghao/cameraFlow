package i221.web;
import i221.config.CameraDevice;
import i221.config.DeviceInfo;
import i221.entity.TaskStatus;
import i221.entity.VideoControlTask;
import i221.entity.TrackException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import i221.service.TrackService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Controller
public class CameraFlowController {
    private Log log = LogFactory.getLog(CameraFlowController.class);
    private boolean inited = false;
    private int syncInx = 0;

    @Autowired
    private TrackService trackService;
    @Autowired
    private CameraDevice cameraDevice;

    final private Map<Integer, VideoControlTask> rtspMap = new HashMap();

    private Map<Integer, Boolean> capStatus = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        for (Map<String, String> map : cameraDevice.getDevice()) {
            for(String key : map.keySet()) {
                VideoControlTask videoControlTask = new VideoControlTask();
                DeviceInfo deviceInfo = new DeviceInfo();
                if (map.get("x") != null) {
                    deviceInfo.setX(Integer.parseInt(map.get("x")));
                }
                if (map.get("y") != null) {
                    deviceInfo.setY(Integer.parseInt(map.get("y")));
                }
                if (map.get("width") != null) {
                    deviceInfo.setWidth(Integer.parseInt(map.get("width")));
                }
                if (map.get("height") != null) {
                    deviceInfo.setHeight(Integer.parseInt(map.get("height")));
                }
                if (map.get("interval") != null) {
                    deviceInfo.setInterval(Integer.parseInt(map.get("interval")));
                }
                deviceInfo.setCaseId(Integer.parseInt(map.get("caseId")));
                deviceInfo.setRtsp(map.get("rtsp"));
                videoControlTask.setDeviceInfo(deviceInfo);
                rtspMap.put(Integer.parseInt(map.get("caseId")), videoControlTask);
            }

        }
    }

    @RequestMapping("/")
    public int getTotalPersonCount() {
        return 10;
    }

    @RequestMapping("/test/save")
    public int testSave() {
        return 10;
    }

    @RequestMapping("/test/track/start")
    public int testTrack(Integer caseId) {

        //String rtspPath = "rtsp://admin:ky221data@192.168.4.183:554/h264/ch1/main/av_stream";
        if (caseId == null)  caseId = 11;
        VideoControlTask videoControlTask = rtspMap.get(caseId);
        if (videoControlTask == null || videoControlTask.getTaskStatus() == TaskStatus.START) {
            log.error("no such task info or task already start");
            return -1;
        }

        try {
            trackService.trackFromRtsp(videoControlTask);
            videoControlTask.setTaskStatus(TaskStatus.START);
        } catch (TrackException e) {
            log.error(e.getMsg());
            return -1;
        }
        return 10;
    }

    @RequestMapping("/test/track/stop")
    public int testTrackStop(Integer caseId) {
        //String rtspPath = "rtsp://admin:ky221data@192.168.4.183:554/h264/ch1/main/av_stream";
        if (caseId == null) caseId = 11;
        VideoControlTask videoControlTask = rtspMap.get(caseId);
        if (videoControlTask == null || videoControlTask.getTaskStatus() != TaskStatus.START) {
            log.error("no such task info or task not start");
            return -1;
        }
        try {
            trackService.stopTrack(videoControlTask);
            videoControlTask.setTaskStatus(TaskStatus.STOP);
        } catch (TrackException e) {
            log.info(e.getMsg());
            return -1;
        }
        return 0;
    }

    //每30秒执行一次
    @Scheduled(fixedRate = 1000 * 300000, initialDelay = 20000)
    public void startGrab(){
        if (!inited) {
            for (Map.Entry<Integer, VideoControlTask> entry  : rtspMap.entrySet()) {
                testTrack(entry.getKey());
            }
//            testTrack(11, 1, null, null, null, null);
//            testTrack(183, 1, null, null, null, null);
            inited = true;
        }
    }

    //每30秒执行一次
    @Scheduled(fixedRate = 10*1000, initialDelay = 30000)
    public void syncCommand(){
       int inx = 0;
        for (Map.Entry<Integer, VideoControlTask> entry  : rtspMap.entrySet()) {
            if (inx++ == syncInx) {
                VideoControlTask videoControlTask = entry.getValue();
                if (videoControlTask.getTaskStatus() != TaskStatus.STOP) {
                    trackService.syncTrack(videoControlTask);
                    syncInx++;
                    return;
                }
            }
        }
        syncInx = 0;
    }
}
