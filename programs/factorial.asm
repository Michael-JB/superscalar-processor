move r0 1       # 0 | r0 <- 1 (Counter variable)
move r1 1       # 1 | r1 <- 1 (Running product)
move r2 12      # 2 | r2 <- 12 (Factorial to calculate)
bgt r0 r2 3     # 3 | PC += {3 if r0 > r2, 0 otherwise}
mul r1 r1 r0    # 4 | r1 <- r1 * r0
addi r0 r0 1    # 5 | r0 <- r0 + 1
jmp 3           # 6 | PC <- 3