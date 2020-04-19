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

/**
 * GropedResourceクラスのテスト。
 * @version 1.0.00 2020/04/11
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
	
	def 'grope'(){
		given:
		URL url
		Closure getText = { URL resource ->
			return resource?.openStream().withCloseable {
				IOUtils.toString(it, Charset.defaultCharset())
			}
		}
		
		when: 'ファイルシステムの基底ディレクトリ上'
		url = Rsrc.setBaseDir('src/test/file').grope('grope01.txt')
		then:
		getText(url) == 'grope01'
		
		when: 'ファイルシステムの平坦ディレクトリ上'
		url = Rsrc.setBaseDir('src/test/file').grope('grope02.txt')
		then:
		getText(url) == 'grope02'
		
		when: 'ファイルシステムの深層ディレクトリ上'
		url = Rsrc.setBaseDir('src/test/file').grope('grope03.txt')
		then:
		getText(url) == 'grope03'
		
		when: 'クラスパス上'
		url = Rsrc.setBaseDir('src/test/file').grope('grope04.txt')
		then:
		getText(url) == 'grope04'
		
		when: 'クラスパスの、パッケージ名の半角コンマ(.)をパス区切り文字に置換した相対パス上'
		url = Rsrc.setBaseDir('src/test/file').grope('grope05.txt')
		then:
		getText(url) == 'grope05'
		
		when: 'リソースがみつからない場合は null'
		url = Rsrc.setBaseDir('src/test/file').grope('nosuchfile.txt')
		then:
		url == null
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
