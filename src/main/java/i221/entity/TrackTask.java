package i221.entity;

import i221.repository.VideoImageRepo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.Optional;

public class TrackTask implements Runnable {
    final static Log logger = LogFactory.getLog(TrackTask.class);

    private int caseId;
    private int id;
    private  int personNumOld;
    private int sameCount;
    private boolean record;
    private int recordNum;
    private String saveDir;
    private String kafkaTopic;
    private i221.repository.VideoImageRepo videoImageRepo;

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public int getId() {
        return id;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public VideoImageRepo getVideoImageRepo() {
        return videoImageRepo;
    }

    public void setVideoImageRepo(VideoImageRepo videoImageRepo) {
        this.videoImageRepo = videoImageRepo;
    }

    private KafkaTemplate<Object, Object> template;

    public KafkaTemplate<Object, Object> getTemplate() {
        return template;
    }

    public void setTemplate(KafkaTemplate<Object, Object> template) {
        this.template = template;
    }

    public VideoImage processImage(int id) throws InterruptedException {
        int fetchInx = id;
        Optional<VideoImage> videoImageOptional = null;
        int timeout = 30;
        do {
            videoImageOptional = videoImageRepo.findById(new CaseData(caseId, id));
            if (!videoImageOptional.isPresent()) {
                timeout--;
                if (timeout <= 0) {
                    System.out.println("map find  " +  fetchInx + "timeout");
                    return null;
                }
                Thread.sleep(1000);
            }
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
        } while (!videoImageOptional.isPresent());
        if (videoImageOptional == null || !videoImageOptional.isPresent()) {
            return null;
        }
        return videoImageOptional.get();
    }

    @Override
    public void run() {
        boolean start = false;
        while (true) {
            VideoImage videoImage = null;
            try {
                videoImage = processImage(id++);
            } catch (InterruptedException e) {
                break;
            }
            if (videoImage == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }

            //baiduProcess(Base64.getDecoder().decode(videoImage.getData()), rectangle, id);
            TrackMessage trackMessage = new TrackMessage();
            trackMessage.setImage(videoImage.getData());
            if (!start) {
               // createServerTrackTask(trackMessage);
                //System.out.println("create track task");
                start = true;
            }
            template.send(kafkaTopic, trackMessage);
        }
        System.out.println("baidu task exit");
    }
}
