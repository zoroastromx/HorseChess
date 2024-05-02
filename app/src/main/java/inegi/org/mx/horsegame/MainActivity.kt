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
    private var cellselectedX = 0
    private var cellselectedY = 0

    // hay que hacer una matriz del tablero para llevar el control de las celdas

    private lateinit var board: Array<Array<Int>>
    private var horseX = 0
    private var horseY = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rlScreen)) { v, insets ->
        //      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        //     v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        //     insets

        initScreenGame()
        resetBoard()
        setFirstPosition() // para pintar de forma aleatoria la primera celda
    }

    fun checkCell(v: View){
        val name = v.tag.toString()
        val x = name.subSequence(1,2).toString().toInt()
        val y = name.subSequence(2,3).toString().toInt()

        selectCell(x,y)


        //val x = v.tag.toString().substring(1,2).toInt()
        //val y = v.tag.toString().substring(2,3).toInt()

    }

    // hacer un reseteo del tablero
    // cuando la celda esté en cero, significa que no hay caballo
    // cuando la celda sea diferente de cero, significa que hay caballo

    // valor 0 no hay caballo
    // valor 1 hay caballo
    // valor 2 es un bonus
    // valor 9 es una opción del movimiento actualk
    private fun resetBoard(){
        board = Array(8) { Array(8) { 0 } }
    }

     private fun setFirstPosition(){
            var x = 0
            var y = 0
            x = (0..7).random()
            y = (0..7).random()

            cellselectedX = x
            cellselectedY = y
            selectCell(x,y)

        }
    private fun selectCell(x: Int, y: Int){
              // hay que pintar el anterior de naranja
        board[x][y] = 1
        paintHorseCell(cellselectedX,cellselectedY, "previous_cell")
        cellselectedX = x
        cellselectedY = y
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