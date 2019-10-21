
package memory

import chisel3._


/** 
 * The Register File for the JOCEL processor implementation.
 */
class RAM (n:Int) extends Module {
  val io = IO(new Bundle {
    val WR_DATA         = Input(Vec(4, UInt(8.W)))
    val WR              = Input(Bool())
    val MASK            = Input(UInt(4))
    val ADDR            = Input(UInt(sizeof(n-2).W))
    val EN              = Input(Bool())

    val RD_DATA         = Output(Vec(4, UInt(8.W)))
  })

  val mem = SyncReadMem(n/4, Vec(4, UInt(8.W)))

  when(io.WR) {
    mem.write(io.ADDR, io.WR_DATA, io.MASK)
    io.RD_DATA := DontCare
  }.otherwise{
    io.RD_DATA := mem.read(io.ADDR, io.EN)
  }
}
