package graphshow;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

import org.json.JSONObject;

/**
 * Created by sy on 2020/6/3.
 */
public class GraphShow {

    String base = "";
    public GraphShow() {
        base = "<html>\n" +
                "    <head>\n" +
                "      <script type=\"text/javascript\" src=\"G:/intellij_idea_workspace/text_grapher/src/main/VIS/dist/vis.min.js\"></script>\n" +
                "      <link href=\"G:/intellij_idea_workspace/text_grapher/src/main/VIS/dist/vis.min.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "      <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "    </head>\n" +
                "    <body>\n" +
                "    <div id=\"VIS_draw\"></div>\n" +
                "    <script type=\"text/javascript\">\n" +
                "      var nodes = data_nodes;\n" +
                "      var edges = data_edges;\n" +
                "      var container = document.getElementById(\"VIS_draw\");\n" +
                "      var data = {\n" +
                "        nodes: nodes,\n" +
                "        edges: edges\n" +
                "      };\n" +
                "      var options = {\n" +
                "          nodes: {\n" +
                "              shape: 'circle',\n" +
                "              size: 5,\n" +
                "              font: {\n" +
                "                  size: 5\n" +
                "              }\n" +
                "          },\n" +
                "          edges: {\n" +
                "              font: {\n" +
                "                  size: 1,\n" +
                "                  align: 'center'\n" +
                "              },\n" +
                "              color: 'gray',\n" +
                "              arrows: {\n" +
                "                  to: {enabled: true, scaleFactor: 0.1}\n" +
                "              },\n" +
                "              smooth: {enabled: true}\n" +
                "          },\n" +
                "          physics: {\n" +
                "              enabled: true\n" +
                "          }\n" +
                "      };\n" +
                "      var network = new vis.Network(container, data, options);\n" +
                "    </script>\n" +
                "    </body>\n" +
                "    </html>";
    }

    /**
     * 创建页面
     * @param events
     */
    public  List<String> createPage(List<List<String>> events) {
        List<String> resultStrList = new ArrayList<>();
        List<String> nodes = new ArrayList<>();
        for(List<String> event:events) {
            nodes.add(event.get(0));
            nodes.add(event.get(1));
        }
        Map<String, Object> nodeDict = new HashMap<>();
        for(int index = 0; index < nodes.size(); index++) {
            if(nodes.get(index).length() >=2)
                nodeDict.put(nodes.get(index), index);
        }
        int nodeDictSize = nodeDict.size();

        StringBuilder sbDataNodes = new StringBuilder();
        StringBuilder sbDataEdges = new StringBuilder();

        sbDataNodes.append("[");
        int lastFlag = 0;
        for(Map.Entry<String, Object> entry:nodeDict.entrySet()) {
            lastFlag +=1;
            Map<String, Object> data = new HashMap<>();
            data.put("group", "Event");
            data.put("id", entry.getValue());
            data.put("label", entry.getKey());
           JSONObject json = new JSONObject(data);
            if(lastFlag < nodeDictSize) {
                sbDataNodes.append(json.toString()).append(",");
            }
        }
        sbDataNodes.append("]");

        lastFlag = 0;
        int eventSize = events.size();
        sbDataEdges.append("[");
        for(List<String> edge:events) {
            Map<String, Object> data = new HashMap<>();
            data.put("from", nodeDict.get(edge.get(0)));
            data.put("label", "");
            data.put("to", nodeDict.get(edge.get(1)));
            JSONObject json = new JSONObject(data);
            if(lastFlag < eventSize) {
                sbDataEdges.append(json.toString()).append(",");
            }
        }
        sbDataEdges.append("]");

        createHtml(sbDataNodes.toString(), sbDataEdges.toString());
        resultStrList.add(sbDataNodes.toString());
        resultStrList.add(sbDataEdges.toString());
        return resultStrList;
    }

    /**
     * 生成html页面
     * @param dataNodes
     * @param dataEdges
     */
    public void createHtml(String dataNodes, String dataEdges) {
        String graphHtmlPath = "G:\\intellij_idea_workspace\\text_grapher\\src\\main\\html\\text_graph.html";
        BufferedWriter bw=null;
        try{
            bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(graphHtmlPath),"utf-8"));
            String html = base.replace("data_nodes", dataNodes).replace("data_edges", dataEdges);
            bw.write(html);
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if(bw!=null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
