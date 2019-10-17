import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

class QueenGraph {
    int n;
    ArrayList<Queen> queens;
    ArrayList<ArrayList<Integer>> solutions;
    int unassignedIndex;
    int numberBacktracks;

    // initialize the QueenGraph object
    public QueenGraph(int numQueens) {
        n = numQueens;
        unassignedIndex = 0;
        queens = new ArrayList<>();
        solutions = new ArrayList<>();
        numberBacktracks = 0;

        int i;
        for(i = 0; i < numQueens; i++)
            queens.add(new Queen(n, i));
    }

    // inference method for forward checking
    private void inference(Queen q) {
        int row = q.xPosition;
        int col = q.yPosition;

        int nextQueen;
        int i;

        // get rid of domain values that are in the same row as the just
        // assigned queen
        for(nextQueen = unassignedIndex; nextQueen < n; nextQueen++)
        {
            Queen nq = queens.get(nextQueen);
            int size = nq.domain.size();
            i = 0;
            while(i < size)
            {
                if(nq.domain.get(i).getKey() == row)
                {
                    nq.domain.remove(i);
                    size--;
                }
                else
                    i++;
            }
        }

        // get rid of domain values that are in the lower diagonal path of
        // the just assigned queen
        if(row + 1 < n && col + 1 < n)
        {
            int xCo = row + 1;
            int yCo = col + 1;
            for(nextQueen = unassignedIndex; nextQueen < n; nextQueen++)
            {
                Queen nq = queens.get(nextQueen);
                while(yCo != nq.yPosition)
                {
                    xCo++;
                    yCo++;
                }
                int size = nq.domain.size();
                i = 0;
                while(i < size)
                {
                    if(nq.domain.get(i).getKey() == xCo && nq.domain.get(i).getValue() == yCo)
                    {
                        nq.domain.remove(i);
                        size--;
                    }
                    else
                        i++;
                }
                if(xCo + 1 < n && yCo + 1 < n)
                {
                    xCo++;
                    yCo++;
                }
                else
                    break;
            }
        }

        // get rid of domain values that are in the upper diagonal path of
        // the just assigned queen
        if(row - 1 >= 0 && col + 1 < n)
        {
            int xCo = row - 1;
            int yCo = col + 1;
            for(nextQueen = unassignedIndex; nextQueen < n; nextQueen++)
            {
                Queen nq = queens.get(nextQueen);
                while(yCo != nq.yPosition)
                {
                    xCo--;
                    yCo++;
                }
                int size = nq.domain.size();
                i = 0;
                while(i < size)
                {
                    if(nq.domain.get(i).getKey() == xCo && nq.domain.get(i).getValue() == yCo)
                    {
                        nq.domain.remove(i);
                        size--;
                    }
                    else
                        i++;
                }
                if(xCo - 1 >= 0 && yCo + 1 < n)
                {
                    xCo--;
                    yCo++;
                }
                else
                    break;
            }
        }
    }

    // check to make sure that no variables have been
    // reduced to a domain size of 0
    public boolean noFailures() {
        int i;
        for(i = unassignedIndex; i < n; i++)
        {
            Queen q = queens.get(i);
            if(q.domain.size() == 0)
                return false;
        }

        return true;
    }

    // reset the domains of unassigned queens if
    // backtracking is required
    public void resetDomains() {
        int i, j;
        // clear the domains of queens that haven't been
        // assigned yet
        for(i = unassignedIndex; i < n; i++)
        {
            Queen q = queens.get(i);
            q.domain.clear();
            for(j = 0; j < n; j++)
            {
                q.domain.add(new AbstractMap.SimpleEntry<>(j, q.yPosition));
            }
        }

        // add back the domain constraints for
        // queens that HAVE been assigned
        for(i = 0; i < unassignedIndex - 1; i++)
        {
            Queen q = queens.get(i);
            inference(q);
        }
    }

    // check to see if you've assigned each
    // queen to a position on the board
    public boolean isComplete() {
        int i;
        for(i = 0; i < n; i++)
        {
            // if any queen hasn't been assigned (their xPosition is MAX_VALUE).
            // return false
            if(queens.get(i).xPosition == Integer.MAX_VALUE)
                return false;
        }

        // otherwise, all queens have been assigned, return true
        return true;
    }

    // print solutions to output file
    public void printSolutions(FileOutputStream rfile) throws IOException {
        int i, j, size = solutions.size();
        String s = "";
        for(i = 0; i < size; i++)
        {
            ArrayList<Integer> solution = solutions.get(i);
            s += "Solution " + i + "\n";
            for(j = 0; j < n; j++)
            {
                s += "Queen " + j + " at position (" + solution.get(j) + ", " + j + ")\n";
            }
            s += "\n";
        }

        rfile.write(s.getBytes());
    }

    // print constraints, variables, and domains to output file
    public void printConstraintsToCFile(FileOutputStream cfile) throws IOException {
        int i;
        String s = "Here are the domains\n";
        for(i = 0; i < n; i++)
        {
            Queen q = queens.get(i);
            s += "Queen " + q.yPosition + " has domain\n";
            s += q.retrieveDomain();
        }
        s += "The constraints are the problem for two queens Xi and Xy are as follows\n";
        s += "1. Xi and Xy cannot be in the same row\n";
        s += "2. If Xy's row is below Xi's, then Xy's row cannot be equal to Xi.row + diff(Xi.col, Xy.col). This would cause Xy to be in the lower diagonal path of Xi.\n";
        s += "3. If Xy's row is above Xi's, then Xy's row cannot be equal to Xi.row - diff(Xi.col, Xy.col). This would cause Xy to be in the upper diagonal path of Xi.\n";

        cfile.write(s.getBytes());
    }

    // do forward checking on queen 0, call recursive
    // function for all future queens
    public void forwardChecking(FileOutputStream cfile, FileOutputStream rfile) throws IOException {
        int i;
        printConstraintsToCFile(cfile);
        long start = System.nanoTime();
        Queen q = queens.get(unassignedIndex++);

        // go through all n possible placements for queen 0
        for(i = 0; i < n; i++)
        {
            q.setXPosition(i);
            inference(q);
            if(noFailures())
            {
                forwardCheckingHelper();
                if(solutions.size() == 2 * n)
                    break;
            }
        }
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        long timeElapsedMS = timeElapsed / 1000000;
        System.out.println("Time to calculate " + solutions.size() + " solutions: " + timeElapsedMS + "ms");
        System.out.println("Number of backtracking steps taken: " + numberBacktracks);

        printSolutions(rfile);
    }

    // recursive function for forward iteration
    public void forwardCheckingHelper() {
        if(isComplete())
        {
            ArrayList<Integer> solution = new ArrayList<>();
            int i;
            for(i = 0; i < n; i++)
            {
                solution.add(queens.get(i).xPosition);
            }
            solutions.add(solution);
            return;
        }
        int i, size;

        Queen q = queens.get(unassignedIndex++);
        size = q.domain.size();

        // assign next queen based off of its current domain
        for(i = 0; i < size; i++)
        {
            q.setXPosition(q.domain.get(i).getKey());
            inference(q);
            if(noFailures())
            {
                forwardCheckingHelper();
                if(solutions.size() == 2 * n)
                    return;
            }

            // if there are conflicts (i.e. a queen has domain size 0),
            // revert this assignment and move onto the next one
            else
            {
                resetDomains();
                numberBacktracks++;
            }
        }

        // once you're done going through the domain,
        // reset this variables assignment, reset the domains,
        // and return
        unassignedIndex--;
        resetDomains();
        q.setXPosition(Integer.MAX_VALUE);
        return;
    }

    // initialize the queue for each recursive call
    // to ac3
    public void initializeQueue(LinkedList<AbstractMap.SimpleEntry<Queen, Queen>> queue) {
        // placedQueen is the queen you just assigned
        int i, placedQueen = unassignedIndex - 1;
        // add the arcs between the just assigned queen
        // and its neighbors (all queens after it)
        for(i = unassignedIndex; i < n; i++)
        {
            queue.add(new AbstractMap.SimpleEntry<>(queens.get(i), queens.get(placedQueen)));
        }
    }

    // revise function that ac3 calls
    public boolean revise(AbstractMap.SimpleEntry<Queen, Queen> arc) {
        boolean revised = false;
        boolean consistent;
        int i = 0, j, size = arc.getKey().domain.size();

        // for each x in Di
        while(i < size)
        {
            // get Xy's domain size and
            // the possible assignment information for Xi
            int dYSize = arc.getValue().domain.size();
            int Xirow = arc.getKey().domain.get(i).getKey();
            int Xicol = arc.getKey().yPosition;
            consistent = false;
            // test values of y for x in Di,
            // where y is the row
            for(j = 0; j < dYSize; j++)
            {
                int Xyrow = arc.getValue().domain.get(j).getKey();
                int Xycol = arc.getValue().yPosition;
                // first test that two queens wouldn't be
                // in the same row
                if(Xyrow != Xirow)
                {
                    int diffY = Xicol - Xycol;
                    // test the lower diagonal from Xy
                    // if you pass the test, you've found an
                    // assignment that is consistent
                    if(Xirow > Xyrow)
                    {
                        if(Xirow != Xyrow + diffY)
                            consistent = true;
                    }
                    // test the upper diagonal from Xy
                    // if you pass the test, you've found an
                    // assignment that is consistent
                    else
                    {
                        if(Xirow != Xyrow - diffY)
                            consistent = true;
                    }
                }
            }

            // if there is no consistency between Xi and Xy
            // for the assignment (x, y), remove x from Xi's domain
            if(!consistent)
            {
                arc.getKey().domain.remove(i);
                size--;
                revised = true;
            }
            else
                i++;
        }

        return revised;
    }

    // ac3 implementation
    public boolean ac3() {
        LinkedList<AbstractMap.SimpleEntry<Queen, Queen>> queue = new LinkedList<>();
        initializeQueue(queue);
        int i;
        while(queue.size() != 0)
        {
            AbstractMap.SimpleEntry<Queen, Queen> arc = queue.poll();
            if(revise(arc))
            {
                if(arc.getKey().domain.size() == 0)
                    return false;
                for(i = arc.getKey().yPosition + 1; i < n; i++)
                    queue.add(new AbstractMap.SimpleEntry<>(queens.get(i), arc.getKey()));
            }
        }

        return true;
    }

    // MAC
    // If assigning queen 0 to a position doesn't cause
    // a failure in the other queens, call the recursive
    // function and you WILL find a solution
    public void maintainingArcConsistency(FileOutputStream cfile, FileOutputStream rfile) throws IOException {
        printConstraintsToCFile(cfile);
        int i, j;
        Queen q = queens.get(unassignedIndex++);
        long start = System.nanoTime();
        for(i = 0; i < n; i++)
        {
            q.setXPosition(i);
            resetDomains();
            int size = q.domain.size();
            j = 0;

            // need to remove from queen 0's domain
            // any value that isn't the assignment
            // in order for ac3 to work
            while(j < size)
            {
                if(q.domain.get(j).getKey() != i)
                {
                    q.domain.remove(j);
                    size--;
                }
                else
                    j++;
            }
            ac3();
            if(noFailures())
            {
                maintainingArcConsistencyHelper();
                if(solutions.size() == 2*n)
                    break;
            }

            // give queen 0 its domain back
            q.domain.clear();
            for(j = 0; j < n; j++)
            {
                q.domain.add(new AbstractMap.SimpleEntry<>(j, 0));
            }
        }
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        long timeElapsedMS = timeElapsed / 1000000;
        System.out.println("Time to calculate " + solutions.size() + " solutions: " + timeElapsedMS + "ms");
        System.out.println("Number of backtracking steps taken: " + numberBacktracks);
        printSolutions(rfile);
    }

    // recursive MAC function
    public void maintainingArcConsistencyHelper() {
        if(isComplete())
        {
            ArrayList<Integer> solution = new ArrayList<>();
            int i;
            for(i = 0; i < n; i++)
            {
                solution.add(queens.get(i).xPosition);
            }
            solutions.add(solution);
            return;
        }

        int i, size;

        Queen q = queens.get(unassignedIndex++);
        size = q.domain.size();

        for(i = 0; i < size; i++)
        {
            q.setXPosition(q.domain.get(i).getKey());
            boolean result = ac3();

            // if you're inside the recursive function,
            // you know that there is a solution,
            // so no need to check that a domain has been reduced to 0,
            // just have to check the return value of ac3
            if(result)
            {
                inference(q);
                maintainingArcConsistencyHelper();
                if(solutions.size() == 2*n)
                    return;
            }

            // this assignment didn't work, so backtrack
            else
            {
                resetDomains();
                numberBacktracks++;
            }
        }

        // reset assignment
        unassignedIndex--;
        resetDomains();
        q.setXPosition(Integer.MAX_VALUE);
        return;
    }
}
