package hoang.graduation.share.constant.rm;

public class RabbitQueueMessage {
    public static final String QUEUE_SEND_CREATE_USER = "QUEUE_SEND_CREATE_USER";
    public static final String QUEUE_SEND_UPDATE_USER = "QUEUE_SEND_UPDATE_USER";
    public static final String QUEUE_SEND_UPDATE_STATUS_USER = "QUEUE_SEND_UPDATE_STATUS_USER";
    public static final String QUEUE_SEND_SCHEDULE_UPDATE_STATUS_USER = "QUEUE_SEND_UPDATE_STATUS_USER";

    public static final String QUEUE_SEND_CREATE_CLASS= "QUEUE_SEND_CREATE_CLASS";
    public static final String QUEUE_SEND_UPDATE_CLASS= "QUEUE_SEND_UPDATE_CLASS";
    public static final String QUEUE_SEND_UPDATE_SLOT_CLASS= "QUEUE_SEND_UPDATE_SLOT_CLASS";
    public static final String QUEUE_SEND_DELETE_CLASS= "QUEUE_SEND_DELETE_CLASS";

    public static final String QUEUE_SEND_CREATE_EXAM= "QUEUE_SEND_CREATE_EXAM";
    public static final String QUEUE_SEND_UPDATE_EXAM= "QUEUE_SEND_UPDATE_EXAM";
    public static final String QUEUE_SEND_DELETE_EXAM= "QUEUE_SEND_DELETE_EXAM";

    public static final String QUEUE_SEND_CREATE_EXAM_SESSION= "QUEUE_SEND_CREATE_EXAM_SESSION";
    public static final String QUEUE_SEND_UPDATE_EXAM_SESSION= "QUEUE_SEND_UPDATE_EXAM_SESSION";
    public static final String QUEUE_SEND_DELETE_EXAM_SESSION= "QUEUE_SEND_DELETE_EXAM_SESSION";

    public static final String QUEUE_SEND_SUBMIT_EXAM_SESSION= "QUEUE_SEND_SUBMIT_EXAM_SESSION";

    public static final String QUEUE_SEND_SCHEDULE_PREMIUM_EXPIRATED= "QUEUE_SEND_SCHEDULE_PREMIUM_EXPIRATED";
}
