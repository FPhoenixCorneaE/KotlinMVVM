package com.wkz.arclayout

import android.graphics.Paint
import android.graphics.Path

interface ClipPathManager {
    fun createMask(width: Int, height: Int): Path
    val shadowConvexPath: Path?
    fun setupClipLayout(width: Int, height: Int)
    val paint: Paint
    fun requiresBitmap(): Boolean
}