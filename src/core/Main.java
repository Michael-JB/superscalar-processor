package core;

import instruction.Instruction;
import instruction.AddInstruction;
import instruction.Opcode;
import instruction.RegisterOperand;

public class Main {

  public static void main(String[] args) {
    System.out.println("Hello!");

    Instruction instruction = new AddInstruction(Opcode.ADD, new RegisterOperand(1), new RegisterOperand(2), new RegisterOperand(3));

    instruction.execute();
  }

}