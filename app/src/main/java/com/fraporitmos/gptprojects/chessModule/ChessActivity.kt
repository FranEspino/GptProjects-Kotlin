package com.fraporitmos.gptprojects.chessModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.fraporitmos.chatkot.openaiModule.chessModule.*
import com.fraporitmos.gptprojects.R
import com.fraporitmos.gptprojects.viewModel.CompletionViewModel
import java.io.PrintWriter
import java.util.concurrent.Executors

class ChessActivity : AppCompatActivity(), ChessDelegate {
    private lateinit var chessView: ChessView
    private var printWriter: PrintWriter? = null
    private lateinit var mCompletionViewModel: CompletionViewModel
    private var previous_move: String= ""
    private var response_ai : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chess)
        chessView = findViewById(R.id.chess_view)
        chessView.chessDelegate = this
        setupViewModel()
    }

    private fun setupViewModel() {
        mCompletionViewModel = ViewModelProvider(this)[CompletionViewModel::class.java]
        mCompletionViewModel.observeCompletionLiveData().observe(this) {
            response_ai=  it.choices[0].message.content
            val regex = Regex("\\d+")
            val numbers = regex.findAll(response_ai).map { it.value.toInt() }.toList()
            val from_ai = Square(numbers[0],numbers[1])
            val to_ai = Square(numbers[2],numbers[3])
            ChessGame.movePiece(from_ai, to_ai)
            chessView.invalidate()
            printWriter?.let {
                val mov_ai = "${from_ai.col},${from_ai.row},${to_ai.col},${to_ai.row}"
                Executors.newSingleThreadExecutor().execute {
                    it.println(mov_ai)
                }
            }
        }
    }


    override fun pieceAt(square: Square): ChessPiece? = ChessGame.pieceAt(square)

    override fun movePiece(from: Square, to: Square) {
        ChessGame.movePiece(from, to)
        chessView.invalidate()

        printWriter?.let {
            val moveStr = "${from.col},${from.row},${to.col},${to.row}"
            Executors.newSingleThreadExecutor().execute {
                it.println(moveStr)
            }

        }
        Log.d("moveee", "movePiece: ${from.col},${from.row},${to.col},${to.row}")
        previous_move = previous_move  + "col=${from.col},row=${from.row} -> col=${to.col}, row=${to.row} "

        mCompletionViewModel.postCompletionLiveData(
            "Actua como un jugador de ajedrez profesional, Diremos nuestros movimientos en orden recíproco. Al principio seré blanco. Además, por favor, no me expliques tus movimientos por nada del mundo porque somos rivales, solo estas permitido a  dar como respuesta nuestro movimiento. Después de mi primer mensaje, solo escribiré mi movimiento. No olvides actualizar el estado del tablero en tu mente a medida que hacemos movimientos. para que sea un juego perfecto Ahora lo movimientos lo definiremos como  el movimiento de origen y destino, se tiene col y row que va desde 0 a 7 respectivamente , si haces una jugada como:  col=3, row=6 -> col=3, row=4  y yo una jugada como col=2, row=2 -> col=3, row=4, significa que devore  tu pieza  ya que el destino es el mismo y por obvias razones ya no podras mover esa pieza.   Explicado todas las reglas empezare, recuerda responder unicamente con tu jugada!. Empiezo yo con las blancas: Movimientos:${previous_move}"
        )

    }
}