package keyword;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.mining.word.TermFrequencyCounter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by sy on 2020/6/3.
 */
public class ExtractWords {
    Set<String> stopWords=new HashSet<String>();//停词
    public ExtractWords() {
        this.loadStopWords("G:\\intellij_idea_workspace\\text_grapher\\src\\main\\resources\\data\\dictionary\\stopwords.txt");
    }

    /**
     * 加载停词
     * @param path
     */
    public void loadStopWords(String path){
        BufferedReader br=null;
        try{
            br=new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf-8"));
            String line=null;
            while((line=br.readLine())!=null){
                stopWords.add(line);
            }
            br.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 利用textrank提取关键词
     * @param wordPos
     * @param text
     * @param topn
     * @return
     */
    public List<String> extractKeywords(Map<String,String> wordPos, String text, int topn) {
        List<String> keywords =  HanLP.extractKeyword(text, 100);
        List<String> wordsList = new ArrayList<>();
        for(String word : keywords) {
            if((this.stopWords.contains(word)) || (word.length() < 2) || (!wordPos.containsKey(word))) {
                continue;
            }
            if(wordPos.get(word).contains("n")) {
                wordsList.add(word);
            }
            if(wordsList.size() >= topn) {
                break;
            }
        }
        return wordsList;
    }

    /**
     * 利用词频提取关键词
     * @param wordPos
     * @param text
     * @param topn
     * @return
     */
    public List<String> extractHighFrequencyWords(Map<String,String> wordPos, String text, int topn) {
        TermFrequencyCounter termFrequencies = new TermFrequencyCounter();
        List<String> keywords = termFrequencies.getKeywords(text, 100);
        List<String> wordsList = new ArrayList<>();
        for(String word : keywords) {
            if((this.stopWords.contains(word)) || (word.length() < 2) || (!wordPos.containsKey(word))) {
                continue;
            }
            if(wordPos.get(word).contains("n")) {
                wordsList.add(word);
            }
            if(wordsList.size() >= topn) {
                break;
            }
        }
        return wordsList;
    }

    /**
     * 提取高频命名实体
     * @param nerList
     * @param wordPos
     * @param text
     * @param topn
     * @return
     */
    public Map<String, String> extractHighFrequencyNer(List<String> nerList, Map<String, String> wordPos, String text, int topn) {
        TermFrequencyCounter termFrequencies = new TermFrequencyCounter();
        List<String> keywords = termFrequencies.getKeywords(text, 100);
        Map<String, String> wordDict = new HashMap<>();
        for(String word : keywords) {
            if((this.stopWords.contains(word)) || (word.length() < 2) || (!wordPos.containsKey(word))) {
                continue;
            }
            if(nerList.contains(wordPos.get(word))) {
                wordDict.put(word, wordPos.get(word));
            }
            if(wordDict.size()>= topn) {
                break;
            }
        }
        return wordDict;
    }

    /**
     * 提取全文命名实体
     * @param ners
     * @return
     */
    public Map<String, String> extractNer(List<String> ners) {
        Map<String, String> wordDict = new HashMap<>();
        for(String ner:ners) {
            String[] nerStr = ner.split("/");
            wordDict.put(nerStr[0], nerStr[1]); //词和词性
        }
        return wordDict;
    }
}
