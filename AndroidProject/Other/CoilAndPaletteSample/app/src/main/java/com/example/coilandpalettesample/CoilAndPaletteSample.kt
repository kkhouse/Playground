package com.example.coilandpalettesample

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

private const val TAG = "CoilAndPaletteTag"

@Composable
fun CoilAndPaletteSample(
    url: String = "https://play.pokemonshowdown.com/sprites/ani/pikachu.gif"
) {
    val context = LocalContext.current
    var bitmap: Bitmap? by remember { mutableStateOf(null) }

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    /*
    Gif からの取得は現状、別で画像をリクエストして取得 -> bitmapとして変換
    という手順が必要
     */
    LaunchedEffect(Unit) {
        val req = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build()

        val result = req.context.imageLoader.execute(req)

        if (result is SuccessResult) {
            bitmap = result.drawable.toBitmap() // Bitmap保管
        }
    }

    // gif でないならこれだけでもいける
//    val painter = rememberAsyncImagePainter(
//        model = url,
//        onState = { state ->
//            when(state) {
//                is AsyncImagePainter.State.Success -> {
//                    Log.d(TAG, "CoilAndPaletteSample: ${state.result.drawable.toBitmap()}")
//                    bitmap = state.result.drawable.toBitmap()
//                }
//                else -> Unit
//            }
//        }
//    )
//    Image(painter = painter, contentDescription = "")

    Column {
        AsyncImage(
            modifier = Modifier,
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
//            .target(  2.0.0 以降はrequest.target must be null.　というエラー
//                onSuccess = { result ->
//
//                }
//            )
                .build(),
            imageLoader = imageLoader,
            placeholder = painterResource(R.drawable.ic_placeholder),
            contentDescription = "",
            contentScale = ContentScale.Fit
        )
        bitmap?.let {
            PaletteOnlySample(it)
        }
    }
}

@Composable
fun CoilOnlySample() {
    val url = "https://play.pokemonshowdown.com/sprites/ani/pikachu.gif"
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    AsyncImage(
        modifier = Modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .transformations()
            .target(
                onSuccess = { result ->

                }
            )
            .build(),
        imageLoader = imageLoader,
        placeholder = painterResource(R.drawable.ic_placeholder),
        contentDescription = "",
        contentScale = ContentScale.Fit
    )
}

/*
Only palette
 */
@Preview
@Composable
fun PaletteOnlySample(_bitmap: Bitmap? = null) {
    val context = LocalContext.current

    /* Convert our Image Resource into a Bitmap */
    val bitmap = remember {
        _bitmap ?: BitmapFactory.decodeResource(context.resources, R.drawable.sample)
    }

    /* Create the Palette, pass the bitmap to it */
    val palette = remember { Palette.from(bitmap).generate() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("rgb", fontSize = 24.sp) }
        item {
            LazyRow(
                contentPadding = PaddingValues(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item { Circle(palette.lightVibrantSwatch?.let { Color(it.rgb) } ?: Color.White, hint = "lightVibrantSwatch") }
                item { Circle(palette.vibrantSwatch?.let { Color(it.rgb) } ?: Color.White, hint = "vibrantSwatch")  }
                item { Circle(palette.darkVibrantSwatch?.let { Color(it.rgb) } ?: Color.White, hint = "darkVibrantSwatch") }
                item { Circle(palette.lightMutedSwatch?.let { Color(it.rgb) } ?: Color.White, hint = "lightMutedSwatch") }
                item { Circle(palette.mutedSwatch?.let { Color(it.rgb) } ?: Color.White, hint = "mutedSwatch") }
                item { Circle(palette.darkMutedSwatch?.let { Color(it.rgb) } ?: Color.White, hint = "darkMutedSwatch") }
            }
        }


        item { Text("Text" , fontSize = 24.sp) }
        item {
            Text(
                text = "Sample Title Text", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = palette.lightVibrantSwatch?.let { Color(it.titleTextColor) } ?: Color.White)
        }
        item {
            Text(
                text = "Sample Body Text", fontSize = 20.sp, fontWeight = FontWeight.Bold ,
                color = palette.lightVibrantSwatch?.let { Color(it.bodyTextColor) } ?: Color.White)
        }

        item { Text("Population", fontSize = 24.sp) }
        item { Text(text = "the number of pixels represented by this swatch") }
        item { Text(text = "lightVibrantSwatch ${palette.lightVibrantSwatch?.population}") }
        item { Text(text = "vibrantSwatch ${palette.vibrantSwatch?.population}") }
        item { Text(text = "darkVibrantSwatch ${palette.darkVibrantSwatch?.population}") }
        item { Text(text = "lightMutedSwatch ${palette.lightMutedSwatch?.population}") }
        item { Text(text = "mutedSwatch ${palette.mutedSwatch?.population}") }
        item { Text(text = "darkMutedSwatch ${palette.darkMutedSwatch?.population}") }

        item { Text("Sample Image", fontSize = 24.sp) }
        item { Image(painter = painterResource(id = R.drawable.sample_), contentDescription = "") }
    }
}

@Composable
fun Circle(
    color : Color,
    hint: String = "hint"
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(color = color)
    ) {
        Text(text = hint, modifier = Modifier.align(Alignment.Center))
    }
}

@Preview
@Composable
fun PreviewCoil() {
    CoilOnlySample()
}