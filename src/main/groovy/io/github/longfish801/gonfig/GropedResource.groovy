/*
 * GropedResource.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gonfig

import java.nio.charset.Charset
import java.util.regex.Pattern
import java.util.regex.Matcher
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * ファイルシステムあるいはクラスパスからリソースを探します。</p>
 * 
 * <p>たとえば次のような使い方を想定しています。</br>
 * デフォルト値を定義した設定ファイルを JARファイルに格納します。
 * この設定ファイルはクラスファイルと同様に、JARファイル内の、
 * パッケージ名に対応する階層のディレクトリに格納します。<br/>
 * ユーザが設定値を変えたいと思ったとします。値を編集した
 * 設定ファイルをファイルシステム上に置きます。<br/>
 * するとファイルシステム上の設定ファイルのほうが優先して
 * 参照されるため、新しい設定値のほうが用いられます。</p>
 * 
 * <p>どのようにリソースを探すのか、詳細は {@link #grope(String)}を
 * 参照してください。<br/>
 * 本特性の実装時は {@link #getClazz()}のオーバーライドが
 * 必要なことに注意してください。
 * @author io.github.longfish801
 */
trait GropedResource {
	/** リソースバンドル */
	static final ResourceBundle rsrc = ResourceBundle.getBundle(GropedResource.class.canonicalName)
	/** ログ出力 */
	static final Logger log = LoggerFactory.getLogger(GropedResource.class)
	/** ConfigSlurper */
	static final ConfigSlurper slurper = new ConfigSlurper()
	/** 基底ディレクトリ */
	static File baseDir = new File(rsrc.getString('CURRENT_DIRECTORY'))
	
	/**
	 * クラスを取得します。<br/>
	 * 本メソッドは必ずオーバーライドしてください。<br/>
	 * 本特性を実装したクラスの Classを取得するためのメソッドです。<br/>
	 * たとえば Someというクラスなら Some.classを返してください。
	 * @return 自クラス
	 * @exception UnsupportedOperationException オーバーライドされていません
	 */
	static Class getClazz(){
		throw new UnsupportedOperationException('Override this method.')
	}
	
	/**
	 * 基底ディレクトリを設定します。
	 * @param path 基底ディレクトリへのパス
	 * @return 自クラス
	 */
	static def setBaseDir(String path){
		baseDir = new File(path)
		return this
	}
	
	/**
	 * 平坦ディレクトリを返します。<br/>
	 * 平坦ディレクトリは次のように定義されます。<br/>
	 * 本特性を実装するクラスが所属するパッケージ名を
	 * 相対パスとします。<br/>
	 * 基底ディレクトリを親ディレクトリした相対パス上の
	 * ディレクトリが平坦ディレクトリです。<br/>
	 * たとえば基底ディレクトリが /foo/barで、
	 * クラスが io.github.Someならば、平坦ディレクトリとして
	 * /foo/bar/io.githubを返します。<br/>
	 * クラスがパッケージに所属しない場合、
	 * 相対パスはカレントディレクトリを用います。
	 * @return 平坦ディレクトリ
	 */
	static File getFlatDir(){
		return new File(baseDir, clazz.package?.name ?: rsrc.getString('CURRENT_DIRECTORY'))
	}
	
	/**
	 * 深層ディレクトリを返します。<br/>
	 * 深層ディレクトリは次のように定義されます。<br/>
	 * 本特性を実装するクラスが所属するパッケージ名の
	 * 半角コンマ(.)をパス区切り文字に置換し、
	 * 相対パスを作成します。<br/>
	 * 基底ディレクトリを親ディレクトリした相対パス上の
	 * ディレクトリが深層ディレクトリです。<br/>
	 * たとえば基底ディレクトリが /foo/barで、
	 * クラスが io.github.Someならば、深層ディレクトリとして
	 * /foo/bar/io/githubを返します。<br/>
	 * クラスがパッケージに所属しない場合、相対パスは
	 * カレントディレクトリを用います。
	 * @return 深層ディレクトリ
	 */
	static File getDeepDir(){
		if (clazz.package == null) return new File(baseDir, rsrc.getString('CURRENT_DIRECTORY'))
		String path = clazz.package.name.replaceAll(
			Pattern.quote(rsrc.getString('PACKAGE_SEPARATOR')),
			Matcher.quoteReplacement(File.separator))
		return new File(baseDir, path)
	}
	
	/**
	 * 指定された名前のリソースを探します。<br/>
	 * まずデフォルトロケールを付与したリソース名で探し、
	 * 次にデフォルトロケールなし（引数で指定されたそのまま）の
	 * リソース名で探します。<br/>
	 * 以下の順番で探し、初めにみつかったリソースを返します。<br/>
	 * みつからなければ WARNログを出力してnullを返します。</p>
	 * <ol>
	 * <li>ファイルシステムの基底ディレクトリ上</li>
	 * <li>ファイルシステムの平坦ディレクトリ上</li>
	 * <li>ファイルシステムの深層ディレクトリ上</li>
	 * <li>クラスパス上</li>
	 * <li>クラスパスの、パッケージ名の半角コンマ(.)をパス区切り文字に置換した相対パス上</li>
	 * </ol>
	 * @param name リソースの名前
	 * @return URL（リソースがみつからない場合は null）
	 * @see #grantLocale(String)
	 * @see #setBaseDir(String)
	 * @see #getDeepDir()
	 * @see #getFlatDir()
	 */
	static URL grope(String name){
		Closure groper = { String rname ->
			// 基底ディレクトリ、平坦ディレクトリ、深層ディレクトリの順にリソースを探し、
			// みつかったならば返します
			for (File dir in [baseDir, flatDir, deepDir]){
				File file = new File(dir, rname)
				if (file.exists()){
					log.debug('Resource file groped: class={}, path={}', clazz.canonicalName, file.absolutePath)
					return file.toURI().toURL()
				}
			}
			
			// パッケージルート、クラスの順にリソースを探し、みつかったならば返します
			for (def loader in [ClassLoader.systemClassLoader, clazz]){
				URL url = loader.getResource(rname)
				if (url != null){
					log.debug('Resource groped: class={}, url={}', clazz.canonicalName, url)
					return url
				}
			}
		}
		URL url = groper(grantLocale(name)) ?: groper(name)
		if (url == null) log.warn('Resource not groped:class={}, name={}', clazz.canonicalName, name)
		return url
	}
	
	/**
	 * リソース名にデフォルトロケールを付与します。<br/>
	 * デフォルトロケールは {@link Locale#getDefault()}で取得します。<br/>
	 * リソース名にファイル名と拡張子との区切り文字(.)が含まれていれば、
	 * 区切り文字の前に半角アンダーバー＋デフォルトロケールを挿入します。<br/>
	 * 区切り文字が含まれていない場合は、リソース名の末尾に
	 * 半角アンダーバー＋デフォルトロケールを付加します。
	 * @param name リソースの名前
	 * @return デフォルトロケールを付与したリソースの名前
	 */
	static String grantLocale(String name){
		int idx = name.lastIndexOf(rsrc.getString('CURRENT_DIRECTORY'))
		if (idx < 0) return "${name}${rsrc.getString('LOCALE_SEPARATOR')}${Locale.default}"
		return "${name.substring(0, idx)}${rsrc.getString('LOCALE_SEPARATOR')}${Locale.default}${name.substring(idx)}"
	}
	
	/**
	 * リソースの入力ストリームを返します。
	 * @param name リソースの名前
	 * @return 入力ストリーム（リソースがみつからない場合は null）
	 * @see #grope(String)
	 */
	static InputStream openStream(String name) {
		return grope(name)?.openStream()
	}
	
	/**
	 * リソース上のファイル内容を文字列として返します。<br/>
	 * 文字コードは {@link Charset#defaultCharset()}を使用します。
	 * @param name リソースの名前
	 * @return ファイル内容（リソースがみつからない場合は null）
	 * @see #openStream(String)
	 */
	static String toString(String name){
		return openStream(name)?.withCloseable { InputStream stream ->
			IOUtils.toString(stream, Charset.defaultCharset())
		}
	}
	
	/**
	 * リソース上のファイルを解析し、その結果を返します。<br/>
	 * 解析には {@link groovy.util.ConfigSlurper#parse(URL)}を用います。
	 * ファイルは {@link groovy.util.ConfigSlurper}で解析できる形式で
	 * 記述してください。
	 * @param name リソースの名前
	 * @return ConfigObject（リソースがみつからない場合は null）
	 * @see #grope(String)
	 */
	static ConfigObject configObject(String name){
		URL url = grope(name)
		return (url == null)? null : slurper.parse(url)
	}
}
