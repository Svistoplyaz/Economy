import com.sun.javafx.binding.StringFormatter;

import java.io.*;
import java.util.*;

import static java.lang.System.out;

/**
 * Created by Alexander on 11.09.2017.
 */
public class Task1and2 {
    private static HashSet<Integer> nods;
    private static HashSet<Integer> left;
    private static HashSet<Integer> right;
    private static HashSet<Integer> begins;
    private static HashSet<Integer> ends;
    private static HashSet<Triple> links;
    private static HashMap<Integer, Integer> color;
    private static HashMap<Integer, Integer> order;
    private static HashMap<Integer, Node> Nods;
    private static Node start;
    private static Node finish;
    private static ArrayList<String> pathways;

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
            pathways = new ArrayList<>();

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

            boolean addeddot = false;

            if (begins.size() > 1) {
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
                    addeddot = true;
                }else if(choose == 2){
                    continue;
                }
            }else if(begins.size() == 0){
                out.print("Было найдено 0 начальных точек. Введите ребра заново");
                continue;
            }

            if (ends.size() > 1) {
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
            }else if(ends.size() == 0){
                out.print("Было найдено 0 конечных точек. Введите ребра заново");
                continue;
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
                        } else if (choose == 2) {
                            skip = true;
                            break;
                        }
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
                order.put(nod,0);
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
            out.println("Вершины\nНомер | Ранний срок | Поздний срок | Резерв времени |");
            for (Integer nod: nods){
                Node Nod = Nods.get(nod);
                Nod.p = Nod.tp - Nod.tr;
                out.format("%5d |%12d |%13d |%15d |\n",Nod.name,Nod.tr,Nod.tp,Nod.p);
            }

            int maxord = 0;
            for (Integer nod : nods) {
                maxord = Math.max(order.get(nod),maxord);
            }

            out.println("Рёбра:");
            int curOrder;
            if(addeddot)
                curOrder = 2;
            else
                curOrder = 1;
            for(; curOrder < maxord+1; curOrder++) {
                out.println("\nТекущий порядок: "+curOrder);
                for (Integer nod : nods) {
                    if (order.get(nod) == curOrder-1){
                        for(Triple link:links){
                            if(link.beg == nod)
                                out.format("%5d |%12d |%13d\n",link.beg,link.end,link.weight);
                        }
                    }
                }
            }

            criticalPath(new ArrayList<Integer>(),begins.iterator().next());

            break;
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

    private static void criticalPath(ArrayList<Integer> previous, int current){
        previous.add(current);

        if(current == ends.iterator().next()){
            String s = "";
            for(Integer node : previous){
                s+=node+" ";
            }
            pathways.add(s);
            return;
        }

        for(Triple link:links){
            if(link.beg == current && Nods.get(link.end).p == 0){
                criticalPath((ArrayList<Integer>)previous.clone(),link.end);
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
