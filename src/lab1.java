import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import javax.swing.*;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
//To explore strange new worlds,
//To seek out new life and new civilizations
public class lab1 {
    public lab1() {

    }
    //辅助函数，用来规格化输入句子
    public List<String> stringset(String inputstr) {
        List<String> finals = new ArrayList<>();
        for (String tmp : inputstr.toLowerCase().replaceAll("[^a-z]", " ").split(" ")) {
            if (!tmp.isEmpty()) {
                finals.add(tmp.replace(" ", ""));
            }
        }
        return finals;
    }
    //辅助函数，用来把句子变成token串
    public List<String> tokens(List<String> finals) {
        List<String> tokens = new ArrayList<>(finals);
        for (int i = 0; i < tokens.size(); i++) {
            for (int j = i; j < tokens.size(); j++) {
                if (tokens.get(i).equals(tokens.get(j)) && i != j) {
                    tokens.remove(j);
                }
            }
        }
        return tokens;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入想要的起点单词: ");
        String startWord = scanner.nextLine();
        System.out.print("请输入想要的终点单词: ");
        String endWord = scanner.nextLine();

        lab1 obj = new lab1();
        List<String> preGraph;
        List<String> set;
        graph g = new graph();
        try {
            System.out.print("请输入txt路径 :");
            String pathtest = scanner.nextLine();
            String str = Files.readString(Paths.get(pathtest));

            preGraph = obj.stringset(str);
            set = obj.tokens(preGraph);
            for (String s : set) {
                graphNode node = new graphNode(s);
                g.addNode(node);
            }

            for (int i = 0; i < preGraph.size(); i++) {
                if (i == 0) {
                    for (graphNode m : g.nodes) {
                        if (Objects.equals(m.node, preGraph.get(i))) {
                            for (graphNode m1 : g.nodes) {
                                if (Objects.equals(m1.node, preGraph.get(i + 1))) {
                                    m.addRight(m1);
                                }
                            }
                        }
                    }
                } else if (i == preGraph.size() - 1) {

                } else {
                    for (graphNode m : g.nodes) {
                        if (Objects.equals(m.node, preGraph.get(i))) {
                            for (graphNode m1 : g.nodes) {
                                if (Objects.equals(m1.node, preGraph.get(i + 1))) {
                                    m.addRight(m1);
                                }
                            }
                        }
                    }
                }
            }

            // Find start and end nodes
            graphNode startNode = null;
            graphNode endNode = null;
            for (graphNode node : g.nodes) {
                if (node.node.equals(startWord)) {
                    startNode = node;
                }
                if (node.node.equals(endWord)) {
                    endNode = node;
                }
            }

            if (startNode == null || endNode == null) {
                System.out.println("起点或终点单词不存在图中！");
                System.exit(1);
            }

            // Find paths
            List<String> path = new ArrayList<>();
            List<List<String>> paths = new ArrayList<>(); // 两点间的所有路径
            path.add(startNode.node);
            g.FindPaths(startNode, endNode, path, paths);

            System.out.println("find path");
            System.out.println(paths.toString());
            System.out.println("---------");

         List<String> bridgeWord1=g.queryBridgeWords(startWord,endWord);
            if ((!bridgeWord1.isEmpty()))
            {
                System.out.println("下列是" + startWord + "和" + endWord + "的bridge words:");
                for(String str1:bridgeWord1){
                    System.out.println(str1);
                }
            }
            else{
                System.out.println("No bridge words from word1 to " + "word2!");
            }




            //显示有向图
            g.showDirectedGraph();


            //显示标记了的有向图
            g.displaystressedGraph(paths);


            //插入bridge words
            System.out.print("请输入要扩充的句子");
            String tobebrigded = scanner.nextLine();

            System.out.println(g.generateNewText(tobebrigded));


            // 6
            g.randomWalk();
        }
        catch (IOException e) {
            System.out.print("Exception");
        }

    }
}



class graphNode {
    String node;
    List<graphNode> right = new ArrayList<graphNode>();
    public graphNode(String n) {
        this.node = n;
    }
    public void addRight(graphNode s) {
        this.right.add(s);
    }
}

class graph {
    List<graphNode> nodes;
    public graph() {
        nodes = new ArrayList<graphNode>();
    }
    public void addNode(graphNode node) {
        nodes.add(node);
    }
    public void showDirectedGraph(){
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            List<Object> vertexs = new ArrayList<>();

            for (graphNode node : nodes) {
                vertexs.add(graph.insertVertex(parent, null, node.node, 0, 0, 80, 30));
            }
            for (graphNode node : nodes) {
                Object vertex1 = null;
                for (Object vertex : vertexs)
                {
                    String LabelName = graph.getLabel(vertex);
                    if (Objects.equals(LabelName, node.node))
                    {
                        vertex1 = vertex;
                    }
                }
                for (graphNode right : node.right)
                {
                    for (Object vertex : vertexs)
                    {
                        String LabelName = graph.getLabel(vertex);
                        if (Objects.equals(LabelName, right.node))
                        {
                            graph.insertEdge(parent, null, "1", vertex1, vertex);
                        }
                    }
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }
        mxIGraphLayout layout = new mxOrganicLayout(graph);
        layout.execute(parent);
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        JFrame frame = new JFrame("Directed Graph Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(graphComponent);
        frame.setSize(200, 200);
        frame.setVisible(true);
    }

    public void displaystressedGraph_protype(List<List<String>> Pairs){
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            List<Object> vertexs = new ArrayList<>();
            for (graphNode node : nodes) {
                vertexs.add(graph.insertVertex(parent, null, node.node, 0, 0, 80, 30));
            }
            for (graphNode node : nodes) {
                Object vertex1 = null;
                for (Object vertex : vertexs)
                {
                    String LabelName = graph.getLabel(vertex);
                    if (Objects.equals(LabelName, node.node))
                    {
                        vertex1 = vertex;
                    }
                }
                for (graphNode right : node.right)
                {
                    int flag = 0;
                    List<String> tmp = new ArrayList<>();
                    tmp.add(node.node);
                    tmp.add(right.node);
                    for (List<String> pair : Pairs)
                    {
                        if (tmp.equals(pair))
                        {
                            flag = 1;
                        }
                    }
                    for (Object vertex : vertexs)
                    {
                        String LabelName = graph.getLabel(vertex);
                        if (Objects.equals(LabelName, right.node))
                        {
                            if (flag == 1)
                            {
                                Object edge = graph.insertEdge(parent, null, "1", vertex1, vertex);
                                mxCell cell = (mxCell) edge;
                                cell.setStyle("strokeColor=red");
                            }
                            else {
                                graph.insertEdge(parent, null, "1", vertex1, vertex);
                            }

                        }
                    }
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }
        mxIGraphLayout layout = new mxOrganicLayout(graph);
        layout.execute(parent);
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        JFrame frame = new JFrame("Directed Graph Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(graphComponent);
        frame.setSize(800, 800);
        frame.setVisible(true);
    }

    public List<String> FindPaths(graphNode node1, graphNode node2, List<String> path, List<List<String>> paths){ // 深度优先找寻路径
//        List<List<String>> paths = new ArrayList<>(); // 存储node1到node2的所有路径
        for(graphNode s : node1.right)
        {
            int flag = 1;
            for (String s1 : path)
            {
                if (Objects.equals(s.node, s1)) {
                    flag = 0;
                    break;
                }
            }
            if (flag == 1)
            {
                List<String> path1 = new ArrayList<String>(path);
                path1.add(s.node);
                List<String> result = FindPaths(s, node2, path1, paths);
                if(result != null)
                {
                    paths.add(result);
                }
            }
        }
        if(node1.equals(node2))
        {
            return path;
        }
        else return null;
    }


    public List<String> queryBridgeWords(String word1, String word2) {
        List<String> bridgeWords = new ArrayList<String>(); // 存储所有桥接词
        // 检查输入单词列表是否至少包含两个单词

        // 遍历图中的节点
        for (graphNode node : nodes) {
            // 检查word1是否存在于图中
            if (Objects.equals(node.node, word1)) {
                // 寻找从word1出发能到达的所有节点
                Set<String> reachableFromWord1 = new HashSet<String>();
                findReachableNodes(node, reachableFromWord1);

                // 检查word2是否存在于从word1出发能到达的节点中
                if (reachableFromWord1.contains(word2)) {
                    // 如果word2可以直接从word1到达，那么word1和word2之间没有桥接词
                    continue;
                }

                // 遍历图中的节点，寻找word2的邻接节点
                for (graphNode neighbor : nodes) {
                    if (Objects.equals(neighbor.node, word2)) {
                        // 寻找从word2出发能到达的所有节点
                        Set<String> reachableFromWord2 = new HashSet<String>();
                        NodesThatCanReach(neighbor, reachableFromWord2);

                        // 寻找同时在reachableFromWord1和reachableFromWord2中的节点，这些就是桥接词
                        reachableFromWord1.retainAll(reachableFromWord2);
                        bridgeWords.addAll(reachableFromWord1);
                        break; // 找到word2的邻接节点后即可停止内层循环
                    }
                }
            }
        }
        return bridgeWords;
    }

    public List<String> stringset(String inputstr) {
        List<String> finals = new ArrayList<>();
        for (String tmp : inputstr.toLowerCase().replaceAll("[^a-z]", " ").split(" ")) {
            if (!tmp.isEmpty()) {
                finals.add(tmp.replace(" ", ""));
            }
        }
        return finals;
    }
    public String generateNewText(String inputText) {
        // 分词，生成单词列表
        List<String> words = stringset(inputText);

        // 存储最终生成的文本行
        StringBuilder newText = new StringBuilder();

        // 遍历单词列表，找到每对相邻单词的桥接词并插入
        for (int i = 0; i < words.size() - 1; i++) {
            // 将当前单词添加到新文本
            newText.append(words.get(i)).append(" ");

            // 查询当前单词和下一个单词的桥接词
            List<String> bridgeWords = queryBridgeWords(words.get(i), words.get(i + 1));

            // 如果存在桥接词，随机选择一个桥接词并插入
            if (!bridgeWords.isEmpty()) {
                Random rand = new Random();
                String bridgeWord = bridgeWords.get(rand.nextInt(bridgeWords.size()));
                newText.append(bridgeWord).append(" "); // 将桥接词插入到两个单词之间
            }
        }

        // 添加最后一个单词到新文本
        if (!words.isEmpty()) {
            newText.append(words.get(words.size() - 1));
        }
        System.out.println(newText.toString());
        // 返回生成的新文本
        return newText.toString();
    }

    // 辅助函数，用于找到指定节点临街的所有节点
    private void findReachableNodes(graphNode startNode, Set<String> reachableNodes) {
        for (graphNode neighbor : startNode.right) {
            reachableNodes.add(neighbor.node);
        }
    }
    // 辅助函数，用于找到临街指定节点的所有节点
    private void NodesThatCanReach(graphNode startNode, Set<String> reachableNodes) {
        // 由于是要找所有可以直接到达startNode的节点，我们从图中所有节点开始
        for (graphNode node : nodes) {
            // 检查当前节点是否可以直接到达startNode
            boolean reachable = false;
            for (graphNode adjacentNode : node.right) {
                if (adjacentNode == startNode) {
                    reachable = true;
                    break;
                }
            }

            // 如果可以直接到达，将当前节点的名称添加到集合中
            if (reachable) {
                reachableNodes.add(node.node);
            }
        }
    }
    public List<String> calcDijkstra(List<List<String>> paths){
        List<String> shortestSublist = null;
        int minLength = Integer.MAX_VALUE;
        for (List<String> path : paths)
        {
            if (path.size() < minLength) {
                shortestSublist = path;
                minLength = path.size();
            }
        }
        System.out.println("最短路径长度" + Integer.toString(minLength-1));
        return shortestSublist;
    }

    public List<List<String>> shortestPairs(List<String> shortestList) { // 生成最短路径的两两组合的节点
        List<List<String>> Pairs = new ArrayList<List<String>>();
        for (int i = 1; i < shortestList.size(); i++) {
            List<String> tmp = new ArrayList<>();
            tmp.add(shortestList.get(i - 1));
            tmp.add(shortestList.get(i));
            Pairs.add(tmp);
        }
        return Pairs;
    }

    public void displaystressedGraph(List<List<String>> paths){
        List<String> shortestList = calcDijkstra(paths);
        System.out.println("最短路径是：" + shortestList.toString());
        System.out.println("\n");
        List<List<String>> Pairs = shortestPairs(shortestList);
        displaystressedGraph_protype(Pairs);
    }


    public void randomWalk_protype(graphNode start, List<List<String>> path){
        if (!start.right.isEmpty())
        {
            int randomIndex = (int) (Math.random() * start.right.size());
            int flag = 1;
            for (List<String> s : path)
            {
                List<String> tmp = new ArrayList<>();
                tmp.add(start.node);
                tmp.add(start.right.get(randomIndex).node);
                if (s.equals(tmp)) {
                    flag = 0;
                    break;
                }
            }
            List<String> tmp1 = new ArrayList<>();
            tmp1.add(start.node);
            tmp1.add(start.right.get(randomIndex).node);
            path.add(tmp1);
            if (flag == 1)
            {
                randomWalk_protype(start.right.get(randomIndex), path);
            }

        }

    }

    public void randomWalk()
    {
        System.out.println("随机游走：");
        int randomIndex = (int) (Math.random() * nodes.size());
        for (int i = 0; i < 100; i++) {
            graphNode start = nodes.get(0);
            List<List<String>> path6 = new ArrayList<>();
            randomWalk_protype(start, path6);
            System.out.println(path6.toString());
        }
    }
}


