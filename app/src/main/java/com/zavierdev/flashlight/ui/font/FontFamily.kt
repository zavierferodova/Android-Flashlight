package com.zavierdev.flashlight.ui.font

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.zavierdev.flashlight.R

val roboto = FontFamily(
    Font(R.font.roboto_black, weight = FontWeight.Black),
    Font(R.font.roboto_blackitalic, style = FontStyle.Italic, weight = FontWeight.Black),
    Font(R.font.roboto_bold, weight = FontWeight.Bold),
    Font(R.font.roboto_bolditalic, style = FontStyle.Italic, weight = FontWeight.Bold),
    Font(R.font.roboto_regular, weight = FontWeight.Normal),
    Font(R.font.roboto_italic, style = FontStyle.Italic, weight = FontWeight.Normal),
    Font(R.font.roboto_medium, weight = FontWeight.Medium),
    Font(R.font.roboto_mediumitalic, style = FontStyle.Italic, weight = FontWeight.Medium),
    Font(R.font.roboto_light, weight = FontWeight.Light),
    Font(R.font.roboto_lightitalic, style = FontStyle.Italic, weight = FontWeight.Light),
    Font(R.font.roboto_thin, weight = FontWeight.Thin),
    Font(R.font.roboto_thinitalic, style = FontStyle.Italic, weight = FontWeight.Thin)
)