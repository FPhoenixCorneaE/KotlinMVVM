package com.fphoenixcorneae.bottomnavigation

import androidx.annotation.Keep

@Keep
class BottomNavigationItem(
    var title: CharSequence?,
    var color: Int,
    var imageResource: Int
) {
    var imageResourceActive = 0
}
