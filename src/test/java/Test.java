import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.mining.word.WordInfo;
import com.hankcs.hanlp.seg.common.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * Created by sy on 2020/6/3.
 */
public class Test {
    public static void main(String[] args) {
        //分词及词性
        /*List<Term> listTerm = CoreStopWordDictionary.apply(HanLP.segment("你好，欢迎使用HanLP汉语处理包！"));
        for(Term term:listTerm) {
            System.out.println(term.word+" : "+term.nature.toString());
        }*/

        //句子依存关系
        String sent ="徐先生还具体帮助他确定了把画雄鹰、松鼠和麻雀作为主攻目标。2020年，我们要全面实现小康社会(加油！)。";
        CoNLLSentence sentence = HanLP.parseDependency(sent);
        System.out.println(sentence);
        System.out.println("----------");
        CoNLLWord[] wordArray = sentence.getWordArray();
        for (int i =0; i < wordArray.length; i++)
        {
            CoNLLWord word = wordArray[i];
            System.out.println(word.LEMMA+";"+word.POSTAG+";"+word.HEAD.ID+";"+word.HEAD+";"+word.DEPREL);

        }
       System.out.println("----------");
        System.out.println(HanLP.segment(sent));
        /*for(CoNLLWord word:sentence) {
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);

        }*/
    }
}
