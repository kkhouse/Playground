package io.github.samples.myapplication

import timber.log.Timber

const val logTag: String  =  "trackInfoTag"

class UserTrackingRepository constructor(
    private val mockAnalytics : MockAnalytics
) {
    fun postAdobeTrackState(screenId: String) {
        mockAnalytics.postAdobeTrackState(screenId)
    }

    fun postAdobeTrackAction(actionName: String) {
        mockAnalytics.postAdobeTrackAction(actionName)
    }

    fun postScreenOperationInfo(screenId : String) : Resource {
        mockAnalytics.postScreenOperationInfo(screenId)
        return Resource.Success
    }
}

//
class MockAnalytics {

    fun postAdobeTrackState(screenId: String) {
        Timber.tag(logTag).d("track state sent with $screenId arg")
    }

    fun postAdobeTrackAction(actionName: String) {
        Timber.tag(logTag).d("track action sent with $actionName arg")
    }

    fun postScreenOperationInfo(screenId : String) : Resource {
        Timber.tag(logTag).d("send track info with $screenId arg")
        return Resource.Success
    }
}

sealed class Resource {
    object Success : Resource()
}