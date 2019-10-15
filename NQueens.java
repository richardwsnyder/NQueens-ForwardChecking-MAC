import java.io.*;

class NQueens {
    public static void main(String[] args) throws IOException {
        if(args.length < 4)
            System.out.println("Not enough arguments!\njava NQueens <ALG> <N> <CFile> <RFile>");

        QueenGraph q = new QueenGraph(Integer.parseInt(args[1]));
        FileOutputStream cfile = null;
        FileOutputStream rfile = null;
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
