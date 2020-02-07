package registerfile

import java.io.File

import chisel3.iotesters
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class RegisterFileUnitTester(c: RegisterFile) extends PeekPokeTester(c) {

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

class RegisterFileTester extends ChiselFlatSpec {
// Disable this until we fix isCommandAvailable to swallow stderr along with stdout
  private val backendNames = if(false && firrtl.FileUtils.isCommandAvailable(Seq("verilator", "--version"))) {
    Array("firrtl", "verilator")
  }
  else {
    Array("firrtl")
  }
  for ( backendName <- backendNames ) {
    "RegisterFile" should s"meet RISC 5 Spec (with $backendName)" in {
      Driver(() => new RegisterFile, backendName) {
        c => new RegisterFileUnitTester(c)
      } should be (true)
    }
  }

  "Basic test using Driver.execute" should "be used as an alternative way to run specification" in {
    iotesters.Driver.execute(Array(), () => new RegisterFile) {
      c => new RegisterFileUnitTester(c)
    } should be (true)
  }

  "using --backend-name verilator" should "be an alternative way to run using verilator" in {
    if(backendNames.contains("verilator")) {
      iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new RegisterFile) {
        c => new RegisterFileUnitTester(c)
      } should be(true)
    }
  }

  "running with --is-verbose" should "show more about what's going on in your tester" in {
    iotesters.Driver.execute(Array("--is-verbose"), () => new RegisterFile) {
      c => new RegisterFileUnitTester(c)
    } should be(true)
  }

  /**
    * By default verilator backend produces vcd file, and firrtl and treadle backends do not.
    * Following examples show you how to turn on vcd for firrtl and treadle and how to turn it off for verilator
    */

  "running with --generate-vcd-output on" should "create a vcd file from your test" in {
    iotesters.Driver.execute(
      Array("--generate-vcd-output", "on", "--target-dir", "test_run_dir/make_a_vcd", "--top-name", "make_a_vcd"),
      () => new RegisterFile
    ) {

      c => new RegisterFileUnitTester(c)
    } should be(true)

    new File("test_run_dir/make_a_vcd/make_a_vcd.vcd").exists should be (true)
  }

  "running with --generate-vcd-output off" should "not create a vcd file from your test" in {
    iotesters.Driver.execute(
      Array("--generate-vcd-output", "off", "--target-dir", "test_run_dir/make_no_vcd", "--top-name", "make_no_vcd",
      "--backend-name", "verilator"),
      () => new RegisterFile
    ) {

      c => new RegisterFileUnitTester(c)
    } should be(true)

    new File("test_run_dir/make_no_vcd/make_a_vcd.vcd").exists should be (false)

  }
}
