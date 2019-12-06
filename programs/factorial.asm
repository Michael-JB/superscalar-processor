move r0 12      # 0 | r0 <- 12 (Factorial to calculate)
move r1 1       # 1 | r1 <- 1 (Running product)
move r2 1       # 2 | r2 <- 1 (Counter variable)
bgt r2 r0 4     # 3 | PC += {3 if r2 > r0, 0 otherwise}
mul r1 r1 r2    # 4 | r1 <- r1 * r2
addi r2 r2 1    # 5 | r2 <- r2 + 1
jmp 3           # 6 | PC <- 3
nop             # 7 |