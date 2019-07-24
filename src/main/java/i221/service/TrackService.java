package i221.service;

import i221.entity.TrackException;
import i221.entity.VideoControlTask;
public interface TrackService {
    void trackFromRtsp(VideoControlTask videoControlTask) throws TrackException;
    void stopTrack(VideoControlTask videoControlTask) throws TrackException;
    void syncTrack(VideoControlTask videoControlTask);
}
