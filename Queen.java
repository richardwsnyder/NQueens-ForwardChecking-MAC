import java.util.*;

class Queen {
    int xPosition, yPosition;
    ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> domain;

    public Queen(int n, int index) {
        xPosition = Integer.MAX_VALUE;
        yPosition = index;
        domain = new ArrayList<>();

        int i;
        for(i = 0; i < n; i++)
            domain.add(new AbstractMap.SimpleEntry<>(i, index));
    }

    public void setXPosition(int x) {
        xPosition = x;
    }

    public void printDomain() {
        int i, size = domain.size();
        for(i = 0; i < size; i++)
            System.out.println("<" + domain.get(i).getKey() + ", " + domain.get(i).getValue() + ">");
        System.out.println();
    }
}