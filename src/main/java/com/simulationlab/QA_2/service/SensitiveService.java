package com.simulationlab.QA_2.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志搜索与前缀树的敏感词匹配算法对比
 *
 * 算法：
 * 我的以树为主要对象进行遍历，进行dfs搜索；前缀树以文本为对象进行遍历，进行搜索
 * 而前缀树呢由于一串文本匹配时必须一个挨着一个，不像日志记录这样有个一个时间约束，可以跳过多个，因此搜索时一大串文本
 * 以文本为主要对象进行搜索，假设text = "abcd", 当前根节点是a，检查树中a下面有没有b该子节点，没有则往前滚，有则继续找有没有c的节点
 *
 * 底层数据结构：
 * 日志搜索：用哈希表存subNodes（没有优先级）
 * 前缀树：用list存subNodes（有优先级）
 *
 * 复杂度上：（敏感词最长h, 文本长n）（日志记录长n，边的数量为k，一条log的长度为l{ength}）
 * 前缀树：因为每次搜索时，每次只会选择一个subNode继续搜，因此回滚时最长是树的深度h，即敏感词的最大长度，则O(nh)
 * 日志搜索：这个是每条边都要遍历一遍，因此盲找的话就是 O(nkl)，另外考虑上时间窗口...
 */
@Service
public class SensitiveService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();

        try {
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream("src/main/resources/SensitiveWords.txt")); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            line = br.readLine();
            while (line != null) {
                addWord(line.trim());
                line = br.readLine(); // 一次读入一行数据
            }
        reader.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败, message:" + e.getMessage());
        }
    }

    // 敏感词替换符

    private static final String DEFAULT_REPLACEMENT = "???";

    private class TrieNode { // 空间O(n)，各个方法的时间均为O(1)

        // true 关键词的终结 ； false 继续

        private boolean end = false;
        // key下一个字符，value是对应的节点

        private Map<Character, TrieNode> subNodes = new HashMap<>();
        // 向指定位置添加节点树

        void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }
        // 获取下个节点

        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        boolean isKeywordEnd() {
            return end;
        }

        void setKeywordEnd(boolean end) {
            this.end = end;
        }

        public int getSubNodeCount() {
            return subNodes.size();
        }


    }


    /**
     * 根节点，不含字母的一个节点，一个“空”节点
     */
    private TrieNode rootNode = new TrieNode();


    /**
     * 判断是否是一个符号
     */
    private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !Character.isLetterOrDigit(c) && (ic < 0x2E80 || ic > 0x9FFF);
//        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }


    /**
     * 过滤敏感词
     */
    public String filter(String text) {
            if (StringUtils.isEmpty(text)) {
            return text;
        }
        String replacement = DEFAULT_REPLACEMENT;
        StringBuilder result = new StringBuilder();

        TrieNode tempNode = rootNode; // 指向前缀树中当前比较的位置
        int begin = 0; // 指向文本中当前比较单词的第一个字符
        int position = 0; // 指向文本中当前比较的位置

        while (position < text.length()) { // n times
            char c = text.charAt(position);
            // 跳过符号 防止这种的绕开：你好X色**情XX
            if (isSymbol(c)) {
                if (tempNode == rootNode) { // 开头的符号加入result中，其他的符号跳过？
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);
            // 以文本为主要对象进行搜索，假设text = "abcd", 当前根节点是a，检查树中a下面有没有b该子节点，没有则往前滚，有则继续找有没有c的节点

            if (tempNode == null) { // 以begin开始的字符串不存在敏感词，相当于该路径返回
                result.append(text.charAt(begin)); //O(1)
                position = begin + 1; // 跳到下一个字符开始测试
                begin = position;
                tempNode = rootNode; // 回到树初始节点
            } else if (tempNode.isKeywordEnd()) { // 发现敏感词并替换
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            } else {
                ++position;
            }
        }

        result.append(text.substring(begin));

        return result.toString();
    }

    // 时间：O(w) => O(1) ，没有一个单词是超过 100个字母??

    /**
     * 添加单词
     */
    public void addWord(String lineTxt) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < lineTxt.length(); ++i) {
            Character c = lineTxt.charAt(i);

            // 过滤符号
            if (isSymbol(c)) {
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);

            if (node == null) { // 没初始化
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;

            if (i == lineTxt.length() - 1) {
                // 关键词结束， 设置结束标志
                tempNode.setKeywordEnd(true);
            }
        }
    }


    public static void main(String[] argv) {
        SensitiveService s = new SensitiveService();
        try {
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream("D:\\001personal space\\SimulationLab\\Big_Clean_Problems\\QA_2\\src\\main\\resources\\SensitiveWords.txt")); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            line = br.readLine();
            while (line != null) {
                s.addWord(line.trim());
                line = br.readLine(); // 一次读入一行数据
            }
        }catch (Exception e) {
            System.out.println("error:"+ e.getMessage());
        }
//        s.addWord("色情");
//        s.addWord("好色");
        System.out.print(s.filter("你好X色**情XX"));

    }
}