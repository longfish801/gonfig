/*
 * GropedResourceSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gonfig

import java.nio.charset.Charset
import java.util.regex.Pattern
import java.util.regex.Matcher
import org.apache.commons.io.IOUtils
import spock.lang.Specification
import spock.lang.Unroll

/**
 * GropedResourceクラスのテスト。
 * @author io.github.longfish801
 */
class GropedResourceSpec extends Specification {
	def 'getClazz'(){
		when: 'オーバーライドされていません'
		RsrcNotOverride.clazz
		then:
		thrown(UnsupportedOperationException)
	}
	
	def 'setBaseDir'(){
		expect: 'デフォルト値はカレントディレクトリ'
		Rsrc.baseDir.path == '.'
		
		when: '基底ディレクトリを設定します'
		Rsrc.setBaseDir('somepath')
		then:
		Rsrc.baseDir.path == 'somepath'
	}
	
	def 'getFlatDir'(){
		given:
		File dir
		
		when: '平坦ディレクトリを返します'
		dir = Rsrc.setBaseDir('somepath').flatDir
		then:
		dir.path == 'somepath/io.github.longfish801.gonfig'.replaceAll(Pattern.quote('/'), Matcher.quoteReplacement(File.separator))
		
		when: 'クラスがパッケージに所属しない場合、相対パスはカレントディレクトリを用います'
		dir = RsrcNoPkg.setBaseDir('somepath').flatDir
		then:
		dir.path == 'somepath/.'.replaceAll(Pattern.quote('/'), Matcher.quoteReplacement(File.separator))
	}
	
	def 'getDeepDir'(){
		given:
		File dir
		
		when: '深層ディレクトリを返します'
		dir = Rsrc.setBaseDir('somepath').deepDir
		then:
		dir.path == 'somepath/io/github/longfish801/gonfig'.replaceAll(Pattern.quote('/'), Matcher.quoteReplacement(File.separator))
		
		when: 'クラスがパッケージに所属しない場合、相対パスはカレントディレクトリを用います'
		dir = RsrcNoPkg.setBaseDir('somepath').deepDir
		then:
		dir.path == 'somepath/.'.replaceAll(Pattern.quote('/'), Matcher.quoteReplacement(File.separator))
	}
	
	@Unroll
	def 'grope - unroll'(){
		given:
		URL url
		Closure getText = { URL resource ->
			return resource?.openStream().withCloseable {
				IOUtils.toString(it, Charset.defaultCharset())
			}
		}
		
		expect:
		getText(Rsrc.setBaseDir('src/test/file').grope(name)) == expect
		
		where:
		name			|| expect
		'grope01.txt'	|| 'grope01'	// デフォルトロケール付き - ファイルシステムの基底ディレクトリ上
		'grope02.txt'	|| 'grope02'	// デフォルトロケール付き - ファイルシステムの平坦ディレクトリ上
		'grope03.txt'	|| 'grope03'	// デフォルトロケール付き - ファイルシステムの深層ディレクトリ上
		'grope04.txt'	|| 'grope04'	// デフォルトロケール付き - クラスパス上
		'grope05.txt'	|| 'grope05'	// デフォルトロケール付き - クラスパスの、パッケージ名の半角コンマ(.)をパス区切り文字に置換した相対パス上
		'grope11.txt'	|| 'grope11'	// ファイルシステムの基底ディレクトリ上
		'grope12.txt'	|| 'grope12'	// ファイルシステムの平坦ディレクトリ上
		'grope13.txt'	|| 'grope13'	// ファイルシステムの深層ディレクトリ上
		'grope14.txt'	|| 'grope14'	// クラスパス上
		'grope15.txt'	|| 'grope15'	// クラスパスの、パッケージ名の半角コンマ(.)をパス区切り文字に置換した相対パス上
	}
	
	def 'grope'(){
		given:
		URL url
		
		when: 'リソースがみつからない場合は null'
		url = Rsrc.setBaseDir('src/test/file').grope('nosuchfile.txt')
		then:
		url == null
	}
	
	@Unroll
	def 'grantLocale'(){
		expect:
		Rsrc.grantLocale(name) == expect
		
		where:
		name			|| expect
		'some'			|| "some_${Locale.default}"			// 拡張子なし
		'some.groovy'	|| "some_${Locale.default}.groovy"	// 拡張子あり
		'some.'			|| "some_${Locale.default}."		// 終端が拡張子区切り
	}
	
	def 'openStream'(){
		given:
		InputStream stream
		Closure getText = { InputStream inputStream ->
			return inputStream?.withCloseable { IOUtils.toString(it, Charset.defaultCharset()) }
		}
		
		when: 'リソースの入力ストリームを返します'
		stream = Rsrc.openStream('stream.txt')
		then:
		getText(stream) == 'This is TEST.'
		
		when: 'リソースがみつからない場合は null'
		stream = Rsrc.openStream('nosuchfile.txt')
		then:
		stream == null
	}
	
	def 'toString'(){
		given:
		String text
		
		when: 'リソース上のファイル内容を文字列として返します。'
		text = Rsrc.toString('text.txt')
		then:
		text == 'これはテストです。'
		
		when: 'リソースがみつからない場合は null'
		text = Rsrc.toString('nosuchfile.txt')
		then:
		text == null
	}
	
	def 'configObject'(){
		given:
		ConfigObject config
		
		when:
		config = Rsrc.configObject('config.groovy')
		then:
		config.str == 'これはテストです。'
		config.specialChars == '～①㈱'
		config.number == 11.7
		config.some.list == [ 1, 2, 3 ]
		
		when: 'リソースがみつからない場合は null'
		config = Rsrc.configObject('nosuchfile.groovy')
		then:
		config == null
	}
}
