class NQueens {
	public static void main(String[] args) {
		if(args.length < 4) {
			System.out.println("Not enough arguments! Proper format is\n" + 
			"java NQueens <ALG> <N> <CFile> <RFile>");
		}

		QueenGraph q = new QueenGraph(Integer.parseInt(args[1]));

		if(args[0].equals("FOR"))
		{
			q.forwardChecking();
		}
	}
}