package sample

import sample.grope.Config as config

class Sample {
	static void main(String[] args){
		config.str == 'これはテストです。'
		config.specialChars == '～①㈱'
		config.number == 11.7
		config.some.list == [ 1, 2, 3 ]
	}
}
