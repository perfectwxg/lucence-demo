package com.wuxg.lucence;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LucenceTest {

    private static final String DIR_PATH = "C:\\index";

    @Test
    public void testCreateIndex() throws IOException {

        ArrayList<Document> docs = Lists.newArrayList();

        //1 创建文档对象,一个文档对象相当于数据库中的一条记录
        Document document = new Document();
        //创建文档，添加Filed，相当于数据库中的列
        //StringFiled创建字段和索引，不分词，Field.Store.YES表示存储  Store.NO代表不存储
        document.add(new StringField("id","1", Field.Store.YES));
        document.add(new TextField("title","谷歌地图之父跳槽", Field.Store.YES));
        docs.add(document);
        // 创建文档对象
        Document document2 = new Document();
        document2.add(new StringField("id", "2", Field.Store.YES));
        document2.add(new TextField("title", "谷歌地图之父加盟FaceBook", Field.Store.YES));
        docs.add(document2);
        // 创建文档对象
        Document document3 = new Document();
        document3.add(new StringField("id", "3", Field.Store.YES));
        document3.add(new TextField("title", "谷歌地图创始人拉斯离开谷歌加盟Facebook", Field.Store.YES));
        docs.add(document3);
        // 创建文档对象
        Document document4 = new Document();
        document4.add(new StringField("id", "4", Field.Store.YES));
        document4.add(new TextField("title", "谷歌地图之父跳槽Facebook与Wave项目取消有关", Field.Store.YES));
        docs.add(document4);
        // 创建文档对象
        Document document5 = new Document();
        document5.add(new StringField("id", "5", Field.Store.YES));
        document5.add(new TextField("title", "谷歌地图之父拉斯加盟社交网站Facebook", Field.Store.YES));
        docs.add(document5);
        
        //2 创建存储目录
        FSDirectory fsDirectory = FSDirectory.open(new File(DIR_PATH));
        //3 创建分词器
//        StandardAnalyzer analyzer = new StandardAnalyzer();
        IKAnalyzer analyzer = new IKAnalyzer();
        //4 创建索引写入器的配置对象
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        //5 创建索引写入器对象
        IndexWriter indexWriter = new IndexWriter(fsDirectory, config);
        //6 将文档交给索引写入器
        indexWriter.addDocuments(docs);
        //7 提交
        indexWriter.commit();
        //8 关闭
        indexWriter.close();
    }

    @Test
    public void testQueryFromIndex() throws IOException, ParseException {
        //1 创建读取目录对象
        FSDirectory fsDirectory = FSDirectory.open(new File(DIR_PATH));
        //2 创建索引读取工具
        DirectoryReader reader = DirectoryReader.open(fsDirectory);
        //3 创建索引搜索工具
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        //4 创建查询解析器
        QueryParser parser = new QueryParser("title", new IKAnalyzer());
        //5 创建查询对象
        Query parse = parser.parse("谷歌地图之父拉斯");
        //6 搜索数据
        TopDocs result = indexSearcher.search(parse, 10);
        //7 各种操作(打印输出）
        System.out.println("查询到"+result.totalHits+"条数据");
        ScoreDoc[] docs = result.scoreDocs;
        for (ScoreDoc doc : docs) {
            int docId = doc.doc;
            Document document = reader.document(docId);
            System.out.println(document.get("id")+","+document.get("title"));
            System.out.println("取出文档得分"+doc.score);
        }
    }
}
