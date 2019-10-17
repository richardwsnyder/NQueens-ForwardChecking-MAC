import java.io.*;

class NQueens {
    public static void main(String[] args) throws IOException {
        if(args.length < 4)
        {
            System.out.println("Not enough arguments! Correct format is:\njava NQueens <ALG> <N> <CFile> <RFile>");
            return;
        }

        // create an nxn board, with n = args[1]
        QueenGraph q = new QueenGraph(Integer.parseInt(args[1]));
        FileOutputStream cfile = null;
        FileOutputStream rfile = null;

        // if args[0] is FOR, do forward checking
        if(args[0].equals("FOR"))
        {
            try {
                cfile = new FileOutputStream(args[2]);
                rfile = new FileOutputStream(args[3]);
                q.forwardChecking(cfile, rfile);
            } finally {
                if (cfile != null)
                    cfile.close();
                if (rfile != null)
                    rfile.close();
            }
        }
        // if args[0] is MAC, do maintaining arc consistency
        else
        {
            try {
                cfile = new FileOutputStream(args[2]);
                rfile = new FileOutputStream(args[3]);
                q.maintainingArcConsistency(cfile, rfile);
            } finally {
                if (cfile != null)
                    cfile.close();
                if (rfile != null)
                    rfile.close();
            }
        }
    }
}
