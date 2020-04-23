package c.m.aurainteriorprojectadmin.model

import com.google.gson.annotations.SerializedName


data class MessageNotification(
    @SerializedName("to")
    val to: String,
    @SerializedName("notification")
    val notification: Notification
)

data class Notification(
    @SerializedName("body")
    val body: String,
    @SerializedName("title")
    val title: String
)