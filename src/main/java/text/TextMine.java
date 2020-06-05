package text;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import keyword.ExtractWords;
import sentence.TextParser;
import graphshow.GraphShow;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by sy on 2020/6/3.
 */
public class TextMine {
    Map<String, String> nerDict = null;
    List<String> nerList = null;

    ExtractWords extractWords = null;
    TextParser textParser = null;
    GraphShow graphShow = null;

    Segment segment = null;

    public TextMine(){

        extractWords = new ExtractWords();
        textParser = new TextParser();
        graphShow = new GraphShow();

        //开启人名，地名，机构名识别
        segment = HanLP.newSegment().enableNameRecognize(true).enablePlaceRecognize(true).enableOrganizationRecognize(true);

        nerDict = new HashMap<String, String>();
        nerDict.put("nr","人物");
        nerDict.put("ni","机构");
        nerDict.put("ns","地名");
        nerDict.put("nt","机构团体名");

        nerList = new ArrayList<String>();
        nerList.add("nr");
        nerList.add("ni");
        nerList.add("ns");
        nerList.add("nt");
    }

    /**
     * 主要处理函数
     * @param text
     * @return
     */
    public String buildGraph(String text) {
        if((null == text) || ("".equals(text.trim()))) {
            return "";
        }
        //分句
        List<String> sentences = cutText(text);

        List<List<String>> subsents_seg = new ArrayList<>();
        //存储文章词频信息
        List<String> words_list = new ArrayList<>();
        //存储具有命名实体的句子
        List<List<Term>> ner_sents = new ArrayList<>();
        //词与词性
        Map<String, String> wordPos = new HashMap<>();
        //保存命名实体
        List<String> ners = new ArrayList<>();
        //保存主谓宾短语
        List<List<String>> triples = new ArrayList<>();
        //保存文章事件
        List<List<String>> events = new ArrayList<>();

        for(String sentStr : sentences) {
            List<Term> wordsPostags = cutSentence(sentStr);
            List<String> subList = new ArrayList<>();//每句中的词
            List<String> subPos = new ArrayList<>();//每句中词的词性
            List<String> ner = new ArrayList<>();//每句中的地名，人名，机构名
            for(Term term : wordsPostags) {
                String word = term.word;
                String pos = term.nature.toString();
                subList.add(word); //词
                subPos.add(pos);//词性
                wordPos.put(word, pos);
                for(String nerStr:nerList) {
                    if(pos.equals(nerStr)) {
                        ner.add(word+"/"+pos);
                    }
                }
            }
            subsents_seg.add(subList);
            if(0!=ner.size()) {
                List<List<String>> triple = extractTrples(subList, subPos, sentStr);
                if(0 == triple.size()) {
                    continue;
                }
                triples.addAll(triple);
                ners.addAll(ner);
                ner_sents.add(wordsPostags);
            }
        }

        //关键词，图谱组织
        List<String> keywords = new ExtractWords().extractKeywords(wordPos, text, 10);
        for(String word:keywords) {
            List<String> wordList = new ArrayList<>();
            wordList.add(word);
            wordList.add("关键词");
            events.add(wordList);
        }

        //对三元组进行event构建，
        for(List<String> tripleList:triples) {
            if((keywords.contains(tripleList.get(0)) || keywords.contains(tripleList.get(1))) && (tripleList.get(0).length()>1) && (tripleList.get(1).length()>1) ) {
                List<String> wordList = new ArrayList<>();
                wordList.add(tripleList.get(0));
                wordList.add(tripleList.get(1));
                events.add(wordList);
            }
        }

        //高频词，图谱组织
        List<String> highFrequencyWords = new ExtractWords().extractHighFrequencyWords(wordPos, text, 10);
        for(String word:highFrequencyWords) {
            List<String> wordList = new ArrayList<>();
            wordList.add(word);
            wordList.add("高频词");
            events.add(wordList);
        }

        //获取全文命名实体，词和词性
        Map<String, String> wordNer = new ExtractWords().extractNer(ners);
        //所有实体名
        for(Map.Entry<String, String> entry : wordNer.entrySet()) {
            List<String> wordList = new ArrayList<>();
            wordList.add(entry.getKey());
            wordList.add(nerDict.get(entry.getValue()));
            events.add(wordList);
        }

        //全文命名实体共现信息，构建事件共现网络
        List<String> coList = collectCoexist(ner_sents, ners);
        List<List<String>> coEvents = new ArrayList<>();
        for(String list:coList) {
            List<String> listEvent = new ArrayList<>();
            String word1 = list.split("@")[0].split("/")[0];
            String word2 = list.split("@")[1].split("/")[0];
            listEvent.add(word1);
            listEvent.add(word2);
            coEvents.add(listEvent);
        }
        events.addAll(coEvents);

        //关键词与实体进行关系抽取
        List<List<String>> eventsEntityKeyword = relEntityKeyword(ners,keywords,subsents_seg);
        events.addAll(eventsEntityKeyword);
        List<String> resultStrList = new GraphShow().createPage(events);
        resultStrList.forEach(str->System.out.println(str));
        return "";
    }

    /**
     * 基于keywords，构建实体与关键词间的关系
     * @param ners
     * @param keywords
     * @param subsents_seg
     * @return
     */
    public List<List<String>> relEntityKeyword(List<String> ners, List<String> keywords,  List<List<String>> subsents_seg) {
        List<List<String>> events = new ArrayList<>();
        List<String> rels = new ArrayList<>();
        List<List<String>> sents = new ArrayList<>();

        //提取所有实体名
        List<String> allNerName = new ArrayList<>();
        Set<String> nersSet = new HashSet<>();
        nersSet.addAll(ners);
        for(String ner:nersSet) {
            allNerName.add(ner.split("/")[0]);
        }

        for(List<String> subsent:subsents_seg) {
            List<String> tmp = new ArrayList<>();
            for(String word:subsent) {
                if(allNerName.contains(word) || keywords.contains(word)) {
                    tmp.add(word);
                }
            }
            if(tmp.size() > 1) {
                sents.add(tmp);
            }
        }

        for(String word:allNerName) {
            for(List<String> sent:sents) {
                if(sent.contains(word)) {
                    List<String> nerSymbol = new ArrayList<>();
                    for(String wd:sent) {
                        if(keywords.contains(wd) && !wd.equals(word) && wd.length()>1) {
                            nerSymbol.add(word+"->"+wd);
                        }
                    }
                    if(nerSymbol.size()>0) {
                        rels.addAll(nerSymbol);
                    }
                }
            }
        }

        Set<String> relSet = new HashSet<>();
        relSet.addAll(rels);
        for(String relStr:relSet) {
            List<String> event = new ArrayList<>();
            event.add(relStr.split("->")[0]);
            event.add(relStr.split("->")[1]);
            events.add(event);
        }
        return events;
    }

    /**
     * 构建实体间的共现关系
     * @param ner_sents
     * @param ners
     * @return
     */
    public List<String> collectCoexist(List<List<Term>> ner_sents, List<String> ners) {
        List<String> coList = new ArrayList<>();
        Set<String> wordNerKeysSet = new HashSet<>();
        wordNerKeysSet.addAll(ners);

        for(List<Term> termList:ner_sents) {
            Set<String> wordsSet = new HashSet<>();
            for(Term term:termList) {
                wordsSet.add(term.word + "/" + term.nature.toString());
            }
            //共现的实体集
            Set<String> coNers = new HashSet<>();
            for(String word:wordNerKeysSet) {
                if(wordsSet.contains(word) && word.length() >= 2) {
                    coNers.add(word);
                }
            }
            List<String> coInfo = combination(coNers);
            coList.addAll(coInfo);
        }
        if(0 == coList.size()) {
            return null;
        }
        return coList;
    }

    /**
     * 列表全排列
     * @param coNers
     * @return
     */
    public List<String> combination(Set<String> coNers) {
        List<String> combines = new ArrayList<>();
        if(0 == coNers.size()) {
            return null;
        }
        for(String ner1:coNers)
            for(String ner2:coNers) {
                if(ner1 == ner2)
                    continue;
                combines.add(ner1+"@"+ner2);
            }
        return combines;
    }


    /**
     * 抽取事件三元组
     * @return
     */
    public  List<List<String>> extractTrples(List<String> words, List<String> pos, String sentence) {
        List<List<String>> svo = new ArrayList<>();
        List<String> tuples = TextParser.syntaxParser(words, pos, sentence);
        Map<String, Map<String, List<String>>> childDictList = TextParser.buildParseChildDict(words, pos, tuples);
        for(String tuple : tuples) {
            String[] strSplit = tuple.split(";");
            String relation = strSplit[6];
            if("主谓关系".equals(relation)) {
                String sub_wd = strSplit[1];
                String verb_wd = strSplit[3];
                String obj = completeVOB(verb_wd, childDictList);
                String subj = sub_wd;
                String verb = verb_wd;
                List<String> list = null;
                if(!"".equals(obj)) {
                    list = new ArrayList<>();
                    list.add(subj);
                    list.add(verb);
                    svo.add(list);
                } else {
                    list = new ArrayList<>();
                    list.add(subj);
                    list.add(verb+obj);
                    svo.add(list);
                }
            }
        }
        return svo;
    }

    /**
     * 根据"主谓关系"找"动宾关系"
     * @param verb
     * @param childDictList
     * @return
     */
    public String completeVOB(String verb, Map<String, Map<String, List<String>>> childDictList) {
        for(Map.Entry<String, Map<String, List<String>>> entry : childDictList.entrySet()) {
            String[] wordPosIndex = entry.getKey().split(";");
            String word = wordPosIndex[0];
            Map<String, List<String>> map = entry.getValue();//关系属性
            if(word.equals(verb)) {
                if(!map.containsKey("动宾关系")) {
                    continue;
                }
                String[] vob = map.get("动宾关系").get(0).split(";");
                String obj = vob[1];
                return obj;
            }

        }
        return "";
    }

    /**
     * 分词和词性
     * @param sent
     * @return
     */
    public List<Term> cutSentence(String sent) {
        List<Term> termList = new ArrayList<>();
        CoNLLSentence sentence = HanLP.parseDependency(sent);
        CoNLLWord[] wordArray = sentence.getWordArray();
        for (int i =0; i < wordArray.length; i++)
        {
            CoNLLWord word = wordArray[i];
            String wordNmae = word.LEMMA;
            String pos = word.POSTAG;
            Term term = new Term(wordNmae, Nature.create(pos));
            termList.add(term);
        }
        return termList;
        //return HanLP.segment(sentence);
        //return CoreStopWordDictionary.apply(segment.seg(sentence));
    }

    /**
     * 分句
     * @param text
     * @return
     */
    public List<String> cutText(String text){
        List<String> sentences=new ArrayList<String>();
        String regEx="[!?。！？.;；]";
        Pattern p=Pattern.compile(regEx);
        Matcher m=p.matcher(text);
        String[] sent=p.split(text);
        int sentLen=sent.length;
        if(sentLen>0){
            int count=0;
            while(count<sentLen){
                if(m.find()){
                    sent[count]+=m.group();
                }
                count++;
            }
        }
        for(String sentence:sent){
            sentence=sentence.replaceAll("(&rdquo;|&ldquo;|&mdash;|&lsquo;|&rsquo;|&middot;|&quot;|&darr;|&bull;)", "");
            sentences.add(sentence.trim());
        }

        return sentences;
    }

}
