import java.util.*;

class Queen {
    int xPosition, yPosition;
    ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> domain;

    // queen constructor
    public Queen(int n, int index) {
        xPosition = Integer.MAX_VALUE;
        yPosition = index;
        domain = new ArrayList<>();

        int i;
        for(i = 0; i < n; i++)
            domain.add(new AbstractMap.SimpleEntry<>(i, index));
    }

    // set the x position of a queen on the board
    public void setXPosition(int x) {
        xPosition = x;
    }

    // print the domain of a given queen
    // used for debugging purposes
    public void printDomain() {
        int i, size = domain.size();
        for(i = 0; i < size; i++)
            System.out.println("<" + domain.get(i).getKey() + ", " + domain.get(i).getValue() + ">");
        System.out.println();
    }

    // return the domain in the form of a string
    // used for printing domains to CFile
    public String retrieveDomain() {
        int i, size = domain.size();
        String s = "";
        for(i = 0; i < size; i++)
            s += "<" + domain.get(i).getKey() + ", " + domain.get(i).getValue() + ">\n";
        s += "\n";
        return s;
    }
}
