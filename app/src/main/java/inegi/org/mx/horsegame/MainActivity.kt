package inegi.org.mx.horsegame

import android.graphics.Point
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()
        setContentView(R.layout.activity_main)
       // ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rlScreen)) { v, insets ->
      //      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
       //     v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
       //     insets

            initScreenGame()
        }

        private fun initScreenGame(){
            setSizeBord()
            hideMessage()
        }
        //se cambian los tamaños de los cuadros del tablero en base a la resolución de la pantalla
        private fun setSizeBord(){
           var iv : ImageView
           val display = windowManager.defaultDisplay
           val size = Point()
           display.getSize(size)
           val width = size.x

           var width_dp = (width / getResources().getDisplayMetrics().density)

           var lateralMarginsDP = 0
           val widthcell = (width_dp - lateralMarginsDP) / 8
           val heigthcell = widthcell

           for (i in 0..7)
               for (j in 0..7){
                   iv = findViewById(resources.getIdentifier("c$i$j", "id", packageName))

                   var height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,heigthcell,getResources().getDisplayMetrics()).toInt()
                   var width  = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,widthcell,getResources().getDisplayMetrics()).toInt()
                   iv.setLayoutParams(TableRow.LayoutParams(width, height))
               }
        }
        private fun hideMessage(){
            val lyMessage = findViewById<LinearLayout>(R.id.lyMessage)
            lyMessage.visibility = View.INVISIBLE
        }

}