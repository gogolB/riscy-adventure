package registerfile

import java.io.File

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class RegisterFileTester(c: RegisterFile) extends PeekPokeTester(c) {

  private val registerfile = c

  poke(registerfile.io.LD, true)
  step(1)

  // Register 0 is hard wired to 0, can't change it.
  poke(registerfile.io.R1_SEL, 0)
  poke(registerfile.io.W_DATA, 10)
  step(1)
  expect(registerfile.io.R1, 0)
  

  // The other registers should run as expected.
  for(i <- 1 to 31) {
    poke(registerfile.io.R1_SEL, i)
    poke(registerfile.io.W_DATA, i+1)
    step(1)
    expect(registerfile.io.R1, i+1)
  }

  // Disable loading and check
  poke(registerfile.io.LD, false)
  step(1)

  for(i <- 1 to 31) {
    poke(registerfile.io.R1_SEL, i)
    poke(registerfile.io.W_DATA, 50)
    step(1)
    expect(registerfile.io.R1, i+1)
  }

  // Now check if R2 is working.
  for(i <- 1 to 31) {
    poke(registerfile.io.R2_SEL, i)
    step(1)
    expect(registerfile.io.R2, i+1)
  }

  // Now check if R2 and R1 is working.
  for(i <- 1 to 30 by 2) {
    poke(registerfile.io.R1_SEL, i)
    poke(registerfile.io.R2_SEL, i+1)
    step(1)
    expect(registerfile.io.R1, i+1)
    expect(registerfile.io.R2, i+2)
  }

  poke(registerfile.io.R2_SEL, 31)
  expect(registerfile.io.R2, 32)

}
