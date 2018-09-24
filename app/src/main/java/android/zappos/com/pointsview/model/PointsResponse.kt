package android.zappos.com.pointsview.model

data class PointsResponse(var currentTier: String? = null, var currentPoints: Int? = 0, var pointsToNextTier: Int? = 500)