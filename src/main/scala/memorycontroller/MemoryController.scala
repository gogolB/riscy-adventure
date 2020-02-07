
package memorycontroller

import chisel3._
import chisel3.util._


/** 
 * The memorycontroller for the processor implementation.
 */
class MemoryController (n:Int) extends Module {
  val io = IO(new Bundle {
    val WR_ADDR         = Input(UInt(log2Ceil(n).W))
    val RD_ADDR         = Input(UInt(log2Ceil(n).W))

    val WR_DATA         = Input(Vec(4, UInt(8.W)))
    val RD_DATA         = Output(Vec(4, UInt(8.W)))

    val NUM_WORDS       = INPUT(Uint(2).W)

    val WR              = Input(Bool())
    val RD              = Input(Bool())

    val isValid         = Ouput(Bool())
  })

  val mem = RAM(n)

  when(io.WR) {
    mem.write(io.WR_ADDR, io.WR_DATA, io.MASK)
  }
  when(io.RD){
    io.RD_DATA := mem.read(io.RD_ADDR, io.RD)
  }.otherwise{
      io.RD_DATA := DontCare
  }
}