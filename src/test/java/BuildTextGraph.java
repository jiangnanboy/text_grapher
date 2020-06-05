import text.TextMine;
import text.TextMine;
/**
 * Created by sy on 2020/6/5.
 */
public class BuildTextGraph {
    public static void main(String[] args) {
        String content1 = "这一切的起因是：是黑人乔治·佛洛伊德（George Floyd）之死。\n" +
                "46岁的明尼阿波利斯黑人男子弗洛伊德，被怀疑使用假钞。\n" +
                "一名白人警察在执法过程中，将自己膝盖压在了弗洛伊德的脖子上。几分钟后男子窒息死亡。\n" +
                "白人警察当街杀死了黑人平民，美国人愤怒了。\n" +
                "从当地时间29日晚起，美国当地抗议者走上了全美22州及华盛顿特区共33个城市街头进行抗议示威。\n" +
                "其中一些抗议游行转变为了暴力游行。\n" +
                "警车被点燃。";

        String content2 = "2020年受新冠肺炎疫情、地缘政治、短期经济冲击等综合因素影响，国际商品市场波动剧烈。" +
                "美国时间2020年4月20日，WTI原油5月期货合约CME官方结算价-37.63美元/桶为有效价格，客户和中国银行都蒙受损失，由此触发“原油宝”事件。" +
                "4月21日，中国银行原油宝产品“美油/美元”、“美油/人民币”两张美国原油合约暂停交易一天，英国原油合约正常交易。\n" +
                "2020年5月4日，国务院金融稳定发展委员会在第28次会议，专项提出要高度重视当前国际商品市场价格波动所带来的部分金融产品风险问题。" +
                "5月5日，中国银行回应“原油宝”产品客户诉求：已经研究提出了回应客户诉求的意见。中行保留依法向外部相关机构追索的权利。";

        TextMine textMine = new TextMine();
        textMine.buildGraph(content2);
    }
}
