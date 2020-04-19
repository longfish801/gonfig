/*
 * GonfigSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gonfig

import io.github.longfish801.gonfig.Config as config
import spock.lang.Specification

/**
 * Gonfigクラスのテスト。
 * @version 1.0.00 2020/04/14
 * @author io.github.longfish801
 */
class GonfigSpec extends Specification {
	def '$static_propertyMissing'(){
		expect: 'キーに紐づく設定値を参照します'
		config.str == 'これはテストです。'
		config.specialChars == '～①㈱'
		config.number == 11.7
		config.some.list == [ 1, 2, 3 ]
	}
}
