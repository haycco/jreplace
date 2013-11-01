/**
 * CopyRright (C) 2013:   haycco All Rights Reserved.
 */
package org.haycco.jreplace;

import java.lang.reflect.Method;

import com.yahoo.platform.yui.compressor.JarClassLoader;

/**
 * @author haycco
 */
public class JReplaceBootstrap {

    /**
     * @param args
     * @throws Exception 
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void main(String[] args) throws Exception {
        ClassLoader loader = new JarClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        Class c = loader.loadClass(WebPageParser.class.getName());
        Method main = c.getMethod("main", new Class[] { String[].class });
        main.invoke(null, new Object[] { args });
    }

}
