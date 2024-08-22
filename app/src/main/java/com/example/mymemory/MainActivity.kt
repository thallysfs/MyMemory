package com.example.mymemory

import android.app.ActivityOptions
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.mymemory.ui.theme.MyMemoryTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    LaunchedEffect(showTime) {
        while (showTime) {
            delay(1000)
            elapsedTime++
            val seconds = elapsedTime % 60
            val minutes = elapsedTime / 60
            val secondsText = if (seconds < 10) "0$seconds" else seconds.toString()
            val minutesText = if (minutes < 10) "0$minutes" else minutes.toString()

            timeText = "$minutesText:$seconds sg"

        }
    }

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
                .padding(vertical = 40.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Attempts: $attempts",
                fontSize = 20.sp,
                color = MyColors.Yellow,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Pairs: $foundPairs",
                fontSize = 20.sp,
                color = MyColors.White,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Time: $timeText",
                fontSize = 20.sp,
                color = MyColors.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(columns = GridCells.Fixed(4)) {
                items(images.size) { index ->
                    val (image, revealed) = selectedImages[index]
                    MemoryCard(
                        painter = if(revealed) painterResource(id = image) else painterResource(id = R.drawable.card_back),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                SoundUtil.playSound(context = context, resourceId = R.raw.show)

                                if(!revealed && canPlay) {
                                    try {
                                        selectedImages = selectedImages
                                            .toMutableList()
                                            .also {
                                                it[index] = Pair(images[index].first, true)
                                            }

                                        if(imageNumber == 1){
                                            imageNumber = 2
                                            lastSelectedImages = images[index].first
                                            lastSelectedIndex = index

                                        } else {
                                            canPlay = false
                                            if(images[index].first == lastSelectedImages){
                                                foundPairs++
                                                attempts++
                                                SoundUtil.playSound(context = context, resourceId = R.raw.win)

                                                if(foundPairs == images.size / 2) {
                                                    scope.launch {
                                                        Toast.makeText(context, "You Won !!", Toast.LENGTH_SHORT).show()

                                                        delay(2000)
                                                        restartGame(context)
                                                    }
                                                } else {
                                                    canPlay = true
                                                }
                                            } else {
                                                scope.launch {
                                                    delay(1000)
                                                    SoundUtil.playSound(context = context, resourceId = R.raw.lose)

                                                    selectedImages = selectedImages
                                                        .toMutableList()
                                                        .also {
                                                            it[index] = Pair(-1, false)
                                                        }

                                                    selectedImages = selectedImages
                                                        .toMutableList()
                                                        .also {
                                                            it[lastSelectedIndex] = Pair(-1, false)
                                                        }
                                                    canPlay = true
                                                }
                                            }
                                            imageNumber = 1
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error   ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Play you other card", Toast.LENGTH_SHORT).show()
                                }
                            }
                    )

                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = {
                    restartGame(context)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if(showTime) "Stop" else "Restart Game",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

            }
        }

    }
}

fun restartGame(context: Context) {
    Toast.makeText(context, "New Game", Toast.LENGTH_SHORT).show()
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

    val optioons = ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out)
    context.startActivity(intent, optioons.toBundle())
}

@Composable
fun MemoryCard(
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String?
) {
   Box(modifier = modifier.size(100.dp)) {
       Image(
           painter = painter,
           contentDescription = contentDescription,
           modifier = Modifier.fillMaxSize()
       )
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

