# NQueens
This project solves the NQueens problem using two different backtracking solutions
1. Forward Checking
2. Maintaining Arc Consistency

## Running
You can run this program using the following format:
`$java -jar NQueens <ALG> <N> <cfile> <rfile>`

Where ALG is either
1. FOR, for forward checking
2. MAC, for maintaining arc consistency

N is the number of queens you want to place on an NxN game board

cfile will be the output file that contains the initial domains of the variables and the constraints placed on each queen

rfile will be the output file that contains up to 2*n solutions to the specific problem
