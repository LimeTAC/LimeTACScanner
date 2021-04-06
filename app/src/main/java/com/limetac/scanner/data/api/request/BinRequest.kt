package com.limetac.scanner.data.api.request

import com.limetac.scanner.data.model.BinTag

class BinRequest {
    var locationCode: String? = null
    var tagList: List<BinTag>? = null
    var username = "eventreviewer1"
    var password = "!5353ns"
}