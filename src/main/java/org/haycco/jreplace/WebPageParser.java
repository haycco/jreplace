/**
 * CopyRright (C) 2013:   haycco All Rights Reserved.
 */
package org.haycco.jreplace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.universalchardet.UniversalDetector;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * @author haycco
 */
public class WebPageParser {

    public static void parser(String fileName, String destFileName) throws IOException {
        int nread;
        byte[] buf = new byte[1024];
        File pageFile = new File(fileName);
        // ���ļ��Ļ�����Դ·��
        String baseUri = fileName.replace(pageFile.getName(), "");

        FileInputStream fis = new FileInputStream(pageFile);
        UniversalDetector detector = new UniversalDetector(null);
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();
        // ̽���ļ������ʽ
        String encoding = detector.getDetectedCharset();
        if (encoding != null) {
            logger.info("Detected encoding = " + encoding);
            Document doc = Jsoup.parse(pageFile, encoding);
            // ��ȡ���о��� src ���Ե�JavaScript��ǩ
            Elements jsTags = doc.select("script[type=text/javascript]").select("script[src]");

            YuiCompressorErrorReporter reporter = new YuiCompressorErrorReporter();

            for (Element element : jsTags) {
                String src = element.attr("src");
                // srcֵ��Ϊ�� �в���Min��
                if (!src.equals("") && !src.endsWith(".min.js")) {
                    // �����ļ�����̽����
                    detector.reset();
                    String jsFileName = baseUri + element.attr("src");
                    logger.info("jsFileName = " + jsFileName);
                    // ����ѹ�����min��JS�ļ���
                    String jsMinFileName = jsFileName.replaceAll(".js$", ".min.js");
                    logger.info("jsMinFileName = " + jsMinFileName);
                    FileInputStream jsFis = new FileInputStream(jsFileName);
                    while ((nread = jsFis.read(buf)) > 0 && !detector.isDone()) {
                        detector.handleData(buf, 0, nread);
                    }
                    detector.dataEnd();
                    // ̽���ļ������ʽ
                    String jsCharsetName = detector.getDetectedCharset();
                    logger.info("Detected js file encoding = " + jsCharsetName);
                    // ִ��YUICompressor����ѹ��js������min��js�ļ�
                    Reader in = new InputStreamReader(new FileInputStream(jsFileName), jsCharsetName);
                    JavaScriptCompressor compressor = new JavaScriptCompressor(in, reporter);
                    FileOutputStream fos = new FileOutputStream(jsMinFileName);
                    Writer out = new OutputStreamWriter(fos, jsCharsetName);
                    try {
                        compressor.compress(out, Options.lineBreakPos, Options.munge, Options.verbose,
                                Options.preserveAllSemiColons, Options.disableOptimizations);
                        // flush��Ӳ��
                        out.flush();
                        FileInputStream minfis = new FileInputStream(jsMinFileName);
                        BufferedReader br = new BufferedReader(new InputStreamReader(minfis, jsCharsetName));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        // д��ڵ�
                        element.text(sb.toString());
                        // �Ƴ�֮ǰ��src��������
                        element.removeAttr("src");
                    } finally {
                        if (null != in)
                            in.close();
                        if (null != out)
                            out.close();
                    }
                }
            }
            // ��дHTML�ļ�
            File destFile = new File(destFileName);
            destFile.getParentFile().mkdirs();
            FileWriter fw = new FileWriter(destFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(doc.html());
            fw.flush();
            bw.close();
        } else {
            logger.info("Can not detected the file" + fileName + " encoding.");
        }

        // �����ļ�����̽����
        detector.reset();
    }

    public static void main(String[] args) throws java.io.IOException {
        if (args == null || args.length == 0) {
            usage();
            return;
        }
        logger.info("source: " + args[0] + "   dest: " + args[1]);
        parser(args[0], args[1]);
    }

    private static void usage() {
        System.out.println("\nUsage: java -jar jreplace-x.y.z.jar [options] [input file] [output file]\n\n");
    }

    private static Logger logger = Logger.getLogger(WebPageParser.class.getName());

    private static class YuiCompressorErrorReporter implements ErrorReporter {
        public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
            if (line < 0) {
                logger.log(Level.WARNING, message);
            } else {
                logger.log(Level.WARNING, "Line: " + line + ", LineOffset: " + lineOffset + ", message: " + message);
            }
        }

        public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
            if (line < 0) {
                logger.log(Level.SEVERE, message);
            } else {
                logger.log(Level.SEVERE, "Line: " + line + ", LineOffset: " + lineOffset + ", message: " + message);
            }
        }

        public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource,
                int lineOffset) {
            error(message, sourceName, line, lineSource, lineOffset);
            return new EvaluatorException(message);
        }
    }

    public static class Options {
        public static String charset = "UTF-8";
        // �޶���
        public static int lineBreakPos = -1;
        // ��������
        public static boolean munge = true;
        // �ر���ϸ��Ϣ����Ϊ�����д���������Ϣ��
        public static boolean verbose = false;
        public static boolean preserveAllSemiColons = false;
        public static boolean disableOptimizations = false;
    }
}
