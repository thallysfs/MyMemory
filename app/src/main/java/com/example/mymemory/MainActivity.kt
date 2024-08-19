package com.example.mymemory

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.mymemory.ui.theme.MyMemoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            MyMemoryTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    MemoryGame()
                }
            }
        }
    }

}
@Composable
fun MemoryGame() {
    val context = LocalContext.current
    var images = remember { generateImages(context) }
    var selectedImages by remember { mutableStateOf(images.map { -1 to false }) }
    var foundPairs by remember { mutableIntStateOf(0) }
    var attempts by remember { mutableIntStateOf(0) }
    var lastSelectedImages by remember { mutableIntStateOf(-1) }
    var imageNumber by remember { mutableIntStateOf(1) }
    var lastSelectedIndex by remember { mutableIntStateOf(-1) }
    var canPlay by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    var elapsedTime by remember { mutableStateOf(0L) }
    var showTime by remember { mutableStateOf(true) }
    var timeText by remember { mutableStateOf("00:00: sg") }

    Box(modifier = Modifier.fillMaxSize()) {
        val background:Painter = painterResource(id = R.drawable.background)
        Image(
                painter = background, contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.8F),
                contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 40.dp, horizontal = 16.dp)
        ) {
            Text(
                text = "Attempts: $attempts",
                fontSize = 20.sp,
                color = MyColors.Yellow,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}


fun generateImages(context: Context) :List<Pair<Int, Boolean>> {
    val images = mutableListOf<Int>()
    val numberofPairs = 8

    for (i in 1..numberofPairs) {
        val imageName = "img_$i"
        val imageResourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        images.add(imageResourceId)
        images.add(imageResourceId)
    }
    images.shuffle()

    return images.map { it to false }
}

object imgs{
    val image1 = R.drawable.img_1
    val image2 = R.drawable.img_2
    val image3 = R.drawable.img_3

    val card_back = R.drawable.card_back
}

object SoundUtil {
    var mediaPlayer:MediaPlayer? = null
    fun playSound(context: Context, resourceId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, resourceId)
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            release()
        }
    }

    fun release(){
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

object MyColors {
    val Yellow = Color(0xFFFFEB3B)
    val White = Color(0xFFFFFFFF)
}

