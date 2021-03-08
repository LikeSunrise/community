package com.newcoder.community.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤器 ，分3步
 * 1、构建前缀树结构
 * 2、根据敏感词库，去初始化前缀树
 * 3、编写敏感词过滤（替换）方法
 */
@Component
public class SensitiveFilter {

    //记录日志
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符号
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    /**
     * 第2步：前缀树 初始化
     * 前缀树依照已有的敏感词库必须一开始就初始化好
     */
    @PostConstruct //服务器启动，会自动执行此方法
    public void init() {
        //先读取敏感词库txt文件的内容,InputStream得到字节流（字节流使用完需要关闭，用try自动关闭）,再转为字符流
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;//记录每一行读取到的字符
            while ((keyword = reader.readLine()) != null) {
                //将读取到的字符一一挂到前缀树上
                this.addKeyword(keyword);//一次处理一行字符
            }
        } catch (IOException e) {
            logger.error("加载敏感词库文件失败：" + e.getMessage());
        }
    }

    //将敏感词挂到前缀树上,方法
    public void addKeyword(String keyword) {

        //定义一个临时节点，它首先指向根节点。负责不断往下延伸前缀树，就相当于一个指针
        TrieNode tmpNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);//当前字符
            TrieNode subNode = tmpNode.getSubNode(c);

            //如果发现当前所查到的字符没有子节点，那么久初始化一个节点挂到当前节点的下面
            if (subNode == null) {
                subNode = new TrieNode();
                tmpNode.addSubNode(c, subNode);//add方法是在当前节点下挂一个子节点，这里c就是要挂的节点
            }
            //进入下一层循环之前，应该把指针更新位置，即指向当前节点的子节点上，这个子节点就是下一轮循环的“父节点”
            tmpNode = subNode;

            if (i == keyword.length() - 1) {
                tmpNode.setKeywordEnd(true);
            }
        }
    }


    //前缀树（定义为内部类） 第1步
    private class TrieNode {

        //关键词结束标识
        private boolean isKeywordEnd = false;

        /**
         * 当前节点的子节点，Character 就是当前节点的子节点的字符值，
         * 这个子节点的数据结构类型是TrieNode,这里为啥要定义一个value值为说明他是什么类型呢？
         * 因为我们要用到一个初始化的指针tmpNode，不断更新tmpNode，这个value就是为了赋值
         * tmpNode的，tmpNode不可能赋值为 当前字符吧 ！！
         */
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        //添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
    }

    //第3步，过滤原始字符串的敏感词（其实就是替换敏感词的过程）
    public String filter(String text) {

        if (StringUtils.isBlank(text)) {
            return null;
        }

        //定义3根指针
        TrieNode tmpNode = rootNode;
        int begin = 0;
        int position = 0;

        //存放结果字符串
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            //跳过特殊符号
            if (isSpecialSymbol(c)) {
                if (tmpNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                //无论情况如何，position都要向后移动
                position++;
                continue;
            }

            //检查下一级节点
            tmpNode = tmpNode.getSubNode(c);
            if (tmpNode == null)
            {
                sb.append(text.charAt(begin));
                position = ++begin;
                tmpNode = rootNode;
            }
            else if(tmpNode.isKeywordEnd)
            {
                sb.append(REPLACEMENT);
                begin = ++position;
                tmpNode = rootNode;
            }
            else
            {
                position++;
            }
        }

        sb.append(text.substring(begin));
        return sb.toString();
    }

    private boolean isSpecialSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
}
