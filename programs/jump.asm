move r0 5              # r0 <- 5
move r1 6              # r1 <- 6

add r2 r0 r1           # r2 <- r0 + r1
add r3 r0 r1           # r3 <- r0 + r1
jmp 6                  # PC <- 6
add r4 r0 r1           # r4 <- r0 + r1 # THIS LINE SHOULD BE SKIPPED!
add r5 r0 r1           # r5 <- r0 + r1
add r6 r0 r1           # r6 <- r0 + r1