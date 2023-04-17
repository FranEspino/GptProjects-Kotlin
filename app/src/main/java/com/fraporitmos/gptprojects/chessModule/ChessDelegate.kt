package com.fraporitmos.chatkot.openaiModule.chessModule

interface ChessDelegate {
    fun pieceAt(square: Square) : ChessPiece?
    fun movePiece(from: Square, to: Square)
}