import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

class QueenGraph {
    int n;
    ArrayList<Queen> queens;
    ArrayList<ArrayList<Integer>> solutions;
    int unassignedIndex;

    public QueenGraph(int numQueens) {
        n = numQueens;
        unassignedIndex = 0;
        queens = new ArrayList<>();
        solutions = new ArrayList<>();

        int i;
        for(i = 0; i < numQueens; i++)
            queens.add(new Queen(n, i));
    }

    private void inference(Queen q) {
        int row = q.xPosition;
        int col = q.yPosition;

        int nextQueen;
        int i;

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

    public void resetDomains() {
        int i, j;
        for(i = unassignedIndex; i < n; i++)
        {
            Queen q = queens.get(i);
            q.domain.clear();
            for(j = 0; j < n; j++)
            {
                q.domain.add(new AbstractMap.SimpleEntry<>(j, q.yPosition));
            }
        }

        for(i = 0; i < unassignedIndex - 1; i++)
        {
            Queen q = queens.get(i);
            inference(q);
        }
    }

    public boolean isComplete() {
        int i;
        for(i = 0; i < n; i++)
        {
            if(queens.get(i).xPosition == Integer.MAX_VALUE)
                return false;
        }

        return true;
    }

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

    public void forwardChecking(FileOutputStream cfile, FileOutputStream rfile) throws IOException {
        int i;
        printConstraintsToCFile(cfile);
        long start = System.nanoTime();
        Queen q = queens.get(unassignedIndex++);
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

        printSolutions(rfile);
    }

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
            else
            {
                resetDomains();
            }
        }
        unassignedIndex--;
        resetDomains();
        q.setXPosition(Integer.MAX_VALUE);
        return;
    }

    public void initializeQueue(LinkedList<AbstractMap.SimpleEntry<Queen, Queen>> queue) {
        int i, placedQueen = unassignedIndex - 1;
        for(i = unassignedIndex; i < n; i++)
        {
            queue.add(new AbstractMap.SimpleEntry<>(queens.get(i), queens.get(placedQueen)));
        }
    }

    public boolean revise(AbstractMap.SimpleEntry<Queen, Queen> arc) {
        boolean revised = false;
        boolean consistent;
        int i = 0, j, size = arc.getKey().domain.size();
        
        // for each x in Di
        while(i < size)
        {
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
                if(Xyrow != Xirow)
                {
                    int diffY = Xicol - Xycol;
                    // test the lower diagonal from Xy
                    if(Xirow > Xyrow)
                    {
                        if(Xirow != Xyrow + diffY)
                            consistent = true;
                    }
                    // test the upper diagonal from Xy
                    else
                    {
                        if(Xirow != Xyrow - diffY)
                            consistent = true;
                    }
                }
            }
            
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

    // AC-3 returns false if inconsistency found true otherwise
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
        printSolutions(rfile);
    }

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
            if(result)
            {
                inference(q);
                maintainingArcConsistencyHelper();
                if(solutions.size() == 2*n)
                    return;
            }
            else
                resetDomains();
        }
        unassignedIndex--;
        resetDomains();
        q.setXPosition(Integer.MAX_VALUE);
        return;
    }
}

// backtrack general algo
// backtrack(empty set assignment) {
// 	if assignment is complete then return assignment
// 	var = select-unassigned-variable
//	for each value in order-domain-values(var, assignment)
//		if value is consistent with assignment
//			add var = value to assignment
//			inferences = Inference(var, value)
//			if inferences != failure
//				add inferences to assignment
//				result = backtrack(assignment)
//				if result != failure
//					return result
//		remove var = value and inferences from assignment


// forward checking
// whenever x is assigned, establish arc consistency for it
// for each unassigned y, delete from y's domain any value that
// is inconsistent

// MAC
// after Xi is assigned, inference calls AC-3.
// no queue of arcs, just do arcs that are unassigned neighbors
// of Xi
