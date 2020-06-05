# java_text_grapher
java版的文章图谱化展示。利用hanlp进行nlp处理。项目参考自python版的https://github.com/liuhuanyong/TextGrapher
# introduction
这里提取文章的关键信息，包括关键词、高频词、实体(地名，人名，机构名)以及依存句法分析提取主谓关系等三元组信息。hanlp可以去官网下载data(数据和模型)，然后在hanlp.properties中进行相关配置。
# quick start
	import text.TextMine;
    String content="文本内容";
    TextMine textMine = new TextMine();
    textMine.buildGraph(content);
图谱利用vis生成有向图，保存在text_graph.html中，可直接打开查询。

# cases
１) 美国黑人暴乱
![image](https://github.com/jiangnanboy/text_grapher/tree/master/src/main/image/美国黑人暴乱.png)

2) 中行原油宝
![image](https://github.com/jiangnanboy/text_grapher/tree/master/src/main/image/中行原油宝.png)

# references
１）https://github.com/liuhuanyong/TextGrapher
２）https://github.com/hankcs/HanLP

