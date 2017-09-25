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
    private static HashMap<Integer, Integer> path;
    private static int cycle_end = 0,cycle_beg = 0;

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
            path = new HashMap<>();

            out.println("Вводите ребра построчно, ввод окончите числом -1\n");
            out.println("Пример:\n8 5\n5 7\n7 6\n-1\n");

            out.println("Ввод:");
            int cur = in.nextInt();//Считыванием все ребра
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

            //Делим на начальные и конечные точки
            for (Integer obj : nods) {
                if (!right.contains(obj)) {
                    begins.add(obj);
                }
                if (!left.contains(obj)) {
                    ends.add(obj);
                }
            }

            boolean skip = false;
            int choosen = 0;

            //Поиск петель
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
            //Если выбрано ввести все ребра заново
            if(skip)
                continue;
            //Копируются ребра без петель
            links = _links;

            //Раскраска графа с целью поиска циклов
            color = new HashMap<>();
            for(Integer nod : nods){
                color.put(nod,0);
            }

            if(begins.size() == 0){
                for(Integer beg : nods)
                if(findCycle(beg)){
                    out.println("Был найден цикл, введите ребра заново.");
                    ArrayList<Integer> cycle = new ArrayList<>();
                    cycle.add(cycle_beg);
                    for (int v=cycle_end; v!=cycle_beg; v=path.get(v))
                        cycle.add(v);
                    cycle.add(cycle_end);
                    for (int i=cycle.size()-1; i>=0; i--)
                        out.print(cycle.get(i)+1+" ");
                    out.print("\n");
                    continue;
                }
            }else{
                int siz = begins.size();
                int beg = 0;
                for(int i = 0; i < siz; i++)
                    beg = begins.iterator().next();
                    if(findCycle(beg)){
                        out.println("Был найден цикл, введите ребра заново.");
                        ArrayList<Integer> cycle = new ArrayList<>();
                        cycle.add(cycle_beg);
                        for (int v=cycle_end; v!=cycle_beg; v=path.get(v))
                            cycle.add(v);
                        cycle.add(cycle_end);
                        for (int i=cycle.size()-1; i>=0; i--)
                            out.print(cycle.get(i)+1+" ");
                        out.print("\n");
                        continue;
                    }
            }


            //Флаг на то что была добавлена фиктивная точка на место начальной
            boolean addeddot = false;

            //Если нашлось больше чем одна начальная точка
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
            }
            //Если не было найдено начальной точки
            else if(begins.size() == 0){
                out.println("Было найдено 0 начальных точек. Введите ребра заново");
                continue;
            }

            //Было найдено больше одной конечной точки
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
            }
            //Не было найдено конечной точки
            else if(ends.size() == 0){
                out.println("Было найдено 0 конечных точек. Введите ребра заново");
                continue;
            }

            //Порядок рёбер
            order = new HashMap<>();
            for(Integer nod : nods){
                order.put(nod,0);
            }
            //Упорядочивание вершин
            DFS(ends.iterator().next());

            //Упорядочивание ребер в зависимости от вершин из которых он выходит
            for(Integer nod:nods){
                Node tmp = new Node(nod);
                if(nod.equals(begins.iterator().next()))
                    start = tmp;
                else if(nod.equals(ends.iterator().next()))
                    finish = tmp;
                Nods.put(nod,tmp);
            }

            //Заполняем раннее время
            fillTr();
            //Раннее время конечной точки равно позднему времени
            finish.tp = finish.tr;
            //Заполняем позднее время
            fillTp();

            //Выводим список вершин с параметрами
            out.println("Вершины:\nНомер | Ранний срок | Поздний срок | Резерв времени |");
            for (Integer nod: nods){
                Node Nod = Nods.get(nod);
                Nod.p = Nod.tp - Nod.tr;
                out.format("%5d |%12d |%13d |%15d |\n",Nod.name,Nod.tr,Nod.tp,Nod.p);
            }

            out.println("Начальная вершина:" + begins.iterator().next());
            out.println("Конечная вершина:" + ends.iterator().next());

            //Находим максимальный порядок
            int maxord = 0;
            for (Integer nod : nods) {
                maxord = Math.max(order.get(nod),maxord);
            }

            //Выводим ребра с порядком
            out.print("\nРёбра:");
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
                                out.format("%5d |%12d |%13d|%12d|\n",link.beg,link.end,link.weight,link.fullreserv);
                        }
                    }
                }
            }

            for(Triple link : links){
                link.fullreserv = Nods.get(link.end).tp - Nods.get(link.beg).tr - link.weight;
            }

            //Ищем критический путь
            criticalPath(new ArrayList<>(),begins.iterator().next());

            //Выводим число критических путей
            int quantity = pathways.size();
            out.println("\nБыло найдено " + quantity + " критических путей.");

            //Выводим по очереди найденные пути
            if(quantity != 0) {
                out.println("Найденные критические пути:");
                int leng = 0;
                String p = "";
                for (String s : pathways) {
                    p+=s + " ";
                    out.print(s);
                }
                String[] sp = p.split(" ");
                for(int i = 0 ; i < sp.length - 1; i++){
                    int b = Integer.parseInt(sp[i]);
                    int e = Integer.parseInt(sp[i+1]);
                    for(Triple link:links)
                        if(link.beg == b && link.end == e)
                            leng += link.weight;
                }
                out.print(" Длина = "+leng+"\n");
            }

            break;
        }
    }

    //Поиск цикла методом раскраски
    private static boolean findCycle(int cur){
        color.put(cur,1);
        for(Triple link : links){
            if(link.beg == cur){
                int to = link.end;
                if(color.get(link.end) == 0){
                    path.put(to,cur);
                    if(findCycle(link.end))
                        return true;
                }else if(color.get(link.end) == 1){
                    cycle_end = cur;
                    cycle_beg = to;
                    return true;
                }
            }
        }

        color.put(cur,2);
        return false;
    }

    //Поиск длиннейшего пути до вершины
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

    //Заполняем раннее время у вершин обходом в ширину от начальной точки
    private static void fillTr(){
        LinkedList<Node> queue = new LinkedList<>();
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

    //Заполняем позднее время у вершин обходом в ширину от конечной точки
    private static void fillTp(){
        LinkedList<Node> queue = new LinkedList<>();
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

    //Поиск критического пути обходом в глубину, без сохранения посещенных вершин
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
            if(link.beg == current && link.fullreserv == 0){
                criticalPath((ArrayList<Integer>)previous.clone(),link.end);
            }
        }
    }

    //Ребро начало, конец, вес
    private static class Triple{
        private int beg;
        private int end;
        private int weight;
        private int fullreserv;

        private Triple(int _b,int _e, int _w){
            beg = _b;
            end = _e;
            weight = _w;
            fullreserv = 0;
        }
    }

    //Узел название, раннее время, конечное время, резерв
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
