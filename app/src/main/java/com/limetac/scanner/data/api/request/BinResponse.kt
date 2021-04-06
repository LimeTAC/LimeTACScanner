package com.limetac.scanner.data.api.request

import com.limetac.scanner.data.model.BinTag
import java.io.Serializable

class BinResponse : Serializable {
    var type: String? = null
    var id: Long = 0
    var code: String? = null
    var isBin = false
    var packingItemId: Long = 0
    var tagDetails: List<BinTag>? = null
}