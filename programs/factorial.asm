init: move r0 12      # r0 <- 12 (Factorial to calculate)
      move r1 1       # Init running product
      move r2 1       # Init counter

loop: bgt r2 r0 end   # Go to end if counter exceeds factorial to calculate
      mul r1 r1 r2    # Perform multiplication
      addi r2 r2 1    # Increment counter variable
      jmp loop

 end: nop