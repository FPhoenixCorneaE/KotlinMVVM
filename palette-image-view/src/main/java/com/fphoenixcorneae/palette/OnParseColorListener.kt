package com.fphoenixcorneae.palette

/**
 * @desc 解析图片颜色监听器
 */
interface OnParseColorListener {
    /**
     * 解析完成
     */
    fun onComplete(paletteImageView: PaletteImageView?)

    /**
     * 解析失败
     */
    fun onFail()
}