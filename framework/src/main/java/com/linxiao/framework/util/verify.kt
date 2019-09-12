package com.linxiao.framework.util

/**
 * 一些校验用的公共方法
 * @author Extends
 * @date 2019/6/14/014
 */


object Verify{
    /**
     * 校验银行卡是否正确
     */
    fun validateCheckBankCard(bankCard: String?): Boolean {
        if (bankCard == null ||bankCard.length < 15 || bankCard.length > 19) {
            return false
        }
        val bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length - 1))
        return if (bit == 'N') {
            false
        } else bankCard[bankCard.length - 1] == bit
    }

    private fun getBankCardCheckCode(nonCheckCodeBankCard: String?): Char {
        if (nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim { it <= ' ' }.isEmpty()
                || !nonCheckCodeBankCard.matches("\\d+".toRegex())) {
            //如果传的不是数据返回N
            return 'N'
        }
        val chs = nonCheckCodeBankCard.trim { it <= ' ' }.toCharArray()
        var luhmSum = 0
        var i = chs.size - 1
        var j = 0
        while (i >= 0) {
            var k = chs[i] - '0'
            if (j % 2 == 0) {
                k *= 2
                k = k / 10 + k % 10
            }
            luhmSum += k
            i--
            j++
        }
        return if (luhmSum % 10 == 0) '0' else (10 - luhmSum % 10 + '0'.toInt()).toChar()
    }

    /**
     * 校验码校验
     *
     *
     * 适用于18位的二代身份证号码
     *
     *
     * @param IDNo18 身份证号码
     * @return true - 校验通过<br></br>
     * false - 校验不通过
     */
    fun validateCheckNumber(iDNo18: String): Boolean {
        // 加权因子
        val w = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
        val iDNoArray = iDNo18.toCharArray()
        var sum = 0
        try {
            for (i in w.indices) {
                sum += Integer.parseInt(iDNoArray[i].toString()) * w[i]
            }
            // 校验位是X，则表示10
            if (iDNoArray[17] == 'X' || iDNoArray[17] == 'x') {
                sum += 10
            } else {
                sum += Integer.parseInt(iDNoArray[17].toString())
            }
        }catch (e:Exception){
            return false
        }
        // 如果除11模1，则校验通过
        return sum % 11 == 1
    }
}