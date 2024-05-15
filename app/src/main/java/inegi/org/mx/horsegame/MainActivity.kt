package inegi.org.mx.horsegame

import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var bitmap: Bitmap? = null

    private var mHandler: Handler? = null
    private var timeInSeconds: Long = 0 // para llevar el control de los segundos
    private var gaming = true

    private var widthBonus = 0

    private var cellselectedX = 0
    private var cellselectedY = 0
    private var nameColorBlack = "black_cell"
    private var nameColorWhite = "white_cell"

    private var nextLevel = false
    private var level = 1
    private var levelMoves = 0 // para saber cuantos movimientos tiene cada nivel
    //control de movimientos
    private var movesRequired = 0
    private var moves = 0
    private var lives = 1 // vidas que tiene el usuario

    private var scoreLives = 1
    private var scoreLevel = 1

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
        startGame()
        // resetBoard()
        // setFirstPosition() // para pintar de forma aleatoria la primera celda
    }

    // función pública que se llama desde el botón
    fun checkCell(v: View) {
        val name = v.tag.toString()
        val x = name.subSequence(1, 2).toString().toInt()
        val y = name.subSequence(2, 3).toString().toInt()

        checkCellChecked(x, y)
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
    private fun resetBoard() {
        board = Array(8) { Array(8) { 0 } } // esto fue una sugerencia de android studio
    }

    // para que el movimiento del caballo sea de forma correcta
    private fun checkCellChecked(x: Int, y: Int) {
        var checkTrue = true
        if (checkMovement) {

            val difX = x - cellselectedX
            val difY = y - cellselectedY
            checkTrue = false

            if (difX == 1 && difY == 2) checkTrue = true // right - top long
            if (difX == 1 && difY == -2) checkTrue = true // right - bottom long
            if (difX == 2 && difY == 1) checkTrue = true // right long - top
            if (difX == 2 && difY == -1) checkTrue = true // right long - bottom
            if (difX == -1 && difY == 2) checkTrue = true // left - top long
            if (difX == -1 && difY == -2) checkTrue = true // left - bottom long
            if (difX == -2 && difY == 1) checkTrue = true // left long - top
            if (difX == -2 && difY == -1) checkTrue = true // left long - bottom

        } else {
            if (board[x][y] != 1) {
                bonus--
                val tvBonusData = findViewById<TextView>(R.id.tvBonusData)
                tvBonusData.text = "+ $bonus" // para poner el bonus en la pantalla

                if (bonus == 0) tvBonusData.text = ""
            }
        }



        if (board[x][y] == 1) checkTrue = false

        if (checkTrue) {
            selectCell(x, y)
        }

    }

    private fun setFirstPosition() {
        var x = 0
        var y = 0
        x = (0..7).random()
        y = (0..7).random()

        cellselectedX = x
        cellselectedY = y
        selectCell(x, y)
    }

    private fun paintBonusCell(x: Int, y: Int) {
        val iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
        iv.setImageResource(R.drawable.bonus)
    }

    private fun checkNewBonus() {
        if (moves % movesRequired == 0) {
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
            paintBonusCell(bonuscellX, bonuscellY)
        }
    }

    private fun clearOption(x: Int, y: Int) {
        val iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
        if (checkColorCell(x, y) == "black") {
            iv.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    resources.getIdentifier(nameColorBlack, "color", packageName)
                )
            )
        } else {
            iv.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    resources.getIdentifier(nameColorWhite, "color", packageName)
                )
            )
        }
        if (board[x][y] == 1) iv.setBackgroundColor(
            ContextCompat.getColor(
                this,
                resources.getIdentifier("previous cell", "color", packageName)
            )
        )
    }

    private fun clearOptions() {
        for (i in 0..7)
            for (j in 0..7) {
                if ((board[i][j] == 9) || board[i][j] == 2) {
                    if (board[i][j] == 9) board[i][j] = 0
                    clearOption(i, j)

                }
            }
    }

    private fun growProgressBonus() {

        val movesDone = levelMoves - moves
        val bonusDone = movesDone / movesRequired
        val movesRest = movesRequired * (bonusDone)
        val bonusGrow = movesDone - movesRest

        val v = findViewById<View>(R.id.vNewBonus)
        val widthBonus = ((widthBonus / movesRequired) * bonusGrow).toFloat()
        // pintar la barrita de progreso roja
        val height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8f,
            getResources().getDisplayMetrics()
        ).toInt()
        val width = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            widthBonus,
            getResources().getDisplayMetrics()
        ).toInt()
        //val v = findViewById<View>(R.id.vNewBonus)
        v.setLayoutParams(TableRow.LayoutParams(width, height))

    }

    private fun selectCell(x: Int, y: Int) {
        moves--
        val tvMovesData = findViewById<TextView>(R.id.tvMovesData)
        tvMovesData.text = moves.toString()

        growProgressBonus()

        if (board[x][y] == 2) {
            bonus++
            // sugerido por la IA

            val tvBonusData = findViewById<TextView>(R.id.tvBonusData)
            tvBonusData.text = "+ $bonus" // para poner el bonus en la pantalla
        }

        // sugerido por la IA
        if (moves == 0) {
            val lyMessage = findViewById<LinearLayout>(R.id.lyMessage)
            lyMessage.visibility = View.VISIBLE
            return
        }

        // hay que pintar el anterior de naranja
        board[x][y] = 1
        paintHorseCell(cellselectedX, cellselectedY, "previous_cell")
        cellselectedX = x
        cellselectedY = y

        clearOptions()


        paintHorseCell(x, y, "selected_cell")
        checkMovement = true
        checkOptions(x, y)

        if (moves > 0) {
            checkNewBonus()
            checkGameOver()
        } else showMessage("You win", "Next level", false)


    }

    private fun checkGameOver() {
        if (options == 0) {
            if (bonus > 0) {
                checkMovement = false
                paintAllOptions()
            }
            //if (bonus == 0) showMessage("GAME OVER","Try again!", true)
            else showMessage("GAME OVER", "Try again!", true)
            //{
            //   checkMovement = false
//                paintAllOptions()
            //          }

        }
    }

    private fun paintAllOptions() {
        for (i in 0..7) {
            for (j in 0..7) {
                if (board[i][j] != 1) { // si no es un caballo, es una opción
                    paintOptions(i, j)
                    if (board[i][j] == 0) {
                        board[i][j] = 9
                    }
                }
            }
        }
    }

    private fun showMessage(title: String, message: String, gameOver: Boolean) {
        gaming = false
        val lyMessage = findViewById<LinearLayout>(R.id.lyMessage)
        lyMessage.visibility = View.VISIBLE

        val tvTitleMessage = findViewById<TextView>(R.id.tvTitleMessage)
        tvTitleMessage.text = title

        val tvTimeData = findViewById<TextView>(R.id.tvTimeData)
        var score: String = ""
        if (gameOver) {
            score = "Score: " + (levelMoves - moves) + "/" + levelMoves

        } else {
            score = tvTimeData.text.toString()
        }
        val tvScoreMessage = findViewById<TextView>(R.id.tvScoreMessage)
        tvScoreMessage.text = score

        val tvAction = findViewById<TextView>(R.id.tvAction)
        tvAction.text = message

    }


    private fun checkOptions(x: Int, y: Int) {
        options = 0

        checkMove(x, y, 1, 2)
        checkMove(x, y, 2, 1)
        checkMove(x, y, 1, -2)
        checkMove(x, y, 2, -1)
        checkMove(x, y, -1, 2)
        checkMove(x, y, -2, 1)
        checkMove(x, y, -1, -2)
        checkMove(x, y, -2, -1)

        val tvOptionsData = findViewById<TextView>(R.id.tvOptionsData)
        tvOptionsData.text = options.toString()


    }

    // dar al usuario las opciones de movimiento, sugerencias pues
    private fun checkMove(x: Int, y: Int, movX: Int, movY: Int) {
        val optionX = x + movX
        val optionY = y + movY

        if (optionX < 8 && optionY < 8 && optionX >= 0 && optionY >= 0) {
            if (board[optionX][optionY] == 0
                || board[optionX][optionY] == 2
            ) {
                //paintHorseCell(optionX,optionY, "option_cell") lo que me dice la IA
                options++
                paintOptions(optionX, optionY)

                if (board[optionX][optionY] == 0) {
                    board[optionX][optionY] = 9
                }
            }
        }
    }

    private fun checkColorCell(x: Int, y: Int): String {
        var color = ""
        val blackColumnX = arrayOf(0, 2, 4, 6)
        val blackRowX = arrayOf(1, 3, 5, 7)
        color = if ((blackColumnX.contains(x) && blackColumnX.contains(y))
            || (blackRowX.contains(x) && blackRowX.contains(y))
        ) {
            "black"
        } else {
            "white"
        }

        return color
    }

    private fun paintOptions(x: Int, y: Int) {
        val iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
        // iv.setBackgroundColor(ContextCompat.getColor(this, resources.getIdentifier(color, "color", packageName)))
        // si la casilla es blanca se le pone un fondo y si es negra otro fondo
        if (checkColorCell(x, y) == "black") {
            iv.setBackgroundResource(R.drawable.option_black)
            //iv.setImageResource(R.drawable.horse) // IA
        } else {
            iv.setBackgroundResource(R.drawable.option_white)
            // iv.setImageResource(R.drawable.horse) // IA
        }

    }

    private fun paintHorseCell(x: Int, y: Int, color: String) {
        //val iv = findViewById<ImageView>(resources.getIdentifier("c$x$y", "id", packageName))
        val iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
        //iv.setTag(tag)
        iv.setBackgroundColor(
            ContextCompat.getColor(
                this,
                resources.getIdentifier(color, "color", packageName)
            )
        )
        iv.setImageResource(R.drawable.horse)


    }

    private fun initScreenGame() {
        setSizeBord()
        hideMessage(false)
    }

    //se cambian los tamaños de los cuadros del tablero en base a la resolución de la pantalla
    private fun setSizeBord() {
        var iv: ImageView
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
                val height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    heigthcell,
                    getResources().getDisplayMetrics()
                ).toInt()
                val width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    cellSize,
                    getResources().getDisplayMetrics()
                ).toInt()
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

    private fun hideMessage(star : Boolean) {
        val lyMessage = findViewById<LinearLayout>(R.id.lyMessage)
        lyMessage.visibility = View.INVISIBLE
        if (star) startGame()

    }

    // este siempre está ejecutándose, hasta que se ejecuta el mHandler?.removeCallbacks(chronometer)
    private var chronometer: Runnable = object : Runnable {
        override fun run() {
            try {
                if (gaming) { // si es que se sigue jugando
                    timeInSeconds++
                    updateStopWatchView(timeInSeconds)
                }
            } finally {
                mHandler!!.postDelayed(this, 1000L)
            }
        }
    }

    private fun updateStopWatchView(timeInSeconds: Long) {
        val formattedTime = getFormattedStopWatch((timeInSeconds * 1000))
        val tvTimeData = findViewById<TextView>(R.id.tvTimeData)
        tvTimeData.text = formattedTime
    }

    private fun getFormattedStopWatch(ms: Long): String {
        var milliseconds = ms
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        return "${if (minutes > 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }


    private fun startGame() {

        setLevel()
        setLevelParameters()

        resetBoard()
        clearBoard()

        setBoardLevel()

        setFirstPosition()

        resetTime()
        startTime()
        gaming = true
    }
    private fun setLevelParameters(){
        val tvLivesData = findViewById<TextView>(R.id.tvLiveData)
        tvLivesData.text = lives.toString()

        scoreLives = lives

        val tvlevelNumber = findViewById<TextView>(R.id.tvLevelNumber)
        tvlevelNumber.text = level.toString()

        scoreLevel = level

        bonus = 0
        val tvBonusData = findViewById<TextView>(R.id.tvBonusData)
        tvBonusData.text = ""

        setlevelMoves()
        moves = levelMoves

        movesRequired = setMovesRequired()
    }

    private fun setlevelMoves(){
        when (level) {
            1 -> levelMoves = 64
            2 -> levelMoves = 56
            3 -> levelMoves = 32
            4 -> levelMoves = 16
            5 -> levelMoves = 48
            6 -> levelMoves = 36
            7 -> levelMoves = 48
            8 -> levelMoves = 49
            9 -> levelMoves = 59
            10 -> levelMoves = 48
            11 -> levelMoves = 64
            12 -> levelMoves = 48
            13 -> levelMoves = 48
        }
    }
    private fun setMovesRequired():Int {
        var movesRequired = 0
        when (level) {
            1 -> movesRequired = 8
            2 -> movesRequired = 10
            3 -> movesRequired = 12
            4 -> movesRequired = 10
            5 -> movesRequired = 10
            6 -> movesRequired = 12
            7 -> movesRequired = 5
            8 -> movesRequired = 7
            9 -> movesRequired = 9
            10 -> movesRequired = 8
            11 -> movesRequired = 1000
            12 -> movesRequired = 5
            13 -> movesRequired = 5
        }
        return movesRequired
    }

    private fun setLevel() {
        if (nextLevel) {
            level++
        } else {
            lives--
            if (lives < 1) {
                level = 1
                lives = 1
            }
        }

    }

    private fun setBoardLevel() {

      when (level) {
          2 -> paintLevel2()
          3 -> paintLevel3()
          4 -> paintLevel4()
          5 -> paintLevel5()
          6 -> paintLevel6()
          7 -> paintLevel7()
          8 -> paintLevel8()
          9 -> paintLevel9()
          10 -> paintLevel10()
          11 -> paintLevel11()
          12 -> paintLevel12()
          13 -> paintLevel13()
      }
    }

    private fun paintColumn(column: Int) {
        for (i in 0..7) {
            board[column][i] = 1
            paintHorseCell(column, i, "previous_cell")
        }
    }
    private fun paintRow(row: Int) {

    }
    private fun paintDiagonal(diagonal: Int) {

    }
     private fun paintDiagonalInverse(diagonal: Int) {

     }

    private fun paintLevel2() {
        paintColumn(6)
    }

    private fun paintLevel3(){
        for (i in 0..7){
            for (j in 4..7){
                board[i][j] = 1
                paintHorseCell(j,i,"previous_cell")
            }
        }
    }

    private fun paintLevel4(){
        paintLevel3(); paintLevel5()
    }

    private fun paintLevel5() {
        for (i in 0..3) {
            for (j in 0..3) {
                board[i][j] = 1
                paintHorseCell(j, i, "previous_cell")
            }
        }
    }
    private fun paintLevel6() {
        for (i in 4..7) {
            for (j in 0..3) {
                board[i][j] = 1
                paintHorseCell(j, i, "previous_cell")
            }
        }
    }
    private fun paintLevel7(){
        paintLevel2()
    }

    private fun paintLevel8(){
        paintLevel3()
        paintLevel4()
    }

    private fun paintLevel9(){
        paintLevel5()
        paintLevel6()
    }

    private fun paintLevel10() {
        paintLevel7()
        paintLevel8()
    }

    private fun paintLevel11() {
        paintLevel9()
        paintLevel10()
    }

    private fun paintLevel12() {
        paintLevel11()
        paintLevel13()
    }

    private fun paintLevel13() {
        paintLevel12()
        paintLevel2()
    }





    private fun clearBoard() {
        var iv: ImageView

        var colorBlack = ContextCompat.getColor(
            this,
            resources.getIdentifier(nameColorBlack, "color", packageName)
        )
        var colorWhite = ContextCompat.getColor(
            this,
            resources.getIdentifier(nameColorWhite, "color", packageName)
        )

        for (i in 0..7) {
            for (j in 0..7) {
                iv = findViewById(resources.getIdentifier("c$i$j", "id", packageName))
                //iv.setImageResource(R.drawable.horse)
                iv.setImageResource(0)

                if (checkColorCell(i, j) == "black") iv.setBackgroundColor(colorBlack)
                else iv.setBackgroundColor(colorWhite)
            }
        }
    }

    // crear funcion para controlar el tiempo
    private fun resetTime() {
        mHandler?.removeCallbacks(chronometer)
        timeInSeconds = 0

        var tvTimeData = findViewById<TextView>(R.id.tvTimeData)
        tvTimeData.text = "00:00"
    }

    private fun startTime() {
        mHandler = Handler(Looper.getMainLooper())
        chronometer.run()

    }

    fun launchAction(v: View) {
        hideMessage(true)

    }

    // función pública para el click del sharegame
    fun launchShareGame(v: View) {
        shareGame()
    }

    private fun shareGame() {
        // en caso de no haber habilitado los permisos, se vuelve a preguntar
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        //val ssc: ScreenCapture = capture(this)
        //bitmap = ssc

    }
}