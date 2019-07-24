package i221.entity;

import i221.config.DeviceInfo;

public class VideoControlTask {
    private TaskStatus taskStatus = TaskStatus.STOP;
    private DeviceInfo deviceInfo;
    private GrabTask grabTask;

    public GrabTask getGrabTask() {
        return grabTask;
    }

    public void setGrabTask(GrabTask grabTask) {
        this.grabTask = grabTask;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
}
