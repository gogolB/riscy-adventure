
package memory

import chisel3._
import chisel3.util._


/** 
 * The RAM for the JOCEL processor implementation.
 */
class RAM (n:Int) extends Module {
  val io = IO(new Bundle {
    val WR_DATA         = Input(Vec(4, UInt(8.W)))
    val WR              = Input(Bool())
    val MASK            = Input(Vec(4, Bool()))
    val WR_ADDR         = Input(UInt(log2Ceil(n-2).W))
    val RD_ADDR         = Input(UInt(log2Ceil(n-2).W))
    val RD              = Input(Bool())

    val RD_DATA         = Output(Vec(4, UInt(8.W)))
  })

  val mem = SyncReadMem(n/4, Vec(4, UInt(8.W)))

  when(io.WR) {
    mem.write(io.WR_ADDR, io.WR_DATA, io.MASK)
  }
  when(io.RD){
    io.RD_DATA := mem.read(io.RD_ADDR, io.RD)
  }.otherwise{
      io.RD_DATA := DontCare
  }
}
