package com.onenineeight.cteee;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import retrofit2.http.Body;

public class NotificationConverterClass {
    @SerializedName("NotificationType")
    @Expose
    private String notificationType;
    @SerializedName("Body")
    @Expose
    private NotificationBodyClass body;

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationBodyClass getBody() {
        return body;
    }

    public void setBody(NotificationBodyClass body) {
        this.body = body;
    }
}
