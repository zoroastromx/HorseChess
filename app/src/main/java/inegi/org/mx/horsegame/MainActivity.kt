package inegi.org.mx.horsegame

import android.graphics.Point
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {


    private var widthBonus = 0

    private var cellselectedX = 0
    private var cellselectedY = 0
    private var nameColorBlack = "black_cell"
    private var nameColorWhite = "white_cell"

    private var levelMoves = 64 // para saber cuantos movimientos tiene cada nivel

    //control de movimientos
    private var movesRequired = 4
    private var moves = 64

    private var options = 0

    // para llevar un control de los bonus conseguidos
    private var bonus = 0

    private var checkMovement = true

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

        checkCellChecked(x,y)
    //selectCell(x,y)


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
        board = Array(8) { Array(8) { 0 } } // esto fue una sugerencia de android studio
    }
    // para que el movimiento del caballo sea de forma correcta
    private fun checkCellChecked(x: Int, y: Int){
        val difX = x - cellselectedX
        val difY = y - cellselectedY
        var checkTrue = false

        if (difX == 1 &&  difY == 2 )  checkTrue = true // right - top long
        if (difX == 1 &&  difY == -2 ) checkTrue = true // right - bottom long
        if (difX == 2 &&  difY == 1 )  checkTrue = true // right long - top
        if (difX == 2 &&  difY == -1 ) checkTrue = true // right long - bottom
        if (difX == -1 && difY == 2 )  checkTrue = true // left - top long
        if (difX == -1 && difY == -2 ) checkTrue = true // left - bottom long
        if (difX == -2 && difY == 1 )  checkTrue = true // left long - top
        if (difX == -2 && difY == -1 ) checkTrue = true // left long - bottom

        if (board[x][y] == 1) checkTrue = false

        if (checkTrue){
            selectCell(x,y)
        }

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
    private fun paintBonusCell(x: Int, y: Int){
        val iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
        iv.setImageResource(R.drawable.bonus)
    }
    private fun checkNewBonus(){
        if (moves%movesRequired == 0 ){
            var bonuscellX = 0
            var bonuscellY = 0

            var bonusCell = false
            while (!bonusCell) {
                bonuscellX = (0..7).random()
                bonuscellY = (0..7).random()
                if (board[bonuscellX][bonuscellY] == 0) {
                    bonusCell = true
                }
            }
            board[bonuscellX][bonuscellY] = 2
            paintBonusCell(bonuscellX,bonuscellY)
        }
    }

    private fun clearOption(x: Int, y: Int){
        val iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
        if (checkColorCell(x,y) == "black"){
            iv.setBackgroundColor(ContextCompat.getColor(this, resources.getIdentifier(nameColorBlack, "color", packageName)))
        }else{
            iv.setBackgroundColor(ContextCompat.getColor(this, resources.getIdentifier(nameColorWhite, "color", packageName)))
        }
        if (board[x][y] == 1) iv.setBackgroundColor(ContextCompat.getColor(this, resources.getIdentifier("previous cell", "color", packageName)))
    }
    private fun clearOptions(){
        for (i in 0..7)
            for (j in 0..7){
                if ((board[i][j] == 9) || board[i][j] == 2){
                    if (board[i][j] == 9) board[i][j] = 0
                    clearOption(i,j)

                }
            }
    }

    private fun growProgressBonus(){

        val movesDone = levelMoves - moves
        val bonusDone = movesDone / movesRequired
        val movesRest = movesRequired * (bonusDone)
        val bonusGrow = movesDone -  movesRest

        val v = findViewById<View>(R.id.vNewBonus)
        val widthBonus = ((widthBonus/movesRequired) * bonusGrow).toFloat()
        // pintar la barrita de progreso roja
        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics()).toInt()
        val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,widthBonus, getResources().getDisplayMetrics()).toInt()
        //val v = findViewById<View>(R.id.vNewBonus)
        v.setLayoutParams(TableRow.LayoutParams(width, height))

    }

    private fun selectCell(x: Int, y: Int){
        moves--
        val tvMovesData = findViewById<TextView>(R.id.tvMovesData)
        tvMovesData.text = moves.toString()

        growProgressBonus()

        if (board[x][y] == 2){
            bonus++
            // sugerido por la IA

            val tvBonusData = findViewById<TextView>(R.id.tvBonusData)
            tvBonusData.text = "+ $bonus" // para poner el bonus en la pantalla
        }

        // sugerido por la IA
        if (moves == 0){
            val lyMessage = findViewById<LinearLayout>(R.id.lyMessage)
            lyMessage.visibility = View.VISIBLE
            return
        }

              // hay que pintar el anterior de naranja
        board[x][y] = 1
        paintHorseCell(cellselectedX,cellselectedY, "previous_cell")
        cellselectedX = x
        cellselectedY = y

        clearOptions()


        paintHorseCell(x,y, "selected_cell")

        checkOptions(x,y)

        if (moves > 0){
            checkNewBonus()
            checkGameOver(x,y)
        }
        else showMessage("You win","Next level", false)



    }
    private fun checkGameOver(x: Int, y: Int){
        if (options == 0){
            if (bonus == 0) showMessage("GAME OVER","Try again!", true)
            else{
                checkMovement = false
            }

        }
    }
    private fun showMessage(title: String, message: String, gameOver : Boolean){
        val lyMessage = findViewById<LinearLayout>(R.id.lyMessage)
        lyMessage.visibility = View.VISIBLE

        val tvTitleMessage = findViewById<TextView>(R.id.tvTitleMessage)
        tvTitleMessage.text = title

        val tvTimeData = findViewById<TextView>(R.id.tvTimeData)
        var score: String = ""
        if (gameOver){
            score = "Score: " + (levelMoves - moves) + "/" + levelMoves

        }else{
            score = tvTimeData.text.toString()
        }
        val tvScoreMessage = findViewById<TextView>(R.id.tvScoreMessage)
        tvScoreMessage.text = score

        val tvAction = findViewById<TextView>(R.id.tvAction)
        tvAction.text = message

    }


        private fun checkOptions(x: Int, y: Int){
            options = 0

            checkMove(x,y,1,2)
            checkMove(x,y,2,1)
            checkMove(x,y,1,-2)
            checkMove(x,y,2,-1)
            checkMove(x,y,-1,2)
            checkMove(x,y,-2,1)
            checkMove(x,y,-1,-2)
            checkMove(x,y,-2,-1)

            val tvOptionsData = findViewById<TextView>(R.id.tvOptionsData)
            tvOptionsData.text = options.toString()


        }
        // dar al usuario las opciones de movimiento, sugerencias pues
        private fun checkMove(x: Int, y: Int, movX: Int, movY: Int){
            val optionX = x + movX
            val optionY = y + movY

            if (optionX < 8 && optionY < 8 && optionX >= 0 && optionY >= 0){
                if (board[optionX][optionY] == 0
                    || board[optionX][optionY] == 2){
                    //paintHorseCell(optionX,optionY, "option_cell") lo que me dice la IA
                    options++
                    paintOptions(optionX, optionY)

                    if (board[optionX][optionY] == 0) {
                        board[optionX][optionY] = 9
                    }
                }
            }
        }
        private fun checkColorCell(x: Int, y: Int): String{
            var color = ""
            val blackColumnX = arrayOf(0,2,4,6)
            val blackRowX = arrayOf(1,3,5,7)
            color = if ((blackColumnX.contains(x) && blackColumnX.contains(y))
                || (blackRowX.contains(x) && blackRowX.contains(y))){
                "black"
            }else{
                "white"
            }

            return color
        }

        private fun paintOptions(x: Int, y: Int){
            val iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
           // iv.setBackgroundColor(ContextCompat.getColor(this, resources.getIdentifier(color, "color", packageName)))
            // si la casilla es blanca se le pone un fondo y si es negra otro fondo
            if (checkColorCell(x,y) == "black"){
                iv.setBackgroundResource(R.drawable.option_black)
                //iv.setImageResource(R.drawable.horse) // IA
            }else{
                iv.setBackgroundResource(R.drawable.option_white)
               // iv.setImageResource(R.drawable.horse) // IA
            }

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

            widthBonus = 2 * cellSize.toInt()

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