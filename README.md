jreplace
========

The tool can meger compressor min js for html page 
jreplace-1.0.0.jar
利用Ant对html正式打包发布的时候，压缩所有HTML页面未带.min.js命名的引用javascript文件内容进HTML页面


Usage
========

Ant Code like it:
[code]
<!-- meger html & js Compress -->
<target name="test" depends="build">
 <mkdir dir="${build.target.dir}/test-result" />
  <!-- java -jar jreplace.jar d:/temp/hello.html d:/output/hello.html-->
  <apply executable="java" parallel="false" failonerror="true">
    <fileset dir="${test.resources}" includes="**/*.html" />
    <arg line="-jar" />
    <arg path="${build.target.dir}/jreplace-1.0.0.jar" />
    <srcfile/>
    <mapper type="glob" from="*.html" to="${build.target.dir}/test-result/*.html" />
    <targetfile />
  </apply>
</target>
[/code]
