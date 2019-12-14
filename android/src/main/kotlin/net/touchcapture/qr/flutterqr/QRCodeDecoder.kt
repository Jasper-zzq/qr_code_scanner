package me.hetian.flutter_qr_reader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.zxing.*
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import java.util.*

/**
 * 描述:解析二维码图片
 */
object QRCodeDecoder {
    val HINTS: MutableMap<DecodeHintType, Any?> = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java)
    /**
     * 同步解析本地图片二维码。该方法是耗时操作，请在子线程中调用。
     *
     * @param picturePath 要解析的二维码图片本地路径
     * @return 返回二维码图片里的内容 或 null
     */
    fun syncDecodeQRCode(picturePath: String): String? {
        return syncDecodeQRCode(getDecodeAbleBitmap(picturePath))
    }

    /**
     * 同步解析bitmap二维码。该方法是耗时操作，请在子线程中调用。
     *
     * @param bitmap 要解析的二维码图片
     * @return 返回二维码图片里的内容 或 null
     */
    fun syncDecodeQRCode(bitmap: Bitmap?): String? {
        var result: Result? = null
        var source: RGBLuminanceSource? = null
        return try {
            val width = bitmap!!.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            source = RGBLuminanceSource(width, height, pixels)
            result = MultiFormatReader().decode(BinaryBitmap(HybridBinarizer(source)), HINTS)
            result.text
        } catch (e: Exception) {
            e.printStackTrace()
            if (source != null) {
                try {
                    result = MultiFormatReader().decode(BinaryBitmap(GlobalHistogramBinarizer(source)), HINTS)
                    return result.text
                } catch (e2: Throwable) {
                    e2.printStackTrace()
                }
            }
            null
        }
    }

    /**
     * 将本地图片文件转换成可解码二维码的 Bitmap。为了避免图片太大，这里对图片进行了压缩。感谢 https://github.com/devilsen 提的 PR
     *
     * @param picturePath 本地图片文件路径
     * @return
     */
    private fun getDecodeAbleBitmap(picturePath: String): Bitmap? {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(picturePath, options)
            var sampleSize = options.outHeight / 400
            if (sampleSize <= 0) {
                sampleSize = 1
            }
            options.inSampleSize = sampleSize
            options.inJustDecodeBounds = false
            BitmapFactory.decodeFile(picturePath, options)
        } catch (e: Exception) {
            null
        }
    }

    init {
        val allFormats: ArrayList<BarcodeFormat> =ArrayList()

        allFormats.add(BarcodeFormat.AZTEC)
        allFormats.add(BarcodeFormat.CODABAR)
        allFormats.add(BarcodeFormat.CODE_39)
        allFormats.add(BarcodeFormat.CODE_93)
        allFormats.add(BarcodeFormat.CODE_128)
        allFormats.add(BarcodeFormat.DATA_MATRIX)
        allFormats.add(BarcodeFormat.EAN_8)
        allFormats.add(BarcodeFormat.EAN_13)
        allFormats.add(BarcodeFormat.ITF)
        allFormats.add(BarcodeFormat.MAXICODE)
        allFormats.add(BarcodeFormat.PDF_417)
        allFormats.add(BarcodeFormat.QR_CODE)
        allFormats.add(BarcodeFormat.RSS_14)
        allFormats.add(BarcodeFormat.RSS_EXPANDED)
        allFormats.add(BarcodeFormat.UPC_A)
        allFormats.add(BarcodeFormat.UPC_E)
        allFormats.add(BarcodeFormat.UPC_EAN_EXTENSION)
        HINTS[DecodeHintType.TRY_HARDER] = BarcodeFormat.QR_CODE
        HINTS[DecodeHintType.POSSIBLE_FORMATS] = allFormats
        HINTS[DecodeHintType.CHARACTER_SET] = "utf-8"
    }
}