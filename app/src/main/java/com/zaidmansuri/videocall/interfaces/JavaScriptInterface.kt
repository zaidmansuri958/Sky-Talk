package com.zaidmansuri.videocall.interfaces

import android.webkit.JavascriptInterface
import com.zaidmansuri.videocall.activities.VideoActivity

class JavaScriptInterface(val videoActivity: VideoActivity) {
    @JavascriptInterface
    public fun onPeerConnected(){
        videoActivity.onPeerConnected()
    }

}