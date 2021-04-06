package com.limetac.scanner.data.model

import java.io.Serializable

class BinTag : Serializable {
    var tagCode: String? = null
    var isRight = false
    var tagType = 0
    var tagIndex = 0
}