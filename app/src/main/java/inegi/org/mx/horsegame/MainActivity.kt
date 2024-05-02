package inegi.org.mx.horsegame

import android.graphics.Point
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private var cellSelected_x = 0
    private var cellSelected_y = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rlScreen)) { v, insets ->
        //      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        //     v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        //     insets

        initScreenGame()
        setFirstPosition() // para pintar de forma aleatoria la primera celda
    }
     private fun setFirstPosition(){
            var x = 0
            var y = 0
            x = (0..7).random()
            y = (0..7).random()

            cellSelected_x = x
            cellSelected_y = y
            selectCell(x,y)

        }
    private fun selectCell(x: Int, y: Int){
              // hay que pintar el anterior de naranja
                paintHorseCell(x,y, "previous_cell")
                cellSelected_x = x
                cellSelected_y = y
                paintHorseCell(x,y, "selected_cell")
            }


        private fun paintHorseCell(x: Int, y: Int, color: String){
            //val iv = findViewById<ImageView>(resources.getIdentifier("c$x$y", "id", packageName))
            val iv : ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
            //iv.setTag(tag)
            iv.setBackgroundColor(ContextCompat.getColor(this, resources.getIdentifier(color, "color", packageName)))
            iv.setImageResource(R.drawable.horse)


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

           val width_dp = (width / getResources().getDisplayMetrics().density)

           val lateralMarginsDP = 0
           val cellSize = (width_dp - lateralMarginsDP) / 8
           val heigthcell = cellSize

            for ((i, row) in (0..7).withIndex()) {
                for ((j, _) in (0..7).withIndex()) {
                    iv = findViewById(resources.getIdentifier("c$i$j", "id", packageName))
                    //
                    val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,heigthcell,getResources().getDisplayMetrics()).toInt()
                    val width  = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,cellSize,getResources().getDisplayMetrics()).toInt()
                    iv.setLayoutParams(TableRow.LayoutParams(width, height))


                }
            }
          /* for (i in 0..7)
               for (j in 0..7){
                   iv = findViewById(resources.getIdentifier("c$i$j", "id", packageName))
                   //iv = findViewById(R.id."c$i$j")
                   val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,heigthcell,getResources().getDisplayMetrics()).toInt()
                   val width  = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,cellSize,getResources().getDisplayMetrics()).toInt()
                   iv.setLayoutParams(TableRow.LayoutParams(width, height))*/
               //}
        }
        private fun hideMessage(){
            val lyMessage = findViewById<LinearLayout>(R.id.lyMessage)
            lyMessage.visibility = View.INVISIBLE
        }

}