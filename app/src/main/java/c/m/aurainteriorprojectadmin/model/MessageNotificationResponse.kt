package c.m.aurainteriorprojectadmin.model

import com.google.gson.annotations.SerializedName


data class MessageNotificationResponse(
    @SerializedName("message_id")
    val messageId: Long
)