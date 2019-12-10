 init: move r1 0          # Initialise counter
       move r8 1000       # Number of iterations

start: sai  r0 0
       addi r1 r1 1
       subi r2 r2 0
       bne  r3 r4 1
       sai  r5 1
       addi r6 r6 1
       subi r7 r7 0
       blt  r1 r8 start