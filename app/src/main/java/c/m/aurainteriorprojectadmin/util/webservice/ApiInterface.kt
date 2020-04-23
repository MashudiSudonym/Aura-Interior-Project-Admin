package c.m.aurainteriorprojectadmin.util.webservice

import c.m.aurainteriorprojectadmin.model.MessageNotification
import c.m.aurainteriorprojectadmin.model.MessageNotificationResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {
    @Headers(
        "Content-Type: application/json",
        "Authorization: key=AAAA7RidnI0:APA91bGt-IpbiAzzGN8zm8auj1LQgCY8rMVU4-IsA5SpV7uABdle1tBILByenk7Sc2eZ5e6-kMlLVcU6E1DkNh8O8lvuI6O_8i9QuIuE_iqSFGH9mEWXtOF4EfcIcZMZsxf4EV6XWDSy"
    )
    @POST("fcm/send")
    suspend fun postMessage(@Body messageNotification: MessageNotification): MessageNotificationResponse
}