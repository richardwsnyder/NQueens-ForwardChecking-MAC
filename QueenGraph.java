<<<<<<< HEAD
import java.util.*;

class QueenGraph {
	int n;
	boolean[][] board;
	ArrayList<Queen> queens;
	int unassignedIndex;

	public QueenGraph(int numQueens) {
		n = numQueens;
		unassignedIndex = 0;
		board = new boolean[n][n];
		queens = new ArrayList<>();
		int i, j;
		for(i = 0; i < n; i++)
		{
			for(j = 0; j < n; j++)
				board[i][j] = true;
			queens.add(new Queen(n, i));
		}
	}

	public boolean isComplete() {
		int i, j;
		for(i = 0; i < n; i++) 
		{
			Queen q = queens.get(i);
			if(q.xPosition == Integer.MAX_VALUE)
				return false;
		}

		return true;
	}

	public void printSolution() {
		int i;
		System.out.println("Found a solution with assignments:");
		for(i = 0; i < n; i++)
		{	
			Queen q = queens.get(i);
			System.out.println("Queen " + i + " at position: (" + q.xPosition + ", " + q.yPosition + ")");
		}
		System.out.println();
	}

	public void printFailure() {
		int i;
		System.out.println("There is no solution for config:");
		for(i = 0; i < unassignedIndex; i++)
		{
			Queen q = queens.get(i);
			System.out.println("Queen " + i + " at position: (" + q.xPosition + ", " + q.yPosition + ")");
		}
		System.out.println();

		unassignedIndex = 1;
		this.resetDomains();
	}

	public void inference(Queen q) {
		int nextQueen;
		int row = q.xPosition;
		int col = q.yPosition;
		int i;
		int xCoordinate, yCoordinate;
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
					continue;
				}

				else
					i++;
			}
		}

		if(row + 1 < n && col + 1 < n)
		{
			xCoordinate = row + 1; yCoordinate = col + 1;
			for(nextQueen = unassignedIndex; nextQueen < n; nextQueen++)
			{
				Queen nq = queens.get(nextQueen);
				int size = nq.domain.size();
				i = 0;

				while(i < size)
				{
					if(nq.domain.get(i).getKey() == xCoordinate && nq.domain.get(i).getValue() == yCoordinate)
					{
						nq.domain.remove(i);
						size--;
						if(xCoordinate + 1 < n && yCoordinate + 1 < n)
						{	
							xCoordinate++;
							yCoordinate++;
							continue;
						}
						else
							break;
					}
					else
						i++;
				}
			}
		}

		if(row - 1 > -1 && col + 1 < n)
		{
			xCoordinate = row - 1; yCoordinate = col + 1;
			for(nextQueen = unassignedIndex; nextQueen < n; nextQueen++)
			{
				Queen nq = queens.get(nextQueen);
				int size = nq.domain.size();
				i = 0;

				while(i < size)
				{
					if(nq.domain.get(i).getKey() == xCoordinate && nq.domain.get(i).getValue() == yCoordinate)
					{
						nq.domain.remove(i);
						size--;
						if(xCoordinate - 1 > -1 && yCoordinate + 1 < n)
						{
							xCoordinate--;
							yCoordinate++;
							continue;
						}
						else
							break;
					}
					else
						i++;
				}
			}
		}
	}

	// check to see that no domains have been reduced
	// to size 0
	public boolean noFailure() {
		int i;
		for(i = unassignedIndex; i < n; i++)
		{
			Queen nq = queens.get(i);
			if(nq.domain.size() == 0)
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
				q.domain.add(new AbstractMap.SimpleEntry<>(j, i));
			}
		}

		for(i = 0; i < unassignedIndex - 1; i++) 
		{
			Queen q = queens.get(i);
			this.inference(q);
		}
	}

	public boolean forwardChecking() {
		int i, j, size;
		boolean allAssigned = false;
		System.out.println("In forwardChecking call with unassignedIndex: " + unassignedIndex + ". Here are the domains");
		for(i = 0; i < n; i++)
		{
			Queen q = queens.get(i);
			System.out.println("Queen " + i + " with xPosition: " + q.xPosition);
			q.printDomain();
		}
		if(this.isComplete()) {
			return true;
		}

		boolean result;

		Queen q = queens.get(unassignedIndex++);
		size = q.domain.size();
		for(i = 0; i < size; i++)
		{
			q.setPositionX(q.domain.get(i).getKey());
			this.inference(q);
			if(this.noFailure())
			{
				result = forwardChecking();
				if(result)
				{
					this.printSolution();
					allAssigned = true;
				}
			}
			else
			{
				System.out.println("Failed on instance of queen " + q.yPosition + " with xPosition: " + q.xPosition);
				this.resetDomains();
			}
		}
		this.printFailure();
		return false;
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
=======
import java.util.*;

class QueenGraph {
	int n;
	boolean[][] board;
	ArrayList<Queen> queens;
	int unassignedIndex;

	public QueenGraph(int numQueens) {
		n = numQueens;
		unassignedIndex = 0;
		board = new boolean[n][n];
		queens = new ArrayList<>();
		int i, j;
		for(i = 0; i < n; i++)
		{
			for(j = 0; j < n; j++)
				board[i][j] = true;
			queens.add(new Queen(n, i));
			queens.get(i).printDomain();
		}
	}

	public boolean isComplete() {
		int i, j;
		for(i = 0; i < n; i++) 
		{
			Queen q = queens.get(i);
			if(q.xPosition == Integer.MAX_VALUE)
				return false;
		}

		return true;
	}

	public void printSolution() {
		int i;
		System.out.println("Found a solution with assignments:");
		for(i = 0; i < n; i++)
		{	
			Queen q = queens.get(i);
			System.out.println("Queen " + i + " at position: (" + q.xPosition + ", " + q.yPosition + ")");
		}
	}

	public void inference(Queen q) {
		int nextQueen;
		int row = q.xPosition;
		int col = q.yPosition;
		int i;
		int xCoordinate, yCoordinate;
		for(nextQueen = unassignedIndex; nextQueen < n; nextQueen++)
		{
			Queen nq = queens.get(nextQueen);
			int size = nq.domain.size();
			i = 0;
			
			while(i < size)
			{
				if(nq.domain.get(i).getKey() == row || nq.domain.get(i).getValue() == col)
				{
					nq.domain.remove(i);
					size--;
					continue;
				}

				else
					i++;
			}
		}

		if(row + 1 < n && col + 1 < n)
		{
			xCoordinate = row + 1; yCoordinate = col + 1;
			for(nextQueen = unassignedIndex; nextQueen < n; nextQueen++)
			{
				Queen nq = queens.get(nextQueen);
				int size = nq.domain.size();
				i = 0;

				while(i < size)
				{
					if(nq.domain.get(i).getKey() == xCoordinate && nq.domain.get(i).getValue() == yCoordinate)
					{
						nq.domain.remove(i);
						size--;
						if(xCoordinate + 1 < n && yCoordinate + 1 < n)
						{	
							xCoordinate++;
							yCoordinate++;
							continue;
						}
						else
							break;
					}
					else
						i++;
				}
			}
		}

		if(row - 1 > -1 && col + 1 < n)
		{
			xCoordinate = row - 1; yCoordinate = col + 1;
			for(nextQueen = unassignedIndex; nextQueen < n; nextQueen++)
			{
				Queen nq = queens.get(nextQueen);
				int size = nq.domain.size();
				i = 0;

				while(i < size)
				{
					if(nq.domain.get(i).getKey() == xCoordinate && nq.domain.get(i).getValue() == yCoordinate)
					{
						nq.domain.remove(i);
						size--;
						if(xCoordinate - 1 > -1 && yCoordinate + 1 < n)
						{
							xCoordinate--;
							yCoordinate++;
							continue;
						}
						else
							break;
					}
					else
						i++;
				}
			}
		}
	}

	// check to see that no domains have been reduced
	// to size 0
	public boolean noFailure() {
		int i;
		for(i = unassignedIndex; i < n; i++)
		{
			Queen nq = queens.get(i);
			if(nq.domain.size() == 0)
				return false;
		}

		return true;
	}

	public boolean forwardChecking() {
		if(this.isComplete()) {
			return true;
		}

		int i, size;
		boolean result;

		Queen q = queens.get(unassignedIndex++);
		size = q.domain.size();
		for(i = 0; i < size; i++)
		{
			q.setPositionX(i);
			this.inference(q);
			if(this.noFailure())
			{
				result = forwardChecking();
				if(result)
				{
					this.printSolution();
				}
			}
		}

		System.out.println("There is no working assignment for size " + n + "x" + n + " board");
		return false;
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
>>>>>>> 993ee12d9c5e2c80a2913e7d4f0be5e508c6e916
// is inconsistent