# java_text_grapher
文章图谱化展示。利用hanlp进行nlp处理。
# introduction
这里提取文章的关键信息，包括关键词、高频词、实体(地名，人名，机构名)以及依存句法分析提取主谓关系等三元组信息。hanlp可以去官网下载data(数据和模型)，然后在hanlp.properties中进行相关配置。
# quick start
	import text.TextMine;
    String content="文本内容";
    TextMine textMine = new TextMine();
    textMine.buildGraph(content);
图谱利用vis生成有向图，保存在text_graph.html中，可直接打开查看。

# cases
１) 美国黑人佛洛伊德被杀

![image](https://raw.githubusercontent.com/jiangnanboy/text_grapher/master/src/main/image/美国黑人佛洛伊德被杀.png)

2) 中国银行原油宝

![image](https://raw.githubusercontent.com/jiangnanboy/text_grapher/master/src/main/image/中国银行原油宝.png)

# references
１）https://github.com/liuhuanyong/TextGrapher

２）https://github.com/hankcs/HanLP

# contact

如有搜索、推荐、nlp以及大数据挖掘等问题或合作，可联系我：

1、我的github项目介绍：https://github.com/jiangnanboy

2、我的博客园技术博客：https://www.cnblogs.com/little-horse/

3、我的QQ号:2229029156
