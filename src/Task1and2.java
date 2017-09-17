import com.sun.javafx.binding.StringFormatter;

import java.io.*;
import java.util.*;

import static java.lang.System.out;

/**
 * Created by Alexander on 11.09.2017.
 */
public class Task1and2 {
    public static HashSet<Integer> nods;
    public static HashSet<Integer> left;
    public static HashSet<Integer> right;
    public static HashSet<Integer> begins;
    public static HashSet<Integer> ends;
    public static HashSet<Triple> links;
    public static HashMap<Integer, Integer> color;
    public static HashMap<Integer, Integer> order;
    public static HashMap<Integer, Node> Nods;
    public static Node start;
    public static Node finish;

    public static void main(String[] args) throws Exception{
        Scanner in  = new Scanner(System.in);
//        Scanner in  = new Scanner(new File("in.in"));

        while(true) {
            nods = new HashSet<>();
            left = new HashSet<>();
            right = new HashSet<>();
            begins = new HashSet<>();
            ends = new HashSet<>();
            links = new HashSet<>();
            Nods = new HashMap<>();
            HashSet<Triple> _links = new HashSet<>();

            out.println("Вводите ребра построчно, ввод окончите числом -1\n");
            out.println("Пример:\n8 5\n5 7\n7 6\n-1\n");

            out.println("Ввод:");
            int cur = in.nextInt();
            while (cur != -1) {
                int cur2 = in.nextInt();
                int wei = in.nextInt();

                nods.add(cur);
                nods.add(cur2);
                left.add(cur);
                right.add(cur2);
                links.add(new Triple(cur,cur2,wei));

                cur = in.nextInt();
            }

            for (Integer obj : nods) {
                if (!right.contains(obj)) {
                    begins.add(obj);
                }
                if (!left.contains(obj)) {
                    ends.add(obj);
                }
            }

            if (begins.size() != 1) {
                out.print("Было найдено " + begins.size() + " начальных точек:");
                for (Integer obj : begins) {
                    out.print(obj + " ");
                }
                out.print("\nВарианты решения:\n1)Добавить фиктивную точку\n2)Ввести рёбра заново\n");
                out.print("Выбранный вариант: ");
                int choose = in.nextInt();
                if(choose == 1){
                    int len = nods.size()+1;
                    for(int i = 0; i < len; i++){
                        if(!nods.contains(i)){
                            nods.add(i);
                            for(Integer j : begins){
                                links.add(new Triple(i, j, 0));
                            }
                            begins.clear();
                            begins.add(i);
                            break;
                        }
                    }
                }else if(choose == 2){
                    continue;
                }
            }

            if (ends.size() != 1) {
                out.print("Было найдено " + ends.size() + " конечных точек:");
                for (Integer obj : ends) {
                    out.print(obj + " ");
                }
                out.print("\nВарианты решения:\n1)Добавить фиктивная точку\n2)Ввести рёбра заново\n");
                out.print("Выбранный вариант: ");
                int choose = in.nextInt();
                if(choose == 1){
                    int len = nods.size()+1;
                    for(int i = 0; i < len; i++){
                        if(!nods.contains(i)){
                            nods.add(i);
                            for(Integer j : ends){
                                links.add(new Triple(j, i, 0));
                            }
                            ends.clear();
                            ends.add(i);
                            break;
                        }
                    }
                }else if(choose == 2){
                    continue;
                }
            }

            boolean skip = false;
            int choosen = 0;

            for(Triple link : links){
                if(link.beg == link.end){
                    if(choosen == 0) {
                        out.println("Была найдена петля.");
                        out.print("Варианты решения:\n1)Удалить все петли\n2)Ввести рёбра заново\n");
                        out.print("Выбранный вариант: ");
                        int choose = in.nextInt();
                        if (choose == 1) {
                            choosen = 1;
//                            links.remove(link);
                        } else if (choose == 2) {
                            skip = true;
                            break;
                        }
                    }else {
//                        links.remove(link);
                    }
                }else{
                    _links.add(link);
                }
            }
            if(skip)
                continue;
            links = _links;

            color = new HashMap<>();
            for(Integer nod : nods){
                color.put(nod,0);
            }
            int beg = begins.iterator().next();
            if(findCycle(beg)){
                out.println("Был найден цикл, введите ребра заново.");
                continue;
            }

            order = new HashMap<>();
            for(Integer nod : nods){
                order.put(nod,-1);
            }
            DFS(ends.iterator().next());

            for(Integer nod:nods){
                Node tmp = new Node(nod);
                if(nod.equals(begins.iterator().next()))
                    start = tmp;
                else if(nod.equals(ends.iterator().next()))
                    finish = tmp;
                Nods.put(nod,tmp);
            }

            fillTr();
            finish.tp = finish.tr;
            fillTp();
            out.println("Вершины\nНомер | Ранний срок | Поздний срок | Резерв времени ");
            for (Integer nod: nods){
                Node Nod = Nods.get(nod);
                Nod.p = Nod.tp - Nod.tr;
                out.format("%11d %d %d %d\n",Nod.name,Nod.tr,Nod.tp,Nod.p);
            }



            int kek = 1;
        }
    }

    private static boolean findCycle(int cur){
        color.put(cur,1);
        for(Triple link : links){
            if(link.beg == cur){
                if(color.get(link.end) == 0 && findCycle(link.end)){
                    return true;
                }else if(color.get(link.end) == 1){
                    return true;
                }
            }
        }

        color.put(cur,2);
        return false;
    }

    private static int DFS(int cur){
        int max = -1;
        for(Triple link : links){
            if(link.end == cur){
                if(link.beg == begins.iterator().next())
                    max = Math.max(max,0);
                else
                    max = Math.max(max,DFS(link.beg));
            }
        }
        order.put(cur,max+1);
        return max + 1;
    }

    private static void fillTr(){
        LinkedList<Node> queue = new LinkedList<Node>();
        queue.add(start);
        while(!queue.isEmpty()) {
            Node cur = queue.poll();
            for (Triple link : links) {
                if (link.beg == cur.name){
                    Node ending = Nods.get(link.end);
                    queue.add(ending);
                    ending.tr = Math.max(ending.tr,link.weight+cur.tr);
                }
            }
        }
    }

    private static void fillTp(){
        LinkedList<Node> queue = new LinkedList<Node>();
        queue.add(finish);
        while(!queue.isEmpty()) {
            Node cur = queue.poll();
            for (Triple link : links) {
                if (link.end == cur.name){
                    Node begining = Nods.get(link.beg);
                    queue.add(begining);
                    begining.tp = Math.min(begining.tp,cur.tp-link.weight);
                }
            }
        }
    }

    private static class Triple{
        private int beg;
        private int end;
        private int weight;

        private Triple(int _b,int _e, int _w){
            beg = _b;
            end = _e;
            weight = _w;
        }
    }

    private static class Node{
        private int name;
        private int tr;
        private int tp;
        private int p;
        Node(int _n){
            name = _n;
            tr = 0;
            tp = Integer.MAX_VALUE;
            p = 0;
        }
    }
}
