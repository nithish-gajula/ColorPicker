package com.nithishgajula.colorpicker

import android.graphics.Color
import kotlin.math.*
import androidx.core.graphics.toColorInt

object ColorConverter {

    data class FormattedColorData(
        val hex: String,
        val rgbInt: String,
        val rgbPercent: String,
        val binary: String,
        val cmy: String,
        val cmyk: String,
        val hsl: String,
        val hsv: String,
        val ryb: String,
        val lab: String,
        val xyz: String
    )

    fun fromHex(hex: String): FormattedColorData {
        val argbHex = normalizeToARGB(hex)
        val colorInt = argbHex.toColorInt()

        Color.alpha(colorInt)
        val r = Color.red(colorInt)
        val g = Color.green(colorInt)
        val b = Color.blue(colorInt)

        // --- Conversions ---
        val rgbInt = "($r, $g, $b)"
        val rgbPercent = "(${toPercent(r)}, ${toPercent(g)}, ${toPercent(b)})"
        val binary = "${toBinary(r)} ${toBinary(g)} ${toBinary(b)}"

        val cmy = rgbToCMY(r, g, b).let { (c, m, y) ->
            "(${toPercent(c)}, ${toPercent(m)}, ${toPercent(y)})"
        }

        val cmyk = rgbToCMYK(r, g, b).let { (c, m, y, k) ->
            "(${toPercent(c)}, ${toPercent(m)}, ${toPercent(y)}, ${toPercent(k)})"
        }

        val hsl = rgbToHSL(r, g, b).let { (h, s, l) ->
            "(${h.roundToInt()}°, ${toPercent(s)}, ${toPercent(l)})"
        }

        val hsv = rgbToHSV(r, g, b).let { (h, s, v) ->
            "(${h.roundToInt()}°, ${toPercent(s)}, ${toPercent(v)})"
        }

        val ryb = rgbToRYB(r, g, b).let { (rr, yy, bb) ->
            "(${toPercent(rr)}, ${toPercent(yy)}, ${toPercent(bb)})"
        }

        val xyz = rgbToXYZ(r, g, b).let { (x, y, z) ->
            "(${toPercent(x / 100)}, ${toPercent(y / 100)}, ${toPercent(z / 100)})"
        }

        val lab = xyzToLAB(rgbToXYZ(r, g, b)).let { (L, A, B) ->
            "(${L.roundToInt()}, ${A.roundToInt()}, ${B.roundToInt()})"
        }

        return FormattedColorData(
            hex = argbHex.uppercase(),
            rgbInt = rgbInt,
            rgbPercent = rgbPercent,
            binary = binary,
            cmy = cmy,
            cmyk = cmyk,
            hsl = hsl,
            hsv = hsv,
            ryb = ryb,
            lab = lab,
            xyz = xyz
        )
    }

    // --- Helpers ---
    private fun normalizeToARGB(hex: String): String {
        var h = hex.trim().uppercase()
        if (!h.startsWith("#")) h = "#$h"
        // If #RRGGBB, prepend full alpha
        if (h.length == 7) {
            h = "#FF${h.substring(1)}"
        }
        return h
    }

    private fun toPercent(value: Int): String = "${((value / 255f) * 100).roundToInt()}%"
    private fun toPercent(value: Float): String = "${(value * 100).roundToInt()}%"
    private fun toPercent(value: Double): String = "${(value * 100).roundToInt()}%"
    private fun toBinary(value: Int): String = Integer.toBinaryString(value).padStart(8, '0')

    private fun rgbToCMY(r: Int, g: Int, b: Int): Triple<Float, Float, Float> {
        val c = 1 - (r / 255f)
        val m = 1 - (g / 255f)
        val y = 1 - (b / 255f)
        return Triple(c, m, y)
    }

    private data class Quadruple<A,B,C,D>(val first:A,val second:B,val third:C,val fourth:D)

    private fun rgbToCMYK(r: Int, g: Int, b: Int): Quadruple<Float, Float, Float, Float> {
        val c = 1 - (r / 255f)
        val m = 1 - (g / 255f)
        val y = 1 - (b / 255f)
        val k = min(c, min(m, y))
        return if (k >= 1f) {
            Quadruple(0f, 0f, 0f, 1f)
        } else {
            Quadruple(
                (c - k) / (1 - k),
                (m - k) / (1 - k),
                (y - k) / (1 - k),
                k
            )
        }
    }

    private fun rgbToHSL(r: Int, g: Int, b: Int): FloatArray {
        val hsv = FloatArray(3)
        Color.RGBToHSV(r, g, b, hsv)
        val h = hsv[0]
        val s = hsv[1]
        val v = hsv[2]
        val l = (2 - s) * v / 2
        val newS = if (l == 0f || l == 1f) 0f else (v - l) / min(l, 1 - l)
        return floatArrayOf(h, newS, l)
    }

    private fun rgbToHSV(r: Int, g: Int, b: Int): FloatArray {
        val hsv = FloatArray(3)
        Color.RGBToHSV(r, g, b, hsv)
        return hsv
    }

    private fun rgbToRYB(r: Int, g: Int, b: Int): Triple<Float, Float, Float> {
        val rn = r / 255f
        val gn = g / 255f
        val bn = b / 255f
        val w = min(rn, min(gn, bn))
        val r1 = rn - w
        val g1 = gn - w
        val b1 = bn - w
        val y = min(r1, g1)
        val r2 = r1 - y
        g1 - y
        val b2 = b1
        return Triple(r2 + y, y, b2)
    }

    private fun rgbToXYZ(r: Int, g: Int, b: Int): Triple<Double, Double, Double> {
        fun pivot(v: Double): Double {
            var x = v / 255.0
            x = if (x > 0.04045) ((x + 0.055) / 1.055).pow(2.4) else x / 12.92
            return x * 100
        }
        val rl = pivot(r.toDouble())
        val gl = pivot(g.toDouble())
        val bl = pivot(b.toDouble())
        val x = rl * 0.4124 + gl * 0.3576 + bl * 0.1805
        val y = rl * 0.2126 + gl * 0.7152 + bl * 0.0722
        val z = rl * 0.0193 + gl * 0.1192 + bl * 0.9505
        return Triple(x, y, z)
    }

    private fun xyzToLAB(xyz: Triple<Double, Double, Double>): Triple<Double, Double, Double> {
        val (x, y, z) = xyz
        val Xn = 95.047
        val Yn = 100.0
        val Zn = 108.883
        fun f(t: Double): Double {
            return if (t > 0.008856) t.pow(1.0 / 3) else (7.787 * t) + 16 / 116.0
        }
        val fx = f(x / Xn)
        val fy = f(y / Yn)
        val fz = f(z / Zn)
        val L = 116 * fy - 16
        val A = 500 * (fx - fy)
        val B = 200 * (fy - fz)
        return Triple(L, A, B)
    }
}
