package android.zappos.com.pointsview.service

import android.zappos.com.pointsview.model.PointsResponse
import retrofit2.http.GET
import rx.Observable

interface PointsService {
    @GET("points.json")
    fun getPoints(): Observable<PointsResponse>
}