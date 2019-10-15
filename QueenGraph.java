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
        System.out.println("inferencing domains after assigning queen " + q.yPosition + " to position (" + row + ", " + col + ")");
        System.out.println("This is unassignedIndex: " + unassignedIndex);

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
        for(i = 0; i < n; i++)
        {
            Queen nq = queens.get(i);
            System.out.println("This is Queen " + i + "'s domain after getting rid of same row");
            nq.printDomain();
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
        for(i = 0; i < n; i++)
        {
            Queen nq = queens.get(i);
            System.out.println("This is Queen " + i + "'s domain after getting rid of lower diagonal");
            nq.printDomain();
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
        for(i = 0; i < n; i++)
        {
            Queen nq = queens.get(i);
            System.out.println("This is Queen " + i + "'s domain after getting rid of upper diagonal");
            nq.printDomain();
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
            System.out.println("This is Queen " + q.yPosition + "'s domain after resetting without inference");
            q.printDomain();
        }

        for(i = 0; i < unassignedIndex - 1; i++)
        {
            Queen q = queens.get(i);
            inference(q);
        }

        for(i = 0; i < n; i++)
        {
            Queen q = queens.get(i);
            System.out.println("This is Queen " + q.yPosition + "'s domain after resetting with inference");
            q.printDomain();
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

        cfile.write(s.getBytes());
    }

    public void forwardChecking(FileOutputStream cfile, FileOutputStream rfile) throws IOException {
        int i;
        printConstraintsToCFile(cfile);
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

        printSolutions(rfile);
    }

    public void forwardCheckingHelper() {
        if(isComplete())
        {
            System.out.println("Found a solution!");
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
        System.out.println("Finding possible solutions for Queen " + unassignedIndex);
        System.out.println("Here are the domains");
        for(i = 0; i < n; i++)
        {
            Queen q = queens.get(i);
            System.out.println("Queen " + q.yPosition + " has domains");
            q.printDomain();
        }

        Queen q = queens.get(unassignedIndex++);
        size = q.domain.size();

        for(i = 0; i < size; i++)
        {
            System.out.println("Assigning Queen " + q.yPosition + " xPosition" + q.domain.get(i).getKey());
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
                System.out.println("SAD! Backtracking becuase Queen " + q.yPosition + " with xPosition " + q.xPosition + " failed the test!");
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
        int size = queue.size();
        for(i = 0; i < size; i++)
        {
            System.out.print("At index " + i + ", queue has arc: (");
            System.out.println(queue.get(i).getKey().yPosition + ", " + queue.get(i).getValue().yPosition + ")");
        }
    }

    public boolean revise(AbstractMap.SimpleEntry<Queen, Queen> arc) {
        boolean revised = false;
        boolean consistent;
        int i = 0, j, size = arc.getKey().domain.size();
        while(i < size)
        {
            consistent = false;
            int xjSize = arc.getValue().domain.size();
            for(j = 0; j < xjSize; j++)
            {
                // if a value in this domain allows for Di to exist,
                // set consistent = true
                ;
            }
            if(!consistent) {
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
        while(queue.size != 0)
        {
            AbstractMap.SimpleEntry<Queen, Queen> arc = queue.poll();
            if(revise(arc)
            {
                System.out.println("Hello!");
                if(!noFailures())
                    return false;
            }
        }

        return true;
    }

    public void maintainingArcConsistency(FileOutputStream cfile, FileOutputStream rfile) throws IOException {
        printConstraintsToCFile(cfile);
        int i;
        Queen q = queens.get(unassignedIndex++);
        for(i = 0; i < n; i++)
        {
            q.setXPosition(i);
            boolean result = ac3();
            System.out.println(result);
        }
        printSolutions(rfile);
    }

    public void maintainingArcConsistencyHelper() {
        if(isComplete())
        {
            System.out.println("Found a solution!");
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
        System.out.println("Finding possible solutions for Queen " + unassignedIndex);
        System.out.println("Here are the domains");
        for(i = 0; i < n; i++)
        {
            Queen q = queens.get(i);
            System.out.println("Queen " + q.yPosition + " has domains");
            q.printDomain();
        }

        Queen q = queens.get(unassignedIndex++);
        size = q.domain.size();

        for(i = 0; i < size; i++)
        {

        }
    }

    public void maintainingArcConsistency() {
        if(isComplete())
        {
            System.out.println("Found a solution!");
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
        System.out.println("Finding possible solutions for Queen " + unassignedIndex);
        System.out.println("Here are the domains");
        for(i = 0; i < n; i++)
        {
            Queen q = queens.get(i);
            System.out.println("Queen " + q.yPosition + " has domains");
            q.printDomain();
        }

        Queen q = queens.get(unassignedIndex++);
        size = q.domain.size();

        for(i = 0; i < 1; i++)
        {
            System.out.println("Assigning Queen " + q.yPosition + " xPosition" + q.domain.get(i).getKey());
            q.setXPosition(q.domain.get(i).getKey());
            ac3();
            if(noFailures())
            {
                maintainingArcConsistency();
                if(solutions.size() == 2 * n)
                    return;
            }
            else
            {
                System.out.println("SAD! Backtracking becuase Queen " + q.yPosition + " with xPosition " + q.xPosition + " failed the test!");
                resetDomains();
            }
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
