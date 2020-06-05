package sentence;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;

import java.util.*;

/**
 * Created by sy on 2020/6/3.
 */
public class TextParser {

    /**
     * 依存关系格式化
     * @param words
     * @param pos
     * @return
     */
    public static List<String> syntaxParser(List<String> words, List<String> pos, String sentence) {
        CoNLLSentence coNLLSentence = HanLP.parseDependency(sentence);
        CoNLLWord[] wordArray = coNLLSentence.getWordArray();
        words.add(0,"Root");
        pos.add(0, "w");
        List<String> tuples = new ArrayList<>();
        StringBuilder sb = null;

        for(int index = 0; index < words.size()-1; index++) {
            int arcIndex = wordArray[index].HEAD.ID;

            String arcRelation = wordArray[index].DEPREL;
            sb = new StringBuilder();
            String param1 = String.valueOf(index+1);
            String param2 = words.get(index + 1);
            String param3 = pos.get(index+1);
            String param4 = words.get(arcIndex);
            String param5 = pos.get(arcIndex);
            String param6 = String.valueOf(arcIndex);

            sb.append(param1).append(";")
                    .append(param2).append(";")
                    .append(param3).append(";")
                    .append(param4).append(";")
                    .append(param5).append(";")
                    .append(param6).append(";")
                    .append(arcRelation);
            tuples.add(sb.toString());
        }
        return tuples;
    }

    /**
     * 为句子中的每个词语维护一个保存句法依存儿子节点的字典
     * @param words
     * @param pos
     * @param tuples
     * @return
     */
    public static Map<String, Map<String, List<String>>> buildParseChildDict(List<String> words, List<String> pos, List<String> tuples) {
        Map<String,  Map<String, List<String>>> childLinkedDict = new LinkedHashMap<>();
        for(int index = 0; index < words.size(); index ++) {
            String word = words.get(index);
            Map<String, List<String>> childDict = new HashMap<>();
            for(String tupleStr:tuples) {
                String[] strSplit = tupleStr.split(";");
                if(strSplit[3].equals(word)) {
                    if(childDict.containsKey(strSplit[6])) {
                        childDict.get(strSplit[6]).add(tupleStr);
                    } else {
                        List<String> arc = new ArrayList<>();
                        arc.add(tupleStr);
                        childDict.put(strSplit[6],arc);
                    }
                }
            }
            StringBuffer sb = new StringBuffer();
            sb.append(word).append(";")
                    .append(pos.get(index)).append(";")
                    .append(String.valueOf(index));
            childLinkedDict.put(sb.toString(), childDict);
        }
        return childLinkedDict;
    }

}
