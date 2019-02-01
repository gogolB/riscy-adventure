package registerfile

import chisel3._


/** 
 * The Register File for the JOCEL processor implementation.
 */
class RegisterFile extends Module {
  val io = IO(new Bundle {
    val W_DATA        = Input(UInt(32.W))
    val LD            = Input(Bool())
    val R1_SEL        = Input(UInt(5.W))
    val R2_SEL        = Input(UInt(5.W))

    val R1            = Output(UInt(32.W))
    val R2            = Output(UInt(32.W))
  })

  val registers = Reg(Vec(32, UInt(32.W)))
  registers(0) := 0.U

  when(io.LD) {
    when(io.R1_SEL > 0.U) {
      registers(io.R1_SEL) := io.W_DATA
    }
  }

  // Set the output to be whatever was selected.
  io.R1 := registers(io.R1_SEL)
  io.R2 := registers(io.R2_SEL)
}
